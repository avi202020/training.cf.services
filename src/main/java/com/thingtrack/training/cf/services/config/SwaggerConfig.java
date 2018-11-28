package com.thingtrack.training.cf.services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.google.common.collect.Lists;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;

import java.util.List;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
@Profile({"dev"})
public class SwaggerConfig extends WebMvcConfigurationSupport {
	/* Configure Swagger API services */
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)        				 
		                 .select()
		                 	//.apis(RequestHandlerSelectors.any())
		                 	.apis(RequestHandlerSelectors.basePackage("com.thingtrack.training.cf.services.controller"))
		                 	.paths(PathSelectors.any())		                 		                
		                 	.build()
		                 .securitySchemes(Lists.newArrayList(apiKey()))
		                 .securityContexts(Lists.newArrayList(securityContext()))		                 	
		                 .apiInfo(metaData());
    }
    
    private ApiKey apiKey() {
        return new ApiKey("mykey", "api_key", "header");
    }
    
    private SecurityContext securityContext() {
        return SecurityContext.builder()
            .securityReferences(defaultAuth())
            .forPaths(PathSelectors.any())
            .build();
    }
    
    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        
        return Lists.newArrayList(new SecurityReference("mykey", authorizationScopes));
    }
    
    @Bean
    SecurityConfiguration security() {
      return SecurityConfigurationBuilder.builder()
          .clientId("training-cf-services-client-id")
          .clientSecret("training-cf-services-client-secret")
          .realm("training-cf-services-realm")
          .appName("training-cf-services")
          .scopeSeparator(",")
          .additionalQueryStringParams(null)
          .useBasicAuthenticationWithAccessCodeGrant(false)
          .build();
    }
    
    /* Swagger API metadata information*/
    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("Spring Boot REST API")
                .description("\"Spring Boot REST API for Pivotal Developer Training\"")
                .version("1.0.0")
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0\"")
                .contact(new Contact("Thingtrack", "https://www.thingtrack.com", "miguel@thingtrack.com"))
                .build();
    }
        
    /* Enable the swagger UI */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
 
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
    
    /* Unable the swagger validation to avoid errors in Pivotal Web services with validatorUrl("") */
    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
	            .displayRequestDuration(true)
	            .validatorUrl("")
	            .build();
    }
    
    /* Enable CORS */
    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
        		.allowedMethods("*")
                //.allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
                //.allowedOrigins("http://localhost:4200")
                .allowCredentials(true);
    }
}
