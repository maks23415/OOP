import React from 'react';
import { Point } from '../../../types/function.types';

interface PointsTableProps {
  points: Point[];
  onPointChange: (index: number, field: 'x' | 'y', value: string) => void;
  onAddPoint?: () => void;
  onRemovePoint?: (index: number) => void;
  showIndices?: boolean;
  editable?: boolean;
  title?: string;
  maxHeight?: string;
}

const PointsTable: React.FC<PointsTableProps> = ({
  points,
  onPointChange,
  onAddPoint,
  onRemovePoint,
  showIndices = true,
  editable = true,
  title = 'Таблица точек',
  maxHeight = '300px'
}) => {
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

  return (
    <div className="w-full">
      <div className="flex justify-between items-center mb-4">
        <h3 className="text-lg font-semibold text-gray-800 dark:text-white">
          {title}
        </h3>
        <div className="flex items-center gap-2 text-sm">
          <span className={`px-2 py-1 rounded ${sorted ? 'bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200' : 'bg-yellow-100 dark:bg-yellow-900 text-yellow-800 dark:text-yellow-200'}`}>
            {sorted ? '✓ Сортированы' : '⚠ Не сортированы'}
          </span>
          <span className={`px-2 py-1 rounded ${!duplicateX ? 'bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200' : 'bg-red-100 dark:bg-red-900 text-red-800 dark:text-red-200'}`}>
            {!duplicateX ? '✓ Уникальные X' : '⚠ Дубликаты X'}
          </span>
        </div>
      </div>

      <div
        className="overflow-x-auto border border-gray-200 dark:border-gray-700 rounded-lg"
        style={{ maxHeight }}
      >
        <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
          <thead className="bg-gray-50 dark:bg-gray-800 sticky top-0">
            <tr>
              {showIndices && (
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider w-16">
                  #
                </th>
              )}
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                X <span className="text-red-500">*</span>
              </th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                Y <span className="text-red-500">*</span>
              </th>
              {editable && onRemovePoint && (
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider w-20">
                  Действия
                </th>
              )}
            </tr>
          </thead>
          <tbody className="bg-white dark:bg-gray-900 divide-y divide-gray-200 dark:divide-gray-800">
            {points.length === 0 ? (
              <tr>
                <td
                  colSpan={showIndices ? 4 : 3}
                  className="px-4 py-8 text-center text-gray-500 dark:text-gray-400"
                >
                  Нет данных для отображения
                </td>
              </tr>
            ) : (
              points.map((point, index) => {
                // Подсветка проблемных строк
                const isDuplicate = points.findIndex(p => p.x === point.x) !== index;
                const isOutOfOrder = index > 0 && point.x < points[index - 1].x;

                let rowClass = 'hover:bg-gray-50 dark:hover:bg-gray-800';
                if (isDuplicate) {
                  rowClass = 'bg-red-50 dark:bg-red-900/20 hover:bg-red-100 dark:hover:bg-red-900/30';
                } else if (isOutOfOrder) {
                  rowClass = 'bg-yellow-50 dark:bg-yellow-900/20 hover:bg-yellow-100 dark:hover:bg-yellow-900/30';
                }

                return (
                  <tr key={index} className={rowClass}>
                    {showIndices && (
                      <td className="px-4 py-3 text-sm text-gray-500 dark:text-gray-400 font-mono">
                        {index}
                      </td>
                    )}
                    <td className="px-4 py-3">
                      <input
                        type="text"
                        value={point.x}
                        onChange={(e) => onPointChange(index, 'x', e.target.value)}
                        disabled={!editable}
                        className={`w-full px-3 py-1 border rounded font-mono text-sm ${
                          isDuplicate || isOutOfOrder
                            ? 'border-red-300 dark:border-red-700 bg-red-50 dark:bg-red-900/30'
                            : 'border-gray-300 dark:border-gray-600'
                        } ${
                          editable
                            ? 'dark:bg-gray-800 dark:text-white focus:ring-2 focus:ring-blue-500'
                            : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 cursor-not-allowed'
                        }`}
                        aria-label={`Точка ${index}, значение X`}
                      />
                      {(isDuplicate || isOutOfOrder) && (
                        <div className="text-xs mt-1">
                          {isDuplicate && (
                            <span className="text-red-600 dark:text-red-400">Дубликат</span>
                          )}
                          {isOutOfOrder && (
                            <span className="text-yellow-600 dark:text-yellow-400">Нарушен порядок</span>
                          )}
                        </div>
                      )}
                    </td>
                    <td className="px-4 py-3">
                      <input
                        type="text"
                        value={point.y}
                        onChange={(e) => onPointChange(index, 'y', e.target.value)}
                        disabled={!editable}
                        className={`w-full px-3 py-1 border rounded font-mono text-sm ${
                          editable
                            ? 'border-gray-300 dark:border-gray-600 dark:bg-gray-800 dark:text-white focus:ring-2 focus:ring-blue-500'
                            : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 cursor-not-allowed'
                        }`}
                        aria-label={`Точка ${index}, значение Y`}
                      />
                    </td>
                    {editable && onRemovePoint && (
                      <td className="px-4 py-3">
                        <button
                          onClick={() => onRemovePoint(index)}
                          className="px-3 py-1 text-sm bg-red-100 dark:bg-red-900 text-red-700 dark:text-red-300
                                   rounded hover:bg-red-200 dark:hover:bg-red-800 transition-colors
                                   focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-opacity-50"
                          aria-label={`Удалить точку ${index}`}
                          disabled={points.length <= 2}
                          title={points.length <= 2 ? "Минимум 2 точки" : "Удалить точку"}
                        >
                          Удалить
                        </button>
                      </td>
                    )}
                  </tr>
                );
              })
            )}
          </tbody>
          <tfoot className="bg-gray-50 dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700">
            <tr>
              <td
                colSpan={showIndices ? 4 : 3}
                className="px-4 py-3"
              >
                <div className="flex justify-between items-center">
                  <div className="text-sm text-gray-600 dark:text-gray-400">
                    Всего точек: <span className="font-semibold">{points.length}</span>
                    {duplicateX && (
                      <span className="ml-3 text-red-600 dark:text-red-400">
                        ⚠ Есть дубликаты X
                      </span>
                    )}
                    {!sorted && (
                      <span className="ml-3 text-yellow-600 dark:text-yellow-400">
                        ⚠ X не отсортированы
                      </span>
                    )}
                  </div>
                  {editable && onAddPoint && (
                    <button
                      onClick={onAddPoint}
                      className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700
                               transition-colors text-sm font-medium flex items-center gap-2"
                      aria-label="Добавить новую точку"
                    >
                      <span>+</span>
                      Добавить точку
                    </button>
                  )}
                </div>
              </td>
            </tr>
          </tfoot>
        </table>
      </div>

      {/* Подсказки */}
      <div className="mt-4 space-y-2">
        <div className="flex items-start gap-2 text-sm text-gray-600 dark:text-gray-400">
          <span className="mt-0.5">💡</span>
          <p>
            <span className="font-semibold">X должны быть уникальными</span> и желательно
            отсортированными по возрастанию для корректной работы алгоритмов.
          </p>
        </div>
        <div className="flex items-start gap-2 text-sm text-gray-600 dark:text-gray-400">
          <span className="mt-0.5">⚡</span>
          <p>
            Для быстрого заполнения можно ввести формулу в первое поле Y и использовать
            автозаполнение (например, "=A2^2" для квадрата X).
          </p>
        </div>
      </div>
    </div>
  );
};

export default PointsTable;