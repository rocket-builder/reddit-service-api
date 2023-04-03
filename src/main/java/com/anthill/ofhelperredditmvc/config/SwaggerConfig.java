package com.anthill.ofhelperredditmvc.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    public static final String SCHEME_NAME = "BearerScheme";
    public static final String SCHEME = "Bearer";

    @Bean
    public OpenAPI customOpenAPI() {
        var openApi = new OpenAPI().info(this.apiInfo());
        this.addSecurity(openApi);
        return openApi;
    }

    private Info apiInfo() {
        return new Info()
                .title("OFHelper Reddit API")
                .description("REST API for ofhelper reddit")
                .version("0.5.1");
    }

    private void addSecurity(OpenAPI openApi) {
        var components = this.createComponents();
        var securityItem = new SecurityRequirement().addList(SCHEME_NAME);
        openApi.components(components).addSecurityItem(securityItem);
    }

    private Components createComponents() {
        var components = new Components();

        components.addSecuritySchemes(
                SCHEME_NAME,
                new SecurityScheme()
                        .name(SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme(SCHEME));
        return components;
    }
}