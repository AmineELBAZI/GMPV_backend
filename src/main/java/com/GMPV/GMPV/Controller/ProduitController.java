package com.GMPV.GMPV.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.GMPV.GMPV.Entity.Produit;
import com.GMPV.GMPV.Service.ProduitService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {
	    "http://localhost:3000",
	    "https://gmpv-frontend-nu.vercel.app"
	})
public class ProduitController {

    @Autowired
    private ProduitService productService;

    // ✅ Obtenir tous les produits
    @GetMapping
    public List<Produit> getAllProducts() {
        return productService.getAllProducts();
    }

    // ✅ Obtenir un produit par ID
    @GetMapping("/{id}")
    public Produit getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    // ✅ Obtenir les produits par catégorie
    @GetMapping("/by-categorie/{id}")
    public List<Produit> getByCategorie(@PathVariable Long id) {
        return productService.getProduitsByCategorieId(id);
    }

    // ✅ Obtenir les produits d'une boutique via Stock
    @GetMapping("/by-boutique/{boutiqueId}")
    public List<Produit> getProduitsParBoutique(@PathVariable Long boutiqueId) {
        return productService.getProduitsParBoutique(boutiqueId);
    }

    // ✅ Obtenir la quantité totale en stock d’un produit
    @GetMapping("/{id}/quantite-totale")
    public double getQuantiteTotale(@PathVariable Long id) {
        return productService.getQuantiteTotaleParProduit(id);
    }

    // ✅ Obtenir Tous les produits quantitystock >0
    @GetMapping("/rupture")
    public ResponseEntity<List<Produit>> getProduitsEnRupture() {
        List<Produit> produits = productService.getProduitsEnRupture();
        return ResponseEntity.ok(produits);
    }


    // ✅ Créer un produit
    @PostMapping
    public Produit createProduct(@RequestBody Produit product) {
        return productService.createProduct(product);
    }

    // ✅ Mettre à jour un produit
    @PutMapping("/{id}")
    public Produit updateProduct(@PathVariable Long id, @RequestBody Produit product) {
        return productService.updateProduct(id, product);
    }

    // ✅ Supprimer un produit et ses stocks associés
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
