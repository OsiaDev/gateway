package co.cetad.umas.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                // CORS preflight requests - DEBEN ser públicos (el navegador los envía sin auth)
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Endpoints públicos - health checks y actuator
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/fallback/**").permitAll()
                // Rutas de AD Connect requieren autenticación
                .pathMatchers("/adconnect/**").authenticated()
                // Todas las demás rutas de API requieren autenticación
                .pathMatchers("/api/**").authenticated()
                // Cualquier otra ruta - permitir (puede ser frontend estático)
                .anyExchange().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );
        
        return http.build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        // El converter por defecto extrae las authorities del claim "scope" o "scp"
        // Keycloak usa "realm_access.roles" - podemos personalizarlo si es necesario
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }
}
