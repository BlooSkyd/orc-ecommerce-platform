package com.order.orders.application.service;

import com.order.orders.application.dto.*;
import com.order.orders.application.mapper.OrderItemMapper;
import com.order.orders.application.mapper.OrderMapper;
import com.order.orders.domain.entity.OrderItem;
import com.order.orders.domain.entity.OrderStatus;
import com.order.orders.domain.repository.OrderItemRepository;
import com.order.orders.infrastructure.exception.ExternalServiceException;
import com.order.orders.infrastructure.exception.FieldValueException;
import com.order.orders.infrastructure.exception.InsufficientStockException;
import com.product.products.application.dto.ProductResponseDTO;
import com.product.products.application.dto.StockUpdateRequestDTO;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.order.orders.domain.entity.Order;
import com.order.orders.domain.repository.OrderRepository;
import com.order.orders.infrastructure.exception.ResourceNotFoundException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

import static com.order.orders.configuration.WebClientConfig.MS_PRODUCT_BASE_URL;
import static com.order.orders.configuration.WebClientConfig.MS_USER_BASE_URL;

/**
 * Service pour la gestion des commandes.
 * Best practices :
 * - @Transactional pour la gestion des transactions
 * - Logging avec SLF4J
 * - Métriques personnalisées avec Micrometer
 * - Gestion d'erreurs explicite avec exceptions métier
 * - Séparation de la logique métier du contrôleur
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final MeterRegistry meterRegistry;
    private final WebClient productServiceWebClient;
    private final WebClient userServiceWebClient;


    private final DoubleAdder dailyTotal = new DoubleAdder();

    @PostConstruct
    public void init() {
        // Enregistrement de la gauge après l'injection des dépendances
        meterRegistry.gauge("commandes.montant.total.jour", dailyTotal, DoubleAdder::sum);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void resetDailyTotal() {
        dailyTotal.reset();
    }

    /**
     * Récupère toutes les commandes
     */
    public List<OrderResponseDTO> getAllOrders() {
        log.debug("Récupération de tous les commandes");
        
        List<Order> orders = orderRepository.findAll();
        
        log.info("Nombre de commandes récupérés: {}", orders.size());
        
        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une commande par son ID
     */
    public OrderDetailsResponseDTO getOrderById(Long id) {
        log.debug("Récupération du commande avec l'ID: {}", id);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));


        List<OrderItemResponseDTO> itemsDTOList = new ArrayList<>();
        // Récupération et conversion des orderItems
        for(OrderItem item : orderItemRepository.searchByOrderId(order.getId())) {
            OrderItemResponseDTO itemDTO = orderItemMapper.toDto(item);
            itemsDTOList.add(itemDTO);
        }

        OrderResponseDTO orderResponseDTO = orderMapper.toDto(order);

        OrderDetailsResponseDTO orderDetailsResponseDTO = orderMapper.toDetailsDto(orderResponseDTO, itemsDTOList);


        log.info("Commande trouvée: ID={}, UserId={}, CreationDate={}, TotalPrice={}, itemAmount={}", orderDetailsResponseDTO.getId(),
                orderDetailsResponseDTO.getUserId(), orderDetailsResponseDTO.getCreatedAt(), orderDetailsResponseDTO.getTotalAmount(), orderDetailsResponseDTO.getOrderItems().size());
        
        return orderDetailsResponseDTO;
    }

    /**
     * Crée une nouvelle commande
     */
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        log.debug("Création d'une nouvelle commande: {} ({} articles)", orderRequestDTO.getUserId(), orderRequestDTO.getItems().size());

        // Vérifications préliminaires
        // 1. Vérif User
        try {
            userServiceWebClient.get().uri(MS_USER_BASE_URL +"/"+orderRequestDTO.getUserId())
                    .retrieve().toBodilessEntity().block();
        } catch (WebClientResponseException.NotFound e) {
            throw new ResourceNotFoundException("Utilisateur introuvable ID: " + orderRequestDTO.getUserId());
        } catch (WebClientRequestException e) {
            throw new ExternalServiceException("Le service utilisateur est indisponible.");
        }

        // 2. Vérif Produits
        for (OrderItemRequestDTO item : orderRequestDTO.getItems()) {
            try {
                ProductResponseDTO product = productServiceWebClient.get()
                        .uri(MS_PRODUCT_BASE_URL+"/"+item.getProductId())
                        .retrieve().bodyToMono(ProductResponseDTO.class).block();

                if (product == null) throw new ResourceNotFoundException("Produit vide ID: " + item.getProductId());

                if (product.getStock() < item.getQuantity()) {
                    throw new InsufficientStockException(
                            String.format("Stock insuffisant pour '%s'. Demandé: %d, Dispo: %d",
                                    product.getName(), item.getQuantity(), product.getStock()));
                }
            } catch (WebClientResponseException.NotFound e) {
                throw new ResourceNotFoundException("Produit introuvable ID: " + item.getProductId());
            } catch (WebClientRequestException e) {
                throw new ExternalServiceException("Le service produit est indisponible.");
            }
        }


        // 3. Création de la commande
        Order order = orderMapper.toEntity(orderRequestDTO);
        orderRepository.save(order); // Sauvegarder afin d'avoir un id

        for (OrderItemRequestDTO itemRequestDTO : orderRequestDTO.getItems()) {
            ProductResponseDTO productResponseDTO = productServiceWebClient.get()
                    .uri(MS_PRODUCT_BASE_URL +"/"+itemRequestDTO.getProductId())
                    .retrieve()
                    .bodyToMono(ProductResponseDTO.class)
                    .block();

            assert productResponseDTO != null;
            OrderItem orderItem = orderItemMapper.toEntity(itemRequestDTO, productResponseDTO, order.getId());
            orderItemRepository.save(orderItem);
            // Méthode custom qui ajoute l'item à la commande et addition son sous total
            order.addItem(orderItem);

            // 4. Mise à jour des stocks
            StockUpdateRequestDTO stockUpdateRequestDTO = new StockUpdateRequestDTO();
            stockUpdateRequestDTO.setStockModification(itemRequestDTO.getQuantity()*-1);
            try {
                productServiceWebClient.patch()
                        .uri(MS_PRODUCT_BASE_URL +"/"+itemRequestDTO.getProductId()+"/stock")
                        .bodyValue(stockUpdateRequestDTO)
                        .retrieve().toBodilessEntity().block();
            } catch (WebClientRequestException e) {
                order.setStatus(OrderStatus.CANCELLED);
                throw new ExternalServiceException("Le service utilisateur est indisponible.");
            }

        }

        //5. Mise à jour de la commande avec le prix final
        orderRepository.saveAndFlush(order);

        // Maj du métric du status
        String counterNameNewStatus = "orders."+order.getStatus().toString().toLowerCase();
        Counter.builder(counterNameNewStatus)
                .register(meterRegistry)
                .increment();

        
        log.info("Commande créé avec succès: ID={}, UserId={}, TotalPrice={}", order.getId(),
                order.getUserId(), order.getTotalAmount());
        
        return orderMapper.toDto(order);
    }

    /**
     * Met à jour une commande existant
     */
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatusRequestDTO orderStatusRequestDTO) {
        log.debug("Mise à jour du commande avec l'ID: {}", id);

        if (OrderStatus.valueOf(orderStatusRequestDTO.getStatus()) == OrderStatus.PENDING) {
            throw new FieldValueException("orderStatusRequestDTO","status",orderStatusRequestDTO.getStatus(),"Une commande ne peut pas revenir au statut 'en attente'");
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));


        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new FieldValueException("order","status",order.getStatus(),"Une commande ne peut pas être modifiée si elle est livrée ou annulée");
        }

        // Si la commande a un statut plus avancé que celui de la demande d'update
        // On refuse la mise à jour
        if (order.getStatus().getScore() > OrderStatus.valueOf(orderStatusRequestDTO.getStatus()).getScore()) {
            throw new FieldValueException("orderStatusRequestDTO","status",orderStatusRequestDTO.getStatus(),"Une commande ne peut pas être modifiée vers un état en amont de son statut actuel");
        }

        if (order.getStatus() == OrderStatus.PENDING &&
                OrderStatus.valueOf(orderStatusRequestDTO.getStatus()) != OrderStatus.CANCELLED) {
            log.info("Nouvelle commande confirmée de la journée, cumule du montant généré");
            order.setOrderDate(LocalDateTime.now());

            // Maj du métric du montant généré ajd
            dailyTotal.add(order.getTotalAmount().doubleValue());
        }

        // Si la date de création (ie confirmation ou +) de la commande est aujourd'hui
        // et qu'on souhaite annuler la commande, il faut soustraire au bilan du jour
        // Rem: le champ orderDate est modifié seulement si la commande change d'état depuis PENDING
        if (
                OrderStatus.valueOf(orderStatusRequestDTO.getStatus()) == OrderStatus.CANCELLED &&
                order.getStatus() != OrderStatus.PENDING &&
                order.getOrderDate().toLocalDate().isEqual(LocalDate.now())
        ) {

            // Maj du métric du montant généré ajd
            dailyTotal.add(order.getTotalAmount().doubleValue() * -1.0);
        }

        String counterName = "orders."+order.getStatus().toString().toLowerCase();
        Counter.builder(counterName)
                .register(meterRegistry)
                .increment(-1);

        order.setStatus(OrderStatus.valueOf(orderStatusRequestDTO.getStatus()));
        orderRepository.saveAndFlush(order);

        log.info(order.toString());

        // Métrique personnalisée
        String counterNameNewStatus = "orders."+order.getStatus().toString().toLowerCase();
        Counter.builder(counterNameNewStatus)
                .register(meterRegistry)
                .increment();
        
        log.info("Commande mis à jour avec succès: ID={}, newStatus={}",
                order.getId(), order.getStatus());
        
        return orderMapper.toDto(order);
    }

    /**
     * Supprime un commande
     */
    @Transactional
    public void cancelOrder(Long id) {
        log.debug("Annulation de la commande avec l'ID: {}", id);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if (order.getStatus().getScore() == 4) { // 4 = DELIVERED or CANCELLED
            throw new FieldValueException("order","status",order.getStatus(),"Une commande ne peut pas être annulée si elle est livrée ou déjà annulée");
        }

        String counterName = "orders."+order.getStatus().toString().toLowerCase();
        Counter.builder(counterName)
                .register(meterRegistry)
                .increment(-1);

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.saveAndFlush(order);
        
        // Métrique personnalisée
        String counterNameNewStatus = "orders."+order.getStatus().toString().toLowerCase();
        Counter.builder(counterNameNewStatus)
                .register(meterRegistry)
                .increment();

        // Si la date de création (ie confirmation ou +) de la commande est aujourd'hui,
        // il faut soustraire au bilan du jour
        // Rem: le champ orderDate est modifié seulement si la commande change d'état depuis PENDING
        if (order.getOrderDate().toLocalDate().isEqual(LocalDate.now())) {

            // Maj du métric du montant généré ajd
            dailyTotal.add(order.getTotalAmount().doubleValue() * -1.0);
        }

        log.info("Commande annulée avec succès: ID={}, Status={}", id, order.getStatus());
    }

    /**
     * Recherche des commandes par nom
     */
    public List<OrderResponseDTO> searchOrdersByUserId(Long userId) {
        log.debug("Recherche des commandes de l'utilisateur: {}", userId);

        // 1. Vérif User
        try {
            userServiceWebClient.get().uri(MS_USER_BASE_URL +"/"+userId)
                    .retrieve().toBodilessEntity().block();
        } catch (WebClientResponseException.NotFound e) {
            throw new ResourceNotFoundException("Utilisateur introuvable ID: " + userId);
        } catch (WebClientRequestException e) {
            throw new ExternalServiceException("Le service utilisateur est indisponible.");
        }
        
        List<Order> orders = orderRepository.findAllUserOrder(userId);
        
        log.info("Nombre de commandes trouvés: {}", orders.size());
        
        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Recherche des commandes par catégorie
     */
    public List<OrderResponseDTO> searchOrdersByStatus(String status) {
        log.debug("Recherche de commandes avec la catégorie: {}", status);

        List<Order> orders = orderRepository.findByStatus(OrderStatus.valueOf(status));

        log.info("Nombre de commandes trouvés: {}", orders.size());

        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }


    
}
