package com.example.Biluthyrningssystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/**").hasRole("CUSTOMER")
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User
                .withUsername("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .build();

        UserDetails annasvensson = User
                .withUsername("19850101-1234")
                .password("{noop}1234")
                .roles("CUSTOMER")
                .build();

        UserDetails erikjohansson = User
                .withUsername("19900215-5678")
                .password("{noop}5678")
                .roles("CUSTOMER")
                .build();

        UserDetails marialindberg = User
                .withUsername("19751230-9101")
                .password("{noop}9101")
                .roles("CUSTOMER")
                .build();

        UserDetails johankarlsson = User
                .withUsername("19881122-3456")
                .password("{noop}3456")
                .roles("CUSTOMER")
                .build();

        UserDetails elinandersson = User
                .withUsername("19950505-7890")
                .password("{noop}7890")
                .roles("CUSTOMER")
                .build();

        return new InMemoryUserDetailsManager(admin, annasvensson, erikjohansson, marialindberg, johankarlsson, elinandersson);
    }



}
