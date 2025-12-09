import React from 'react';
import { FactoryType } from '../../../types/function.types';

interface FunctionFactorySelectorProps {
  selectedFactory: FactoryType;
  onFactoryChange: (factory: FactoryType) => void;
  disabled?: boolean;
  showDescription?: boolean;
  compact?: boolean;
}

const FunctionFactorySelector: React.FC<FunctionFactorySelectorProps> = ({
  selectedFactory,
  onFactoryChange,
  disabled = false,
  showDescription = true,
  compact = false
}) => {
  const factories = [
    {
      type: FactoryType.ARRAY,
      name: 'Массив (Array)',
      description: 'Хранит точки в массиве. Быстрый доступ по индексу, но медленная вставка/удаление.',
      icon: '📊',
      pros: ['Быстрый доступ по индексу', 'Экономия памяти', 'Простая реализация'],
      cons: ['Медленная вставка/удаление', 'Фиксированный размер (при переполнении)']
    },
    {
      type: FactoryType.LINKED_LIST,
      name: 'Связный список (LinkedList)',
      description: 'Хранит точки в связном списке. Быстрая вставка/удаление, но медленный доступ по индексу.',
      icon: '🔗',
      pros: ['Быстрая вставка/удаление', 'Динамический размер', 'Эффективное изменение структуры'],
      cons: ['Медленный доступ по индексу', 'Больше использует памяти', 'Сложнее в реализации']
    }
  ];

  const selectedFactoryInfo = factories.find(f => f.type === selectedFactory);

  if (compact) {
    return (
      <div className="w-full">
        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
          Фабрика создания функций:
        </label>
        <select
          value={selectedFactory}
          onChange={(e) => onFactoryChange(e.target.value as FactoryType)}
          disabled={disabled}
          className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg
                   focus:ring-2 focus:ring-blue-500 focus:border-transparent
                   dark:bg-gray-700 dark:text-white disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {factories.map((factory) => (
            <option key={factory.type} value={factory.type}>
              {factory.name}
            </option>
          ))}
        </select>

        {showDescription && selectedFactoryInfo && (
          <p className="mt-2 text-sm text-gray-500 dark:text-gray-400">
            {selectedFactoryInfo.description}
          </p>
        )}
      </div>
    );
  }

  return (
    <div className="w-full">
      <h3 className="text-lg font-semibold text-gray-800 dark:text-white mb-4">
        Выбор фабрики создания функций
      </h3>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
        {factories.map((factory) => {
          const isSelected = selectedFactory === factory.type;

          return (
            <div
              key={factory.type}
              onClick={() => !disabled && onFactoryChange(factory.type)}
              className={`p-4 border-2 rounded-xl cursor-pointer transition-all ${
                isSelected
                  ? 'border-blue-500 dark:border-blue-400 bg-blue-50 dark:bg-blue-900/20'
                  : 'border-gray-200 dark:border-gray-700 hover:border-gray-300 dark:hover:border-gray-600'
              } ${disabled ? 'opacity-50 cursor-not-allowed' : ''}`}
              role="radio"
              aria-checked={isSelected}
              tabIndex={disabled ? -1 : 0}
              onKeyDown={(e) => {
                if (!disabled && (e.key === 'Enter' || e.key === ' ')) {
                  e.preventDefault();
                  onFactoryChange(factory.type);
                }
              }}
            >
              <div className="flex items-start gap-3">
                <div className={`w-10 h-10 rounded-full flex items-center justify-center text-xl ${
                  isSelected
                    ? 'bg-blue-100 dark:bg-blue-800 text-blue-600 dark:text-blue-300'
                    : 'bg-gray-100 dark:bg-gray-800 text-gray-500 dark:text-gray-400'
                }`}>
                  {factory.icon}
                </div>
                <div className="flex-1">
                  <div className="flex items-center justify-between">
                    <h4 className="font-semibold text-gray-800 dark:text-white">
                      {factory.name}
                    </h4>
                    {isSelected && (
                      <span className="px-2 py-1 text-xs bg-blue-100 dark:bg-blue-800 text-blue-700 dark:text-blue-300 rounded">
                        Выбрано
                      </span>
                    )}
                  </div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-2">
                    {factory.description}
                  </p>

                  <div className="mt-4 space-y-2">
                    <div>
                      <p className="text-xs font-medium text-green-600 dark:text-green-400 mb-1">
                        Преимущества:
                      </p>
                      <ul className="text-xs text-gray-600 dark:text-gray-400 space-y-1">
                        {factory.pros.map((pro, idx) => (
                          <li key={idx} className="flex items-center gap-2">
                            <span className="text-green-500">✓</span>
                            {pro}
                          </li>
                        ))}
                      </ul>
                    </div>
                    <div>
                      <p className="text-xs font-medium text-red-600 dark:text-red-400 mb-1">
                        Недостатки:
                      </p>
                      <ul className="text-xs text-gray-600 dark:text-gray-400 space-y-1">
                        {factory.cons.map((con, idx) => (
                          <li key={idx} className="flex items-center gap-2">
                            <span className="text-red-500">✗</span>
                            {con}
                          </li>
                        ))}
                      </ul>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Индикатор текущего выбора */}
      {selectedFactoryInfo && (
        <div className="p-4 bg-gray-50 dark:bg-gray-800/50 rounded-lg border border-gray-200 dark:border-gray-700">
          <div className="flex items-center gap-3 mb-2">
            <div className="w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-900 flex items-center justify-center">
              <span className="text-blue-600 dark:text-blue-300">⚙️</span>
            </div>
            <div>
              <h4 className="font-semibold text-gray-800 dark:text-white">
                Текущая фабрика: {selectedFactoryInfo.name}
              </h4>
              <p className="text-sm text-gray-600 dark:text-gray-400">
                Все новые функции будут создаваться с использованием этой фабрики
              </p>
            </div>
          </div>

          {/* Рекомендация по выбору */}
          <div className="mt-3 p-3 bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded">
            <p className="text-sm text-blue-700 dark:text-blue-300">
              <strong>Рекомендация:</strong>{' '}
              {selectedFactory === FactoryType.ARRAY
                ? 'Используйте массив для функций с фиксированным количеством точек и частым доступом по индексу.'
                : 'Используйте связный список для функций с частыми вставками/удалениями точек.'
              }
            </p>
          </div>
        </div>
      )}

      {/* Предупреждение при изменении фабрики */}
      {!disabled && (
        <div className="mt-4 p-3 bg-yellow-50 dark:bg-yellow-900/20 border border-yellow-200 dark:border-yellow-800 rounded">
          <p className="text-sm text-yellow-700 dark:text-yellow-300">
            <strong>Внимание:</strong> Изменение фабрики повлияет только на вновь создаваемые функции.
            Существующие функции останутся неизменными.
          </p>
        </div>
      )}
    </div>
  );
};

export default FunctionFactorySelector;