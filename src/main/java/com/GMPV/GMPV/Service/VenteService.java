package com.GMPV.GMPV.Service;

import com.GMPV.GMPV.Entity.Produit;
import com.GMPV.GMPV.Entity.Stock;
import com.GMPV.GMPV.Entity.Vente;
import com.GMPV.GMPV.Repository.StockRepository;
import com.GMPV.GMPV.Repository.VenteRepository;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.GMPV.GMPV.DTO.VenteProduitFiniRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

@Service
public class VenteService {

    private final VenteRepository venteRepository;
    private final StockRepository stockRepository;
    private final StockService stockService;  // Add this

    private static final Logger logger = LoggerFactory.getLogger(VenteService.class);

    // Constructor injection updated
    public VenteService(VenteRepository venteRepository, StockRepository stockRepository, StockService stockService) {
        this.venteRepository = venteRepository;
        this.stockRepository = stockRepository;
        this.stockService = stockService;
    }

    public Vente enregistrerVente(Vente vente) {
        return venteRepository.save(vente);
    }
    
    public void deleteVente(Long id) {
        venteRepository.deleteById(id);
    }

    public void deleteVentesByIds(List<Long> ids) {
        for (Long id : ids) {
            deleteVente(id);
        }
    }
    
    
    public void deleteVenteWithStockRestore(Long id) {
        Vente vente = venteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Vente introuvable avec ID : " + id));

        Long boutiqueId = vente.getBoutique().getId();
        List<Produit> produitsVendus = vente.getProduits();
        double quantiteVendue = vente.getQuantity();

        for (Produit produit : produitsVendus) {
            Long produitId = produit.getId();

            Stock stock = stockRepository.findByBoutiqueIdAndProduitId(boutiqueId, produitId)
                .orElseThrow(() -> new RuntimeException("Stock introuvable pour le produit ID : " + produitId));

            stock.setQuantity(stock.getQuantity() + quantiteVendue);
            stockService.saveStock(stock);
        }

        venteRepository.deleteById(id);
    }

    


    public List<Vente> enregistrerVentesMultiples(Vente venteRequest) {
        List<Vente> ventesEnregistrees = new ArrayList<>();

        Long boutiqueId = venteRequest.getBoutique().getId();
        if (boutiqueId == null) {
            throw new RuntimeException("ID de la boutique manquant.");
        }

        for (Produit produitVendu : venteRequest.getProduits()) {
            Long produitId = produitVendu.getId();
            double quantiteVendue = produitVendu.getQuantityStock();
            double priceSell = produitVendu.getPrice_sell();

            Stock stock = stockRepository.findByBoutiqueIdAndProduitId(boutiqueId, produitId)
                    .orElseThrow(() -> new RuntimeException("Stock introuvable pour produit ID: " + produitId));

            double stockDisponible = stock.getQuantity();
            if (quantiteVendue > stockDisponible) {
                throw new RuntimeException("Stock insuffisant pour le produit '" + produitVendu.getName() + "'.");
            }

            stock.setQuantity(stockDisponible - quantiteVendue);

            // IMPORTANT: use stockService to save with notification check
            stockService.saveStock(stock);

            Vente vente = new Vente();
            vente.setBoutique(venteRequest.getBoutique());
            vente.setProduits(List.of(produitVendu)); // 1 produit par vente
            vente.setQuantity(quantiteVendue);
            vente.setMontantTotal(quantiteVendue * priceSell);
            vente.setDateVente(LocalDateTime.now());

            Vente saved = venteRepository.save(vente);
            ventesEnregistrees.add(saved);
        }

        return ventesEnregistrees;
    }

    public Vente vendreProduitFini(VenteProduitFiniRequest request) {
        Long boutiqueId = request.getBoutiqueId();
        Long bouteilleId = request.getBouteilleId();
        Long huileId = request.getHuileId();
        Long alcoolId = request.getAlcoolId();
        String taille = request.getTaille(); // NEW PARAMETER from frontend

        Stock stockBouteille = stockRepository.findByBoutiqueIdAndProduitId(boutiqueId, bouteilleId)
            .orElseThrow(() -> new RuntimeException("Stock de bouteille introuvable"));

        Stock stockHuile = stockRepository.findByBoutiqueIdAndProduitId(boutiqueId, huileId)
            .orElseThrow(() -> new RuntimeException("Stock d'huile introuvable"));

        Stock stockAlcool = stockRepository.findByBoutiqueIdAndProduitId(boutiqueId, alcoolId)
            .orElseThrow(() -> new RuntimeException("Stock d'alcool introuvable"));

        double huileNeeded, alcoolNeeded;

        switch (taille) {
            case "20":
                huileNeeded = 7;
                alcoolNeeded = 13;
                break;
            case "30":
                huileNeeded = 10;
                alcoolNeeded = 20;
                break;
            case "50":
                huileNeeded = 18;
                alcoolNeeded = 32;
                break;
            default:
                throw new RuntimeException("Taille de bouteille non reconnue : " + taille);
        }

        if (stockBouteille.getQuantity() < 1) {
            throw new RuntimeException("Stock de bouteille insuffisant");
        }
        if (stockHuile.getQuantity() < huileNeeded) {
            throw new RuntimeException("Stock d'huile insuffisant");
        }
        if (stockAlcool.getQuantity() < alcoolNeeded) {
            throw new RuntimeException("Stock d'alcool insuffisant");
        }

        stockBouteille.setQuantity(stockBouteille.getQuantity() - 1);
        stockHuile.setQuantity(stockHuile.getQuantity() - huileNeeded);
        stockAlcool.setQuantity(stockAlcool.getQuantity() - alcoolNeeded);

        stockService.saveStock(stockBouteille);
        stockService.saveStock(stockHuile);
        stockService.saveStock(stockAlcool);

        Vente vente = new Vente();
        vente.setDateVente(LocalDateTime.now());
        vente.setQuantity(1);
        vente.setBoutique(stockBouteille.getBoutique());
        vente.setMontantTotal(request.getMontantTotal());
        vente.setProduits(List.of(
            stockBouteille.getProduit(),
            stockHuile.getProduit(),
            stockAlcool.getProduit()
        ));

        return venteRepository.save(vente);
    }

    
    public List<Vente> getAllVentes() {
        return venteRepository.findAll();
    }

    public Vente getVenteById(Long id) {
        return venteRepository.findById(id).orElse(null);
    }

    public List<Vente> getVentesByBoutique(Long boutiqueId) {
        return venteRepository.findByBoutiqueId(boutiqueId);
    }

	public static Logger getLogger() {
		return logger;
	}
	
}
