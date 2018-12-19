package com.thingtrack.training.cf.services.config;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.google.common.collect.Lists;

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
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
@Profile({"dev"})
public class SwaggerConfig extends WebMvcConfigurationSupport {
	// Configure Swagger API services
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)        				 
		                 .select()
		                 	.apis(RequestHandlerSelectors.basePackage("com.thingtrack.training.cf.services.controller"))
		                 	.paths(PathSelectors.any())		                 		                
		                 	.build()		
		                 .directModelSubstitute(LocalDate.class, String.class)
		                 .genericModelSubstitutes(ResponseEntity.class)		                 	
		                 .apiInfo(metaData())	
		                 .securitySchemes(Lists.newArrayList(apiKey()))
		                 .securityContexts(Arrays.asList(securityContext()));
    }

    @Bean
    public SecurityConfiguration security() {
    	return SecurityConfigurationBuilder.builder()
    		.scopeSeparator(",")
    		.additionalQueryStringParams(null)
    		.useBasicAuthenticationWithAccessCodeGrant(false).build();
    }
    
    private ApiKey apiKey() {    	
        return new ApiKey("Bearer", "Authorization", "header");
    }
    
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        
        return Arrays.asList(new SecurityReference("Bearer", authorizationScopes));
    }
    
    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth())
        	.forPaths(PathSelectors.ant("/api/products/**")).build();
    }
    
    // Swagger API metadata information
    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("Training REST API")
                .description("\"Training REST API for Pivotal Developer Training\"")
                .version("1.0.0")
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0\"")
                .contact(new Contact("Thingtrack", "https://www.thingtrack.com", "miguel@thingtrack.com"))
                .build();
    }
        
    // Enable the swagger UI
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
             .addResourceLocations("classpath:/META-INF/resources/");
        
        registry.addResourceHandler("/webjars/**")
             .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }              
    
    // Unable the swagger validation to avoid errors in Pivotal Web services with validatorUrl("")
    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
	            .displayRequestDuration(true)
	            .validatorUrl("")
	            .build();
    }  
}
