# ==============================================
# NEWMAN TESTS RUN SCRIPT
# For laboratory work #6 (framework)
# ==============================================

Clear-Host
Write-Host "=============================================="
Write-Host "   RUNNING NEWMAN TESTS FOR LAB6 FRAMEWORK"
Write-Host "=============================================="
Write-Host ""

# ========== CONFIGURATION ==========
$collectionFile = "collection.json"
$environmentFile = "environment.json"
$resultsDir = "../performance-results"
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"

# Create results directory if not exists
if (-not (Test-Path $resultsDir)) {
    New-Item -ItemType Directory -Path $resultsDir -Force
}

# ========== DEPENDENCIES CHECK ==========
Write-Host "[1/5] Checking dependencies..."

# Check Newman
try {
    $version = newman --version
    Write-Host "    Newman installed: $version"
} catch {
    Write-Host "    Newman not installed!"
    Write-Host "   Install: npm install -g newman"
    exit 1
}

# Check files
if (-not (Test-Path $collectionFile)) {
    Write-Host "   Collection file not found: $collectionFile"
    exit 1
}
Write-Host "    Collection file found"

if (-not (Test-Path $environmentFile)) {
    Write-Host "     Environment file not found"
}

# ========== SERVER CHECK ==========
Write-Host "`n[2/5] Checking server..."
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/users" -Method GET
    Write-Host "    Server is running (status: $($response.StatusCode))"
} catch {
    Write-Host "    Server not available!"
    Write-Host "   Start Spring Boot application"
    exit 1
}

# ========== RUN TESTS ==========
Write-Host "`n[3/5] Running Newman tests..."

$allResults = @()
$csvFile = "$resultsDir/results_$timestamp.csv"

# CSV header
"Number,Test,Method,URL,Time(ms),Status" | Out-File $csvFile -Encoding UTF8

# Run 3 iterations
for ($i = 1; $i -le 3; $i++) {
    Write-Host "`n   Iteration $i of 3..."

    # Run Newman
    $jsonFile = "$resultsDir/run_${i}_$timestamp.json"
    newman run $collectionFile --environment $environmentFile --reporters json --reporter-json-export $jsonFile

    # Read results
    if (Test-Path $jsonFile) {
        $data = Get-Content $jsonFile -Raw | ConvertFrom-Json

        foreach ($test in $data.run.executions) {
            $result = [PSCustomObject]@{
                TestName = $test.item.name
                Method = $test.request.method
                URL = $test.request.url.raw
                Time = $test.response.responseTime
                Status = if ($test.response.code -in @(200, 201)) { "PASS" } else { "FAIL" }
            }

            $allResults += $result
            "$i,$($result.TestName),$($result.Method),$($result.URL),$($result.Time),$($result.Status)" | Out-File $csvFile -Encoding UTF8 -Append
        }
    }

    Write-Host "     Iteration $i completed"
}

# ========== ANALYZE RESULTS ==========
Write-Host "`n[4/5] Analyzing results..."

if ($allResults.Count -eq 0) {
    Write-Host "    No data"
    exit 1
}

# Group by URL
$summary = $allResults | Group-Object URL | ForEach-Object {
    $url = $_.Name
    $method = ($_.Group[0].Method)
    $times = $_.Group.Time | ForEach-Object { [double]$_ }

    $avg = [math]::Round(($times | Measure-Object -Average).Average, 1)
    $min = [math]::Round(($times | Measure-Object -Minimum).Minimum, 1)
    $max = [math]::Round(($times | Measure-Object -Maximum).Maximum, 1)

    [PSCustomObject]@{
        URL = $url.Replace("http://localhost:8080", "")
        Method = $method
        Avg = $avg
        Min = $min
        Max = $max
    }
}

Write-Host "`n" + ("="*80)
Write-Host " RESULTS"
Write-Host ("="*80)
$summary | Format-Table URL, Method, Avg, Min, Max -AutoSize

# ========== CREATE TABLE ==========
Write-Host "`n[5/5] Creating table..."

$table = @"
# API Performance Test Results - Laboratory Work #6

**Date:** $(Get-Date -Format "dd.MM.yyyy HH:mm")
**Technology:** Spring Boot
**Iterations:** 3

## Request Execution Speed Table

| # | Method | URL | Average Time (ms) |
|---|--------|-----|------------------|
"@

$num = 1
foreach ($item in $summary) {
    $table += "| $num | $($item.Method) | $($item.URL) | $($item.Avg) |`n"
    $num++
}

$table += @"

## Statistics
- Total tests: $($allResults.Count)
- Average time: $([math]::Round(($allResults.Time | Measure-Object -Average).Average, 1)) ms
- Min time: $([math]::Round(($allResults.Time | Measure-Object -Minimum).Minimum, 1)) ms
- Max time: $([math]::Round(($allResults.Time | Measure-Object -Maximum).Maximum, 1)) ms

## Conclusion
Testing completed successfully.
"@

$table | Out-File "../docs/sorting_performance.md" -Encoding UTF8
Write-Host "   Table created: docs/sorting_performance.md"

Write-Host "`n" + ("="*80)
Write-Host " COMPLETED"
Write-Host ("="*80)