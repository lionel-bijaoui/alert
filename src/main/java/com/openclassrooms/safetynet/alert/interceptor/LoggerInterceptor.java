package com.openclassrooms.safetynet.alert.interceptor;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Interceptor to log incoming HTTP requests and outgoing HTTP responses. */
@Slf4j
@Component
public class LoggerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            @Nullable HttpServletResponse response,
            @Nullable Object handler) {
        // Log basic request information
        log.info(
                "Incoming request: {} {}{} from {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString() != null ? "?" + request.getQueryString() : "",
                request.getRemoteAddr());

        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            @Nullable Object handler,
            Exception ex) {
        // Log response status
        log.info(
                "← {} {} returned {}",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus());

        // Log error responses (4xx and 5xx)
        if (response.getStatus() >= 400) {
            log.warn(
                    "❌ Error response: {} for {} {}",
                    response.getStatus(),
                    request.getMethod(),
                    request.getRequestURI());
        }
    }
}
