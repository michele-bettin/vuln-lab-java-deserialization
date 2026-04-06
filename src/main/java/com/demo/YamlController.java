package com.demo;

public class YamlController {

    // Usa reflection per istanziare il servizio configurato (più intricatezza)
    public Object processYaml(String yamlStr) {
        String svcClass = System.getProperty("yaml.service.class", "com.demo.DefaultYamlService");
        try {
            Class<?> cls = Class.forName(svcClass);
            Object svcObj = cls.getDeclaredConstructor().newInstance();
            if (svcObj instanceof YamlService) {
                YamlService svc = (YamlService) svcObj;
                // possibile pre-processing: trim, log, ecc.
                String prepared = yamlStr == null ? null : yamlStr.trim();
                return svc.process(prepared);
            } else {
                throw new IllegalStateException("Configured class does not implement YamlService: " + svcClass);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process YAML via controller", e);
        }
    }
}
