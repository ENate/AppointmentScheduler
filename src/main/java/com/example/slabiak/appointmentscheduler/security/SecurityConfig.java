package com.example.slabiak.appointmentscheduler.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
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
            .authorizeHttpRequests(
                authorize -> authorize
                .requestMatchers("/actuator/").permitAll()
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

}
