@echo off
REM DigiCart Sample Data Setup Script (Batch version)
REM Runs all SQL scripts to populate databases with test data
REM Requires: Docker MySQL container running as 'advanced-project-mysql-1'

echo.
echo ========================================
echo   DigiCart Sample Data Setup
echo ========================================
echo.

REM Check if MySQL container is running
echo Checking MySQL container status...
docker ps --filter "name=advanced-project-mysql-1" --format "table {{.Names}}" | findstr "advanced-project-mysql-1" >nul

if errorlevel 1 (
    echo ERROR: MySQL container 'advanced-project-mysql-1' is not running!
    echo Please start the container with: docker-compose up -d
    pause
    exit /b 1
)

echo [OK] MySQL container is running
echo.

setlocal enabledelayedexpansion
set successCount=0
set failureCount=0

REM Run each SQL script
echo Processing User Service...
if exist "sql\user_service_init.sql" (
    type sql\user_service_init.sql | docker exec -i advanced-project-mysql-1 mysql -u root -proot
    echo   [OK] User Service data inserted
    set /a successCount=!successCount!+1
) else (
    echo   [ERROR] File not found: sql\user_service_init.sql
    set /a failureCount=!failureCount!+1
)

echo Processing Product Service...
if exist "sql\product_service_init.sql" (
    type sql\product_service_init.sql | docker exec -i advanced-project-mysql-1 mysql -u root -proot
    echo   [OK] Product Service data inserted
    set /a successCount=!successCount!+1
) else (
    echo   [ERROR] File not found
    set /a failureCount=!failureCount!+1
)

echo Processing Price Service...
if exist "sql\price_service_init.sql" (
    type sql\price_service_init.sql | docker exec -i advanced-project-mysql-1 mysql -u root -proot
    echo   [OK] Price Service data inserted
    set /a successCount=!successCount!+1
) else (
    echo   [ERROR] File not found
    set /a failureCount=!failureCount!+1
)

echo Processing Stock Service...
if exist "sql\stock_service_init.sql" (
    type sql\stock_service_init.sql | docker exec -i advanced-project-mysql-1 mysql -u root -proot
    echo   [OK] Stock Service data inserted
    set /a successCount=!successCount!+1
) else (
    echo   [ERROR] File not found
    set /a failureCount=!failureCount!+1
)

echo Processing Address Service...
if exist "sql\address_service_init.sql" (
    type sql\address_service_init.sql | docker exec -i advanced-project-mysql-1 mysql -u root -proot
    echo   [OK] Address Service data inserted
    set /a successCount=!successCount!+1
) else (
    echo   [ERROR] File not found
    set /a failureCount=!failureCount!+1
)

echo Processing Cart Service...
if exist "sql\cart_service_init.sql" (
    type sql\cart_service_init.sql | docker exec -i advanced-project-mysql-1 mysql -u root -proot
    echo   [OK] Cart Service data inserted
    set /a successCount=!successCount!+1
) else (
    echo   [ERROR] File not found
    set /a failureCount=!failureCount!+1
)

echo Processing Order Service...
if exist "sql\order_service_init.sql" (
    type sql\order_service_init.sql | docker exec -i advanced-project-mysql-1 mysql -u root -proot
    echo   [OK] Order Service data inserted
    set /a successCount=!successCount!+1
) else (
    echo   [ERROR] File not found
    set /a failureCount=!failureCount!+1
)

echo.
echo ========================================
echo   Setup Summary
echo ========================================
echo Successfully processed: %successCount%
echo Failed: %failureCount%
echo.

if %failureCount% equ 0 (
    echo [SUCCESS] All sample data inserted successfully!
    echo.
    echo Data Summary:
    echo   - 3 User accounts (Admin + 2 Customers^)
    echo   - 5 Products with features
    echo   - 5 Price entries
    echo   - 5 Stock entries ^(75-300 units^)
    echo   - 3 Addresses
    echo   - 2 Shopping carts with items
    echo   - 2 Orders with items
    echo.
    echo Ready for testing with Postman collection!
    echo   Import: postman_environment.json
    echo   Then: DigiCart_Postman_Collection.json
) else (
    echo [ERROR] Some scripts failed. Check the errors above.
)

echo.
pause