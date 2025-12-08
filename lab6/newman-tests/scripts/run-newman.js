const newman = require('newman');
const fs = require('fs');
const path = require('path');

console.log('============================================');
console.log('NEWMAN TESTS FOR SPRING BOOT PERFORMANCE');
console.log('============================================');

// Пути к файлам
const collectionPath = path.join(__dirname, '../collections/springboot-api-tests.json');
const environmentPath = path.join(__dirname, '../environments/springboot-local.json');
const resultsDir = path.join(__dirname, '../../test-results');
const timestamp = new Date().toISOString().replace(/[:.]/g, '-');

// Проверяем существование файлов
if (!fs.existsSync(collectionPath)) {
    console.error('Файл коллекции не найден:', collectionPath);
    process.exit(1);
}

console.log('Запуск тестов производительности...');
console.log('Технология: Spring Boot Framework');
console.log('Base URL: http://localhost:8080/api/v1');
console.log('Итераций: 10 для статистики');
console.log('Результаты в: ' + resultsDir);

// Создаем папку для результатов
if (!fs.existsSync(resultsDir)) {
    fs.mkdirSync(resultsDir, { recursive: true });
}

// Загружаем окружение если существует
let environment;
if (fs.existsSync(environmentPath)) {
    try {
        environment = require(environmentPath);
        console.log('Используется окружение: Spring Boot Local Environment');
    } catch (err) {
        console.warn('Ошибка загрузки окружения:', err.message);
        environment = null;
    }
} else {
    console.warn('Файл окружения не найден, используются переменные по умолчанию');
}

// Конфигурация Newman
const newmanConfig = {
    collection: require(collectionPath),
    iterationCount: 10,
    reporters: ['cli', 'json'],
    reporter: {
        htmlextra: {
            export: path.join(resultsDir, `springboot-report-${timestamp}.html`),
            title: "Spring Boot API Performance Tests",
            logs: true
        },
        json: {
            export: path.join(resultsDir, `springboot-raw-${timestamp}.json`)
        }
    },
    timeout: {
        request: 30000
    }
};

// Добавляем окружение если есть
if (environment) {
    newmanConfig.environment = environment;
}

newman.run(newmanConfig, function (err, summary) {
    if (err) {
        console.error('Ошибка Newman:', err);
        throw err;
    }

    console.log('\nТесты Newman завершены!');
    console.log('Статистика: ' + summary.run.stats.items.total + ' запросов, ' + summary.run.stats.items.failed + ' ошибок');

    // Анализ результатов
    analyzePerformance(summary, timestamp);
});

function analyzePerformance(summary, timestamp) {
    const executions = summary.run.executions;
    const stats = {};

    // Собираем статистику по каждому запросу
    executions.forEach(exec => {
        const requestName = exec.item.name;
        const responseTime = exec.response.responseTime;
        const method = exec.request.method;
        const url = exec.request.url.path.join('/');

        const key = method + ' ' + url;

        if (!stats[key]) {
            stats[key] = {
                name: requestName,
                method: method,
                url: url,
                times: [],
                statusCodes: []
            };
        }

        stats[key].times.push(responseTime);
        stats[key].statusCodes.push(exec.response.code);
    });

    // Рассчитываем статистику
    const performanceStats = {};
    Object.keys(stats).forEach(key => {
        const data = stats[key];
        const times = data.times;

        const min = Math.min(...times);
        const max = Math.max(...times);
        const avg = times.reduce((a, b) => a + b, 0) / times.length;
        const median = times.sort((a, b) => a - b)[Math.floor(times.length / 2)];

        // Стандартное отклонение
        const squareDiffs = times.map(time => Math.pow(time - avg, 2));
        const avgSquareDiff = squareDiffs.reduce((a, b) => a + b, 0) / times.length;
        const stdDev = Math.sqrt(avgSquareDiff);

        performanceStats[key] = {
            request: data.name,
            method: data.method,
            endpoint: '/' + data.url,
            iterations: times.length,
            min: Math.round(min),
            max: Math.round(max),
            average: Math.round(avg),
            median: Math.round(median),
            stdDev: Math.round(stdDev)
        };
    });

    // Генерируем отчеты
    generateCSVReport(performanceStats, timestamp);
    generateMarkdownTable(performanceStats, timestamp);

    console.log('\nАНАЛИЗ ПРОИЗВОДИТЕЛЬНОСТИ:');
    console.log('='.repeat(80));

    // Выводим таблицу в консоль
    console.log('\n| Метод | Эндпоинт | Ср. время (мс) | Мин | Макс | Итераций |');
    console.log('|-------|----------|----------------|-----|------|----------|');

    Object.values(performanceStats).forEach(stat => {
        console.log('| ' + stat.method + ' | ' + stat.endpoint + ' | ' + stat.average + ' | ' + stat.min + ' | ' + stat.max + ' | ' + stat.iterations + ' |');
    });

    console.log('\nОтчеты сохранены в: ' + resultsDir);
}

function generateCSVReport(stats, timestamp) {
    const csvPath = path.join(resultsDir, 'springboot-performance-' + timestamp + '.csv');
    let csv = 'Method,Endpoint,Request,Iterations,Avg(ms),Min(ms),Max(ms),Median(ms),StdDev\n';

    Object.values(stats).forEach(stat => {
        csv += stat.method + ',' + stat.endpoint + ',' + stat.request + ',' + stat.iterations + ',' + stat.average + ',' + stat.min + ',' + stat.max + ',' + stat.median + ',' + stat.stdDev + '\n';
    });

    fs.writeFileSync(csvPath, csv);
    console.log('   CSV отчет: ' + csvPath);
}

function generateMarkdownTable(stats, timestamp) {
    const mdPath = path.join(resultsDir, 'springboot-performance-' + timestamp + '.md');
    const date = new Date().toLocaleString('ru-RU');

    let markdown = '# Результаты тестирования производительности Spring Boot API\n\n';
    markdown += '**Дата тестирования:** ' + date + '  \n';
    markdown += '**Технология:** Spring Boot Framework  \n';
    markdown += '**Базовый URL:** http://localhost:8080/api/v1  \n';
    markdown += '**Итераций на запрос:** 10  \n\n';

    markdown += '## Таблица скорости выполнения запросов (время в мс)\n\n';
    markdown += '| № | Метод | Эндпоинт | Запрос | Ср. время | Мин | Макс | Медиана |\n';
    markdown += '|---|-------|----------|--------|-----------|-----|------|---------|\n';

    let counter = 1;
    Object.values(stats).forEach(stat => {
        markdown += '| ' + counter + ' | ' + stat.method + ' | ' + stat.endpoint + ' | ' + stat.request + ' | ' + stat.average + ' | ' + stat.min + ' | ' + stat.max + ' | ' + stat.median + ' |\n';
        counter++;
    });

    fs.writeFileSync(mdPath, markdown);
    console.log('   Markdown таблица: ' + mdPath);
}