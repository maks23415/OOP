
import React, { useState, useEffect, useRef, useCallback } from 'react';
import { Point } from '../../../types/function.types';

interface VirtualizedPointsTableProps {
  points: Point[];
  onPointChange: (index: number, field: 'x' | 'y', value: string) => void;
  onAddPoint?: () => void;
  onRemovePoint?: (index: number) => void;
  showIndices?: boolean;
  editable?: boolean;
  title?: string;
  maxVisibleRows?: number;
  rowHeight?: number;
}

const VirtualizedPointsTable: React.FC<VirtualizedPointsTableProps> = ({
  points,
  onPointChange,
  onAddPoint,
  onRemovePoint,
  showIndices = true,
  editable = true,
  title = 'Таблица точек',
  maxVisibleRows = 20,
  rowHeight = 40
}) => {
  const [scrollTop, setScrollTop] = useState(0);
  const [visibleStartIndex, setVisibleStartIndex] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [filteredPoints, setFilteredPoints] = useState<Point[]>(points);
  const [sortConfig, setSortConfig] = useState<{ key: 'x' | 'y'; direction: 'asc' | 'desc' } | null>(null);

  const containerRef = useRef<HTMLDivElement>(null);
  const tableHeight = points.length * rowHeight;

  // Фильтрация точек
  useEffect(() => {
    if (!searchQuery.trim()) {
      setFilteredPoints(points);
      return;
    }

    const query = searchQuery.toLowerCase();
    const filtered = points.filter((point, index) => {
      return (
        point.x.toString().includes(query) ||
        point.y.toString().includes(query) ||
        index.toString().includes(query)
      );
    });

    setFilteredPoints(filtered);
  }, [points, searchQuery]);

  // Сортировка точек
  useEffect(() => {
    if (!sortConfig) return;

    const sorted = [...filteredPoints].sort((a, b) => {
      if (a[sortConfig.key] < b[sortConfig.key]) {
        return sortConfig.direction === 'asc' ? -1 : 1;
      }
      if (a[sortConfig.key] > b[sortConfig.key]) {
        return sortConfig.direction === 'asc' ? 1 : -1;
      }
      return 0;
    });

    setFilteredPoints(sorted);
  }, [sortConfig]);

  // Обработка скролла
  const handleScroll = useCallback((e: React.UIEvent<HTMLDivElement>) => {
    const newScrollTop = e.currentTarget.scrollTop;
    setScrollTop(newScrollTop);
    setVisibleStartIndex(Math.floor(newScrollTop / rowHeight));
  }, [rowHeight]);

  // Получение видимых строк
  const visibleRows = filteredPoints.slice(
    visibleStartIndex,
    visibleStartIndex + maxVisibleRows
  );

  // Переход к определенной строке
  const scrollToRow = (index: number) => {
    if (containerRef.current) {
      containerRef.current.scrollTop = index * rowHeight;
    }
  };

  // Быстрое заполнение
  const handleQuickFill = (pattern: 'linear' | 'quadratic' | 'exponential') => {
    const newPoints = [...points];

    switch (pattern) {
      case 'linear':
        newPoints.forEach((point, i) => {
          onPointChange(i, 'y', i.toString());
        });
        break;
      case 'quadratic':
        newPoints.forEach((point, i) => {
          onPointChange(i, 'y', (i * i).toString());
        });
        break;
      case 'exponential':
        newPoints.forEach((point, i) => {
          onPointChange(i, 'y', Math.pow(2, i).toString());
        });
        break;
    }
  };

  // Проверка на дублирующиеся X
  const hasDuplicateX = () => {
    const xValues = points.map(p => p.x);
    return new Set(xValues).size !== xValues.length;
  };

  // Проверка на сортировку X
  const isSorted = () => {
    for (let i = 1; i < points.length; i++) {
      if (points[i].x < points[i - 1].x) return false;
    }
    return true;
  };

  const duplicateX = hasDuplicateX();
  const sorted = isSorted();

  // Обработчик сортировки
  const handleSort = (key: 'x' | 'y') => {
    setSortConfig(prev => {
      if (prev?.key === key) {
        return {
          key,
          direction: prev.direction === 'asc' ? 'desc' : 'asc'
        };
      }
      return { key, direction: 'asc' };
    });
  };

  return (
    <div className="w-full">
      {/* Панель управления */}
      <div className="mb-4 p-4 bg-gray-50 dark:bg-gray-800 rounded-lg">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div className="flex-1 min-w-[300px]">
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Поиск по точкам:
            </label>
            <div className="flex gap-2">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Поиск по X, Y или индексу..."
                className="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg
                         dark:bg-gray-700 dark:text-white"
              />
              {searchQuery && (
                <button
                  onClick={() => setSearchQuery('')}
                  className="px-3 py-2 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300"
                >
                  ✕
                </button>
              )}
            </div>
          </div>

          <div className="flex gap-2">
            <div className="relative">
              <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
                Быстрое заполнение ▽
              </button>
              <div className="absolute hidden group-hover:block bg-white dark:bg-gray-800 shadow-lg rounded mt-1 z-10">
                <button
                  onClick={() => handleQuickFill('linear')}
                  className="block w-full px-4 py-2 text-left hover:bg-gray-100 dark:hover:bg-gray-700"
                >
                  Линейная функция (y = x)
                </button>
                <button
                  onClick={() => handleQuickFill('quadratic')}
                  className="block w-full px-4 py-2 text-left hover:bg-gray-100 dark:hover:bg-gray-700"
                >
                  Квадратичная функция (y = x²)
                </button>
                <button
                  onClick={() => handleQuickFill('exponential')}
                  className="block w-full px-4 py-2 text-left hover:bg-gray-100 dark:hover:bg-gray-700"
                >
                  Экспоненциальная функция (y = 2ˣ)
                </button>
              </div>
            </div>

            <button
              onClick={() => scrollToRow(0)}
              className="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg
                       text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700"
            >
              В начало
            </button>
          </div>
        </div>

        {/* Статистика */}
        <div className="mt-4 grid grid-cols-2 md:grid-cols-4 gap-3">
          <div className="text-center">
            <div className="text-2xl font-bold text-blue-600 dark:text-blue-400">
              {points.length.toLocaleString()}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-400">Всего точек</div>
          </div>
          <div className="text-center">
            <div className={`text-2xl font-bold ${sorted ? 'text-green-600 dark:text-green-400' : 'text-yellow-600 dark:text-yellow-400'}`}>
              {sorted ? '✓' : '⚠'}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-400">Сортировка X</div>
          </div>
          <div className="text-center">
            <div className={`text-2xl font-bold ${!duplicateX ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'}`}>
              {!duplicateX ? '✓' : '⚠'}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-400">Уникальные X</div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-purple-600 dark:text-purple-400">
              {filteredPoints.length.toLocaleString()}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-400">Найдено</div>
          </div>
        </div>
      </div>

      {/* Виртуализированная таблица */}
      <div className="relative">
        <div
          ref={containerRef}
          onScroll={handleScroll}
          className="border border-gray-300 dark:border-gray-600 rounded-lg overflow-auto"
          style={{ height: maxVisibleRows * rowHeight + 50 }}
        >
          {/* Заголовок таблицы */}
          <div className="sticky top-0 bg-gray-50 dark:bg-gray-800 z-10 border-b border-gray-300 dark:border-gray-600">
            <div className="flex">
              {showIndices && (
                <div className="w-16 px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300">
                  #
                </div>
              )}
              <div
                className="flex-1 px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700"
                onClick={() => handleSort('x')}
              >
                X {sortConfig?.key === 'x' && (sortConfig.direction === 'asc' ? '↑' : '↓')}
              </div>
              <div
                className="flex-1 px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700"
                onClick={() => handleSort('y')}
              >
                Y {sortConfig?.key === 'y' && (sortConfig.direction === 'asc' ? '↑' : '↓')}
              </div>
              {editable && onRemovePoint && (
                <div className="w-24 px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300">
                  Действия
                </div>
              )}
            </div>
          </div>

          {/* Видимые строки */}
          <div style={{ height: tableHeight, position: 'relative' }}>
            {visibleRows.map((point, visibleIndex) => {
              const actualIndex = visibleStartIndex + visibleIndex;
              const isDuplicate = points.findIndex(p => p.x === point.x) !== actualIndex;
              const isOutOfOrder = actualIndex > 0 && point.x < points[actualIndex - 1].x;

              return (
                <div
                  key={actualIndex}
                  className={`absolute left-0 right-0 flex items-center ${
                    isDuplicate
                      ? 'bg-red-50 dark:bg-red-900/20'
                      : isOutOfOrder
                      ? 'bg-yellow-50 dark:bg-yellow-900/20'
                      : 'bg-white dark:bg-gray-900 even:bg-gray-50 dark:even:bg-gray-800'
                  } hover:bg-gray-100 dark:hover:bg-gray-700 border-b border-gray-200 dark:border-gray-700`}
                  style={{
                    top: actualIndex * rowHeight,
                    height: rowHeight
                  }}
                >
                  {showIndices && (
                    <div className="w-16 px-4 py-2 text-sm text-gray-500 dark:text-gray-400 font-mono">
                      {actualIndex}
                    </div>
                  )}

                  <div className="flex-1 px-4 py-2">
                    <input
                      type="text"
                      value={point.x}
                      onChange={(e) => onPointChange(actualIndex, 'x', e.target.value)}
                      disabled={!editable}
                      className={`w-full px-3 py-1 border rounded font-mono text-sm ${
                        isDuplicate || isOutOfOrder
                          ? 'border-red-300 dark:border-red-700'
                          : 'border-gray-300 dark:border-gray-600'
                      } ${editable ? 'dark:bg-gray-800 dark:text-white' : 'bg-gray-100 dark:bg-gray-700'}`}
                    />
                  </div>

                  <div className="flex-1 px-4 py-2">
                    <input
                      type="text"
                      value={point.y}
                      onChange={(e) => onPointChange(actualIndex, 'y', e.target.value)}
                      disabled={!editable}
                      className="w-full px-3 py-1 border border-gray-300 dark:border-gray-600 rounded font-mono text-sm dark:bg-gray-800 dark:text-white"
                    />
                  </div>

                  {editable && onRemovePoint && (
                    <div className="w-24 px-4 py-2">
                      <button
                        onClick={() => onRemovePoint(actualIndex)}
                        className="px-3 py-1 text-sm bg-red-100 dark:bg-red-900 text-red-700 dark:text-red-300
                                 rounded hover:bg-red-200 dark:hover:bg-red-800 transition-colors"
                        disabled={points.length <= 2}
                        title={points.length <= 2 ? "Минимум 2 точки" : "Удалить точку"}
                      >
                        Удалить
                      </button>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>

        {/* Индикатор позиции */}
        <div className="mt-2 flex items-center justify-between text-sm text-gray-600 dark:text-gray-400">
          <div>
            Показано {visibleStartIndex + 1}-{Math.min(visibleStartIndex + maxVisibleRows, filteredPoints.length)}
            из {filteredPoints.length} точек
          </div>
          <div className="flex items-center gap-2">
            <input
              type="range"
              min="0"
              max={Math.max(0, filteredPoints.length - maxVisibleRows)}
              value={visibleStartIndex}
              onChange={(e) => scrollToRow(parseInt(e.target.value))}
              className="w-32"
            />
            <button
              onClick={() => scrollToRow(Math.max(0, visibleStartIndex - maxVisibleRows))}
              className="px-3 py-1 bg-gray-200 dark:bg-gray-700 rounded"
            >
              ↑ Назад
            </button>
            <button
              onClick={() => scrollToRow(Math.min(filteredPoints.length - maxVisibleRows, visibleStartIndex + maxVisibleRows))}
              className="px-3 py-1 bg-gray-200 dark:bg-gray-700 rounded"
            >
              ↓ Вперед
            </button>
          </div>
        </div>
      </div>

      {/* Подсказки для больших наборов данных */}
      {points.length > 1000 && (
        <div className="mt-4 p-4 bg-yellow-50 dark:bg-yellow-900/20 border border-yellow-200 dark:border-yellow-800 rounded-lg">
          <div className="flex items-start gap-3">
            <div className="text-yellow-600 dark:text-yellow-400 text-xl">⚠</div>
            <div>
              <h4 className="font-semibold text-yellow-800 dark:text-yellow-300 mb-1">
                Работа с большим набором данных ({points.length.toLocaleString()} точек)
              </h4>
              <ul className="text-sm text-yellow-700 dark:text-yellow-400 space-y-1">
                <li>• Используйте поиск для быстрого нахождения нужных точек</li>
                <li>• Для массового редактирования используйте "Быстрое заполнение"</li>
                <li>• Рекомендуется сохранить данные и работать с ними в файлах</li>
                <li>• При проблемах с производительностью уменьшите количество точек</li>
              </ul>
            </div>
          </div>
        </div>
      )}

      {/* Экспорт/импорт */}
      <div className="mt-4 flex gap-4">
        <button
          onClick={() => {
            // Экспорт в JSON
            const dataStr = JSON.stringify(points, null, 2);
            const dataUri = 'data:application/json;charset=utf-8,'+ encodeURIComponent(dataStr);
            const link = document.createElement('a');
            link.href = dataUri;
            link.download = `function-points-${Date.now()}.json`;
            link.click();
          }}
          className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 flex items-center gap-2"
        >
          📥 Экспорт в JSON
        </button>

        <button
          onClick={() => {
            // Импорт из JSON
            const input = document.createElement('input');
            input.type = 'file';
            input.accept = '.json';
            input.onchange = (e) => {
              const file = (e.target as HTMLInputElement).files?.[0];
              if (file) {
                const reader = new FileReader();
                reader.onload = (event) => {
                  try {
                    const points = JSON.parse(event.target?.result as string);
                    // Обработка импортированных точек
                    console.log('Импортировано точек:', points.length);
                  } catch (err) {
                    alert('Ошибка при чтении файла');
                  }
                };
                reader.readAsText(file);
              }
            };
            input.click();
          }}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2"
        >
          📤 Импорт из JSON
        </button>
      </div>
    </div>
  );
};

export default VirtualizedPointsTable;