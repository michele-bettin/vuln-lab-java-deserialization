package com.demo;

public class YamlController {

    /**
     * Dynamically resolves and instantiates a {@link YamlService} via reflection.
     * The implementation class name is read from the {@code yaml.service.class}
     * system property (untrusted, not known at compile time), loaded with
     * {@link Class#forName}, instantiated through its no-arg constructor, and used
     * to process the trimmed input.
     *
     * @param yamlStr the raw YAML input; may be {@code null}
     * @return the result of {@link YamlService#process}
     * @throws RuntimeException if class resolution, instantiation, or processing fails,
     *                          or if the configured class does not implement {@link YamlService}
     */
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

    /**
     * Loads and instantiates {@code com.demo.DefaultYamlService} via reflection
     * using a compile-time string literal. Because the target is statically
     * resolvable, this is a "safe" reflection call (no taint, no reflection warning).
     *
     * @throws RuntimeException if loading or instantiation fails
     */
    public void loadKnownHelper() {
        try {
            Class<?> cls = Class.forName("com.demo.DefaultYamlService");
            cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a {@link java.lang.reflect.Proxy} implementing {@link YamlService}.
     * Each method call on the returned proxy is dispatched to an
     * {@link java.lang.reflect.InvocationHandler} that logs the method name and
     * delegates the invocation to a real {@link DefaultYamlService} target.
     *
     * @param input currently unused by the delegating handler
     * @return a proxy instance implementing {@link YamlService}
     * @throws RuntimeException if proxy creation fails
     */
    public Object processWithProxy(String input) {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class<?>[] interfaces = new Class<?>[]{ YamlService.class };

            YamlService target = new DefaultYamlService();

            Object proxy = java.lang.reflect.Proxy.newProxyInstance(cl, interfaces,
                (proxyObj, method, args) -> {
                    System.out.println("Called: " + method.getName());
                    return method.invoke(target, args);
                }
            );
            return proxy;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
