package com.xdpsx.music.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Value("${app.url:}")
    private String appUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Music REST APIs")
                        .description("Documentation for Music REST APIs")
                        .version("1.0")
                        .license(new License().name("Licence name").url("https://some-url.com"))
                        .termsOfService("Terms of service")
                )
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("JWT"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER))
                );

        List<Server> servers = new ArrayList<>();

        if (!appUrl.isEmpty()) {
            servers.add(new Server().description("Prod ENV").url(appUrl + contextPath));
        }else {
            servers.add(new Server().description("Local ENV").url("http://localhost:8080" + contextPath));
        }

        return openAPI.servers(servers);
    }
}
