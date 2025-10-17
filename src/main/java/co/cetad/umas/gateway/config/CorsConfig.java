package co.cetad.umas.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Permite todos los orígenes
        corsConfig.setAllowedOriginPatterns(Collections.singletonList("*"));

        // Permite todos los métodos
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Permite todos los headers
        corsConfig.setAllowedHeaders(Collections.singletonList("*"));

        // Headers expuestos
        corsConfig.setExposedHeaders(Arrays.asList(
                "Content-Type", "Authorization"
        ));

        // Si necesitas credenciales (cookies, auth headers)
        corsConfig.setAllowCredentials(true);

        // Tiempo de cache del preflight
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

}