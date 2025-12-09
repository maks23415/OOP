import React, { useState } from 'react';
import CreateFromArrays from './CreateFromArrays';
import CreateFromMathFunction from './CreateFromMathFunction';
import { FactoryType } from '../../../types/function.types';

interface CreateTabulatedFunctionProps {
  factoryType: FactoryType;
  onFunctionCreated: (func: any) => void;
}

const CreateTabulatedFunction: React.FC<CreateTabulatedFunctionProps> = ({
  factoryType,
  onFunctionCreated
}) => {
  const [showCreateFromArrays, setShowCreateFromArrays] = useState(false);
  const [showCreateFromMath, setShowCreateFromMath] = useState(false);

  const handleSuccess = (createdFunction: any) => {
    onFunctionCreated(createdFunction);
    setShowCreateFromArrays(false);
    setShowCreateFromMath(false);
  };

  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold text-gray-800 dark:text-white mb-8">
        Создание табулированной функции
      </h1>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-4xl mx-auto">
        {/* Вариант 1: Из массивов */}
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-lg p-6 border border-gray-200 dark:border-gray-700">
          <div className="text-center mb-6">
            <div className="w-16 h-16 bg-blue-100 dark:bg-blue-900 rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="text-blue-600 dark:text-blue-300 text-2xl">📊</span>
            </div>
            <h3 className="text-xl font-semibold text-gray-800 dark:text-white mb-2">
              Из массивов значений
            </h3>
            <p className="text-gray-600 dark:text-gray-400">
              Создайте функцию, указав значения X и Y вручную
            </p>
          </div>
          <ul className="space-y-3 mb-6">
            <li className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <span className="w-5 h-5 bg-blue-100 dark:bg-blue-800 rounded-full flex items-center justify-center text-xs">1</span>
              Введите количество точек
            </li>
            <li className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <span className="w-5 h-5 bg-blue-100 dark:bg-blue-800 rounded-full flex items-center justify-center text-xs">2</span>
              Заполните таблицу значений
            </li>
            <li className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <span className="w-5 h-5 bg-blue-100 dark:bg-blue-800 rounded-full flex items-center justify-center text-xs">3</span>
              Нажмите "Создать функцию"
            </li>
          </ul>
          <button
            onClick={() => setShowCreateFromArrays(true)}
            className="w-full py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700
                     transition-colors font-medium"
          >
            Создать из массивов
          </button>
        </div>

        {/* Вариант 2: Из математической функции */}
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-lg p-6 border border-gray-200 dark:border-gray-700">
          <div className="text-center mb-6">
            <div className="w-16 h-16 bg-green-100 dark:bg-green-900 rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="text-green-600 dark:text-green-300 text-2xl">📈</span>
            </div>
            <h3 className="text-xl font-semibold text-gray-800 dark:text-white mb-2">
              Из математической функции
            </h3>
            <p className="text-gray-600 dark:text-gray-400">
              Создайте функцию, табулировав математическое выражение
            </p>
          </div>
          <ul className="space-y-3 mb-6">
            <li className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <span className="w-5 h-5 bg-green-100 dark:bg-green-800 rounded-full flex items-center justify-center text-xs">1</span>
              Выберите математическую функцию
            </li>
            <li className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <span className="w-5 h-5 bg-green-100 dark:bg-green-800 rounded-full flex items-center justify-center text-xs">2</span>
              Укажите интервал и количество точек
            </li>
            <li className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <span className="w-5 h-5 bg-green-100 dark:bg-green-800 rounded-full flex items-center justify-center text-xs">3</span>
              Нажмите "Создать функцию"
            </li>
          </ul>
          <button
            onClick={() => setShowCreateFromMath(true)}
            className="w-full py-3 bg-green-600 text-white rounded-lg hover:bg-green-700
                     transition-colors font-medium"
          >
            Создать из математической функции
          </button>
        </div>
      </div>

      {/* Информация о фабрике */}
      <div className="mt-8 p-4 bg-gray-50 dark:bg-gray-800/50 rounded-lg border border-gray-200 dark:border-gray-700 max-w-2xl mx-auto">
        <p className="text-center text-gray-600 dark:text-gray-400">
          <strong>Текущая фабрика:</strong> {
            factoryType === FactoryType.ARRAY ?
            'Массив' :
            'Связный список'
          }
        </p>
        <p className="text-center text-sm text-gray-500 dark:text-gray-500 mt-1">
          Фабрику можно изменить в настройках
        </p>
      </div>

      {/* Модальные окна */}
      {showCreateFromArrays && (
        <CreateFromArrays
          isOpen={showCreateFromArrays}
          onClose={() => setShowCreateFromArrays(false)}
          onSuccess={handleSuccess}
          factoryType={factoryType}
        />
      )}

      {showCreateFromMath && (
        <CreateFromMathFunction
          isOpen={showCreateFromMath}
          onClose={() => setShowCreateFromMath(false)}
          onSuccess={handleSuccess}
          factoryType={factoryType}
        />
      )}
    </div>
  );
};

export default CreateTabulatedFunction;