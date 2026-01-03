package co.cetad.umas.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AddUserIdHeaderGatewayFilterFactory
        extends AbstractGatewayFilterFactory<AddUserIdHeaderGatewayFilterFactory.Config> {

    public AddUserIdHeaderGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    if (securityContext.getAuthentication() instanceof JwtAuthenticationToken jwtAuth) {
                        Jwt jwt = jwtAuth.getToken();

                        // Imprimir TODOS los claims para debug
                        System.out.println("=== CLAIMS DEL TOKEN ===");
                        jwt.getClaims().forEach((key, value) -> System.out.println(key + " : " + value));

                        // Extraer el ID del usuario del claim "sub" (ajusta según necesites)
                        String userId = jwt.getClaimAsString("sub");

                        if (userId != null) {
                            // Agregar header X-User-Id
                            var modifiedRequest = exchange.getRequest()
                                    .mutate()
                                    .header("X-User-Id", userId)
                                    .build();

                            System.out.println("✅ Usuario extraído del token: " + userId);

                            return chain.filter(exchange.mutate().request(modifiedRequest).build());
                        } else {
                            System.out.println("⚠️ No se pudo extraer userId del token");
                        }
                    }

                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    // Clase de configuración (puede estar vacía)
    public static class Config {
        // Propiedades de configuración si las necesitas
    }

}