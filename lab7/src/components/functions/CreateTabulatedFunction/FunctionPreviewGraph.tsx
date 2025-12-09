
import React, { useEffect, useRef } from 'react';
import { Chart, registerables } from 'chart.js';
import zoomPlugin from 'chartjs-plugin-zoom';
import { Point } from '../../../types/function.types';
import { LoadingOverlay } from '../../common/LoadingOverlay';

Chart.register(...registerables, zoomPlugin);

interface FunctionPreviewGraphProps {
  points: Point[];
  title?: string;
  width?: number;
  height?: number;
  showControls?: boolean;
  isLoading?: boolean;
}

const FunctionPreviewGraph: React.FC<FunctionPreviewGraphProps> = ({
  points,
  title = 'График функции',
  width = 800,
  height = 400,
  showControls = true,
  isLoading = false
}) => {
  const chartRef = useRef<HTMLCanvasElement>(null);
  const chartInstance = useRef<Chart | null>(null);

  // Инициализация графика
  useEffect(() => {
    if (!chartRef.current || points.length === 0) return;

    const ctx = chartRef.current.getContext('2d');
    if (!ctx) return;

    // Уничтожаем предыдущий график
    if (chartInstance.current) {
      chartInstance.current.destroy();
    }

    // Подготавливаем данные
    const sortedPoints = [...points].sort((a, b) => a.x - b.x);
    const xValues = sortedPoints.map(p => p.x);
    const yValues = sortedPoints.map(p => p.y);

    // Определяем границы для осей
    const xMin = Math.min(...xValues);
    const xMax = Math.max(...xValues);
    const yMin = Math.min(...yValues);
    const yMax = Math.max(...yValues);

    // Добавляем отступы
    const xPadding = (xMax - xMin) * 0.1;
    const yPadding = (yMax - yMin) * 0.1;

    chartInstance.current = new Chart(ctx, {
      type: 'line',
      data: {
        labels: xValues.map(x => x.toFixed(2)),
        datasets: [
          {
            label: 'f(x)',
            data: yValues,
            borderColor: 'rgb(59, 130, 246)',
            backgroundColor: 'rgba(59, 130, 246, 0.1)',
            borderWidth: 2,
            tension: 0.1,
            fill: true,
            pointRadius: points.length > 100 ? 0 : 2,
            pointBackgroundColor: 'rgb(59, 130, 246)',
            pointBorderColor: 'rgb(255, 255, 255)',
            pointBorderWidth: 1,
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          title: {
            display: true,
            text: title,
            font: {
              size: 16,
              weight: 'bold'
            },
            color: '#4B5563'
          },
          legend: {
            display: false
          },
          tooltip: {
            mode: 'index',
            intersect: false,
            callbacks: {
              label: (context) => {
                const point = sortedPoints[context.dataIndex];
                return `x = ${point.x.toFixed(4)}, y = ${point.y.toFixed(4)}`;
              }
            }
          },
          zoom: {
            zoom: {
              wheel: {
                enabled: true,
              },
              pinch: {
                enabled: true
              },
              mode: 'xy',
            },
            pan: {
              enabled: true,
              mode: 'xy',
            },
            limits: {
              x: { min: xMin - xPadding, max: xMax + xPadding },
              y: { min: yMin - yPadding, max: yMax + yPadding }
            }
          }
        },
        scales: {
          x: {
            title: {
              display: true,
              text: 'X',
              font: {
                size: 14,
                weight: 'bold'
              }
            },
            grid: {
              color: 'rgba(0, 0, 0, 0.1)'
            },
            min: xMin - xPadding,
            max: xMax + xPadding
          },
          y: {
            title: {
              display: true,
              text: 'Y = f(X)',
              font: {
                size: 14,
                weight: 'bold'
              }
            },
            grid: {
              color: 'rgba(0, 0, 0, 0.1)'
            },
            min: yMin - yPadding,
            max: yMax + yPadding
          }
        },
        interaction: {
          intersect: false,
          mode: 'nearest'
        },
        animation: {
          duration: 300
        }
      }
    });

    return () => {
      if (chartInstance.current) {
        chartInstance.current.destroy();
      }
    };
  }, [points, title]);

  // Функции управления графиком
  const handleResetZoom = () => {
    if (chartInstance.current) {
      chartInstance.current.resetZoom();
    }
  };

  const handleZoomIn = () => {
    if (chartInstance.current) {
      chartInstance.current.zoom(1.1);
    }
  };

  const handleZoomOut = () => {
    if (chartInstance.current) {
      chartInstance.current.zoom(0.9);
    }
  };

  const handleDownload = () => {
    if (!chartRef.current) return;

    const link = document.createElement('a');
    link.download = `graph-${Date.now()}.png`;
    link.href = chartRef.current.toDataURL('image/png');
    link.click();
  };

  if (points.length === 0) {
    return (
      <div className="relative border border-gray-300 dark:border-gray-600 rounded-lg bg-gray-50 dark:bg-gray-900"
           style={{ width, height }}>
        <div className="absolute inset-0 flex items-center justify-center">
          <div className="text-center">
            <div className="text-gray-400 text-4xl mb-2">📈</div>
            <p className="text-gray-500 dark:text-gray-400">Нет данных для отображения графика</p>
            <p className="text-sm text-gray-400 dark:text-gray-500 mt-1">
              Выберите функцию и параметры для генерации графика
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="relative border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-800 p-4">
      <LoadingOverlay
        isLoading={isLoading}
        message="Генерация графика..."
        transparent={true}
      />

      {/* Контейнер графика */}
      <div style={{ width: '100%', height }}>
        <canvas ref={chartRef} width={width} height={height} />
      </div>

      {/* Панель управления графиком */}
      {showControls && (
        <div className="mt-4 flex flex-wrap items-center justify-between gap-2">
          <div className="flex gap-2">
            <button
              onClick={handleZoomIn}
              className="px-3 py-1 text-sm bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300
                       rounded hover:bg-gray-300 dark:hover:bg-gray-600 flex items-center gap-1"
              title="Увеличить масштаб"
            >
              <span>+</span>
              <span>Приблизить</span>
            </button>
            <button
              onClick={handleZoomOut}
              className="px-3 py-1 text-sm bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300
                       rounded hover:bg-gray-300 dark:hover:bg-gray-600 flex items-center gap-1"
              title="Уменьшить масштаб"
            >
              <span>−</span>
              <span>Отдалить</span>
            </button>
            <button
              onClick={handleResetZoom}
              className="px-3 py-1 text-sm bg-blue-600 text-white rounded hover:bg-blue-700"
              title="Сбросить масштаб"
            >
              Сбросить масштаб
            </button>
          </div>

          <div className="flex gap-2">
            <button
              onClick={handleDownload}
              className="px-3 py-1 text-sm bg-green-600 text-white rounded hover:bg-green-700
                       flex items-center gap-1"
            >
              <span>📥</span>
              Сохранить график
            </button>
          </div>
        </div>
      )}

      {/* Статистика графика */}
      <div className="mt-4 grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
        <div className="bg-gray-50 dark:bg-gray-900 p-3 rounded">
          <div className="text-gray-500 dark:text-gray-400">Количество точек</div>
          <div className="font-semibold text-gray-800 dark:text-white">{points.length}</div>
        </div>
        <div className="bg-gray-50 dark:bg-gray-900 p-3 rounded">
          <div className="text-gray-500 dark:text-gray-400">Диапазон X</div>
          <div className="font-semibold text-gray-800 dark:text-white">
            [{Math.min(...points.map(p => p.x)).toFixed(2)}, {Math.max(...points.map(p => p.x)).toFixed(2)}]
          </div>
        </div>
        <div className="bg-gray-50 dark:bg-gray-900 p-3 rounded">
          <div className="text-gray-500 dark:text-gray-400">Диапазон Y</div>
          <div className="font-semibold text-gray-800 dark:text-white">
            [{Math.min(...points.map(p => p.y)).toFixed(2)}, {Math.max(...points.map(p => p.y)).toFixed(2)}]
          </div>
        </div>
        <div className="bg-gray-50 dark:bg-gray-900 p-3 rounded">
          <div className="text-gray-500 dark:text-gray-400">Среднее значение</div>
          <div className="font-semibold text-gray-800 dark:text-white">
            {(points.reduce((sum, p) => sum + p.y, 0) / points.length).toFixed(4)}
          </div>
        </div>
      </div>

      {/* Подсказки */}
      <div className="mt-4 p-3 bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded">
        <p className="text-sm text-blue-700 dark:text-blue-300">
          <strong>Управление графиком:</strong> Используйте колесо мыши для масштабирования,
          перетаскивание для перемещения. Для сенсорных устройств - жесты смахивания и pinch-to-zoom.
        </p>
      </div>
    </div>
  );
};

export default FunctionPreviewGraph;