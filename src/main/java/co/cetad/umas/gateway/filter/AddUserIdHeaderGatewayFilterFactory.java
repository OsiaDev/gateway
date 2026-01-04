package co.cetad.umas.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                        //System.out.println("=== CLAIMS DEL TOKEN ===");
                        //jwt.getClaims().forEach((key, value) -> System.out.println(key + " : " + value));

                        // Extraer el ID del usuario del claim "sub" (ajusta según necesites)
                        String userId = jwt.getClaimAsString("sub");

                        // Extraer roles de Keycloak
                        List<String> roles = extractRoles(jwt);
                        String rolesHeader = String.join(",", roles);

                        if (userId != null) {
                            // Agregar headers X-User-Id y X-User-Roles
                            var modifiedRequest = exchange.getRequest()
                                    .mutate()
                                    .header("X-User-Id", userId)
                                    .header("X-User-Roles", rolesHeader)
                                    .build();

                            System.out.println("✅ Usuario extraído del token: " + userId);
                            System.out.println("✅ Roles extraídos: " + roles);

                            return chain.filter(exchange.mutate().request(modifiedRequest).build());
                        } else {
                            System.out.println("⚠️ No se pudo extraer userId del token");
                        }
                    }

                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    /**
     * Extrae los roles del JWT de Keycloak
     *
     * Keycloak estructura: resource_access.{client-id}.roles
     * Ejemplo: resource_access.commander.roles = ["commander"]
     */
    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Jwt jwt) {
        try {
            // Obtener el objeto resource_access
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

            if (resourceAccess == null || resourceAccess.isEmpty()) {
                System.out.println("⚠️ No se encontró resource_access en el token");
                return List.of();
            }

            // Iterar sobre todos los clientes y recopilar sus roles
            return resourceAccess.entrySet().stream()
                    .filter(entry -> entry.getValue() instanceof Map)
                    .flatMap(entry -> {
                        Map<String, Object> client = (Map<String, Object>) entry.getValue();
                        Object rolesObj = client.get("roles");

                        if (rolesObj instanceof List) {
                            return ((List<?>) rolesObj).stream()
                                    .filter(role -> role instanceof String)
                                    .map(Object::toString);
                        }
                        return java.util.stream.Stream.empty();
                    })
                    .distinct()
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("❌ Error extrayendo roles: " + e.getMessage());
            return List.of();
        }
    }

    // Clase de configuración (puede estar vacía)
    public static class Config {
        // Propiedades de configuración si las necesitas
    }

}