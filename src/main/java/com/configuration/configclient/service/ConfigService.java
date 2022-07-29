package com.configuration.configclient.service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import lombok.Data;

@Data
@RefreshScope
@Service
public class ConfigService {
    
    @Value("${foo:bar}")
    private String foo;

    @Value("${bar:baz}")
    private String bar;

    @Value("${baz:url}")
    private String baz;

    @Value("${url:foo}")
    private String url;

    private Map<String, Object> props = new HashMap<>();

    @PostConstruct
    void init() {
        Field[] fields = this.getClass().getDeclaredFields();
        if (fields.length == 0) return;

        String fieldName;
        Object fieldValue;

        for (Field field : fields) {
            field.setAccessible(true);
            fieldName = field.getName();
        
            if (ignoredField(field)) continue;
        
            try {
                fieldValue = field.get(this);
            } catch (IllegalAccessException e) {
                fieldValue = null;
            }

        props.put(fieldName, fieldValue);
        }
    }

    private boolean ignoredField(Field field) {
        return field.getName().equals("props") || Modifier.isStatic(field.getModifiers());
    }

    public String getPropertyAsString(String propName) {
        Object propertyValue = props.get(propName);
        return propertyValue == null ? null : propertyValue.toString();
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String propName) {
        return (T)props.get(propName);
    }
}
