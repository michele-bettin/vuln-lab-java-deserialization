package com.demo;

import org.yaml.snakeyaml.Yaml;

public class YamlProcessor {

    // Metodo che esegue la deserializzazione YAML (vulnerabile)
    public static Object loadYaml(String yamlStr) {
        Yaml yaml = new Yaml();
        Object obj = yaml.load(yamlStr);  // <-- Punto vulnerabile ora qui
        System.out.println("YAML parsed (from YamlProcessor): " + obj);
        return obj;
    }
}
