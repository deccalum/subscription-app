package se.lexicon.subscriptionapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Locale;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
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
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("subscription-api-public")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public GlobalOperationCustomizer messageSourceOperationCustomizer(MessageSource messageSource) {
        return (operation, handlerMethod) -> {
            operation.setSummary(resolveMessageKey(messageSource, operation.getSummary()));
            operation.setDescription(resolveMessageKey(messageSource, operation.getDescription()));
            return operation;
        };
    }

    private String resolveMessageKey(MessageSource messageSource, String value) {
        if (value == null || !value.startsWith("{") || !value.endsWith("}")) {
            return value;
        }

        String key = value.substring(1, value.length() - 1);
        try {
            return messageSource.getMessage(key, null, Locale.ENGLISH);
        } catch (NoSuchMessageException ignored) {
            return value;
        }
    }
}
