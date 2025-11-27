const newman = require('newman');
const fs = require('fs');
const path = require('path');

// Конфигурация тестов
const config = {
    collection: require('./lab5-manual.postman_collection.json'),
    environment: {
        "id": "lab5-manual-env",
        "name": "Lab5 Manual Environment",
        "values": [
            {
                "key": "baseUrl",
                "value": "http://localhost:8080/lab5",
                "enabled": true
            }
        ]
    },
    iterationCount: 10, // 10 итераций для статистики
    reporters: ['cli', 'json', 'htmlextra'],
    reporter: {
        htmlextra: {
            export: './newman-reports/report.html',
            logs: true,
            showOnlyFails: false
        },
        json: {
            export: './newman-reports/report.json'
        }
    }
};

console.log('🚀 Starting Newman performance tests for Manual API...');
console.log('📊 Running 10 iterations for statistical accuracy...');

newman.run(config, function (err, summary) {
    if (err) {
        throw err;
    }

    console.log('✅ Newman tests completed!');

    // Анализ результатов
    analyzeResults(summary);
});

function analyzeResults(summary) {
    const results = summary.run.executions;
    const performanceData = {};

    // Сбор данных о времени выполнения
    results.forEach(execution => {
        const requestName = execution.item.name;
        const responseTime = execution.response.responseTime;

        if (!performanceData[requestName]) {
            performanceData[requestName] = [];
        }

        performanceData[requestName].push(responseTime);
    });

    // Расчет статистики
    const stats = calculateStatistics(performanceData);

    // Генерация отчета
    generateReport(stats);
    generateMarkdownTable(stats);

    console.log('📈 Performance analysis completed!');
    console.log('📄 Reports generated in ./newman-reports/');
}

function calculateStatistics(performanceData) {
    const stats = {};

    Object.keys(performanceData).forEach(requestName => {
        const times = performanceData[requestName];
        const sortedTimes = times.slice().sort((a, b) => a - b);

        const min = sortedTimes[0];
        const max = sortedTimes[sortedTimes.length - 1];
        const avg = times.reduce((sum, time) => sum + time, 0) / times.length;

        // Медиана
        const mid = Math.floor(sortedTimes.length / 2);
        const median = sortedTimes.length % 2 === 0
            ? (sortedTimes[mid - 1] + sortedTimes[mid]) / 2
            : sortedTimes[mid];

        // Стандартное отклонение
        const squareDiffs = times.map(time => Math.pow(time - avg, 2));
        const avgSquareDiff = squareDiffs.reduce((sum, diff) => sum + diff, 0) / times.length;
        const stdDev = Math.sqrt(avgSquareDiff);

        stats[requestName] = {
            min: Math.round(min),
            max: Math.round(max),
            avg: Math.round(avg),
            median: Math.round(median),
            stdDev: Math.round(stdDev),
            count: times.length
        };
    });

    return stats;
}

function generateReport(stats) {
    const report = {
        timestamp: new Date().toISOString(),
        environment: "Manual API (Tomcat + Servlets)",
        testConfig: {
            iterations: 10,
            collection: "Lab5 Manual API Tests"
        },
        performanceStats: stats
    };

    fs.writeFileSync(
        './newman-reports/performance-analysis.json',
        JSON.stringify(report, null, 2)
    );
}

function generateMarkdownTable(stats) {
    let markdown = `# Manual API Performance Report\n\n`;
    markdown += `**Environment**: Tomcat + Java Servlets  \n`;
    markdown += `**Test Date**: ${new Date().toLocaleString()}  \n`;
    markdown += `**Iterations**: 10  \n\n`;

    markdown += `## Response Time Statistics (ms)\n\n`;
    markdown += `| API Endpoint | Min | Max | Average | Median | Std Dev |\n`;
    markdown += `|-------------|-----|-----|---------|--------|----------|\n`;

    // Группировка по категориям API
    const categories = {
        'Users': [],
        'Functions': [],
        'Points': [],
        'Other': []
    };

    Object.keys(stats).forEach(requestName => {
        const stat = stats[requestName];

        if (requestName.includes('User')) {
            categories.Users.push({ name: requestName, ...stat });
        } else if (requestName.includes('Function')) {
            categories.Functions.push({ name: requestName, ...stat });
        } else if (requestName.includes('Point')) {
            categories.Points.push({ name: requestName, ...stat });
        } else {
            categories.Other.push({ name: requestName, ...stat });
        }
    });

    // Вывод всех запросов
    Object.keys(categories).forEach(category => {
        if (categories[category].length > 0) {
            markdown += `\n### ${category}\n\n`;
            markdown += `| Endpoint | Min | Max | Avg | Median | Std Dev |\n`;
            markdown += `|----------|-----|-----|-----|--------|----------|\n`;

            categories[category].forEach(stat => {
                markdown += `| ${stat.name} | ${stat.min} | ${stat.max} | ${stat.avg} | ${stat.median} | ${stat.stdDev} |\n`;
            });
        }
    });

    // Сводная статистика
    markdown += `\n## Summary\n\n`;
    markdown += `| Metric | Value |\n`;
    markdown += `|--------|-------|\n`;

    const allTimes = Object.values(stats).flatMap(stat =>
        Array(stat.count).fill(stat.avg)
    );
    const totalAvg = allTimes.reduce((sum, time) => sum + time, 0) / allTimes.length;

    markdown += `| Total Requests Tested | ${Object.keys(stats).length} |\n`;
    markdown += `| Average Response Time | ${Math.round(totalAvg)} ms |\n`;
    markdown += `| Fastest Endpoint | ${findFastestEndpoint(stats)} |\n`;
    markdown += `| Slowest Endpoint | ${findSlowestEndpoint(stats)} |\n`;

    fs.writeFileSync('./newman-reports/performance-report.md', markdown);
    console.log('📊 Markdown report generated: newman-reports/performance-report.md');
}

function findFastestEndpoint(stats) {
    let fastest = { name: '', avg: Infinity };
    Object.keys(stats).forEach(name => {
        if (stats[name].avg < fastest.avg) {
            fastest = { name, avg: stats[name].avg };
        }
    });
    return `${fastest.name} (${fastest.avg} ms)`;
}

function findSlowestEndpoint(stats) {
    let slowest = { name: '', avg: 0 };
    Object.keys(stats).forEach(name => {
        if (stats[name].avg > slowest.avg) {
            slowest = { name, avg: stats[name].avg };
        }
    });
    return `${slowest.name} (${slowest.avg} ms)`;
}