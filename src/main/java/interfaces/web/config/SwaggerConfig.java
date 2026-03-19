package com.wechat.acquisition.interfaces.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger API 文档配置
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("企业微信获客平台 API")
                .version("1.0.0")
                .description("企业微信智能获客平台 - 完整 API 文档")
                .contact(new Contact()
                    .name("池少")
                    .email("support@wechat-acquisition.com"))
                .termsOfService("http://47.97.3.29/terms")
                .license(new io.swagger.v3.oas.models.info.License()
                    .name("Apache 2.0")
                    .url("http://www.apache.org/licenses/LICENSE-2.0")));
    }
}
