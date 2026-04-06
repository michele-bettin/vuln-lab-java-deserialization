package com.demo;

public class DefaultYamlService implements YamlService {

    public DefaultYamlService() {
    }

    @Override
    public Object process(String yamlStr) {
        // Delego al YamlProcessor che contiene il punto vulnerabile
        return YamlProcessor.loadYaml(yamlStr);
    }
}
