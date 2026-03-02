package se.lexicon.subscriptionapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Subscription API")
                        .version("1.0.0")
                        .description("""
                                This API provides endpoints for managing subscriptions.

                                Authorization:
                                - Use the Authorize button and paste your token as: Bearer <JWT>
                                - Role rules are documented per endpoint (see each endpoint description).
                                """)
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                // Make security available globally in Swagger UI (Authorize button)
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Authorization header. Example: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                        ));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("subscription-api-public")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}