package com.okta.myaccount.myaccountmanagement.invoker;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonConfig {
    public static ObjectMapper OBJECT_MAPPER = Jackson2ObjectMapperBuilder.json().build();
}