package com.thingtrack.training.cf.services.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.thingtrack.training.cf.services.security.jwt.JwtAuthEntryPoint;
import com.thingtrack.training.cf.services.security.jwt.JwtAuthTokenFilter;
import com.thingtrack.training.cf.services.security.services.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler;
    
	private static final String[] AUTH_WHITELIST = {
            // -- swagger ui
			"/v2/api-docs", 
			"/swagger-resources/configuration/ui", 
			"/swagger-resources", 
			"/swagger-resources/configuration/security", 
			"/swagger-ui.html", 
			"/webjars/**"
            // other public endpoints of your API may be appended to this array
    };
		
    @Bean
    public JwtAuthTokenFilter authenticationJwtTokenFilter() {
        return new JwtAuthTokenFilter();
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
    
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.cors()
				.and()
			.csrf()
				.disable()
			.authorizeRequests()
				.antMatchers("/api/auth/**").permitAll()
				.antMatchers(AUTH_WHITELIST).permitAll() 
			.anyRequest()
				.authenticated()
				.and()
			.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
		    // this disables session creation on Spring Security
	        .sessionManagement()
	         	.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);		
	}    
}
