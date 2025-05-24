package com.kamehoot.kamehoot_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.kamehoot.kamehoot_backend.security.JwtAuthenticationFilter;
import com.kamehoot.kamehoot_backend.security.JwtService;
import com.kamehoot.kamehoot_backend.services.CustomUserDetailsService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableWebSecurity
public class JWTConfig {

        private final RsaKeyProperties rsaKeys;

        public JWTConfig(RsaKeyProperties rsaKeys) {
                this.rsaKeys = rsaKeys;

        }

        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService,
                        CustomUserDetailsService userDetailsService) {
                return new JwtAuthenticationFilter(jwtService, userDetailsService);
        }

        @Bean
        public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {
                var authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder);
                return new ProviderManager(authProvider);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                        JwtAuthenticationFilter jwtAuthenticationFilter, SecurityExceptionHandler authEntryPoint)
                        throws Exception {

                return http
                                .csrf(csrf -> csrf.disable())
                                .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))
                                .authorizeHttpRequests(auth -> auth

                                                .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")

                                                .requestMatchers("/auth/**").permitAll()

                                                .requestMatchers(HttpMethod.GET, "/categories", "/questions")
                                                .permitAll()

                                                .anyRequest().authenticated())

                                .addFilterBefore(jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)
                                // .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                                // jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                                .sessionManagement(seasion -> seasion
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .build();
        }

        @Bean
        JwtDecoder JwtDecoder() {
                return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
        }

        @Bean
        JwtEncoder jwtEncoder() {

                JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
                JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
                return new NimbusJwtEncoder(jwkSource);
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
                authoritiesConverter.setAuthorityPrefix("ROLE_");
                authoritiesConverter.setAuthoritiesClaimName("roles");

                JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
                converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
                return converter;
        }
}
