package com.nguyensao.ecommerce_layered_architecture.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nguyensao.ecommerce_layered_architecture.constant.OpenApiConstant;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

    private SecurityScheme createBearerScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }

    private Server createServer(String url, String description) {
        Server server = new Server();
        server.setUrl(url);
        server.setDescription(description);
        return server;
    }

    private Contact createContact() {
        return new Contact()
                .email(OpenApiConstant.CONTACT_EMAIL)
                .name(OpenApiConstant.CONTACT_NAME)
                .url(OpenApiConstant.CONTACT_URL);
    }

    private License createLicense() {
        return new License()
                .name(OpenApiConstant.LICENSE_NAME)
                .url(OpenApiConstant.LICENSE_URL);
    }

    private Info createApiInfo() {
        return new Info()
                .title(OpenApiConstant.API_TITLE)
                .version(OpenApiConstant.API_VERSION)
                .contact(createContact())
                .description(OpenApiConstant.API_DESCRIPTION)
                .termsOfService(OpenApiConstant.TERMS_OF_SERVICE_URL)
                .license(createLicense());
    }

    @Bean
    public OpenAPI myOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(List.of(
                        createServer(OpenApiConstant.SERVER_URL, OpenApiConstant.SERVER_DESCRIPTION)))
                .addSecurityItem(new SecurityRequirement().addList(OpenApiConstant.SECURITY_SCHEME_NAME))
                .components(new Components().addSecuritySchemes(OpenApiConstant.SECURITY_SCHEME_NAME,
                        createBearerScheme()));
    }
}
