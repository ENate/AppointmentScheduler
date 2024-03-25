package com.example.slabiak.appointmentscheduler.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration(proxyBeanMethods = true) // consider checking
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final PasswordEncoder passwordEncoder;


    public SecurityConfig(
        CustomUserDetailsService customUserDetailsService,
        CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler,
        PasswordEncoder passwordEncoder
    ) {
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(
                authorize -> authorize
                .requestMatchers("/actuator/").permitAll()
                // .requestMatchers("/").permitAll()
                .requestMatchers("/").hasAnyRole("CUSTOMER", "PROVIDER", "ADMIN")
                .requestMatchers("/api/**").hasAnyRole("CUSTOMER", "PROVIDER", "ADMIN")
                .requestMatchers("/customers/all").hasRole("ADMIN")
                .requestMatchers("/providers/new").hasRole("ADMIN")
                .requestMatchers("/invoices/all").hasRole("ADMIN")
                .requestMatchers("/providers/all").hasRole("ADMIN")
                .requestMatchers("/customers/**").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers("/providers/availability/**").hasRole("PROVIDER")
                .requestMatchers("/providers/**").hasAnyRole("PROVIDER", "ADMIN")
                .requestMatchers("/works/**").hasRole("ADMIN")
                .requestMatchers("/exchange/**").hasRole("CUSTOMER")
                .requestMatchers("/appointments/new/**").hasRole("CUSTOMER")
                .requestMatchers("/appointments/**").hasAnyRole("CUSTOMER", "PROVIDER", "ADMIN")
                .requestMatchers("/invoices/**").hasAnyRole("CUSTOMER", "PROVIDER", "ADMIN")
                .anyRequest().authenticated())
                .formLogin(form -> form
                    .loginPage("/login")
                    .loginProcessingUrl("perform_login")
                    .successHandler(customAuthenticationSuccessHandler).permitAll())
                .logout(logout -> logout
                    .logoutUrl("/perform_logout"))
                .exceptionHandling(ex -> ex 
                    .accessDeniedPage("/access-denied"));
        return http.build();
    }



    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(customUserDetailsService);
        auth.setPasswordEncoder(passwordEncoder);
        return auth;
    }

    // WARNING: If you are configuring WebSecurity to ignore requests, 
    // consider using permitAll

    @Bean
    public WebSecurityCustomizer  webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/customers/new/**");
    }

}
