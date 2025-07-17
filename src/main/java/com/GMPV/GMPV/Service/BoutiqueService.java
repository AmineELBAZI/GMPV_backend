package com.GMPV.GMPV.Service;

import com.GMPV.GMPV.Entity.Boutique;
import com.GMPV.GMPV.Entity.Produit;
import com.GMPV.GMPV.Entity.Stock;
import com.GMPV.GMPV.Entity.StockStatus;
import com.GMPV.GMPV.Entity.Vente;
import com.GMPV.GMPV.Repository.BoutiqueRepository;
import com.GMPV.GMPV.Repository.ProduitRepository;
import com.GMPV.GMPV.Repository.StockRepository;
import com.GMPV.GMPV.Repository.VenteRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoutiqueService {

	  private final BoutiqueRepository boutiqueRepository;
	    private final StockRepository stockRepository;
	    private final ProduitRepository produitRepository;  
	    private final VenteRepository venteRepository;  
	    
	    

	    public BoutiqueService(BoutiqueRepository boutiqueRepository, StockRepository stockRepository, ProduitService produitService ,ProduitRepository produitRepository , VenteRepository venteRepository) {
	        this.boutiqueRepository = boutiqueRepository;
	        this.stockRepository = stockRepository;
	        this.produitRepository = produitRepository;
	        this.venteRepository = venteRepository;
	    }

    public List<Boutique> getAllBoutiques() {
        return boutiqueRepository.findAll();
    }

    public Optional<Boutique> getById(Long id) {
        return boutiqueRepository.findById(id);
    }

    public Boutique createBoutique(Boutique boutique) {
        return boutiqueRepository.save(boutique);
    }

    public Boutique updateBoutique(Long id, Boutique updatedBoutique) {
        return boutiqueRepository.findById(id).map(b -> {
            b.setNom(updatedBoutique.getNom());
            b.setAdresse(updatedBoutique.getAdresse());
            return boutiqueRepository.save(b);
        }).orElse(null);
    }
    
    @Transactional
    public void deleteBoutique(Long id) {
        // Vérifier que la boutique existe
        Boutique boutique = boutiqueRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Boutique non trouvée"));

        // 1) Rendre boutique=null dans les ventes liées
        List<Vente> ventes = venteRepository.findByBoutiqueId(id);
        for (Vente vente : ventes) {
            vente.setBoutique(null);
            venteRepository.save(vente);
        }

        // 2) Remettre la quantité de stock des produits
        List<Stock> stocks = stockRepository.findByBoutiqueId(id);
        for (Stock stock : stocks) {
            Produit produit = stock.getProduit();
            double quantityToAdd = stock.getQuantity();

            produit.setQuantityStock(produit.getQuantityStock() + quantityToAdd);
            produitRepository.save(produit);
        }

        // 3) Supprimer les stocks liés à la boutique
        stockRepository.deleteByBoutiqueId(id);

        // 4) Supprimer la boutique
        boutiqueRepository.deleteById(id);
    }



    // ✅ Obtenir tous les produits d'une boutique
    public List<Produit> getProduitsByBoutiqueId(Long boutiqueId) {
        return stockRepository.findByBoutiqueId(boutiqueId).stream()
                .map(Stock::getProduit)
                .distinct()
                .collect(Collectors.toList());
    }

    // ✅ Ajouter un produit à une boutique (avec quantité)
    public Stock ajouterProduitDansBoutique(Long boutiqueId, Produit produit, double quantity) {
        Boutique boutique = boutiqueRepository.findById(boutiqueId)
                .orElseThrow(() -> new RuntimeException("Boutique non trouvée"));

        Optional<Stock> existingStock = stockRepository.findByBoutiqueIdAndProduitId(boutiqueId, produit.getId());
        if (existingStock.isPresent()) {
            Stock stock = existingStock.get();
            stock.setQuantity(stock.getQuantity() + quantity);
            return stockRepository.save(stock);
        }

        Stock stock = new Stock();
        stock.setProduit(produit);
        stock.setBoutique(boutique);
        stock.setQuantity(quantity);
        return stockRepository.save(stock);
    }

    // ✅ Supprimer liste de  produit d'une boutique (supprime juste le stock)
    public void supprimerTousLesProduitsDeLaBoutique(Long boutiqueId) {
        List<Stock> stocks = stockRepository.findByBoutiqueId(boutiqueId);

        for (Stock stock : stocks) {
            Produit produit = stock.getProduit();
            double quantityToReturn = stock.getQuantity();

            // Ajouter la quantité au stock général du produit
            produit.setQuantityStock(produit.getQuantityStock() + quantityToReturn);
            produitRepository.save(produit);

            // Supprimer le stock
            stockRepository.delete(stock);
        }
    }
    
    // ✅ Supprimer un produit d'une boutique (supprime juste le stock)
    public void supprimerProduitDeLaBoutique(Long boutiqueId, Long produitId) {
        Boutique boutique = boutiqueRepository.findById(boutiqueId)
                .orElseThrow(() -> new RuntimeException("Boutique non trouvée"));

        Stock stock = stockRepository.findByBoutiqueIdAndProduitId(boutiqueId, produitId)
                .orElseThrow(() -> new RuntimeException("Stock non trouvé pour ce produit dans la boutique"));

        Produit produit = stock.getProduit();
        produit.setQuantityStock(produit.getQuantityStock() + stock.getQuantity());

        stockRepository.delete(stock);
        produitRepository.save(produit);
    }


    
    // ... ensuite dans ta méthode ajouterProduitDansBoutiqueAvecVerification
    public Stock ajouterProduitDansBoutiqueAvecVerification(Long boutiqueId, Produit produit, double quantity) {
        Boutique boutique = boutiqueRepository.findById(boutiqueId)
                .orElseThrow(() -> new RuntimeException("Boutique non trouvée"));

        // Check if existing Stock for this boutique and produit
        Optional<Stock> existingStock = stockRepository.findByBoutiqueIdAndProduitId(boutiqueId, produit.getId());
        if (existingStock.isPresent()) {
            // Here instead of updating quantity and product stock, create a NEW Stock entry with status NOT_VALIDATED
            Stock newStock = new Stock();
            newStock.setProduit(produit);
            newStock.setBoutique(boutique);
            newStock.setQuantity(quantity);
            newStock.setStatus(StockStatus.NOT_VALIDATED);
            return stockRepository.save(newStock);
        }

        // No existing stock - create a new stock entry with NOT_VALIDATED status
        Stock stock = new Stock();
        stock.setProduit(produit);
        stock.setBoutique(boutique);
        stock.setQuantity(quantity);
        stock.setStatus(StockStatus.NOT_VALIDATED);
        return stockRepository.save(stock);
    }


}
