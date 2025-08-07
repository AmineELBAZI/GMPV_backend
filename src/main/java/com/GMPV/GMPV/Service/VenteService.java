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

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;

import javax.print.Doc;
import javax.print.*;

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
            stockService.saveStock(stock);

            Vente vente = new Vente();
            vente.setBoutique(venteRequest.getBoutique());
            vente.setProduits(List.of(produitVendu));
            vente.setQuantity(quantiteVendue);
            vente.setMontantTotal(quantiteVendue * priceSell);
            vente.setDateVente(LocalDateTime.now());

            Vente saved = venteRepository.save(vente);
            ventesEnregistrees.add(saved);
        }

        // 🔽 Generate and print receipt here
        String receipt = generateReceiptText(ventesEnregistrees);
        printReceipt(receipt); // Add this method as explained earlier

        return ventesEnregistrees;
    }


    public Vente vendreProduitFini(VenteProduitFiniRequest request) {
        Long boutiqueId = request.getBoutiqueId();
        Long bouteilleId = request.getBouteilleId();
        Long huileId = request.getHuileId();
        Long alcoolId = request.getAlcoolId();
        String taille = request.getTaille();

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

        Vente savedVente = venteRepository.save(vente);

        // ✅ Print receipt
        String receiptText = generateProduitFiniReceipt(savedVente, taille, huileNeeded, alcoolNeeded);
        printReceipt(receiptText);

        return savedVente;
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
	
	
	
	private String generateReceiptText(List<Vente> ventes) {
	    StringBuilder sb = new StringBuilder();
	    
	    sb.append(" ***** Future Fragrance  Recu De Vente *****\n");
	    sb.append("     ***********************************\n");
	    sb.append("\n");

	    // Format date
	    String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
	    sb.append("Date : ").append(dateStr).append("\n");
	    sb.append("------------------------------------------------\n");
	    sb.append("\n");
	    sb.append(String.format("%-12s %8s %12s\n", "Produit", "Qte", "Total"));
	    sb.append("------------------------------------------------\n");
	    double total = 0;

	    for (Vente vente : ventes) {
	        Produit produit = vente.getProduits().get(0);
	        String nomProduit = produit.getName();
	        int quantite = (int) vente.getQuantity();
	        double montant = vente.getMontantTotal();

	        sb.append(String.format("%-12s %8d %13.2f\n", truncate(nomProduit, 12), quantite, montant));
	        total += montant;
	    }

	    sb.append("------------------------------------------------\n");
	    sb.append(String.format("%-16s %15.2f DH\n", "TOTAL:", total));
	    sb.append("------------------------------------------------\n");
	    sb.append("          Merci pour votre achat !\n");
	    sb.append("                 A bientot !\n");
	  
	   
	    return sb.toString();
	}

	// Truncate product names if too long
	private String truncate(String input, int maxLength) {
	    return input.length() <= maxLength ? input : input.substring(0, maxLength - 1) + "…";
	}
	private String generateProduitFiniReceipt(Vente vente, String taille, double huileUsed, double alcoolUsed) {
	    StringBuilder sb = new StringBuilder();
	    sb.append(" ***** Future Fragrance  Recu De Vente *****\n");
	    sb.append("     ***********************************\n");
	    sb.append("\n");
	    sb.append("Date : ").append(
	        vente.getDateVente().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
	    ).append("\n");
	    
	    sb.append("------------------------------------------------\n");
	    sb.append(String.format("%-12s \n", "Ingredient"));

	    for (Produit p : vente.getProduits()) {
	        String name = p.getName().toLowerCase();

	        // Afficher seulement les produits contenant "huile" ou "bouteille"
	        if (name.contains("huile") || name.contains("bouteille")) {
	            sb.append(String.format(" %6s\n", p.getName()));
	        }
	    }
	    sb.append("Taille : ").append(taille).append(" ml\n");
	    sb.append("------------------------------------------------\n");
	    sb.append(String.format("TOTAL : %.2f DH\n", vente.getMontantTotal()));
	    sb.append("------------------------------------------------\n");
	    sb.append("          Merci pour votre achat !\n");
	    sb.append("              A bientot !\n\n");

	    return sb.toString();
	}


	private void printReceipt(String receiptText) {
	    try {
	        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();

	        if (printService == null) {
	            System.out.println("No printer found.");
	            return;
	        }

	        // Convert receipt text to bytes (UTF-8 or printer encoding)
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        outputStream.write(receiptText.getBytes("UTF-8"));

	        // Add the ESC/POS cut command at the end
	        byte[] cutCommand = new byte[]{0x1D, 'V', 66, 0}; // GS V 66 0
	        outputStream.write(cutCommand);

	        DocPrintJob job = printService.createPrintJob();
	        Doc doc = new SimpleDoc(outputStream.toByteArray(), DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
	        job.print(doc, null);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	
}
