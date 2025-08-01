package com.GMPV.GMPV.Controller;

import com.GMPV.GMPV.Entity.Stock;
import com.GMPV.GMPV.Service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = {
	    "http://localhost:3000",
	    "https://gmpv-frontend-nu.vercel.app"
	})
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable Long id) {
        return stockService.getStockById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/boutique/{boutiqueId}")
    public List<Stock> getStocksByBoutique(@PathVariable Long boutiqueId) {
        return stockService.getStocksByBoutiqueId(boutiqueId);
    }

    @GetMapping("/produit/{produitId}")
    public List<Stock> getStocksByProduit(@PathVariable Long produitId) {
        return stockService.getStocksByProduitId(produitId);
    }

    @PostMapping
    public Stock createStock(@RequestBody Stock stock) {
        return stockService.createStock(stock);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stock> updateStock(@PathVariable Long id, @RequestBody Stock stock) {
        try {
            Stock updatedStock = stockService.updateStock(id, stock);
            return ResponseEntity.ok(updatedStock);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{stockId}/valider")
    public ResponseEntity<?> validerStock(@PathVariable Long stockId) {
        try {
            stockService.validerStock(stockId);
            return ResponseEntity.ok("Stock validé avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
