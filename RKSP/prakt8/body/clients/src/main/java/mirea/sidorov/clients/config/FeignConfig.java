package mirea.sidorov.clients.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    private final FeignClientInterceptor feignClientInterceptor;

    public FeignConfig(FeignClientInterceptor feignClientInterceptor) {
        this.feignClientInterceptor = feignClientInterceptor;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return feignClientInterceptor;
    }
}
