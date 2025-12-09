import React from 'react';
import { ErrorType } from '../../types/function.types';

interface ErrorModalProps {
  errorType: string;
  message: string;
  onClose: () => void;
}

const ErrorModal: React.FC<ErrorModalProps> = ({ errorType, message, onClose }) => {
  // Локализованные сообщения об ошибках
  const errorTitles: Record<string, string> = {
    'INVALID_NUMBER': 'Некорректное число',
    'NEGATIVE_SIZE': 'Отрицательное количество точек',
    'TOO_LARGE_SIZE': 'Слишком много точек',
    'INVALID_INTERVAL': 'Некорректный интервал',
    'DUPLICATE_X': 'Дублирующиеся значения X',
    'EMPTY_FIELD': 'Пустое поле',
    'SERVER_ERROR': 'Ошибка сервера',
    'DEFAULT': 'Ошибка'
  };

  const title = errorTitles[errorType] || errorTitles.DEFAULT;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-70 flex items-center justify-center z-[100]">
      <div className="bg-white dark:bg-gray-800 rounded-lg p-6 max-w-md w-full mx-4">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-red-100 dark:bg-red-900 rounded-full flex items-center justify-center">
              <span className="text-red-600 dark:text-red-300 text-xl">!</span>
            </div>
            <h3 className="text-xl font-bold text-red-700 dark:text-red-300">
              {title}
            </h3>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 dark:text-gray-500 dark:hover:text-gray-300"
            aria-label="Закрыть окно ошибки"
          >
            ✕
          </button>
        </div>

        <div className="mb-6">
          <p className="text-gray-700 dark:text-gray-300">{message}</p>

          {/* Рекомендации по исправлению */}
          {errorType === 'TOO_LARGE_SIZE' && (
            <div className="mt-4 p-3 bg-yellow-50 dark:bg-yellow-900/20 border border-yellow-200 dark:border-yellow-800 rounded">
              <p className="text-sm text-yellow-700 dark:text-yellow-300">
                <strong>Рекомендация:</strong> Для больших данных используйте файловый импорт или уменьшите количество точек.
              </p>
            </div>
          )}

          {errorType === 'DUPLICATE_X' && (
            <div className="mt-4 p-3 bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded">
              <p className="text-sm text-blue-700 dark:text-blue-300">
                <strong>Решение:</strong> Убедитесь, что все значения X уникальны.
              </p>
            </div>
          )}
        </div>

        <div className="flex justify-end">
          <button
            onClick={onClose}
            className="px-6 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700
                     transition-colors focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-opacity-50"
            autoFocus
          >
            Понятно
          </button>
        </div>
      </div>
    </div>
  );
};

export default ErrorModal;