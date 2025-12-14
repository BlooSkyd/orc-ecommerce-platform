package com.product.products.application.service;

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

import java.util.List;
import java.util.stream.Collectors;

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
        Counter.builder("products.created")
                .description("Nombre de produits créés")
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
        
                
        productMapper.updateEntityFromDto(productRequestDTO, product);
        Product updatedProduct = productRepository.save(product);
        
        // Métrique personnalisée
        Counter.builder("products.updated")
                .description("Nombre de produits mis à jour")
                .tag("type", "product")
                .register(meterRegistry)
                .increment();
        
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
        
        productRepository.delete(product);
        
        // Métrique personnalisée
        Counter.builder("products.deleted")
                .description("Nombre de produits supprimés")
                .tag("type", "product")
                .register(meterRegistry)
                .increment();
        
        log.info("Produit supprimé avec succès: ID={}, Name={}", id, product.getName());
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

        List<Product> products = productRepository.searchByCategory(category);

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
