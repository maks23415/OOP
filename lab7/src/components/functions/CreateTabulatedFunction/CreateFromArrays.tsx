import React, { useState, useEffect, useCallback } from 'react';
import PointsTable from './PointsTable';
import ErrorModal from '../../common/ErrorModal';
import { Point, FactoryType } from '../../../types/function.types';
import { validateSize, validatePoints } from '../../../utils/validation';
import { functionApi } from '../../../api/functionApi';
import VirtualizedPointsTable from './VirtualizedPointsTable';
import { useLargeDataHandler } from '../../../hooks/useLargeDataHandler';

interface CreateFromArraysProps {
  onSuccess: (func: any) => void;
  factoryType: FactoryType;
  isOpen: boolean;
  onClose: () => void;
}

const CreateFromArrays: React.FC<CreateFromArraysProps> = ({
  onSuccess,
  factoryType,
  isOpen,
  onClose
}) => {
  const [size, setSize] = useState<string>('');
  const [points, setPoints] = useState<Point[]>([]);
  const [showTable, setShowTable] = useState<boolean>(false);
  const [isCreating, setIsCreating] = useState<boolean>(false);
  const [error, setError] = useState<{ type: string; message: string; recommendations?: string[] } | null>(null);
  const [name, setName] = useState<string>('');
  const [useVirtualizedTable, setUseVirtualizedTable] = useState<boolean>(false);
  const [quickFillPattern, setQuickFillPattern] = useState<string>('');

  // Хук для работы с большими данными
  const largeDataHandler = useLargeDataHandler({
    initialPoints: points,
    maxPoints: 100000,
    chunkSize: 5000
  });

  // Сброс формы при закрытии
  useEffect(() => {
    if (!isOpen) {
      setSize('');
      setPoints([]);
      setShowTable(false);
      setError(null);
      setName('');
      setUseVirtualizedTable(false);
      setQuickFillPattern('');
    }
  }, [isOpen]);

  // Автоматическое переключение на виртуализированную таблицу при большом количестве точек
  useEffect(() => {
    if (points.length > 1000) {
      setUseVirtualizedTable(true);
    }
  }, [points.length]);

  // Обработка изменения количества точек
  const handleSizeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setSize(value);

    // Если пользователь вводит новое значение при уже существующей таблице
    if (showTable) {
      const validation = validateSize(value);
      if (!validation.isValid) {
        setError({ type: validation.type!, message: validation.message! });
        return;
      }

      const newSize = parseInt(value);
      if (newSize !== points.length) {
        // Если размер изменился, пересоздаем таблицу
        const newPoints: Point[] = Array(newSize)
          .fill(null)
          .map((_, index) => ({
            x: points[index]?.x || index,
            y: points[index]?.y || 0
          }));
        setPoints(newPoints);
      }
    }
  };

  // Создание таблицы для ввода точек
  const handleCreateTable = useCallback(async () => {
    const validation = validateSize(size);
    if (!validation.isValid) {
      setError({ type: validation.type!, message: validation.message! });
      return;
    }

    const tableSize = parseInt(size);

    // Проверка на очень большое значение
    if (tableSize > 5000) {
      setError({
        type: 'TOO_LARGE_SIZE',
        message: `Вы создаете большую таблицу (${tableSize.toLocaleString()} точек).`,
        recommendations: [
          'Для лучшей производительности будет использована виртуализация',
          'Рекомендуется использовать импорт из файла для больших данных',
          'Можно уменьшить количество точек до 1000 для ручного ввода'
        ]
      });

      const shouldContinue = window.confirm(
        `Создать таблицу с ${tableSize.toLocaleString()} точками? ` +
        `Для производительности будет использована оптимизированная таблица.`
      );

      if (!shouldContinue) return;
    } else if (tableSize > 1000) {
      setError({
        type: 'LARGE_SIZE_WARNING',
        message: `Вы создаете таблицу с ${tableSize} точками.`,
        recommendations: [
          'Для лучшей производительности рекомендуется использовать виртуализацию',
          'Можно переключиться на виртуализированную таблицу в настройках'
        ]
      });
    }

    // Создание начальных точек с оптимизацией для больших наборов
    if (tableSize > 10000) {
      // Для очень больших наборов используем постепенное создание
      setIsCreating(true);
      const chunkSize = 5000;
      const initialPoints: Point[] = [];

      for (let i = 0; i < tableSize; i += chunkSize) {
        const chunkEnd = Math.min(i + chunkSize, tableSize);
        const chunk = Array(chunkEnd - i)
          .fill(null)
          .map((_, index) => ({
            x: i + index,
            y: 0
          }));

        initialPoints.push(...chunk);

        // Обновляем состояние каждые 5000 точек для отзывчивости
        if (i % 5000 === 0 || i + chunkSize >= tableSize) {
          setPoints([...initialPoints]);
          await new Promise(resolve => setTimeout(resolve, 0)); // Даем браузеру перерисовать
        }
      }

      setPoints(initialPoints);
      setIsCreating(false);
    } else {
      // Для небольших наборов создаем сразу
      const initialPoints: Point[] = Array(tableSize)
        .fill(null)
        .map((_, index) => ({
          x: index,
          y: 0
        }));

      setPoints(initialPoints);
    }

    setShowTable(true);

    // Автоматически генерируем имя
    if (!name) {
      setName(`Функция_${tableSize}точек_${new Date().toLocaleDateString('ru-RU')}`);
    }
  }, [size, name]);

  // Обновление точки
  const handlePointChange = (index: number, field: 'x' | 'y', value: string) => {
    const newPoints = [...points];
    const numValue = parseFloat(value);

    if (!isNaN(numValue)) {
      newPoints[index] = { ...newPoints[index], [field]: numValue };
      setPoints(newPoints);
    }
  };

  // Добавление точки
  const handleAddPoint = () => {
    const lastX = points.length > 0 ? points[points.length - 1].x + 1 : 0;
    setPoints([...points, { x: lastX, y: 0 }]);
    setSize((points.length + 1).toString());
  };

  // Удаление точки
  const handleRemovePoint = (index: number) => {
    if (points.length <= 2) {
      setError({
        type: 'MIN_POINTS',
        message: 'Функция должна содержать минимум 2 точки'
      });
      return;
    }

    const newPoints = points.filter((_, i) => i !== index);
    setPoints(newPoints);
    setSize(newPoints.length.toString());
  };

  // Быстрое заполнение точек по шаблону
  const handleQuickFill = (pattern: string) => {
    setQuickFillPattern(pattern);

    const newPoints = [...points];
    switch (pattern) {
      case 'linear':
        newPoints.forEach((point, i) => {
          newPoints[i] = { ...point, y: i };
        });
        break;
      case 'quadratic':
        newPoints.forEach((point, i) => {
          newPoints[i] = { ...point, y: i * i };
        });
        break;
      case 'sin':
        newPoints.forEach((point, i) => {
          newPoints[i] = { ...point, y: Math.sin(i * 0.1) };
        });
        break;
      case 'cos':
        newPoints.forEach((point, i) => {
          newPoints[i] = { ...point, y: Math.cos(i * 0.1) };
        });
        break;
      case 'exp':
        newPoints.forEach((point, i) => {
          newPoints[i] = { ...point, y: Math.exp(i * 0.1) };
        });
        break;
      case 'random':
        newPoints.forEach((point, i) => {
          newPoints[i] = { ...point, y: Math.random() * 10 };
        });
        break;
      default:
        break;
    }

    setPoints(newPoints);
  };

  // Импорт точек из файла
  const handleImportFromFile = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    try {
      setIsCreating(true);
      const text = await file.text();
      const parsedData = JSON.parse(text);

      // Проверяем формат данных
      if (Array.isArray(parsedData)) {
        const importedPoints: Point[] = parsedData.map((item: any, index: number) => ({
          x: typeof item.x === 'number' ? item.x : index,
          y: typeof item.y === 'number' ? item.y : 0
        }));

        if (importedPoints.length > 0) {
          setPoints(importedPoints);
          setSize(importedPoints.length.toString());
          setShowTable(true);
          setName(file.name.replace('.json', ''));
        }
      } else {
        throw new Error('Неверный формат файла');
      }
    } catch (err: any) {
      setError({
        type: 'IMPORT_ERROR',
        message: `Ошибка при импорте файла: ${err.message}`
      });
    } finally {
      setIsCreating(false);
      event.target.value = ''; // Сброс input
    }
  };

  // Экспорт точек в файл
  const handleExportToFile = () => {
    const dataStr = JSON.stringify(points, null, 2);
    const dataUri = 'data:application/json;charset=utf-8,' + encodeURIComponent(dataStr);
    const link = document.createElement('a');
    link.href = dataUri;
    link.download = `function_points_${Date.now()}.json`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  // Создание функции
  const handleCreateFunction = async () => {
    try {
      setIsCreating(true);

      // Валидация точек
      const pointsValidation = validatePoints(points);
      if (!pointsValidation.isValid) {
        setError({
          type: pointsValidation.type!,
          message: pointsValidation.message!
        });
        return;
      }

      // Используем API сервис
      const createdFunction = await functionApi.createFromArrays(
        points,
        factoryType,
        name || `Function_${Date.now()}`
      );

      onSuccess(createdFunction);
      onClose();

    } catch (err: any) {
      setError({
        type: err.type || 'SERVER_ERROR',
        message: `Не удалось создать функцию: ${err.message || 'Неизвестная ошибка'}`,
        recommendations: [
          'Проверьте подключение к серверу',
          'Убедитесь, что все поля заполнены корректно',
          'Попробуйте уменьшить количество точек'
        ]
      });
    } finally {
      setIsCreating(false);
    }
  };

  // Автоматическая сортировка точек по X
  const handleSortPoints = () => {
    const sortedPoints = [...points].sort((a, b) => a.x - b.x);
    setPoints(sortedPoints);
  };

  // Удаление дубликатов
  const handleRemoveDuplicates = async () => {
    await largeDataHandler.removeDuplicates();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white dark:bg-gray-800 rounded-lg p-6 max-w-6xl w-full max-h-[95vh] overflow-y-auto">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-800 dark:text-white">
            Создание табулированной функции из массивов
          </h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
            aria-label="Закрыть окно"
          >
            ✕
          </button>
        </div>

        {/* Информация о фабрике */}
        <div className="mb-6 p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg border border-blue-200 dark:border-blue-800">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-blue-100 dark:bg-blue-800 flex items-center justify-center">
              <span className="text-blue-600 dark:text-blue-300">⚙️</span>
            </div>
            <div>
              <p className="font-medium text-gray-800 dark:text-white">
                Используемая фабрика: <span className="font-bold">
                  {factoryType === FactoryType.ARRAY ? 'Массив' : 'Связный список'}
                </span>
              </p>
              <p className="text-sm text-gray-600 dark:text-gray-400">
                Все новые функции будут создаваться с использованием этой фабрики
              </p>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Левая колонка: управление */}
          <div className="lg:col-span-1 space-y-6">
            {/* Имя функции */}
            <div>
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Название функции:
              </label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Введите название функции"
                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg
                         focus:ring-2 focus:ring-blue-500 focus:border-transparent
                         dark:bg-gray-700 dark:text-white"
              />
            </div>

            {/* Ввод количества точек */}
            <div>
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Количество точек функции:
              </label>
              <div className="flex gap-4">
                <input
                  type="text"
                  value={size}
                  onChange={handleSizeChange}
                  placeholder="Введите число"
                  className="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg
                           focus:ring-2 focus:ring-blue-500 focus:border-transparent
                           dark:bg-gray-700 dark:text-white"
                />
                <button
                  onClick={handleCreateTable}
                  className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700
                           transition-colors disabled:opacity-50 disabled:cursor-not-allowed
                           whitespace-nowrap"
                  disabled={!size.trim()}
                >
                  {showTable ? 'Обновить' : 'Создать'}
                </button>
              </div>
              <p className="mt-2 text-sm text-gray-500 dark:text-gray-400">
                От 2 до 100,000 точек. При {">"}1,000 точек используется оптимизация.
              </p>
            </div>

            {/* Быстрое заполнение */}
            {showTable && (
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Быстрое заполнение:
                </label>
                <div className="grid grid-cols-2 gap-2">
                  {['linear', 'quadratic', 'sin', 'cos', 'exp', 'random'].map((pattern) => (
                    <button
                      key={pattern}
                      onClick={() => handleQuickFill(pattern)}
                      className={`px-3 py-2 text-sm rounded ${
                        quickFillPattern === pattern
                          ? 'bg-blue-600 text-white'
                          : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-300 dark:hover:bg-gray-600'
                      }`}
                    >
                      {pattern === 'linear' && 'Линейная'}
                      {pattern === 'quadratic' && 'Квадратичная'}
                      {pattern === 'sin' && 'Синус'}
                      {pattern === 'cos' && 'Косинус'}
                      {pattern === 'exp' && 'Экспонента'}
                      {pattern === 'random' && 'Случайная'}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Импорт/экспорт */}
            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Импорт данных:
                </label>
                <div className="flex gap-2">
                  <label className="flex-1">
                    <input
                      type="file"
                      accept=".json,.txt"
                      onChange={handleImportFromFile}
                      className="hidden"
                      id="import-file"
                    />
                    <div className="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg
                                 text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700
                                 cursor-pointer text-center">
                      📁 Выбрать файл
                    </div>
                  </label>
                  <button
                    onClick={handleExportToFile}
                    className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
                    disabled={points.length === 0}
                  >
                    📤 Экспорт
                  </button>
                </div>
              </div>
            </div>

            {/* Управление таблицей */}
            {showTable && (
              <div className="space-y-3">
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                  Управление таблицей:
                </label>
                <div className="grid grid-cols-2 gap-2">
                  <button
                    onClick={handleSortPoints}
                    className="px-3 py-2 bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300
                             rounded hover:bg-gray-300 dark:hover:bg-gray-600 text-sm"
                  >
                    📊 Сортировать по X
                  </button>
                  <button
                    onClick={() => handleRemoveDuplicates()}
                    className="px-3 py-2 bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300
                             rounded hover:bg-gray-300 dark:hover:bg-gray-600 text-sm"
                  >
                    🗑️ Удалить дубликаты
                  </button>
                  <button
                    onClick={handleAddPoint}
                    className="px-3 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 text-sm"
                  >
                    ➕ Добавить точку
                  </button>
                  <div className="flex items-center gap-2">
                    <input
                      type="checkbox"
                      id="virtualized"
                      checked={useVirtualizedTable}
                      onChange={(e) => setUseVirtualizedTable(e.target.checked)}
                      className="rounded"
                    />
                    <label htmlFor="virtualized" className="text-sm text-gray-600 dark:text-gray-400">
                      Виртуализация
                    </label>
                  </div>
                </div>
              </div>
            )}

            {/* Статистика */}
            {showTable && (
              <div className="p-4 bg-gray-50 dark:bg-gray-900 rounded-lg">
                <h4 className="font-medium text-gray-800 dark:text-white mb-3">
                  Статистика
                </h4>
                <div className="space-y-2 text-sm">
                  <div className="flex justify-between">
                    <span className="text-gray-600 dark:text-gray-400">Всего точек:</span>
                    <span className="font-semibold text-gray-800 dark:text-white">
                      {points.length.toLocaleString()}
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600 dark:text-gray-400">Диапазон X:</span>
                    <span className="font-semibold text-gray-800 dark:text-white">
                      {Math.min(...points.map(p => p.x)).toFixed(2)} - {Math.max(...points.map(p => p.x)).toFixed(2)}
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600 dark:text-gray-400">Диапазон Y:</span>
                    <span className="font-semibold text-gray-800 dark:text-white">
                      {Math.min(...points.map(p => p.y)).toFixed(2)} - {Math.max(...points.map(p => p.y)).toFixed(2)}
                    </span>
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Правая колонка: таблица */}
          <div className="lg:col-span-2">
            {showTable ? (
              <>
                {points.length > 1000 && useVirtualizedTable ? (
                  <VirtualizedPointsTable
                    points={points}
                    onPointChange={handlePointChange}
                    onAddPoint={handleAddPoint}
                    onRemovePoint={handleRemovePoint}
                    title="Таблица точек (виртуализированная)"
                    maxVisibleRows={15}
                    rowHeight={45}
                  />
                ) : (
                  <PointsTable
                    points={points}
                    onPointChange={handlePointChange}
                    onAddPoint={handleAddPoint}
                    onRemovePoint={handleRemovePoint}
                    title="Таблица точек"
                    maxHeight="500px"
                  />
                )}

                {/* Предупреждение о большом наборе данных */}
                {points.length > 5000 && (
                  <div className="mt-4 p-4 bg-yellow-50 dark:bg-yellow-900/20 rounded-lg border border-yellow-200 dark:border-yellow-800">
                    <div className="flex items-start gap-3">
                      <div className="text-yellow-600 dark:text-yellow-400 text-xl">⚠</div>
                      <div>
                        <h4 className="font-semibold text-yellow-800 dark:text-yellow-300 mb-1">
                          Работа с большим набором данных
                        </h4>
                        <ul className="text-sm text-yellow-700 dark:text-yellow-400 space-y-1">
                          <li>• Используется виртуализация для производительности</li>
                          <li>• Для поиска используйте поле поиска в таблице</li>
                          <li>• Рекомендуется использовать импорт/экспорт для работы с файлами</li>
                          <li>• При необходимости можно уменьшить количество точек</li>
                        </ul>
                      </div>
                    </div>
                  </div>
                )}
              </>
            ) : (
              <div className="h-96 flex flex-col items-center justify-center border-2 border-dashed border-gray-300 dark:border-gray-700 rounded-lg">
                <div className="text-gray-400 text-6xl mb-4">📊</div>
                <h3 className="text-xl font-semibold text-gray-600 dark:text-gray-400 mb-2">
                  Таблица точек
                </h3>
                <p className="text-gray-500 dark:text-gray-500 text-center max-w-md">
                  Введите количество точек и нажмите "Создать таблицу" для начала работы.
                  Вы также можете импортировать данные из JSON файла.
                </p>
              </div>
            )}
          </div>
        </div>

        {/* Кнопки действий */}
        <div className="flex justify-between items-center mt-6 pt-6 border-t border-gray-200 dark:border-gray-700">
          <div className="text-sm text-gray-600 dark:text-gray-400">
            {showTable && `Готово к созданию: ${points.length} точек`}
          </div>

          <div className="flex gap-4">
            <button
              onClick={onClose}
              className="px-6 py-2 border border-gray-300 dark:border-gray-600 rounded-lg
                       text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700"
            >
              Отмена
            </button>
            <button
              onClick={handleCreateFunction}
              disabled={!showTable || isCreating || points.length < 2}
              className="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700
                       transition-colors disabled:opacity-50 disabled:cursor-not-allowed
                       flex items-center gap-2"
            >
              {isCreating ? (
                <>
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  Создание...
                </>
              ) : (
                'Создать функцию'
              )}
            </button>
          </div>
        </div>
      </div>

      {/* Модальное окно ошибок */}
      {error && (
        <ErrorModal
          errorType={error.type}
          message={error.message}
          recommendations={error.recommendations}
          onClose={() => setError(null)}
        />
      )}
    </div>
  );
};

export default CreateFromArrays;