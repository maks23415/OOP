
import React, { useState, useEffect, useCallback } from 'react';
import ErrorModal from '../../common/ErrorModal';
import { FactoryType, MathFunctionInfo } from '../../../types/function.types';
import { validateInterval, validatePointsCount } from '../../../utils/validation';
import { mathFunctionApi, functionApi } from '../../../api/functionApi';
import { LoadingOverlay, InlineLoading } from '../../common/LoadingOverlay';
import FunctionPreviewGraph from './FunctionPreviewGraph';

interface MathFunctionMap {
  [key: string]: {
    label: string;          // Локализованное название
    instance: any;          // Экземпляр MathFunction
    factory: () => any;     // Фабричная функция для создания
  };
}

interface CreateFromMathFunctionProps {
  onSuccess: (func: any) => void;
  factoryType: FactoryType;
  isOpen: boolean;
  onClose: () => void;
}

const CreateFromMathFunction: React.FC<CreateFromMathFunctionProps> = ({
  onSuccess,
  factoryType,
  isOpen,
  onClose
}) => {
  // Состояния для выбора функции
  const [selectedFunctionKey, setSelectedFunctionKey] = useState<string>('');
  const [selectedLocalizedName, setSelectedLocalizedName] = useState<string>('');

  // Состояния для параметров
  const [pointsCount, setPointsCount] = useState<string>('50');
  const [leftBound, setLeftBound] = useState<string>('-10');
  const [rightBound, setRightBound] = useState<string>('10');

  // Состояния UI
  const [isCreating, setIsCreating] = useState<boolean>(false);
  const [error, setError] = useState<{ type: string; message: string } | null>(null);
  const [previewPoints, setPreviewPoints] = useState<Array<{x: number, y: number}>>([]);
  const [mathFunctions, setMathFunctions] = useState<MathFunctionInfo[]>([]);
  const [mathFunctionMap, setMathFunctionMap] = useState<MathFunctionMap>({});
  const [isLoadingFunctions, setIsLoadingFunctions] = useState<boolean>(false);
  const [isGeneratingPreview, setIsGeneratingPreview] = useState<boolean>(false);
  const [categories, setCategories] = useState<string[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [searchQuery, setSearchQuery] = useState<string>('');

  // Загрузка математических функций при открытии
  useEffect(() => {
    if (isOpen) {
      loadMathFunctions();
    }
  }, [isOpen]);

  // Загрузка функций с бэкенда и создание Map
  const loadMathFunctions = async () => {
    try {
      setIsLoadingFunctions(true);

      // Загружаем информацию о функциях
      const functions = await mathFunctionApi.getAllMathFunctions();

      // Сортировка по локализованным названиям
      const sortedFunctions = functions.sort((a, b) =>
        a.label.localeCompare(b.label, 'ru-RU')
      );

      setMathFunctions(sortedFunctions);

      // Извлечение категорий
      const uniqueCategories = [...new Set(functions.map(f => f.category))].sort();
      setCategories(['all', ...uniqueCategories]);

      // Создаем Map функций
      const functionMap: MathFunctionMap = {};

      // Для каждой функции создаем запись в Map
      for (const func of functions) {
        try {
          // Создаем экземпляр функции через API
          const instance = await mathFunctionApi.createMathFunctionInstance(func.key);

          functionMap[func.key] = {
            label: func.label,
            instance: instance,
            factory: () => {
              // Фабричная функция для создания новых экземпляров
              // В реальном приложении это может быть вызов API или создание через рефлексию
              return instance;
            }
          };
        } catch (err) {
          console.warn(`Не удалось создать экземпляр функции ${func.label}:`, err);
        }
      }

      setMathFunctionMap(functionMap);

    } catch (err: any) {
      setError({
        type: 'LOAD_ERROR',
        message: `Не удалось загрузить математические функции: ${err.message}`
      });
    } finally {
      setIsLoadingFunctions(false);
    }
  };

  // Обработчик выбора функции
  const handleFunctionSelect = useCallback((functionKey: string, localizedName: string) => {
    setSelectedFunctionKey(functionKey);
    setSelectedLocalizedName(localizedName);

    // Сбрасываем предпросмотр при смене функции
    setPreviewPoints([]);
  }, []);

  // Фильтрация функций по категории и поиску
  const filteredFunctions = mathFunctions.filter(func => {
    const matchesCategory = selectedCategory === 'all' || func.category === selectedCategory;
    const matchesSearch = searchQuery === '' ||
      func.label.toLowerCase().includes(searchQuery.toLowerCase()) ||
      func.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
      func.example.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  // Обновление предпросмотра при изменении параметров
  useEffect(() => {
    if (selectedFunctionKey && pointsCount && leftBound && rightBound) {
      const debounceTimer = setTimeout(() => {
        updatePreview();
      }, 500);

      return () => clearTimeout(debounceTimer);
    }
  }, [selectedFunctionKey, pointsCount, leftBound, rightBound]);

  // Обновление предпросмотра графика
  const updatePreview = async () => {
    const count = parseInt(pointsCount);
    const left = parseFloat(leftBound);
    const right = parseFloat(rightBound);

    // Валидация параметров
    if (isNaN(count) || isNaN(left) || isNaN(right) || count < 2 || left >= right) {
      return;
    }

    // Ограничение для предпросмотра
    if (count > 1000) {
      setError({
        type: 'PREVIEW_LIMIT',
        message: 'Для предпросмотра используйте не более 1000 точек. При создании функции можно использовать больше точек.'
      });
      return;
    }

    try {
      setIsGeneratingPreview(true);

      // Используем Map для получения функции
      const funcEntry = mathFunctionMap[selectedFunctionKey];
      if (!funcEntry) {
        throw new Error(`Функция "${selectedLocalizedName}" не найдена в Map`);
      }

      // Альтернативно можно использовать API для предпросмотра
      const points = await mathFunctionApi.previewMathFunction(
        selectedFunctionKey,
        Math.min(count, 100), // Для предпросмотра используем максимум 100 точек
        left,
        right
      );

      setPreviewPoints(points);
    } catch (err: any) {
      console.error('Ошибка при генерации предпросмотра:', err);
      setPreviewPoints([]);

      // Показываем ошибку, если не критическая
      if (!err.message.includes('не найдена')) {
        setError({
          type: 'PREVIEW_ERROR',
          message: `Ошибка при генерации предпросмотра: ${err.message}`
        });
      }
    } finally {
      setIsGeneratingPreview(false);
    }
  };

  // Создание табулированной функции
  const handleCreateFunction = async () => {
    try {
      // Валидация
      const countValidation = validatePointsCount(pointsCount);
      if (!countValidation.isValid) {
        setError({ type: countValidation.type!, message: countValidation.message! });
        return;
      }

      const intervalValidation = validateInterval(leftBound, rightBound);
      if (!intervalValidation.isValid) {
        setError({ type: intervalValidation.type!, message: intervalValidation.message! });
        return;
      }

      if (!selectedFunctionKey) {
        setError({
          type: 'EMPTY_FIELD',
          message: 'Пожалуйста, выберите математическую функцию'
        });
        return;
      }

      // Проверяем наличие функции в Map
      const funcEntry = mathFunctionMap[selectedFunctionKey];
      if (!funcEntry) {
        setError({
          type: 'FUNCTION_NOT_FOUND',
          message: `Выбранная функция "${selectedLocalizedName}" не найдена в системе`
        });
        return;
      }

      setIsCreating(true);

      // Создаем табулированную функцию через фабрику
      const createdFunction = await functionApi.createFromMathFunction(
        selectedFunctionKey,
        parseInt(pointsCount),
        parseFloat(leftBound),
        parseFloat(rightBound),
        factoryType,
        `Табулированная_${selectedLocalizedName}_${Date.now()}`
      );

      onSuccess(createdFunction);
      onClose();

    } catch (err: any) {
      setError({
        type: err.type || 'SERVER_ERROR',
        message: `Не удалось создать функцию: ${err.message || 'Неизвестная ошибка'}`
      });
    } finally {
      setIsCreating(false);
    }
  };

  // Поиск функции по локализованному названию
  const findFunctionByLocalizedName = useCallback((name: string): string | null => {
    for (const [key, value] of Object.entries(mathFunctionMap)) {
      if (value.label === name) {
        return key;
      }
    }
    return null;
  }, [mathFunctionMap]);

  // Получение локализованного названия по ключу
  const getLocalizedNameByKey = useCallback((key: string): string => {
    return mathFunctionMap[key]?.label || key;
  }, [mathFunctionMap]);

  // Получение выбранной функции из Map
  const getSelectedFunctionFromMap = useCallback(() => {
    if (!selectedFunctionKey) return null;
    return mathFunctionMap[selectedFunctionKey];
  }, [selectedFunctionKey, mathFunctionMap]);

  // Сброс формы
  const handleReset = () => {
    setSelectedFunctionKey('');
    setSelectedLocalizedName('');
    setPointsCount('50');
    setLeftBound('-10');
    setRightBound('10');
    setPreviewPoints([]);
    setSearchQuery('');
    setSelectedCategory('all');
    setError(null);
  };

  // Получение информации о выбранной функции
  const selectedFunctionInfo = mathFunctions.find(f => f.key === selectedFunctionKey);
  const selectedFunctionFromMap = getSelectedFunctionFromMap();

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white dark:bg-gray-800 rounded-lg p-6 max-w-6xl w-full max-h-[95vh] overflow-y-auto">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-800 dark:text-white">
            Создание функции из математической функции
          </h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
            aria-label="Закрыть окно"
          >
            ✕
          </button>
        </div>

        {/* Отладочная информация о Map (можно скрыть в production) */}
        <div className="mb-4 p-3 bg-gray-100 dark:bg-gray-900 rounded text-xs">
          <div className="flex justify-between">
            <span>Загружено функций: {Object.keys(mathFunctionMap).length}</span>
            <span>Выбрана: {selectedLocalizedName || 'нет'}</span>
            <span>Ключ: {selectedFunctionKey || 'нет'}</span>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Левая колонка: выбор функции */}
          <div className="lg:col-span-1">
            {/* Поиск функции */}
            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Поиск функции:
              </label>
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Введите название функции..."
                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg
                         focus:ring-2 focus:ring-blue-500 focus:border-transparent
                         dark:bg-gray-700 dark:text-white"
              />
            </div>

            {/* Категории */}
            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Категория:
              </label>
              <div className="flex flex-wrap gap-2">
                {categories.map(category => (
                  <button
                    key={category}
                    onClick={() => setSelectedCategory(category)}
                    className={`px-3 py-1 text-sm rounded-full transition-colors ${
                      selectedCategory === category
                        ? 'bg-blue-600 text-white'
                        : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-300 dark:hover:bg-gray-600'
                    }`}
                  >
                    {category === 'all' ? 'Все' : category}
                  </button>
                ))}
              </div>
            </div>

            {/* Список функций */}
            <div className="mb-6 relative">
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Выберите математическую функцию:
              </label>

              {isLoadingFunctions ? (
                <div className="flex items-center justify-center py-8">
                  <InlineLoading text="Загрузка функций..." />
                </div>
              ) : (
                <div className="border border-gray-300 dark:border-gray-600 rounded-lg max-h-96 overflow-y-auto">
                  {filteredFunctions.length === 0 ? (
                    <div className="p-4 text-center text-gray-500 dark:text-gray-400">
                      {searchQuery ? 'Функции не найдены' : 'Нет доступных функций'}
                    </div>
                  ) : (
                    filteredFunctions.map((func) => {
                      const isSelected = selectedFunctionKey === func.key;
                      const mapEntry = mathFunctionMap[func.key];

                      return (
                        <div
                          key={func.key}
                          onClick={() => handleFunctionSelect(func.key, func.label)}
                          className={`p-3 border-b border-gray-200 dark:border-gray-700 cursor-pointer transition-colors ${
                            isSelected
                              ? 'bg-blue-50 dark:bg-blue-900/30 border-blue-200 dark:border-blue-800'
                              : 'hover:bg-gray-50 dark:hover:bg-gray-700'
                          } ${!mapEntry ? 'opacity-50' : ''}`}
                          title={!mapEntry ? 'Функция не загружена в Map' : undefined}
                        >
                          <div className="flex justify-between items-start">
                            <div>
                              <div className="font-medium text-gray-800 dark:text-white">
                                {func.label} {!mapEntry && '⚠'}
                              </div>
                              <div className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                                {func.description}
                              </div>
                            </div>
                            <div className="flex flex-col items-end gap-1">
                              <div className="text-xs px-2 py-1 bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400 rounded">
                                {func.category}
                              </div>
                              {mapEntry && (
                                <div className="text-xs px-2 py-0.5 bg-green-100 dark:bg-green-800 text-green-700 dark:text-green-300 rounded">
                                  ✓ В Map
                                </div>
                              )}
                            </div>
                          </div>
                          {func.example && (
                            <div className="text-xs font-mono text-gray-600 dark:text-gray-400 mt-2 bg-gray-50 dark:bg-gray-900 p-2 rounded">
                              {func.example}
                            </div>
                          )}
                        </div>
                      );
                    })
                  )}
                </div>
              )}
            </div>

            {/* Информация о выбранной функции из Map */}
            {selectedFunctionFromMap && (
              <div className="mb-6 p-4 bg-green-50 dark:bg-green-900/20 rounded-lg border border-green-200 dark:border-green-800">
                <h4 className="font-medium text-green-800 dark:text-green-300 mb-2">
                  Функция загружена в Map
                </h4>
                <div className="text-sm space-y-1">
                  <div className="flex justify-between">
                    <span className="text-green-700 dark:text-green-400">Ключ:</span>
                    <code className="text-green-800 dark:text-green-300 font-mono">
                      {selectedFunctionKey}
                    </code>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-green-700 dark:text-green-400">Тип:</span>
                    <span className="text-green-800 dark:text-green-300">
                      {selectedFunctionFromMap.instance?.constructor?.name || 'Unknown'}
                    </span>
                  </div>
                </div>
              </div>
            )}

            {/* Параметры функции */}
            <div className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Количество точек разбиения:
                </label>
                <div className="flex items-center gap-4">
                  <input
                    type="range"
                    min="2"
                    max="10000"
                    value={pointsCount}
                    onChange={(e) => setPointsCount(e.target.value)}
                    className="flex-1"
                  />
                  <input
                    type="number"
                    min="2"
                    max="100000"
                    value={pointsCount}
                    onChange={(e) => setPointsCount(e.target.value)}
                    className="w-24 px-3 py-1 border border-gray-300 dark:border-gray-600 rounded text-center
                             dark:bg-gray-700 dark:text-white"
                  />
                </div>
                <div className="flex justify-between text-xs text-gray-500 dark:text-gray-400 mt-1">
                  <span>2</span>
                  <span>100</span>
                  <span>1000</span>
                  <span>10000</span>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Интервал разбиения:
                </label>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs text-gray-500 dark:text-gray-400 mb-1">
                      Левая граница
                    </label>
                    <input
                      type="number"
                      step="0.1"
                      value={leftBound}
                      onChange={(e) => setLeftBound(e.target.value)}
                      className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg
                               focus:ring-2 focus:ring-blue-500 focus:border-transparent
                               dark:bg-gray-700 dark:text-white"
                    />
                  </div>
                  <div>
                    <label className="block text-xs text-gray-500 dark:text-gray-gray-400 mb-1">
                      Правая граница
                    </label>
                    <input
                      type="number"
                      step="0.1"
                      value={rightBound}
                      onChange={(e) => setRightBound(e.target.value)}
                      className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg
                               focus:ring-2 focus:ring-blue-500 focus:border-transparent
                               dark:bg-gray-700 dark:text-white"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Правая колонка: предпросмотр */}
          <div className="lg:col-span-2">
            <div className="bg-gray-50 dark:bg-gray-900 rounded-xl p-4 h-full">
              {selectedFunctionKey ? (
                <>
                  <div className="mb-4">
                    <div className="flex items-center justify-between mb-2">
                      <h3 className="text-lg font-semibold text-gray-800 dark:text-white">
                        Предпросмотр функции
                      </h3>
                      <div className="flex gap-2">
                        <button
                          onClick={updatePreview}
                          disabled={isGeneratingPreview}
                          className="px-3 py-1 text-sm bg-blue-600 text-white rounded hover:bg-blue-700
                                   disabled:opacity-50 flex items-center gap-2"
                        >
                          {isGeneratingPreview ? (
                            <>
                              <div className="w-3 h-3 border-2 border-white border-t-transparent rounded-full animate-spin" />
                              Обновление...
                            </>
                          ) : (
                            'Обновить предпросмотр'
                          )}
                        </button>
                      </div>
                    </div>

                    <div className="mb-4 p-3 bg-white dark:bg-gray-800 rounded-lg">
                      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                        <div>
                          <p className="text-sm text-gray-600 dark:text-gray-400">Функция:</p>
                          <p className="font-semibold text-gray-800 dark:text-white">
                            {selectedLocalizedName}
                          </p>
                        </div>
                        <div>
                          <p className="text-sm text-gray-600 dark:text-gray-400">Ключ в Map:</p>
                          <code className="font-mono text-sm text-gray-800 dark:text-white">
                            {selectedFunctionKey}
                          </code>
                        </div>
                        <div>
                          <p className="text-sm text-gray-600 dark:text-gray-400">Количество точек:</p>
                          <p className="font-semibold text-gray-800 dark:text-white">{pointsCount}</p>
                        </div>
                        <div>
                          <p className="text-sm text-gray-600 dark:text-gray-400">Интервал:</p>
                          <p className="font-semibold text-gray-800 dark:text-white">
                            [{leftBound}, {rightBound}]
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* График предпросмотра */}
                  <div className="mb-6">
                    <FunctionPreviewGraph
                      points={previewPoints}
                      title={`График функции: ${selectedLocalizedName}`}
                      height={300}
                      isLoading={isGeneratingPreview}
                    />
                  </div>

                  {/* Информация о Map */}
                  <div className="mb-6 p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
                    <h4 className="font-medium text-blue-800 dark:text-blue-300 mb-2">
                      Информация о Map соответствия
                    </h4>
                    <div className="text-sm space-y-2">
                      <div className="grid grid-cols-2 gap-4">
                        <div>
                          <p className="text-blue-700 dark:text-blue-400">Локализованное название:</p>
                          <p className="font-semibold">{selectedLocalizedName}</p>
                        </div>
                        <div>
                          <p className="text-blue-700 dark:text-blue-400">Ключ в системе:</p>
                          <code className="font-mono">{selectedFunctionKey}</code>
                        </div>
                      </div>
                      <div className="mt-2">
                        <p className="text-blue-700 dark:text-blue-400">Соответствие в Map:</p>
                        <div className="bg-white dark:bg-gray-800 p-3 rounded mt-1 font-mono text-xs">
                          {JSON.stringify({
                            key: selectedFunctionKey,
                            label: selectedLocalizedName,
                            instanceType: selectedFunctionFromMap?.instance?.constructor?.name,
                            inMap: !!selectedFunctionFromMap
                          }, null, 2)}
                        </div>
                      </div>
                    </div>
                  </div>
                </>
              ) : (
                <div className="flex flex-col items-center justify-center h-96">
                  <div className="text-gray-400 text-6xl mb-4">📊</div>
                  <h3 className="text-xl font-semibold text-gray-600 dark:text-gray-400 mb-2">
                    Выберите функцию для предпросмотра
                  </h3>
                  <p className="text-gray-500 dark:text-gray-500 text-center max-w-md">
                    Выберите математическую функцию из списка слева. Каждая функция имеет
                    локализованное название и связана с объектом MathFunction через Map.
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Кнопки действий */}
        <div className="flex justify-between items-center mt-6 pt-6 border-t border-gray-200 dark:border-gray-700">
          <div className="flex gap-2">
            <button
              onClick={handleReset}
              className="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg
                       text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700
                       text-sm"
            >
              Сбросить форму
            </button>
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
              disabled={!selectedFunctionKey || isCreating || !selectedFunctionFromMap}
              className="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700
                       transition-colors disabled:opacity-50 disabled:cursor-not-allowed
                       flex items-center gap-2"
              title={!selectedFunctionFromMap ? 'Функция не загружена в Map' : undefined}
            >
              {isCreating ? (
                <>
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  Создание...
                </>
              ) : (
                'Создать табулированную функцию'
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
          onClose={() => setError(null)}
        />
      )}
    </div>
  );
};

export default CreateFromMathFunction;