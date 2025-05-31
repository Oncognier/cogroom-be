package oncog.cogroom.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT 인증 토큰 사용 (Bearer {Token})");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("JWT TOKEN");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("JWT TOKEN", securityScheme))
                .addSecurityItem(securityRequirement)
                .info(new Info()
                        .title("Cogroom API")
                        .version("1.0")
                        .description("온코그니어 코그룸 API 명세서입니다."));
    }
}
