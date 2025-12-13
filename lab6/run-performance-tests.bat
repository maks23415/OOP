@echo off
echo ========================================
echo   Lab5 Manual API Performance Tests
echo ========================================

echo Checking if Tomcat is running...
curl -s http://localhost:8080/lab5/ > nul
if %errorlevel% neq 0 (
    echo ❌ Tomcat is not running on http://localhost:8080/lab5/
    echo Please start the application with: mvn tomcat7:run
    pause
    exit /b 1
)

echo ✅ Tomcat is running
echo Installing Newman dependencies...
npm install

echo Running performance tests...
node run-newman-tests.js

if %errorlevel% equ 0 (
    echo ✅ Performance tests completed successfully!
    echo 📊 Reports generated in ./newman-reports/
) else (
    echo ❌ Performance tests failed!
)

pause