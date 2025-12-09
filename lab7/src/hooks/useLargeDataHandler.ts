
import { useState, useCallback, useEffect } from 'react';
import { Point } from '../types/function.types';

interface UseLargeDataHandlerOptions {
  initialPoints: Point[];
  maxPoints: number;
  chunkSize: number;
}

export const useLargeDataHandler = (options: UseLargeDataHandlerOptions) => {
  const { initialPoints, maxPoints = 10000, chunkSize = 1000 } = options;

  const [points, setPoints] = useState<Point[]>(initialPoints);
  const [isProcessing, setIsProcessing] = useState(false);
  const [progress, setProgress] = useState(0);
  const [error, setError] = useState<string | null>(null);

  // Проверка на превышение лимита
  const checkLimit = useCallback((newPointsCount: number): boolean => {
    if (newPointsCount > maxPoints) {
      setError(`Превышен лимит в ${maxPoints.toLocaleString()} точек. Текущее количество: ${newPointsCount.toLocaleString()}`);
      return false;
    }
    return true;
  }, [maxPoints]);

  // Постепенная обработка больших данных
  const processInChunks = useCallback(async (
    operation: (chunk: Point[], chunkIndex: number) => Promise<Point[]>,
    description: string
  ) => {
    setIsProcessing(true);
    setError(null);

    try {
      const chunks = [];
      for (let i = 0; i < points.length; i += chunkSize) {
        chunks.push(points.slice(i, i + chunkSize));
      }

      let result: Point[] = [];

      for (let i = 0; i < chunks.length; i++) {
        setProgress((i / chunks.length) * 100);

        // Имитация задержки для больших данных
        if (chunks[i].length > 500) {
          await new Promise(resolve => setTimeout(resolve, 10));
        }

        const processedChunk = await operation(chunks[i], i);
        result = [...result, ...processedChunk];

        // Обновление состояния после каждой порции
        if (i % 5 === 0 || i === chunks.length - 1) {
          setPoints(prev => [...prev.slice(0, i * chunkSize), ...processedChunk]);
        }
      }

      setPoints(result);
      setProgress(100);

      // Сброс прогресса через 2 секунды
      setTimeout(() => setProgress(0), 2000);

    } catch (err: any) {
      setError(`Ошибка при ${description}: ${err.message}`);
    } finally {
      setIsProcessing(false);
    }
  }, [points, chunkSize]);

  // Фильтрация точек
  const filterPoints = useCallback(async (predicate: (point: Point) => boolean) => {
    await processInChunks(
      async (chunk) => chunk.filter(predicate),
      'фильтрации точек'
    );
  }, [processInChunks]);

  // Сортировка точек
  const sortPoints = useCallback(async (compareFn: (a: Point, b: Point) => number) => {
    if (!checkLimit(points.length)) return;

    if (points.length > 5000) {
      // Для больших наборов используем постепенную сортировку
      await processInChunks(
        async (chunk) => [...chunk].sort(compareFn),
        'сортировки точек'
      );
    } else {
      // Для небольших наборов - обычная сортировка
      setPoints([...points].sort(compareFn));
    }
  }, [points, checkLimit, processInChunks]);

  // Добавление точек
  const addPoints = useCallback((newPoints: Point[]) => {
    if (!checkLimit(points.length + newPoints.length)) return;
    setPoints(prev => [...prev, ...newPoints]);
  }, [points.length, checkLimit]);

  // Удаление дубликатов
  const removeDuplicates = useCallback(async () => {
    await processInChunks(
      async (chunk) => {
        const seen = new Set<string>();
        return chunk.filter(point => {
          const key = `${point.x}:${point.y}`;
          if (seen.has(key)) return false;
          seen.add(key);
          return true;
        });
      },
      'удаления дубликатов'
    );
  }, [processInChunks]);

  // Интерполяция пропущенных значений
  const interpolateMissing = useCallback(async () => {
    if (points.length < 2) return;

    await processInChunks(
      async (chunk, chunkIndex) => {
        // Интерполяция линейная
        return chunk.map((point, index) => {
          if (isNaN(point.y)) {
            const globalIndex = chunkIndex * chunkSize + index;
            const prevPoint = points[globalIndex - 1];
            const nextPoint = points[globalIndex + 1];

            if (prevPoint && nextPoint) {
              const slope = (nextPoint.y - prevPoint.y) / (nextPoint.x - prevPoint.x);
              return {
                x: point.x,
                y: prevPoint.y + slope * (point.x - prevPoint.x)
              };
            }
          }
          return point;
        });
      },
      'интерполяции пропущенных значений'
    );
  }, [points, chunkSize, processInChunks]);

  // Сжатие данных (уменьшение количества точек)
  const compressData = useCallback(async (targetCount: number) => {
    if (points.length <= targetCount || targetCount < 2) return;

    const ratio = points.length / targetCount;
    const compressed: Point[] = [];

    for (let i = 0; i < targetCount; i++) {
      const startIdx = Math.floor(i * ratio);
      const endIdx = Math.floor((i + 1) * ratio);
      const segment = points.slice(startIdx, endIdx);

      if (segment.length > 0) {
        // Используем среднее значение сегмента
        const avgX = segment.reduce((sum, p) => sum + p.x, 0) / segment.length;
        const avgY = segment.reduce((sum, p) => sum + p.y, 0) / segment.length;
        compressed.push({ x: avgX, y: avgY });
      }
    }

    setPoints(compressed);
  }, [points]);

  // Статистика
  const getStatistics = useCallback(() => {
    if (points.length === 0) return null;

    const xValues = points.map(p => p.x);
    const yValues = points.map(p => p.y);

    const xMin = Math.min(...xValues);
    const xMax = Math.max(...xValues);
    const yMin = Math.min(...yValues);
    const yMax = Math.max(...yValues);
    const ySum = yValues.reduce((sum, y) => sum + y, 0);
    const yAvg = ySum / yValues.length;
    const yStd = Math.sqrt(
      yValues.reduce((sum, y) => sum + Math.pow(y - yAvg, 2), 0) / yValues.length
    );

    // Поиск дубликатов
    const xSet = new Set(xValues);
    const duplicateCount = xValues.length - xSet.size;

    return {
      totalPoints: points.length,
      xRange: { min: xMin, max: xMax, span: xMax - xMin },
      yRange: { min: yMin, max: yMax, span: yMax - yMin },
      yStatistics: {
        average: yAvg,
        stdDev: yStd,
        sum: ySum
      },
      duplicates: duplicateCount,
      memoryEstimate: points.length * 16, // примерно 16 байт на точку
      isSorted: xValues.every((x, i) => i === 0 || x >= xValues[i - 1])
    };
  }, [points]);

  // Оптимизация для рендеринга
  const getPointsForRendering = useCallback((maxPointsToShow = 1000) => {
    if (points.length <= maxPointsToShow) {
      return points;
    }

    // Для отображения берем каждую N-ную точку
    const step = Math.ceil(points.length / maxPointsToShow);
    return points.filter((_, index) => index % step === 0);
  }, [points]);

  return {
    points,
    setPoints,
    isProcessing,
    progress,
    error,
    clearError: () => setError(null),
    filterPoints,
    sortPoints,
    addPoints,
    removeDuplicates,
    interpolateMissing,
    compressData,
    getStatistics,
    getPointsForRendering,
    checkLimit
  };
};