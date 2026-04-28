# DigiCart Sample Data Setup Script
# Runs all SQL scripts to populate databases with test data
# Requires: Docker MySQL container running as 'advanced-project-mysql-1'

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  DigiCart Sample Data Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if MySQL container is running
Write-Host "Checking MySQL container status..." -ForegroundColor Yellow
$containerStatus = docker ps --filter "name=advanced-project-mysql-1" --format "{{.Status}}"

if ([string]::IsNullOrEmpty($containerStatus)) {
    Write-Host "ERROR: MySQL container 'advanced-project-mysql-1' is not running!" -ForegroundColor Red
    Write-Host "Please start the container with: docker-compose up -d" -ForegroundColor Yellow
    exit 1
}

Write-Host "✓ MySQL container is running" -ForegroundColor Green
Write-Host ""

# Arrays for SQL files and their descriptions
$sqlScripts = @(
    @{ file = "sql/user_service_init.sql"; name = "User Service"; db = "user_service_db" },
    @{ file = "sql/product_service_init.sql"; name = "Product Service"; db = "product_service_db" },
    @{ file = "sql/price_service_init.sql"; name = "Price Service"; db = "price_service_db" },
    @{ file = "sql/stock_service_init.sql"; name = "Stock Service"; db = "stock_service_db" },
    @{ file = "sql/address_service_init.sql"; name = "Address Service"; db = "address_service_db" },
    @{ file = "sql/cart_service_init.sql"; name = "Cart Service"; db = "cart_service_db" },
    @{ file = "sql/order_service_init.sql"; name = "Order Service"; db = "order_service_db" }
)

$successCount = 0
$failureCount = 0

# Execute each SQL script
foreach ($script in $sqlScripts) {
    Write-Host "Processing: $($script.name)..." -ForegroundColor Yellow
    
    if (Test-Path $script.file) {
        try {
            $sqlContent = Get-Content $script.file -Raw
            $sqlContent | docker exec -i advanced-project-mysql-1 mysql -u root -proot 2>&1 | Out-Null
            
            Write-Host "  ✓ $($script.name) data inserted successfully" -ForegroundColor Green
            $successCount++
        }
        catch {
            Write-Host "  ✗ Exception: $_" -ForegroundColor Red
            $failureCount++
        }
    }
    else {
        Write-Host "  ✗ File not found: $($script.file)" -ForegroundColor Red
        $failureCount++
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Setup Summary" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Successfully processed: $successCount" -ForegroundColor Green
Write-Host "Failed: $failureCount" -ForegroundColor $(if ($failureCount -eq 0) { "Green" } else { "Red" })
Write-Host ""

if ($failureCount -eq 0) {
    Write-Host "✓ All sample data inserted successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "📊 Data Summary:" -ForegroundColor Cyan
    Write-Host "  - 3 User accounts (Admin + 2 Customers)" -ForegroundColor White
    Write-Host "  - 5 Products with features" -ForegroundColor White
    Write-Host "  - 5 Price entries" -ForegroundColor White
    Write-Host "  - 5 Stock entries (75-300 units)" -ForegroundColor White
    Write-Host "  - 3 Addresses" -ForegroundColor White
    Write-Host "  - 2 Shopping carts with items" -ForegroundColor White
    Write-Host "  - 2 Orders with items" -ForegroundColor White
    Write-Host ""
    Write-Host "🚀 Ready for testing with Postman collection!" -ForegroundColor Green
    Write-Host "   Import: postman_environment.json" -ForegroundColor Yellow
    Write-Host "   Then: DigiCart_Postman_Collection.json" -ForegroundColor Yellow
} else {
    Write-Host "Please check the errors above and try again." -ForegroundColor Yellow
}

Write-Host ""
Read-Host "Press Enter to exit"
