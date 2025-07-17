package com.GMPV.GMPV.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GMPV.GMPV.Entity.Boutique;

@Repository
public interface BoutiqueRepository extends JpaRepository<Boutique, Long> {
}