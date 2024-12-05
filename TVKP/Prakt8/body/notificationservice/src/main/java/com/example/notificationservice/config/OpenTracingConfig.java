package com.example.notificationservice.config;

import io.jaegertracing.Configuration;
import io.opentracing.Tracer;
import io.opentracing.contrib.jdbc.TracingDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class OpenTracingConfig {

    @Bean
    public Tracer jaegerTracer(
            @Value("${opentracing.jaeger.service-name}") String serviceName,
            @Value("${opentracing.jaeger.http-sender.url}") String senderUrl,
            @Value("${opentracing.jaeger.sampler.type}") String samplerType,
            @Value("${opentracing.jaeger.sampler.param}") double samplerParam
    ) {
        return new Configuration(serviceName)
                .withSampler(
                        new Configuration.SamplerConfiguration()
                                .withType(samplerType)
                                .withParam(samplerParam)
                )
                .withReporter(
                        new Configuration.ReporterConfiguration()
                                .withLogSpans(true)
                                .withSender(
                                        new Configuration.SenderConfiguration()
                                                .withEndpoint(senderUrl)
                                )
                )
                .getTracer();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties, Tracer tracer) {
        DataSource originalDataSource = properties.initializeDataSourceBuilder()
                .build();

        return new TracingDataSource(tracer, originalDataSource);
    }
}

