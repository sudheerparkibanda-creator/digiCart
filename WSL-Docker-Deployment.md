# WSL Docker Deployment for DigiCart

## Overview
These steps assume you are running in WSL and that Docker Desktop / Docker Engine is accessible from WSL.
The repository uses `docker-compose.yml` to launch the full DigiCart stack, including MySQL, Kafka, Zipkin, config server, Eureka, and all microservices.

## Start the stack
1. Open WSL and go to the repository root:
   ```bash
   cd /mnt/advancedProject
   ```

2. Confirm Docker is available:
   ```bash
   docker version
   docker compose version
   ```

3. Start the full stack and build images:
   ```bash
   docker compose up -d --build
   ```

4. Confirm services are running:
   ```bash
   docker compose ps
   docker compose logs -f
   ```

## Important service URLs
- API Gateway: `http://localhost:8080`
- Eureka: `http://localhost:8761`
- Config Server: `http://localhost:8888`
- Zipkin: `http://localhost:9411`

## SQL sample data import
If you have a single SQL dump file containing all databases, import it into the MySQL container after MySQL is healthy.

1. Find the MySQL container ID:
   ```bash
   MYSQL_CONTAINER=$(docker compose ps -q mysql)
   ```

2. Import the SQL dump file:
   ```bash
   docker exec -i "$MYSQL_CONTAINER" mysql -uroot -proot < /mnt/advancedProject/dumpDigiCart-sql-dump.sql
   ```

Replace `/mnt/advancedProject/dumpDigiCart-sql-dump.sql` with the actual path to your single SQL file, for example:
```bash
docker exec -i "$MYSQL_CONTAINER" mysql -uroot -proot < sql/dump.sql
```

## Notes
- The Compose file exposes MySQL on port `3306` and uses root password `root`.
- The application services use individual database names such as `user_service_db`, `cart_service_db`, `order_service_db`, etc.
- Ensure the dump file includes all required schemas and data for those databases.

## Troubleshooting
- If MySQL is not ready, wait and retry the import.
- If Docker Compose fails due to network or build errors, inspect `docker compose logs` and fix missing dependencies or port conflicts.
