package cn.sakuramiku.lightblog.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

/**
 * Swagger2配置类
 *
 * @author LYY
 */
@Configuration
@EnableSwagger2
@PropertySource("classpath:base-conf.properties")
public class Swagger2Config {

    private static final Logger logger = LoggerFactory.getLogger(Swagger2Config.class);

    @Value("${swagger.enable:true}")
    private boolean enable;
    @Value("${swagger.basePackage:}")
    private String basePackage;

    @Bean
    public Docket indexDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(enable)
                .groupName("Index")
                .securityContexts(Collections.singletonList(securityContexts()))
                .securitySchemes(Collections.singletonList(securitySchemes()))
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build();
    }

    private SecurityScheme securitySchemes() {
        return new ApiKey("Authorization", "Authorization", "header");
    }

    private SecurityContext securityContexts() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("xxx", "描述信息");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference("Authorization", authorizationScopes));
    }

}