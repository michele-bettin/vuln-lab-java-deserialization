package com.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.fluent.Request;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class App {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting vulnerable demo app...");

        // If a YAML file is passed as an argument, we use it for the exploit.
        if (args.length > 0) {
            String yamlContent = new String(Files.readAllBytes(Paths.get(args[0])));
            yamlExample2(yamlContent);
        } else {
            // Otherwise, run the normal demos
            jsonExample();
            yamlExample();
            httpExample();
            ioExample();
            System.out.println("Usage: java -cp ... com.demo.App <yaml_file>");
        }
    }

    // Jackson usage (deserialization surface)
    public static void jsonExample() throws Exception {
        String json = "{\"name\":\"test\"}";
        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> obj = mapper.readValue(json, Map.class);
        System.out.println("JSON parsed: " + obj);
    }

    // SnakeYAML usage - not vulnerable, fixed input
    public static void yamlExample() {
        String yamlStr = "name: test";
        Yaml yaml = new Yaml();
        Object obj = yaml.load(yamlStr);
        System.out.println("YAML parsed: " + obj);
    }

    // Apache HTTP client
    public static void httpExample() throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        
        HttpGet request = new HttpGet("http://example.com");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            String body = EntityUtils.toString(entity);
            System.out.println("HTTP response length: " + body.length());
        } finally {
            httpClient.close();
        }
    }

    // Commons IO usage
    public static void ioExample() throws Exception {
        String data = "demo";
        ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        String result = IOUtils.toString(input, StandardCharsets.UTF_8);
        System.out.println("IO result: " + result);
    }

    // Vulnerable method: deserialize YAML from an external input
    public static void yamlExample2(String yamlStr) {

        YamlController controller = new YamlController();

        Object obj = controller.processYaml(yamlStr);
        System.out.println("YAML parsed (dynamic): " + obj);

        controller.loadKnownHelper();
        System.out.println("Known helper loaded (literal).");

        Object proxy = controller.processWithProxy(yamlStr);
        System.out.println("Proxy created (dynamic): " + proxy);
    }
}