# Price Service

Spring Boot service for storing product prices in MySQL.

## PriceRow Fields
- `productCode` (string, unique)
- `price` (`Double`)
- `unit` (string)

## Endpoints
- `GET /prices` -> list all price rows
- `GET /prices/{productCode}` -> get one price row
- `GET /prices/by-product-codes?codes=P1&codes=P2` -> batch lookup for product-service
- `POST /prices` -> add price row
- `PUT /prices` -> update price row (same upsert flow as add)
- `DELETE /prices/{productCode}` -> remove price row

## Local Database
Configured in `src/main/resources/application.yaml`:
- URL: `jdbc:mysql://localhost:3306/price_service_db`
- User: `root`
- Password: `root`

## Quick Start
```powershell
# create db in running mysql-container
Docker exec mysql-container mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS price_service_db;"

# run tests
.\mvnw.cmd test

# run service
.\mvnw.cmd spring-boot:run
```

