package com.kamehoot.kamehoot_backend.config;

import java.util.Arrays;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    @Primary
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization",
                "Content-Type", "Accept"));
        // configuration.setAllowedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainerCustomizer() {
        return factory -> {

            // Add the HTTP connector (your existing code)
            Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
            connector.setScheme("http");
            connector.setPort(8081);
            connector.setSecure(false);
            connector.setRedirectPort(8443);
            factory.addAdditionalTomcatConnectors(connector);

            // Add the missing security constraint
            // factory.addContextCustomizers(context -> {
            // SecurityConstraint securityConstraint = new SecurityConstraint();
            // securityConstraint.setUserConstraint("CONFIDENTIAL");
            // SecurityCollection collection = new SecurityCollection();
            // collection.addPattern("/*");
            // securityConstraint.addCollection(collection);
            // context.addConstraint(securityConstraint);
            // });
        };

    }
}
