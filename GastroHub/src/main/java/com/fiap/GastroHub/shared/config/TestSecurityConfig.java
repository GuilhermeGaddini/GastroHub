//package com.fiap.GastroHub.shared.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@Profile("test") // Essa configuração será aplicada apenas no perfil de teste
//public class TestSecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable) // Desativa proteção CSRF
//                .authorizeHttpRequests(authorize -> authorize
//                        .anyRequest().permitAll() // Permite acesso a todos os endpoints
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Define sessões como stateless
//                );
//
//        // Removemos quaisquer filtros adicionais que possam verificar autorização, como authorizationFilter.
//
//        return http.build();
//    }
//
//}
//
