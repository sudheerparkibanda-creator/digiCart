# Service Failure Diagnosis

## Compilation Status ✅
All 10 services compile successfully without errors.

## Likely Causes of Runtime Failures

### By Service Type

#### Services with NEW Security Config
- **price-service** - May fail if Eureka/Config Server down
- **product-service** - May fail if Eureka/Config Server down  
- **stock-service** - May fail if Eureka/Config Server down

**How to fix:** Ensure config-server and service-registry are running first.

#### Services with Kafka Dependency
- **cart-service** - Requires Kafka to be running
- **order-service** - Requires Kafka to be running
- **notification-service** - Requires Kafka to be running
- **payment-service** - Requires Kafka to be running

**Error symptom:** "Connection refused" or "KafkaProducerFactory bean creation failed"  
**How to fix:** Start Kafka broker before these services.

#### Services with Database (MySQL)
- **address-service** - Uses JPA
- **cart-service** - Uses JPA
- **order-service** - Uses JPA
- **price-service** - Uses JPA
- **product-service** - Uses JPA
- **stock-service** - Uses JPA
- **user-service** - Uses JPA

**Error symptom:** "Unable to create new native thread" or "Connection refused"  
**How to fix:** Start MySQL database and ensure it's accessible.

#### Services without Kafka or Heavy I/O
- **api-gateway** - Lightweight, mainly routing
- **config-server** - Config server itself
- **service-registry** - Eureka server
- **notification-service** - Has Kafka + Mail
- **payment-service** - Has Kafka

**How to fix:** These should start fine if dependencies are met.

---

## Quick Diagnostic Script

To identify which service is failing, create a test file and run each:

```powershell
# Test each service individually (PowerShell)

$services = @("address-service", "api-gateway", "cart-service", "notification-service", 
              "order-service", "payment-service", "price-service", "product-service", 
              "stock-service", "user-service")

foreach ($service in $services) {
    Write-Host "Testing $service..." -ForegroundColor Cyan
    
    Push-Location "C:\Users\sudhe\OneDrive\Documents\advancedProject\$service"
    
    # Check if compiles
    $compileResult = & .\mvnw.cmd clean compile -DskipTests 2>&1 | Select-String -Pattern "ERROR|BUILD FAILURE|BUILD SUCCESS" | Select-Object -Last 1
    
    if ($compileResult -match "SUCCESS") {
        Write-Host "✅ $service compiles OK" -ForegroundColor Green
    } else {
        Write-Host "❌ $service compile FAILED" -ForegroundColor Red
        Write-Host $compileResult
    }
    
    Pop-Location
}
```

---

## Dependency Chain Required

```
Startup Order (Prerequisites First):

Tier 1 (Infrastructure)
  └─ MySQL (for JPA services)
  └─ Kafka (for event services)
  └─ Zipkin (optional, but recommended for tracing)

Tier 2 (Spring Cloud Infrastructure)
  └─ config-server (all services need config)
  └─ service-registry (Eureka, for service discovery)

Tier 3 (Core Services)
  └─ user-service (others may depend on auth)
  └─ api-gateway (entry point)

Tier 4 (Business Services)
  └─ address-service
  └─ cart-service
  └─ order-service
  └─ payment-service
  └─ notification-service
  └─ price-service
  └─ product-service
  └─ stock-service
```

---

## Common Errors & Solutions

### ERROR 1: "Connection to Kafka refused"
```
Services: cart-service, order-service, notification-service, payment-service
Fix: docker run -d --name kafka -p 9092:9092 apache/kafka
```

### ERROR 2: "Unable to obtain Jdbc Connection"
```
Services: Any with JPA
Fix: Ensure MySQL is running and accessible
```

### ERROR 3: "Connect to localhost:8888 [localhost/127.0.0.1:8888] failed"
```
All services
Fix: Start config-server first
cd config-server && ./mvnw spring-boot:run
```

### ERROR 4: "Connection refused to Eureka"
```
All services
Fix: Start service-registry first (it's Eureka)
cd service-registry && ./mvnw spring-boot:run
Note: Not fatal - services will still start but won't register
```

### ERROR 5: "Port 8080 already in use"
```
API Gateway
Fix: Either kill the process on 8080 or change port in application.yaml
```

### ERROR 6: Missing beans related to Security
```
price-service, product-service, stock-service
Fix: Ensure SecurityConfig.java is in the config/ folder
Check: SecurityContextFilter is auto-discovered as @Component
```

### ERROR 7: Zipkin endpoint unreachable
```
All services
Note: This won't cause failure, just connection spam in logs
Fix: docker run -d -p 9411:9411 openzipkin/zipkin
Or suppress logs: logging.level.io.zipkin=WARN
```

---

## What NOT to Worry About

These are expected and safe:

❌ But OK: "Connection refused to Zipkin" - Services will still work, traces just won't be sent
❌ But OK: "Eureka client registration failed" - Service discovery fails but app still runs locally  
❌ But OK: "Spring Cloud Config not available" - Will use local config from application.yaml
❌ But OK: Deprecation warnings about RestTemplate timeouts - Just warnings, works fine

---

## Tell Me & I'll Fix It

If a service fails to start, provide:

1. **Exact error message** from the console output
2. **Service name** that failed
3. **Whether** the error appeared in startup output or after startup
4. **Any lines** mentioning specific exceptions

Example problem report:
```
"price-service fails to start with:
ERROR org.springframework.boot.SpringApplication : Application run failed
java.net.ConnectException: Connection refused
  at java.net.Socket.connect (Socket.java:629)
  ..."
```

Then I can pinpoint exactly what's missing and how to fix it.

---

## Quick Start Reference

### Start One Service Completely

```bash
# Example: Start price-service with ALL prerequisites

# 1. In Terminal 1: Start MySQL
# 2. In Terminal 2: Start Kafka
# 3. In Terminal 3: Start Zipkin
docker run -d -p 9411:9411 openzipkin/zipkin

# 4. In Terminal 4: Start Config Server
cd config-server && ./mvnw spring-boot:run

# 5. In Terminal 5: Start Service Registry
cd service-registry && ./mvnw spring-boot:run

# 6. In Terminal 6: Start price-service
cd price-service && ./mvnw spring-boot:run

# 7. Verify: Check logs for "started in X seconds"
# 8. Test: curl http://localhost:8080/prices
```

Ready for full diagnosis when you have the error details!

