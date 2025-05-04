# Cloud-Native Architecture / Microservice Template

## Non Functional Requirements Template

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=arafkarsh_ms-springboot-334-vanilla&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=arafkarsh_ms-springboot-334-vanilla)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=arafkarsh_ms-springboot-334-vanilla&metric=bugs)](https://sonarcloud.io/summary/new_code?id=arafkarsh_ms-springboot-334-vanilla)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=arafkarsh_ms-springboot-334-vanilla&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=arafkarsh_ms-springboot-334-vanilla)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=arafkarsh_ms-springboot-334-vanilla&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=arafkarsh_ms-springboot-334-vanilla)

---

# 雲原生架構 / 微服務範本

## 非功能性需求範本

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=arafkarsh_ms-springboot-334-vanilla&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=arafkarsh_ms-springboot-334-vanilla)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=arafkarsh_ms-springboot-334-vanilla&metric=bugs)](https://sonarcloud.io/summary/new_code?id=arafkarsh_ms-springboot-334-vanilla)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=arafkarsh_ms-springboot-334-vanilla&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=arafkarsh_ms-springboot-334-vanilla)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=arafkarsh_ms-springboot-334-vanilla&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=arafkarsh_ms-springboot-334-vanilla)

---

1. Java 23 (Minimum Requirement: Java 17)
2. SpringBoot 3.4.1
3. Jakarta EE 10

---

1. Java 23 (最低要求：Java 17)
2. SpringBoot 3.4.1
3. Jakarta EE 10

---

Cloud-native (or microservice) architecture is an approach to application design in which software is
broken down into small, independent services that communicate through lightweight APIs, enabling
more agile development, scalability, and resilience. Rather than running a single monolithic codebase,
each microservice can be developed, deployed, and scaled independently.

---

雲原生（或微服務）架構是一種應用程式設計方法，其中軟體被分解成小型、獨立的服務，這些服務透過輕量級 API 進行通訊，從而實現更敏捷的開發、可擴展性和彈性。與執行單一的巨石型程式碼庫不同，每個微服務都可以獨立開發、部署和擴展。

---

This decomposition—often containerized and orchestrated using tools such as Kubernetes—allows teams
to quickly iterate on features, take advantage of cloud-native capabilities (like auto-scaling and
automated deployments), and release updates with minimal disruption to the entire system. According
to the Cloud Native Computing Foundation (CNCF), this approach fosters loosely coupled systems that
are resilient, manageable, and observable, combined with robust automation (CNCF, 2023).

---

這種分解——通常使用 Kubernetes 等工具進行容器化和編排——使團隊能夠快速迭代功能，利用雲原生能力（如自動擴展和自動化部署），並以最小的系統中斷發布更新。根據雲原生運算基金會（CNCF）的說法，這種方法促進了鬆散耦合的系統，這些系統具有彈性、可管理性和可觀察性，並結合了強大的自動化（CNCF, 2023）。

---

Key Features of Microservice (Sources: CNCF, 2023; Fowler, 2014):

1. Service Independence: Each microservice is autonomous, allowing for separate development, deployment, and scaling without affecting others.
2. Containerization: Services are commonly packaged in containers (e.g., Docker), providing consistency across different environments and efficient resource utilization.
3. Lightweight Communication: Microservices communicate via lightweight protocols (often HTTP/REST or gRPC), reducing overhead and complexity.
4. Scalability: Independent scaling of services ensures you can allocate resources exactly where needed, improving performance and cost-efficiency.
5. Continuous Delivery and Deployment: Automation enables frequent, reliable releases to production while minimizing disruption.
6. Resilience: Failure of one service doesn't necessarily bring the entire system down, as microservices are loosely coupled and can handle faults gracefully.

References

- CNCF. (2023). What is Cloud Native? https://www.cncf.io/blog/2023/02/03/what-is-cloud-native/
- Fowler, M. (2014). Microservices. https://martinfowler.com/articles/microservices.html

---

微服務的主要特點 (資料來源：CNCF, 2023; Fowler, 2014)：

1.  服務獨立性：每個微服務都是自治的，允許獨立開發、部署和擴展，而不影響其他服務。
2.  容器化：服務通常打包在容器中（例如 Docker），在不同環境中提供一致性並有效利用資源。
3.  輕量級通訊：微服務透過輕量級協定（通常是 HTTP/REST 或 gRPC）進行通訊，減少了開銷和複雜性。
4.  可擴展性：服務的獨立擴展確保您可以精確地分配資源到需要的地方，從而提高效能和成本效益。
5.  持續交付與部署：自動化實現了頻繁、可靠的生產發布，同時將中斷降至最低。
6.  彈性：單一服務的故障不一定會導致整個系統癱瘓，因為微服務是鬆散耦合的，可以優雅地處理故障。

參考資料

- CNCF. (2023). What is Cloud Native? https://www.cncf.io/blog/2023/02/03/what-is-cloud-native/
- Fowler, M. (2014). Microservices. https://martinfowler.com/articles/microservices.html

---

## What the Template Provides out of the box

1. Security Auth/Authorization using AOP and Filters
2. Exception Handling with Exception Framework using AOP ( ..microservice.adapters.aop)
3. Log Management using AOP (json and text formats) using Logback (...adapters.filters)
4. Standardized REST Responses (...domain.models.StandardResponse)
5. Security using JWT Tokens / KeyCloak Auth (...microservice.adapters.security, ...microservice.security)
6. Encrypting Sensitive Data using Encryption Algorithms (...microservice.security)
7. JPA configurations for H2 and PostgreSQL (...server.config)
8. Observability Using Micrometer, Prometheus and Open Telemetry.
9. Database Password Encryption using Jasypt. Checkout the shell programs encrypt and decrypt.
10. Digital Signatures using Standard Java Cryptography.
11. Open API based Swagger API Docs (...microservice.adapters.controllers)

---

## 此範本提供的開箱即用功能

1. 使用 AOP 和過濾器的安全認證/授權
2. 使用 AOP 的例外處理框架進行例外處理 ( ..microservice.adapters.aop)
3. 使用 AOP 和 Logback 進行日誌管理（json 和文字格式）(...adapters.filters)
4. 標準化的 REST 回應 (...domain.models.StandardResponse)
5. 使用 JWT Tokens / KeyCloak Auth 的安全性 (...microservice.adapters.security, ...microservice.security)
6. 使用加密演算法加密敏感資料 (...microservice.security)
7. H2 和 PostgreSQL 的 JPA 配置 (...server.config)
8. 使用 Micrometer、Prometheus 和 Open Telemetry 的可觀察性。
9. 使用 Jasypt 的資料庫密碼加密。請查看 shell 程式 `encrypt` 和 `decrypt`。
10. 使用標準 Java 加密技術的數位簽章。
11. 基於 Open API 的 Swagger API 文件 (...microservice.adapters.controllers)

---

## How to Setup and use the template

### Encrypting Database Passwords for the Property Files

This microservice template offers a range of built-in functionalities. To simplify the demonstration of
various features, an encrypted password is utilized for connecting to H2 and PostgreSQL databases.
The template includes utilities for encrypting and decrypting passwords, ensuring that the encryption
key is securely stored outside the application's runtime context.

To know more about how to setup these passwords (for H2 & PostgreSQL) and environment variables
checkout Session 1.2

Encrypted H2 (In Memory) Database Password. Uses H2 database in Dev (Profile) mode.
![Package Structure](https://raw.githubusercontent.com/arafkarsh/ms-springboot-334-vanilla/master/diagrams/encrypt/Security-H2-psd.jpg)
Encrypted PostgreSQL Database Password. Uses PostgreSQL DB in Staging & Prod (profile) mode.
![Package Structure](https://raw.githubusercontent.com/arafkarsh/ms-springboot-334-vanilla/master/diagrams/encrypt/Security-PostgreSQL-psd.jpg)
Password can be decrypted only using an Encryption Key stored in System Enviornment variable
![Package Structure](https://raw.githubusercontent.com/arafkarsh/ms-springboot-334-vanilla/master/diagrams/encrypt/Security-Encryption-pro.jpg)

If the Quality Gate check fails, it's because the password is encrypted within the application's
properties file, with the encryption key stored externally, outside the application's context.

However, quality standards mandate that passwords should be securely stored in a vault, such as
HashiCorp Vault, for enhanced security.

---

## 如何設定和使用此範本

### 為屬性檔案加密資料庫密碼

這個微服務範本提供了一系列內建功能。為了簡化各種功能的演示，使用加密密碼連接到 H2 和 PostgreSQL 資料庫。範本包含用於加密和解密密碼的工具，確保加密金鑰安全地儲存在應用程式執行期上下文之外。

要了解如何設定這些密碼（針對 H2 和 PostgreSQL）和環境變數，請查看 Session 1.2

加密的 H2（記憶體中）資料庫密碼。在 Dev (Profile) 模式下使用 H2 資料庫。
![Package Structure](https://raw.githubusercontent.com/arafkarsh/ms-springboot-334-vanilla/master/diagrams/encrypt/Security-H2-psd.jpg)
加密的 PostgreSQL 資料庫密碼。在 Staging & Prod (profile) 模式下使用 PostgreSQL 資料庫。
![Package Structure](https://raw.githubusercontent.com/arafkarsh/ms-springboot-334-vanilla/master/diagrams/encrypt/Security-PostgreSQL-psd.jpg)
密碼只能使用儲存在系統環境變數中的加密金鑰進行解密
![Package Structure](https://raw.githubusercontent.com/arafkarsh/ms-springboot-334-vanilla/master/diagrams/encrypt/Security-Encryption-pro.jpg)

如果 Quality Gate 檢查失敗，那是因為密碼在應用程式的屬性檔案中被加密，而加密金鑰儲存在外部，不在應用程式的上下文內。

然而，品質標準要求密碼應安全地儲存在諸如 HashiCorp Vault 之類的保險庫中，以增強安全性。

---

### Microservice Package Structure

![Package Structure](https://raw.githubusercontent.com/arafkarsh/ms-springboot-334-vanilla/master/diagrams/MS-Pkg-Structure.jpg)

io.fusion.air.microservice

1. adapters (All the Implementations from App/Service perspective)
2. domain (All Entities, Models, Interfaces for the implementations)
3. security (All Security related modules)
4. server (Managing the Service - from a server perspective, Setups (Cache, DB, Kafka etc, Configs)
5. utils (Standard Utilities)

---

### 微服務套件結構

![Package Structure](https://raw.githubusercontent.com/arafkarsh/ms-springboot-334-vanilla/master/diagrams/MS-Pkg-Structure.jpg)

io.fusion.air.microservice

1. adapters (從應用程式/服務角度看的所有實作)
2. domain (所有實體、模型、介面)
3. security (所有安全相關模組)
4. server (管理服務 - 從伺服器角度看，設定（快取、資料庫、Kafka 等）、配置)
5. utils (標準工具程式)

---

### Security Framework with Spring Security, JWT, KeyCloak, & Cryptography

![Security Structure](https://raw.githubusercontent.com/arafkarsh/ms-springboot-334-vanilla/master/diagrams/Fusion-Security-Pkg.png)

1.\tAdapters Package (left side) – Integrations with Spring MVC, AOP, Filters, and Web Security. 2. Security Package (right side) – Core libraries and utilities for JWT creation, validation, cryptography, etc.

#### 1. Adapters Package (io.fusion.air.microservice.adapters)

A. Filters Package
1.\tJWT Auth Filter

- A javax.servlet.Filter (or jakarta.servlet.Filter) that intercepts requests early in the servlet chain.
- It extracts JWTs from headers, validates or parses them, and stores user claims in a ClaimsManager for downstream use.
  2.\tLog Filter
- Another servlet filter for logging requests. Possibly logs details like request URIs, IP addresses, timings, etc.
  3.\tSecurity Filter
- A filter that enforces security rules at the servlet layer (e.g., blocking requests with invalid data or applying firewall rules).
- Complements or replaces Spring Security's default filter chain in some scenarios.
  These filters run before the DispatcherServlet. They can reject or manipulate requests if authentication or security checks fail.

B. Spring Framework (DispatcherServlet)

- DispatcherServlet is the central Spring MVC component that routes incoming HTTP requests to the
  appropriate controller endpoints. Checkout the [API flow in Part 4 of my Java 23 series.](https://arafkarsh.medium.com/java-23-springboot-3-3-4-api-flow-logging-part-4-1000546bcd62)

C. AOP Package

- Authorization Request Aspect
- A Spring AOP aspect that intercepts controller or service methods to enforce authorization rules.
- Typically checks whether the user has the necessary roles/permissions based on JWT claims or
  custom annotations (@AuthorizationRequired).

Checkout the [Java 23, SpringBoot 3.3.4, & Jakarta EE 10](https://arafkarsh.medium.com/java-23-springboot-3-3-4-jakarta-10-125bc815d6c1)
for more details on this topic.

---

### 使用 Spring Security、JWT、KeyCloak 和密碼學的安全框架

![Security Structure](https://raw.githubusercontent.com/arafkarsh/ms-springboot-334-vanilla/master/diagrams/Fusion-Security-Pkg.png)

1.  Adapters 套件 (左側) – 與 Spring MVC、AOP、過濾器和 Web Security 整合。
2.  Security 套件 (右側) – 用於 JWT 建立、驗證、密碼學等的核心函式庫和工具程式。

#### 1. Adapters 套件 (io.fusion.air.microservice.adapters)

A. Filters 套件

1.  JWT Auth Filter
    - 一個 `javax.servlet.Filter` (或 `jakarta.servlet.Filter`)，在 Servlet 鏈的早期攔截請求。
    - 它從標頭中提取 JWT，驗證或解析它們，並將使用者聲明儲存在 `ClaimsManager` 中供下游使用。
2.  Log Filter
    - 另一個用於記錄請求的 Servlet 過濾器。可能記錄請求 URI、IP 位址、時間等詳細資訊。
3.  Security Filter - 在 Servlet 層強制執行安全規則的過濾器（例如，阻止包含無效資料的請求或應用防火牆規則）。 - 在某些情況下補充或取代 Spring Security 的預設過濾器鏈。
    這些過濾器在 `DispatcherServlet` 之前執行。如果身份驗證或安全檢查失敗，它們可以拒絕或操縱請求。

B. Spring Framework (DispatcherServlet)

- `DispatcherServlet` 是 Spring MVC 的核心元件，負責將傳入的 HTTP 請求路由到適當的控制器端點。請查看我的 [Java 23 系列第 4 部分中的 API 流程](https://arafkarsh.medium.com/java-23-springboot-3-3-4-api-flow-logging-part-4-1000546bcd62)。

C. AOP 套件

- Authorization Request Aspect
- 一個 Spring AOP 切面，攔截控制器或服務方法以強制執行授權規則。
- 通常根據 JWT 聲明或自訂註解 (`@AuthorizationRequired`) 檢查使用者是否具有必要的角色/權限。

請查看 [Java 23, SpringBoot 3.3.4, & Jakarta EE 10](https://arafkarsh.medium.com/java-23-springboot-3-3-4-jakarta-10-125bc815d6c1)
以獲取有關此主題的更多詳細資訊。

---

### Template Tutorials - Java 23, SpringBoot 3.3.4 & Jakarta 10 Series

1. [Java 23, SpringBoot 3.3.4 & Jakarta 10 — Part 1](https://arafkarsh.medium.com/java-23-springboot-3-3-4-jakarta-10-125bc815d6c1)
2. [Java 23, SpringBoot 3.3.4: AOP Exception Handling — Part 2](https://arafkarsh.medium.com/java-23-springboot-3-3-4-aop-exception-handling-part-2-e6adc86c8a26)
3. [Java 23, SpringBoot 3.3.4: Logback Setup — Part 3 ](https://arafkarsh.medium.com/java-23-springboot-3-3-4-logback-setup-part-3-c2ffe2d0a358)
4. [Java 23, SpringBoot 3.3.4: Log/Events: API Flow & Logging — Part 4](https://arafkarsh.medium.com/java-23-springboot-3-3-4-api-flow-logging-part-4-1000546bcd62)
5. [Java 23, SpringBoot 3.3.4: Metrics: Micrometer, Prometheus, Actuator — Part 5](https://arafkarsh.medium.com/java-23-springboot-3-3-4-metrics-micrometer-prometheus-actuator-part-5-f67f0581815c)
6. [Java 23, SpringBoot 3.3.4: Metrics: Micrometer & AOP — Part 6](https://arafkarsh.medium.com/java-23-springboot-3-3-4-metrics-micrometer-aop-part-6-808dcb97dcb7)
7. [Java 23, SpringBoot 3.3.4: Tracing: OpenTelemetry — Part 7](https://arafkarsh.medium.com/java-23-springboot-3-3-4-tracing-opentelemetry-part-7-937df4867c9c)
8. Java 23, SpringBoot 3.4.1: Tracing: OpenTelemetry Zero Code— Part 8 Coming Soon
9. [Java 23, SpringBoot 3.4.1: Containers: Alpine Multi-Architecture — Part 9](https://arafkarsh.medium.com/java-23-springboot-3-4-1-multi-architecture-containers-part-9-b8c70ed3842f)
10. [Java 23, SpringBoot 3.4.1: Containers: Kubernetes — Part 10](https://arafkarsh.medium.com/java-23-springboot-3-4-1-kubernetes-containers-part-10-1b3b3b3b1b1b)
11. Java 23, SpringBoot 3.4.1: Filters: Security, Log — Part 11 Coming Soon
12. Java 23, SpringBoot 3.4.1: AOP: Spring Security — Part 12 Coming Soon
13. Java 23, SpringBoot 3.4.1: Security: JSON Web Token — Part 13 Coming Soon
14. Java 23, SpringBoot 3.4.1: CRUD : Domain Driven Design — Part 14 Coming Soon
15. Java 23, SpringBoot 3.4.1: CRUD Queries & Page Sort — Part 15 Coming Soon

---

### 範本教學 - Java 23, SpringBoot 3.3.4 & Jakarta 10 系列

1. [Java 23, SpringBoot 3.3.4 & Jakarta 10 — 第 1 部分](https://arafkarsh.medium.com/java-23-springboot-3-3-4-jakarta-10-125bc815d6c1)
2. [Java 23, SpringBoot 3.3.4：AOP 例外處理 — 第 2 部分](https://arafkarsh.medium.com/java-23-springboot-3-3-4-aop-exception-handling-part-2-e6adc86c8a26)
3. [Java 23, SpringBoot 3.3.4：Logback 設定 — 第 3 部分](https://arafkarsh.medium.com/java-23-springboot-3-3-4-logback-setup-part-3-c2ffe2d0a358)
4. [Java 23, SpringBoot 3.3.4：日誌/事件：API 流程與記錄 — 第 4 部分](https://arafkarsh.medium.com/java-23-springboot-3-3-4-api-flow-logging-part-4-1000546bcd62)
5. [Java 23, SpringBoot 3.3.4：指標：Micrometer, Prometheus, Actuator — 第 5 部分](https://arafkarsh.medium.com/java-23-springboot-3-3-4-metrics-micrometer-prometheus-actuator-part-5-f67f0581815c)
6. [Java 23, SpringBoot 3.3.4：指標：Micrometer & AOP — 第 6 部分](https://arafkarsh.medium.com/java-23-springboot-3-3-4-metrics-micrometer-aop-part-6-808dcb97dcb7)
7. [Java 23, SpringBoot 3.3.4：追蹤：OpenTelemetry — 第 7 部分](https://arafkarsh.medium.com/java-23-springboot-3-3-4-tracing-opentelemetry-part-7-937df4867c9c)
8. Java 23, SpringBoot 3.4.1：追蹤：OpenTelemetry Zero Code — 第 8 部分 即將推出
9. [Java 23, SpringBoot 3.4.1：容器：Alpine 多架構 — 第 9 部分](https://arafkarsh.medium.com/java-23-springboot-3-4-1-multi-architecture-containers-part-9-b8c70ed3842f)
10. [Java 23, SpringBoot 3.4.1：容器：Kubernetes — 第 10 部分](https://arafkarsh.medium.com/java-23-springboot-3-4-1-kubernetes-containers-part-10-1b3b3b3b1b1b)
11. Java 23, SpringBoot 3.4.1：過濾器：Security, Log — 第 11 部分 即將推出
12. Java 23, SpringBoot 3.4.1：AOP：Spring Security — 第 12 部分 即將推出
13. Java 23, SpringBoot 3.4.1：安全：JSON Web Token — 第 13 部分 即將推出
14. Java 23, SpringBoot 3.4.1：CRUD：領域驅動設計 — 第 14 部分 即將推出
15. Java 23, SpringBoot 3.4.1：CRUD 查詢與分頁排序 — 第 15 部分 即將推出

---

### Pre-Requisites

1. SpringBoot 3.3.4
2. Java 23 (Minimum Requirement Java 17)
3. Jakarta EE 10 (jakarta.servlet._, jakarta.persistence._, javax.validation.\*)
4. Maven 3.8.6
5. Git 2.31

---

### 先決條件

1. SpringBoot 3.3.4
2. Java 23 (最低要求 Java 17)
3. Jakarta EE 10 (jakarta.servlet._, jakarta.persistence._, javax.validation.\*)
4. Maven 3.8.6
5. Git 2.31

---

## 1. Setting up the Template

### Step 1.1 - Getting Started

1. `git clone https://github.com/arafkarsh/ms-springboot-334-vanilla`
2. `cd ms-springboot-334-vanilla`

---

## 1. 設定範本

### 步驟 1.1 - 開始使用

1. `git clone https://github.com/arafkarsh/ms-springboot-334-vanilla`
2. `cd ms-springboot-334-vanilla`

---

### Step 1.2 - Setup Encrypted DB Password in Property files

#### 1.2.1 Encrypt the Database passwords for H2 and PostgreSQL

If you dont encrypt the password with your Encryption Key it will throw an exception saying unable to decrypt the password.
Here are the steps to encrypt the password.

Run the follwing command line option

```
$ source encrypt your-db-password your-encrypton-key
```

![Passowrd-Gen](https://raw.githubusercontent.com/arafkarsh/ms-springboot-334-vanilla/master/diagrams/Password-Gen.jpg)

Your encryption key will be set in the following Environment variable. SpringBoot Will automatically
pickup the encryption key from this environment variable.

```
JASYPT_ENCRYPTOR_PASSWORD=your-encrypton-key
```

#### 1.2.2 Update the Database passwords for H2 and PostgreSQL in the Property files

Update the property file in the local file

```
spring.datasource.password=ENC(kkthRIyJ7ogLJP8PThfXjqko33snTUa9lY1GkyFpzr7KFRVhRVXLOMwNSIzr4EjFGAOWLhWTH5cAWzRzAfs33g==)
```

AND

- the property template in src/main/resources/app.props.tmpl
- dev src/main/resources/application-dev.properties

```
spring.datasource.password=ENC(kkthRIyJ7ogLJP8PThfXjqko33snTUa9lY1GkyFpzr7KFRVhRVXLOMwNSIzr4EjFGAOWLhWTH5cAWzRzAfs33g==)
```

AND
the property files for

- staging src/main/resources/application-staging.properties
- prod src/main/resources/application-prod.properties

```
spring.datasource.password=ENC(/J0gRHIdlhBHFwpNo3a+1q3+8Uig5+uSNQHO/lCGOrfg/e8Wt2o3v1eC4TaquaDVGREOEFphpw1B84lOtxgeIA==)
```

#### 1.2.3 - Generating the Encrypted Text from REST Endpoint

You can use the following REST Endpoint to encrypt the sensitive data. This will work only after setting
the environment variable JASYPT_ENCRYPTOR_PASSWORD and creating the first DB password
using the command line options.

![Passowrd-Van](https://raw.githubusercontent.com/arafkarsh/ms-springboot-334-vanilla/master/diagrams/ms-vanilla-encrypt.jpg)

---

### 步驟 1.2 - 在屬性檔案中設定加密的資料庫密碼

#### 1.2.1 加密 H2 和 PostgreSQL 的資料庫密碼

如果您未使用您的加密金鑰加密密碼，它將拋出一個例外，說明無法解密密碼。
以下是加密密碼的步驟。

執行以下命令列選項

```
$ source encrypt your-db-password your-encrypton-key
```

![Passowrd-Gen](https://raw.githubusercontent.com/arafkarsh/ms-springboot-334-vanilla/master/diagrams/Password-Gen.jpg)

您的加密金鑰將設定在以下環境變數中。SpringBoot 將自動從此環境變數中讀取加密金鑰。

```
JASYPT_ENCRYPTOR_PASSWORD=your-encrypton-key
```

#### 1.2.2 更新屬性檔案中 H2 和 PostgreSQL 的資料庫密碼

更新本地檔案中的屬性檔案

```
spring.datasource.password=ENC(kkthRIyJ7ogLJP8PThfXjqko33snTUa9lY1GkyFpzr7KFRVhRVXLOMwNSIzr4EjFGAOWLhWTH5cAWzRzAfs33g==)
```

以及

- `src/main/resources/app.props.tmpl` 中的屬性範本
- `dev` `src/main/resources/application-dev.properties`

```
spring.datasource.password=ENC(kkthRIyJ7ogLJP8PThfXjqko33snTUa9lY1GkyFpzr7KFRVhRVXLOMwNSIzr4EjFGAOWLhWTH5cAWzRzAfs33g==)
```

以及
用於以下環境的屬性檔案

- `staging` `src/main/resources/application-staging.properties`
- `prod` `src/main/resources/application-prod.properties`

```
spring.datasource.password=ENC(/J0gRHIdlhBHFwpNo3a+1q3+8Uig5+uSNQHO/lCGOrfg/e8Wt2o3v1eC4TaquaDVGREOEFphpw1B84lOtxgeIA==)
```

#### 1.2.3 - 從 REST 端點生成加密文本

您可以使用以下 REST 端點來加密敏感資料。這只有在設定了環境變數 `JASYPT_ENCRYPTOR_PASSWORD` 並使用命令列選項創建了第一個資料庫密碼後才能運作。

![Passowrd-Van](https://raw.githubusercontent.com/arafkarsh/ms-springboot-334-vanilla/master/diagrams/ms-vanilla-encrypt.jpg)

---

### Step 1.3 - Compile (Once your code is ready)

#### 1.3.1 Compile the Code

Execute the "compile" from ms-springboot-334-vanilla

1. compile OR ./compile (Runs in Linux and Mac OS)
2. mvn clean; mvn -e package; (All Platforms)
3. Use the IDE Compile options

#### 1.3.2 What the "Compile" Script will do

1. Clean up the target folder
2. Generate the build no. and build date (takes application.properties backup)
3. build final output SpringBoot fat jar and maven thin jar
4. copy the jar files (and dependencies) to src/docker folder
5. copy the application.properties file to current folder and src/docker folder

In Step 1.3.2 application.properties file will be auto generated by the "compile" script. This is a critical step.
Without generated application.properties file the service will NOT be running. There is pre-built application properties file.
Following three property files are critical (to be used with Spring Profiles)

1. application.properties
2. application-dev.properties
3. application-staging.properties
4. application-prod.properties

---

### 步驟 1.3 - 編譯 (當您的程式碼準備好時)

#### 1.3.1 編譯程式碼

從 `ms-springboot-334-vanilla` 執行 "compile"

1. `compile` 或 `./compile` (在 Linux 和 Mac OS 上執行)
2. `mvn clean; mvn -e package;` (所有平台)
3. 使用 IDE 編譯選項

#### 1.3.2 "Compile" 腳本會做什麼

1. 清理 `target` 資料夾
2. 生成建置編號和建置日期 (備份 `application.properties`)
3. 建置最終輸出的 SpringBoot fat jar 和 maven thin jar
4. 將 jar 檔案 (和依賴項) 複製到 `src/docker` 資料夾
5. 將 `application.properties` 檔案複製到目前資料夾和 `src/docker` 資料夾

在步驟 1.3.2 中，`application.properties` 檔案將由 "compile" 腳本自動生成。這是關鍵步驟。
沒有生成的 `application.properties` 檔案，服務將無法執行。有一個預先建置的 `application.properties` 檔案。
以下三個屬性檔案至關重要 (與 Spring Profiles 一起使用)

1. `application.properties`
2. `application-dev.properties`
3. `application-staging.properties`
4. `application-prod.properties`

---

### Step 1.4 - Run the Application

#### 1.4.1 - Spring Profiles

1. dev (Development Mode)
2. staging (Staging Mode)
3. prod (Production Mode)

#### 1.4.2 - Start the Service

1. Linux or Mac OS - Profiles (dev, staging, or prod)

```aiignore
run
```

```aiignore
run dev
```

```aiignore
run staging
```

```aiignore
run prod
```

2. All Platforms - Profiles (dev, staging, or prod)

```aiignore
 mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

```aiignore

```

---

### 步驟 1.4 - 執行應用程式

#### 1.4.1 - Spring Profiles

1. `dev` (開發模式)
2. `staging` (預備模式)
3. `prod` (生產模式)

#### 1.4.2 - 啟動服務

1. Linux 或 Mac OS - Profiles (`dev`, `staging`, 或 `prod`)

```aiignore
run
```

```aiignore
run dev
```

```aiignore
run staging
```

```aiignore
run prod
```

2. 所有平台 - Profiles (`dev`, `staging`, 或 `prod`)

```aiignore
 mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

```aiignore

```
