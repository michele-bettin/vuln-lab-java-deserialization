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

    // Test known reflection
    public void loadKnownHelper() {

        // CASO 2 — risolvibile: argomento stringa letterale
        // → NON deve generare ReflectionWarning
        try {
            Class<?> cls = Class.forName("com.demo.DefaultYamlService");
            cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Test unknown reflection
    public Object processWithProxy(String input) {

        // CASO 3 — NON risolvibile: Proxy con interfaccia da variabile
        // → DEVE generare ReflectionWarning
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class<?>[] interfaces = new Class<?>[]{ YamlService.class };
            Object proxy = java.lang.reflect.Proxy.newProxyInstance(cl, interfaces,
                (proxyObj, method, args) -> method.invoke(input, args)  // method.invoke con variabile
            );
            return proxy;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
