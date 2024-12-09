package mirea.sidorov.payments.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import mirea.sidorov.payments.feign.AuthServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableFeignClients()
public class SecurityConfig {

    private final AuthServiceClient authServiceClient;

    public SecurityConfig(AuthServiceClient authServiceClient) {
        this.authServiceClient = authServiceClient;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationFilter authenticationFilter) throws Exception {
        http.csrf().disable().cors().disable()
                .addFilterBefore(authenticationFilter, BasicAuthenticationFilter.class)
                .authorizeRequests()
                .anyRequest().authenticated();

        return http.build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter(new ObjectMapper(), authServiceClient);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
