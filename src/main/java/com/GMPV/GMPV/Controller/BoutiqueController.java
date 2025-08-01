package com.GMPV.GMPV.Controller;


import com.GMPV.GMPV.Entity.Boutique;

import com.GMPV.GMPV.Entity.Produit;
import com.GMPV.GMPV.Entity.Stock;
import com.GMPV.GMPV.Service.BoutiqueService;
import com.GMPV.GMPV.Service.ProduitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/boutiques")
@CrossOrigin(origins = {
	    "http://localhost:3000",
	    "https://gmpv-frontend-nu.vercel.app"
	})
public class BoutiqueController {

    private final BoutiqueService boutiqueService;
    private final ProduitService produitService;

    public BoutiqueController(BoutiqueService boutiqueService, ProduitService produitService) {
        this.boutiqueService = boutiqueService;
        this.produitService = produitService;
    }

    @GetMapping
    public List<Boutique> getAllBoutiques() {
        return boutiqueService.getAllBoutiques();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Boutique> getBoutiqueById(@PathVariable Long id) {
        return boutiqueService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/produits")
    public ResponseEntity<List<Produit>> getProduitsByBoutique(@PathVariable Long id) {
        List<Produit> produits = boutiqueService.getProduitsByBoutiqueId(id);
        return ResponseEntity.ok(produits);
    }

    @PostMapping(consumes = "application/json")
    public Boutique createBoutique(@RequestBody Boutique boutique) {
        return boutiqueService.createBoutique(boutique);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Boutique> updateBoutique(@PathVariable Long id, @RequestBody Boutique boutique) {
        Boutique updated = boutiqueService.updateBoutique(id, boutique);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoutique(@PathVariable Long id) {
        try {
            boutiqueService.deleteBoutique(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur interne: " + e.getMessage());
        }
    }


    // Mise à jour ici : on passe la quantité en paramètre dans la requête (ex: ?quantity=10)
    @PostMapping("/{id}/produits/{produitId}")
    public ResponseEntity<Stock> ajouterProduit(
            @PathVariable Long id,
            @PathVariable Long produitId,
            @RequestParam(defaultValue = "1") double quantity) {

        Optional<Produit> produitOptional = produitService.getProductById(produitId);
        if (produitOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Stock savedStock = boutiqueService.ajouterProduitDansBoutique(id, produitOptional.get(), quantity);
            return ResponseEntity.ok(savedStock);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}/produits")
    public ResponseEntity<String> supprimerTousLesProduits(
            @PathVariable Long id) {
        try {
            boutiqueService.supprimerTousLesProduitsDeLaBoutique(id);
            return ResponseEntity.ok("Tous les produits ont été supprimés de la boutique avec succès.");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body("Erreur : " + ex.getMessage());
        }
    }
    
    @DeleteMapping("/{boutiqueId}/produits/{produitId}")
    public ResponseEntity<String> supprimerProduitDeBoutique(
            @PathVariable Long boutiqueId,
            @PathVariable Long produitId) {
        try {
            boutiqueService.supprimerProduitDeLaBoutique(boutiqueId, produitId);
            return ResponseEntity.ok("Produit supprimé de la boutique avec succès.");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body("Erreur : " + ex.getMessage());
        }
    }


    
 // DTO simple pour recevoir la requête
    public static class AddProduitRequest {
        public Long produitId;
        public double quantity;

        // getters/setters si besoin
    }

    @PostMapping("/{boutiqueId}/produits")
    public ResponseEntity<?> ajouterProduitDansBoutique(
            @PathVariable Long boutiqueId,
            @RequestBody AddProduitRequest request) {

        Produit produit = produitService.getProductById(request.produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        try {
            Stock stock = boutiqueService.ajouterProduitDansBoutiqueAvecVerification(
                    boutiqueId, produit, request.quantity);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage()); 
        }
    }
}
