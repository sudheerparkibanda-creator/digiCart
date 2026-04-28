# Zipkin Tracing Setup Verification

## Services Configuration Status

All 10 services have been configured for Zipkin tracing:

### Dependencies Added to All Services
✅ `io.micrometer:micrometer-tracing-bridge-brave`
✅ `io.zipkin.reporter2:zipkin-reporter-brave`

### Configuration Added to All Services

#### application.yaml
```yaml
management:
  tracing:
    sampling:
      probability: 1.0  # 100% trace sampling
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans  # Zipkin endpoint

logging:
  pattern:
    level: "%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]"
```

## Compilation Status

All services compile successfully:
- ✅ address-service
- ✅ api-gateway
- ✅ cart-service
- ✅ notification-service
- ✅ order-service
- ✅ payment-service (added dependency management)
- ✅ price-service
- ✅ product-service
- ✅ stock-service
- ✅ user-service

## Runtime Troubleshooting

### If services fail to start:

**1. Zipkin Not Running**
- Ensure Zipkin is running on `localhost:9411`
- Docker command:
  ```bash
  docker run -d -p 9411:9411 openzipkin/zipkin
  ```

**2. Port Already in Use**
- Check if port 9411 is free
- Zipkin can run on different port if configured

**3. Memory Issues**
- Micrometer Tracing + Zipkin Reporter minimal overhead
- Typical memory footprint: ~50MB per service

**4. Network/Firewall Issues**
- Ensure services can reach `localhost:9411`
- Check firewall rules

### Graceful Degradation
If Zipkin endpoint is unreachable:
- Services will still start normally
- Traces won't be sent, but no error
- Change log level if connection spam is concerning:
  ```yaml
  logging:
    level:
      io.zipkin: WARN
  ```

## Verifying Traces in Zipkin

1. Start Zipkin: `docker run -d -p 9411:9411 openzipkin/zipkin`
2. Start all services
3. Make a request through API Gateway: `POST http://localhost:8080/auth/login`
4. Open Zipkin UI: `http://localhost:9411`
5. Look for traces with service names (api-gateway, user-service, etc.)

## Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| "Connection refused" in logs | Zipkin not running or wrong port |
| Traces not appearing in Zipkin | Check if `probability: 1.0` is set in all services |
| Missing traceId in logs | Ensure log pattern includes `%X{traceId:-}` |
| High memory usage | Reduce sampling: `probability: 0.1` (10% sampling) |

## Next Steps

1. Verify Zipkin is running
2. Start services (they should auto-configure)
3. Make test requests
4. Check Zipkin UI for distributed traces
5. Correlate logs by traceId if needed

All configuration is complete and services are ready to send traces to Zipkin!

