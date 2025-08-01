package com.GMPV.GMPV.Security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService uds) {
        this.userDetailsService = uds;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors() // ✅ Add this line to enable CORS
            .and()
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/logout").permitAll()
                .requestMatchers("/api/boutiques/*").hasAnyRole("VENDEUR", "ADMIN")
                .requestMatchers("/api/boutiques/*/produits").hasAnyRole("VENDEUR", "ADMIN")
                .requestMatchers("/api/stocks/*/valider").hasAnyRole("VENDEUR", "ADMIN")
                .requestMatchers("/api/ventes/multiple").hasAnyRole("VENDEUR", "ADMIN")
                .requestMatchers("/api/ventes/**").hasAnyRole("VENDEUR", "ADMIN")
                .requestMatchers("/api/ventes/produit-fini").hasAnyRole("VENDEUR", "ADMIN")
                .requestMatchers("/api/ventes/boutique/**").hasAnyRole("VENDEUR", "ADMIN")
                .requestMatchers("/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(200);
                    response.getWriter().write("Logout successful");
                })
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }


 
}
