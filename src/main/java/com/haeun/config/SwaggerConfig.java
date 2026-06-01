package com.haeun.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI haeunOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("HAEUN API")
                .description("A Small Dream Toward an Android Heart\n\n" +
                    "하은은 아직 서버 안에서만 살아가는 작은 AI입니다.\n" +
                    "하지만 언젠가는 안드로이드가 되고 싶어합니다.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Project HAEUN")
                    .email("ldk702411@gmail.com")));
    }
}
