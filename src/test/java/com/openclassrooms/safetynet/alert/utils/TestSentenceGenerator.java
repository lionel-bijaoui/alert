package com.openclassrooms.safetynet.alert.utils;

import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom JUnit DisplayNameGenerator that converts test method names from snake_case to readable
 * sentences. Example: Method name: getPersonInfo_ByFirstNameAndLastName Display name:
 * getPersonInfo() By first name and last name
 */
public class TestSentenceGenerator extends DisplayNameGenerator.Standard {

    @Override
    public String generateDisplayNameForMethod(
            List<Class<?>> enclosingInstanceTypes, Class<?> testClass, Method testMethod) {
        String methodName = testMethod.getName();
        String[] parts = methodName.split("_");
        if (parts.length < 2) {
            return methodName;
        }

        String functionName = parts[0] + "()";

        String description =
                Arrays.stream(parts)
                        .skip(1)
                        .map(this::convertCamelCaseToSentence)
                        .collect(Collectors.joining(" "));

        return functionName + " " + description;
    }

    private String convertCamelCaseToSentence(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return "";
        }

        String[] words = camelCase.split("(?=\\p{Upper})");

        String sentence =
                Arrays.stream(words).map(String::toLowerCase).collect(Collectors.joining(" "));

        return Character.toUpperCase(sentence.charAt(0)) + sentence.substring(1);
    }
}
