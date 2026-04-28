# Product Service

Spring Boot service for product catalog entries stored in MySQL.

## Product Fields
- `code` (string, unique)
- `description` (string)
- `features` (`Map<String, String>`)
- `priceRow` (fetched from `price-service` using `code`)
  - `productCode`
  - `price`
  - `unit`

## Endpoints
- `GET /products` -> getAllProducts (includes `priceRow`)
- `GET /products/{code}` -> getProductByID (includes `priceRow`)
- `POST /products` -> add product
- `PUT /products` -> update product (same upsert flow as add)
- `DELETE /products/{code}` -> remove product

## Local Database
Configured in `src/main/resources/application.yaml`:
- URL: `jdbc:mysql://localhost:3306/product_service_db`
- User: `root`
- Password: `root`

## Price Service Integration
Configured in `src/main/resources/application.yaml`:
- `price-service.base-url: http://localhost:8084`

## Quick Start
```powershell
# create db in running mysql-container
Docker exec mysql-container mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS product_service_db;"

# run tests
.\mvnw.cmd test

# run service
.\mvnw.cmd spring-boot:run
```
