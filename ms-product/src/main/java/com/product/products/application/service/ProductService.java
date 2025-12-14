package com.product.products.application.service;

import com.product.products.domain.entity.Category;
import com.product.products.infrastructure.exception.ExternalServiceException;
import com.product.products.infrastructure.exception.ExternalUsageException;
import com.product.products.infrastructure.exception.FieldValueException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.product.products.application.dto.ProductRequestDTO;
import com.product.products.application.dto.ProductResponseDTO;
import com.product.products.application.mapper.ProductMapper;
import com.product.products.domain.entity.Product;
import com.product.products.domain.repository.ProductRepository;
import com.product.products.infrastructure.exception.ResourceNotFoundException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.stream.Collectors;

import static com.product.products.configuration.WebClientConfig.MS_ORDER_BASE_URL;

/**
 * Service pour la gestion des produits.
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
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final MeterRegistry meterRegistry;

    private final WebClient orderServiceWebClient;

    /**
     * Récupère tous les produits
     */
    public List<ProductResponseDTO> getAllProducts() {
        log.debug("Récupération de tous les produits");
        
        List<Product> products = productRepository.findAll();
        
        log.info("Nombre de produits récupérés: {}", products.size());
        
        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un produit par son ID
     */
    public ProductResponseDTO getProductById(Long id) {
        log.debug("Récupération du produit avec l'ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        log.info("Produit trouvé: {}", product.getName());
        
        return productMapper.toDto(product);
    }

    /**
     * Crée un nouveau produit
     */
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        log.debug("Création d'un nouveau produit: {}", productRequestDTO.getName());
        
        // Vérifier si produit existe déjà
        if (!productRepository.searchByName(productRequestDTO.getName()).isEmpty()) {
            log.warn("Tentative de création d'un produit avec un nom similaire existant: {}",
                    productRequestDTO.getName());
        }

        Product product = productMapper.toEntity(productRequestDTO);
        Product savedProduct = productRepository.save(product);
        
        // Métrique personnalisée
        String counterName = "products.created."+product.getCategory().toString().toLowerCase();
        Counter.builder(counterName)
                .description("Nombre de produits créés dans la catégorie "+ product.getCategory().toString().toLowerCase())
                .tag("type", "product")
                .register(meterRegistry)
                .increment();
        
        log.info("Produit créé avec succès: ID={}, Name={}", savedProduct.getId(), savedProduct.getName());
        
        return productMapper.toDto(savedProduct);
    }

    /**
     * Met à jour un produit existant
     */
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        log.debug("Mise à jour du produit avec l'ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        String counterName = "products.created."+product.getCategory().toString().toLowerCase();
        Counter.builder(counterName)
                .register(meterRegistry)
                .increment(-1);

        productMapper.updateEntityFromDto(productRequestDTO, product);
        Product updatedProduct = productRepository.save(product);
        
        // Métrique personnalisée
        String counterUpdateName = "products.created."+updatedProduct.getCategory().toString().toLowerCase();
        Counter.builder(counterUpdateName)
                .register(meterRegistry)
                .increment(1);
        
        log.info("Produit mis à jour avec succès: ID={}, Name={}", 
                updatedProduct.getId(), updatedProduct.getName());
        
        return productMapper.toDto(updatedProduct);
    }

    /**
     * Supprime un produit
     */
    @Transactional
    public void deleteProduct(Long id) {
        log.debug("Suppression du produit avec l'ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

                try {
                        // Si la requête retourne 200 => le produit est utilisé dans au moins une commande
                        orderServiceWebClient.get().uri(MS_ORDER_BASE_URL+"/product/"+id)
                                        .retrieve().bodyToMono(String.class).block();

                        // Refuser la suppression si le produit est référencé
                        throw new ExternalUsageException("Le produit est utilisé dans au moins une commande et ne peut pas être supprimé");
                } catch (WebClientResponseException.NotFound e) {
                        // Produit non trouvé dans les commandes => suppression autorisée
                        productRepository.delete(product);

                        // Métrique personnalisée
                        String counterName = "products.created."+product.getCategory().toString().toLowerCase();
                        Counter.builder(counterName)
                                .register(meterRegistry)
                                .increment(-1);
                        
                        log.info("Produit supprimé avec succès: ID={}, Name={}", id, product.getName());
                } catch (WebClientRequestException | WebClientResponseException e) {
                        // Erreur de communication ou erreur serveur côté MS_ORDER
                        throw new ExternalServiceException("Le service de commandes est indisponible.");
                }


        
        
    }

    /**
     * Recherche des produits par nom
     */
    public List<ProductResponseDTO> searchProductsByName(String name) {
        log.debug("Recherche de produits avec le nom: {}", name);
        
        List<Product> products = productRepository.searchByName(name);
        
        log.info("Nombre de produits trouvés: {}", products.size());
        
        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Recherche des produits par catégorie
     */
    public List<ProductResponseDTO> searchProductsByCategory(String category) {
        log.debug("Recherche de produits avec la catégorie: {}", category);

        List<Product> products = productRepository.findByCategory(Category.valueOf(category));

        log.info("Nombre de produits trouvés: {}", products.size());

        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Recherche des produits par catégorie
     */
    public List<ProductResponseDTO> getAvailableProducts() {
        log.debug("Recherche de produits en stock");

        List<Product> products = productRepository.findAvailableProduct();

        log.info("Nombre de produits trouvés: {}", products.size());

        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Met à jour un le stock d'un produit existant
     */
    @Transactional
    public ProductResponseDTO updateStock(Long id, int stock) {
        log.debug("Mise à jour du produit avec l'ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Vérification des règles métiers
        if (product.getStock()+stock < 0) {
            throw new FieldValueException("Product","stock", product.getStock()+stock, "le nouveau stock doit être positif ou nul");
        }

        int oldStock = product.getStock();
        product.setStock(product.getStock()+stock);
        Product updatedProduct = productRepository.save(product);

        // Métrique personnalisée
        Counter.builder("products.updated")
                .description("Nombre de produits mis à jour")
                .tag("type", "product")
                .register(meterRegistry)
                .increment();

        log.info("Stock produit mis à jour avec succès: ID={}, Name={}, Ancien stock={}, Nouveau stock={}",
                updatedProduct.getId(), updatedProduct.getName(), oldStock, product.getStock());

        return productMapper.toDto(updatedProduct);
    }

    
}
