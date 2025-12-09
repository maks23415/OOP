import React from 'react';

interface LoadingOverlayProps {
  isLoading: boolean;
  message?: string;
  progress?: number; // 0-100
  showSpinner?: boolean;
  showProgressBar?: boolean;
  transparent?: boolean;
  fullScreen?: boolean;
  zIndex?: number;
}

const LoadingOverlay: React.FC<LoadingOverlayProps> = ({
  isLoading,
  message = 'Загрузка...',
  progress,
  showSpinner = true,
  showProgressBar = false,
  transparent = false,
  fullScreen = false,
  zIndex = 50
}) => {
  if (!isLoading) return null;

  const content = (
    <div className="flex flex-col items-center justify-center">
      {showSpinner && (
        <div className="relative mb-4">
          {/* Внешнее кольцо */}
          <div className="w-16 h-16 border-4 border-gray-200 dark:border-gray-700 rounded-full"></div>
          {/* Вращающееся кольцо */}
          <div className="absolute top-0 left-0 w-16 h-16 border-4 border-blue-500 dark:border-blue-400
                        rounded-full border-t-transparent animate-spin"></div>
          {/* Внутреннее кольцо (опционально) */}
          <div className="absolute top-2 left-2 w-12 h-12 border-2 border-blue-300 dark:border-blue-500
                        rounded-full border-b-transparent animate-spin-slow"></div>
        </div>
      )}

      {message && (
        <p className="text-lg font-medium text-gray-800 dark:text-white mb-2">
          {message}
        </p>
      )}

      {showProgressBar && progress !== undefined && (
        <div className="w-64 mt-4">
          <div className="flex justify-between text-sm text-gray-600 dark:text-gray-400 mb-1">
            <span>Прогресс</span>
            <span>{Math.round(progress)}%</span>
          </div>
          <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2 overflow-hidden">
            <div
              className="bg-blue-500 dark:bg-blue-400 h-full rounded-full transition-all duration-300"
              style={{ width: `${progress}%` }}
            ></div>
          </div>
        </div>
      )}

      {/* Анимация точек (опционально) */}
      {!showProgressBar && (
        <div className="flex space-x-1 mt-2">
          {[0, 1, 2].map((i) => (
            <div
              key={i}
              className="w-2 h-2 bg-blue-500 dark:bg-blue-400 rounded-full animate-bounce"
              style={{ animationDelay: `${i * 0.1}s` }}
            ></div>
          ))}
        </div>
      )}
    </div>
  );

  if (fullScreen) {
    return (
      <div
        className={`fixed inset-0 flex items-center justify-center ${
          transparent ? 'bg-black/30' : 'bg-white/90 dark:bg-gray-900/95'
        }`}
        style={{ zIndex }}
        role="status"
        aria-live="polite"
        aria-label="Идет загрузка"
      >
        {content}
      </div>
    );
  }

  return (
    <div
      className={`absolute inset-0 flex items-center justify-center ${
        transparent ? 'bg-black/20' : 'bg-white/80 dark:bg-gray-800/90'
      } rounded-lg backdrop-blur-sm`}
      style={{ zIndex }}
      role="status"
      aria-live="polite"
      aria-label="Идет загрузка"
    >
      {content}
    </div>
  );
};

// Дополнительный компонент для встроенной загрузки
export const InlineLoading: React.FC<{
  size?: 'sm' | 'md' | 'lg';
  text?: string;
}> = ({ size = 'md', text }) => {
  const sizeClasses = {
    sm: 'w-4 h-4 border-2',
    md: 'w-6 h-6 border-3',
    lg: 'w-8 h-8 border-4'
  };

  return (
    <div className="flex items-center gap-2">
      <div className="relative">
        <div className={`${sizeClasses[size]} border-gray-200 dark:border-gray-700 rounded-full`}></div>
        <div className={`${sizeClasses[size]} border-blue-500 dark:border-blue-400
                       absolute top-0 left-0 rounded-full border-t-transparent animate-spin`}></div>
      </div>
      {text && <span className="text-sm text-gray-600 dark:text-gray-400">{text}</span>}
    </div>
  );
};

// Компонент для загрузки скелетона
export const SkeletonLoader: React.FC<{
  type?: 'text' | 'card' | 'table' | 'graph';
  count?: number;
}> = ({ type = 'text', count = 1 }) => {
  const renderSkeleton = () => {
    switch (type) {
      case 'text':
        return (
          <div className="space-y-3">
            {Array.from({ length: count }).map((_, i) => (
              <div
                key={i}
                className="h-4 bg-gray-200 dark:bg-gray-700 rounded animate-pulse"
                style={{ width: `${80 - i * 10}%` }}
              ></div>
            ))}
          </div>
        );

      case 'card':
        return (
          <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-12 h-12 bg-gray-200 dark:bg-gray-700 rounded-full animate-pulse"></div>
              <div className="flex-1 space-y-2">
                <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-3/4 animate-pulse"></div>
                <div className="h-3 bg-gray-200 dark:bg-gray-700 rounded w-1/2 animate-pulse"></div>
              </div>
            </div>
            <div className="space-y-2">
              <div className="h-3 bg-gray-200 dark:bg-gray-700 rounded animate-pulse"></div>
              <div className="h-3 bg-gray-200 dark:bg-gray-700 rounded animate-pulse"></div>
              <div className="h-3 bg-gray-200 dark:bg-gray-700 rounded w-2/3 animate-pulse"></div>
            </div>
          </div>
        );

      case 'table':
        return (
          <div className="space-y-3">
            {/* Заголовок таблицы */}
            <div className="flex gap-4">
              {Array.from({ length: 4 }).map((_, i) => (
                <div
                  key={i}
                  className="h-6 bg-gray-200 dark:bg-gray-700 rounded flex-1 animate-pulse"
                ></div>
              ))}
            </div>
            {/* Строки таблицы */}
            {Array.from({ length: count }).map((_, rowIdx) => (
              <div key={rowIdx} className="flex gap-4">
                {Array.from({ length: 4 }).map((_, colIdx) => (
                  <div
                    key={colIdx}
                    className="h-8 bg-gray-200 dark:bg-gray-700 rounded flex-1 animate-pulse"
                  ></div>
                ))}
              </div>
            ))}
          </div>
        );

      case 'graph':
        return (
          <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4">
            <div className="h-6 bg-gray-200 dark:bg-gray-700 rounded w-1/3 mb-4 animate-pulse"></div>
            <div className="h-64 bg-gray-200 dark:bg-gray-700 rounded animate-pulse"></div>
          </div>
        );

      default:
        return null;
    }
  };

  return renderSkeleton();
};

export default LoadingOverlay;