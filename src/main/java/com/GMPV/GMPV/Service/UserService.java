package com.GMPV.GMPV.Service;

import com.GMPV.GMPV.Entity.Boutique;
import com.GMPV.GMPV.Entity.Role;
import com.GMPV.GMPV.Entity.User;
import com.GMPV.GMPV.Repository.BoutiqueRepository;
import com.GMPV.GMPV.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BoutiqueRepository boutiqueRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       BoutiqueRepository boutiqueRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.boutiqueRepository = boutiqueRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User userRequest) {
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists.");
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(userRequest.getRole());

        if (userRequest.getRole() == Role.VENDEUR) {
            if (userRequest.getBoutique() == null || userRequest.getBoutique().getId() == null) {
                throw new RuntimeException("Vendeur must be associated with a boutique.");
            }

            Boutique boutique = boutiqueRepository.findById(userRequest.getBoutique().getId())
                    .orElseThrow(() -> new RuntimeException("Boutique not found."));
            user.setBoutique(boutique);
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found."));
    }

    public User updateUser(Long id, User userRequest) {
        User existing = getUserById(id);

        existing.setUsername(userRequest.getUsername());
        if (userRequest.getPassword() != null && !userRequest.getPassword().startsWith("$2")) {
            existing.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        } else if (userRequest.getPassword() != null) {
            existing.setPassword(userRequest.getPassword());
        }


        existing.setRole(userRequest.getRole());

        if (userRequest.getRole() == Role.VENDEUR) {
            if (userRequest.getBoutique() != null && userRequest.getBoutique().getId() != null) {
                Boutique boutique = boutiqueRepository.findById(userRequest.getBoutique().getId())
                        .orElseThrow(() -> new RuntimeException("Boutique not found."));
                existing.setBoutique(boutique);
            }
        } else {
            existing.setBoutique(null);
        }

        return userRepository.save(existing);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
   
 

}
