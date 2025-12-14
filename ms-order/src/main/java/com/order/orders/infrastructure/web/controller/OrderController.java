package com.order.orders.infrastructure.web.controller;

import com.order.orders.application.dto.*;
import com.product.products.infrastructure.validation.ValidCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.order.orders.application.service.OrderService;

import java.net.URI;
import java.util.List;


/**
 * Contrôleur REST pour la gestion des commandes.
 * 
 * Best practices REST :
 * - Utilisation correcte des verbes HTTP (GET, POST, PUT, DELETE, PATCH)
 * - Codes de statut HTTP appropriés (200, 201, 204, 404, etc.)
 * - URI RESTful (/api/v1/orders, /api/v1/orders/{id})
 * - Content negotiation avec MediaType
 * - Documentation OpenAPI/Swagger
 * - Validation des données avec @Valid
 * - ResponseEntity pour un contrôle total de la réponse
 * - Location header pour les ressources créées
 * - Séparation des préoccupations (délégation au service)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "API de gestion des commandes")
public class OrderController {

    private final OrderService orderService;

    /**
     * GET /api/v1/orders
     * Récupère la liste de tous les commandes
     * 
     * @return Liste des commandes avec code 200 OK
     */
    @Operation(summary = "Récupérer tous les commandes", 
               description = "Retourne la liste complète de tous les commandes enregistrés")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = OrderResponseDTO.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        log.info("GET /api/v1/orders - Récupération de tous les commandes");
        
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/v1/orders/{id}
     * Récupère un commande par son ID
     * 
     * @param id L'identifiant du commande
     * @return Le commande avec code 200 OK ou 404 NOT FOUND
     */
    @Operation(summary = "Récupérer un commande par ID", 
               description = "Retourne un commande spécifique basé sur son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Utilisateur trouvé",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Utilisateur non trouvé",
                    content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDetailsResponseDTO> getOrderById(
            @Parameter(description = "ID du commande", required = true)
            @PathVariable Long id) {
        
        log.info("GET /api/v1/orders/{} - Récupération du commande", id);

        OrderDetailsResponseDTO order = orderService.getOrderById(id);
        
        return ResponseEntity.ok(order);
    }

    /**
     * POST /api/v1/orders
     * Crée une nouvelle commande
     * 
     * @param orderRequestDTO Les données de la commande à créer
     * @return La commande créée avec code 201 CREATED et Location header
     */
    @Operation(summary = "Créer une nouvelle commande",
               description = "Crée une nouvelle commande avec les données fournies")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", 
                    description = "Utilisateur créé avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Données invalides",
                    content = @Content),
        @ApiResponse(responseCode = "409", 
                    description = "Le commande existe déjà",
                    content = @Content),
        @ApiResponse(responseCode = "412",
                    description = "Stock insuffisant",
                    content = @Content)
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createOrder(
            @Parameter(description = "Données du commande à créer", required = true)
            @Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        
        log.info("POST /api/v1/orders - Création d'une commande: {}", orderRequestDTO.getUserId());
        log.info("Reçu : {}", orderRequestDTO);


        OrderResponseDTO createdOrder = orderService.createOrder(orderRequestDTO);
        
        // Best practice REST : retourner l'URI de la ressource créée dans le header Location
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdOrder.getId())
                .toUri();
        
        return ResponseEntity
                .created(location)
                .body(createdOrder);
    }

    /**
     * PUT /api/v1/orders/{id}
     * Met à jour complètement une commande existant
     * 
     * @param id Le identifiant du commande
     * @param orderStatusDTO Les nouvelles données de la commande
     * @return Le commande mis à jour avec code 200 OK
     */
    @Operation(summary = "Mettre à jour un commande", 
               description = "Met à jour complètement les informations d'une commande existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Utilisateur mis à jour avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Données invalides",
                    content = @Content),
        @ApiResponse(responseCode = "404", 
                    description = "Utilisateur non trouvé",
                    content = @Content),
        @ApiResponse(responseCode = "409", 
                    description = "Conflit avec un commande existant",
                    content = @Content),
        @ApiResponse(responseCode = "412",
                    description = "Status demandé incorrect",
                    content = @Content)
    })
    @PutMapping(value = "/{id}/status",
                consumes = MediaType.APPLICATION_JSON_VALUE, 
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @Parameter(description = "ID du commande", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données du commande", required = true)
            @Valid @RequestBody OrderStatusRequestDTO orderStatusDTO) {
        
        log.info("PUT /api/v1/orders/{} - Mise à jour de la commande", id);

        OrderResponseDTO updatedOrder = orderService.updateOrderStatus(id, orderStatusDTO);
        
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * DELETE /api/v1/orders/{id}
     * Supprime un commande
     * 
     * @param id L'identifiant de la commande
     * @return Code 204 NO CONTENT
     */
    @Operation(summary = "Supprimer un commande", 
               description = "Supprime définitivement un commande")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204",
                    description = "Utilisateur supprimé avec succès",
                    content = @Content),
        @ApiResponse(responseCode = "404",
                    description = "Utilisateur non trouvé",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "ID du commande", required = true)
            @PathVariable Long id) {
        
        log.info("DELETE /api/v1/orders/{} - Suppression du commande", id);
        
        orderService.cancelOrder(id);
        
        // Best practice REST : 204 No Content pour une suppression réussie
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/orders/search?lastName={lastName}
     * Recherche des commandes par nom
     * 
     * @param id L'id à rechercher
     * @return Liste des commandes correspondantes
     */
    @Operation(summary = "Rechercher des commandes par nom", 
               description = "Recherche des commandes dont le nom contient la chaîne spécifiée")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Recherche effectuée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = OrderResponseDTO.class)))
    })
    @GetMapping(value = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponseDTO>> searchUserOrders(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathVariable Long id) {
        
        log.info("GET /api/v1/orders/user/{} - Recherche de commandes", id);
        
        List<OrderResponseDTO> orders = orderService.searchOrdersByUserId(id);
        
        return ResponseEntity.ok(orders);
    }

    /**
     * PATCH /api/v1/orders/{id}/deactivate
     * Désactive un commande (soft delete)
     * 
     * @param status Le statut des commandes
     * @return Les commandes avec le statut demandé
     */
    @Operation(summary = "Modifier le stock d'une commande",
               description = "Modifier le stock d'une commande")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur désactivé avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "404",
                    description = "Statut non trouvé",
                    content = @Content)
    })
    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponseDTO>> filterOrderByStatus(
            @Parameter(description = "statut des commandes", required = true)
            @PathVariable @Valid String status) {

        log.info("PATCH /api/v1/orders/status/{} - Recherche de commande", status);

        List<OrderResponseDTO> orders = orderService.searchOrdersByStatus(status);
        
        return ResponseEntity.ok(orders);
    }
}
