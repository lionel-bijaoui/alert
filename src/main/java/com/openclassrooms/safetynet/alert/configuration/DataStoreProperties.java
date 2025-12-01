package com.openclassrooms.safetynet.alert.configuration;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Data
@ConfigurationProperties(prefix = "data.store.path")
@Validated
public class DataStoreProperties {

    @NotBlank private String current;

    @NotBlank private String initial;
}
