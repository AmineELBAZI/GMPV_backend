package com.GMPV.GMPV.Service;

import com.GMPV.GMPV.Entity.Produit;
import com.GMPV.GMPV.Entity.Stock;
import com.GMPV.GMPV.Repository.ProduitRepository;
import com.GMPV.GMPV.Repository.StockRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProduitService {

    @Autowired
    private ProduitRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    // ✅ Tous les produits
    public List<Produit> getAllProducts() {
        return productRepository.findAll();
    }
 // ✅ Tous les produits quantitystock >0
    public List<Produit> getProduitsEnRupture() {
        return productRepository.findByQuantityStockGreaterThan(0.0);
    }

    // ✅ Produits d'une boutique via Stock
    public List<Produit> getProduitsParBoutique(Long boutiqueId) {
        return stockRepository.findByBoutiqueId(boutiqueId)
                .stream()
                .map(Stock::getProduit)
                .distinct()
                .collect(Collectors.toList());
    }

    // ✅ Produit par ID
    public Optional<Produit> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // ✅ Créer un produit
    public Produit createProduct(Produit product) {
        // Vérifie si une référence existe déjà
        if (product.getReference() == null || product.getReference().isEmpty()) {
            // Récupère le dernier produit par ID décroissant
            Optional<Produit> lastProductOpt = productRepository.findTopByOrderByIdDesc();
            long nextNumber = lastProductOpt.map(p -> p.getId() + 1).orElse(1L);

            // Génère la référence auto-incrémentée
            String reference = "PROD" + String.format("%04d", nextNumber);
            product.setReference(reference);
        }

        return productRepository.save(product);
    }


    // ✅ Mettre à jour un produit
    public Produit updateProduct(Long id, Produit updatedProduct) {
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedProduct.getName());
                    existing.setPrice_buy(updatedProduct.getPrice_buy());
                    existing.setPrice_sell(updatedProduct.getPrice_sell());
                    existing.setCategorie(updatedProduct.getCategorie());
                    existing.setQuantityStock(updatedProduct.getQuantityStock());
                    return productRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // ✅ Supprimer un produit et ses stocks associés
    public void deleteProduct(Long id) {
        // Supprimer tous les stocks du produit avant de le supprimer
        stockRepository.deleteByProduitId(id);
        productRepository.deleteById(id);
    }

    // ✅ Produits par catégorie
    public List<Produit> getProduitsByCategorieId(Long categorieId) {
        return productRepository.findByCategorieId(categorieId);
    }

    // ✅ Quantité totale en stock d’un produit (utile pour affichage ou contrôle)
    public double getQuantiteTotaleParProduit(Long produitId) {
        return stockRepository.findByProduitId(produitId)
                .stream()
                .mapToDouble(Stock::getQuantity)
                .sum();
    }
    
    // ✅ decrement  Quantity  de Stock
    public void decrementQuantityStock(Long produitId, double quantity) {
        Produit produit = productRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        if (produit.getQuantityStock() < quantity) {
            throw new RuntimeException("Quantité en stock insuffisante");
        }
        produit.setQuantityStock(produit.getQuantityStock() - quantity);
        productRepository.save(produit);
    }

}
