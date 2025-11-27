package com.openclassrooms.safetynet.alert.configuration;

import com.openclassrooms.safetynet.alert.interceptor.LoggerInterceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Web configuration to register logging interceptors. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoggerInterceptor loggingInterceptor;

    public WebConfig(LoggerInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**") // All paths
                .excludePathPatterns("/actuator/**"); // Exclude actuator endpoints
    }
}
