package com.GMPV.GMPV.Service;

import com.GMPV.GMPV.Entity.Produit;
import com.GMPV.GMPV.Entity.Stock;
import com.GMPV.GMPV.Entity.StockStatus;
import com.GMPV.GMPV.Repository.ProduitRepository;
import com.GMPV.GMPV.Repository.StockRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final ProduitRepository produitRepository;

    public StockService(StockRepository stockRepository, ProduitRepository produitRepository) {
        this.stockRepository = stockRepository;
        this.produitRepository = produitRepository;
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Optional<Stock> getStockById(Long id) {
        return stockRepository.findById(id);
    }

    public List<Stock> getStocksByBoutiqueId(Long boutiqueId) {
        return stockRepository.findByBoutiqueId(boutiqueId);
    }

    public List<Stock> getStocksByProduitId(Long produitId) {
        return stockRepository.findByProduitId(produitId);
    }

    public Stock createStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public Stock updateStock(Long id, Stock updatedStock) {
        return stockRepository.findById(id).map(stock -> {
            stock.setBoutique(updatedStock.getBoutique());
            stock.setProduit(updatedStock.getProduit());
            stock.setQuantity(updatedStock.getQuantity());
            return stockRepository.save(stock);
        }).orElseThrow(() -> new RuntimeException("Stock not found"));
    }

    public void deleteStock(Long id) {
        stockRepository.deleteById(id);
    }

    public void deleteByBoutiqueId(Long boutiqueId) {
        stockRepository.deleteByBoutiqueId(boutiqueId);
    }

    public void deleteByProduitId(Long produitId) {
        stockRepository.deleteByProduitId(produitId);
    }

    @Transactional
    public void validerStock(Long stockId) {
        Stock stock = stockRepository.findById(stockId)
            .orElseThrow(() -> new RuntimeException("Stock non trouvé"));

        if (stock.getStatus() == StockStatus.VALIDATED) {
            throw new RuntimeException("Stock déjà validé");
        }

        Produit produit = stock.getProduit();
        double newQuantityStock = produit.getQuantityStock() - stock.getQuantity();

        if (newQuantityStock < 0) {
            throw new RuntimeException("Stock général insuffisant pour valider cette opération");
        }

        Optional<Stock> existingStockOpt = stockRepository.findByProduitIdAndBoutiqueIdAndStatus(
            produit.getId(),
            stock.getBoutique().getId(),
            StockStatus.VALIDATED
        );

        if (existingStockOpt.isPresent()) {
            Stock existingStock = existingStockOpt.get();
            existingStock.setQuantity(existingStock.getQuantity() + stock.getQuantity());
            stockRepository.save(existingStock);
            stockRepository.delete(stock);
        } else {
            stock.setStatus(StockStatus.VALIDATED);
            stockRepository.save(stock);
        }

        produit.setQuantityStock(newQuantityStock);
        produitRepository.save(produit);
    }
}
