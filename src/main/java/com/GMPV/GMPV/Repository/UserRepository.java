package com.GMPV.GMPV.Repository;

import com.GMPV.GMPV.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByBoutiqueId(Long boutiqueId);
}
