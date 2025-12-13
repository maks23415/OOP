@echo off
echo ========================================
echo   Запуск Lab6 Manual на Tomcat
echo ========================================

REM Сборка проекта
echo Сборка WAR файла...
call mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo Ошибка при сборке проекта!
    pause
    exit /b 1
)

REM Поиск Tomcat
set TOMCAT_DIR=C:\Users\maks4\Desktop\tomact\jakartaee-migration-1.0.10-bin\jakartaee-migration-1.0.10

if not exist "%TOMCAT_DIR%" (
    echo Tomcat не найден по пути: %TOMCAT_DIR%
    echo Скачайте Apache Tomcat с https://tomcat.apache.org/
    pause
    exit /b 1
)

REM Копирование WAR файла
echo Копирование WAR файла в Tomcat...
copy target\lab5-manual.war "%TOMCAT_DIR%\webapps\"

if %ERRORLEVEL% NEQ 0 (
    echo Ошибка при копировании WAR файла!
    pause
    exit /b 1
)

echo ========================================
echo   Проект успешно развернут!
echo   Откройте: http://localhost:8080/lab5-manual
echo ========================================
pause