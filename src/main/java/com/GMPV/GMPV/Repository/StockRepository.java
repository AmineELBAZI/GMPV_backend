package com.GMPV.GMPV.Repository;

import com.GMPV.GMPV.Entity.Stock;
import com.GMPV.GMPV.Entity.StockStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findByBoutiqueId(Long boutiqueId);

    List<Stock> findByProduitId(Long produitId);

    Optional<Stock> findByBoutiqueIdAndProduitId(Long boutiqueId, Long produitId);

    void deleteByProduitId(Long produitId);

    void deleteByBoutiqueId(Long boutiqueId);

    Optional<Stock> findByProduitIdAndBoutiqueIdAndStatus(Long produitId, Long boutiqueId, StockStatus status);
}