
import { useState, useEffect, useCallback } from 'react';
import { MathFunctionMapper, getGlobalMathFunctionMapper } from '../utils/mathFunctionMapper';
import { mathFunctionApi } from '../api/functionApi';
import { MathFunctionInfo } from '../types/function.types';

export const useMathFunctionMapper = () => {
  const [mapper, setMapper] = useState<MathFunctionMapper | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Загрузка и инициализация маппера
  useEffect(() => {
    const loadMapper = async () => {
      try {
        setIsLoading(true);

        // Получаем глобальный маппер
        const globalMapper = getGlobalMathFunctionMapper();

        // Загружаем функции с сервера
        const functions = await mathFunctionApi.getAllMathFunctions();

        // Очищаем старые маппинги
        globalMapper.clear();

        // Добавляем новые функции в маппер
        for (const func of functions) {
          try {
            // Создаем экземпляр функции
            const instance = await mathFunctionApi.createMathFunctionInstance(func.key);

            // Добавляем в маппер
            globalMapper.addMapping(func.label, instance, func);
          } catch (err) {
            console.warn(`Не удалось загрузить функцию ${func.label}:`, err);
          }
        }

        setMapper(globalMapper);
        setError(null);
      } catch (err: any) {
        setError(`Ошибка загрузки функций: ${err.message}`);
        console.error('Ошибка при загрузке маппера:', err);
      } finally {
        setIsLoading(false);
      }
    };

    loadMapper();
  }, []);

  // Получить функцию по локализованному названию
  const getFunctionByLocalizedName = useCallback((localizedName: string) => {
    if (!mapper) return null;
    return mapper.getFunctionByLocalizedName(localizedName);
  }, [mapper]);

  // Получить локализованное название по функции
  const getLocalizedNameByFunction = useCallback((functionInstance: any) => {
    if (!mapper) return null;
    return mapper.getLocalizedNameByFunction(functionInstance);
  }, [mapper]);

  // Получить информацию о функции
  const getFunctionInfo = useCallback((localizedName: string): MathFunctionInfo | null => {
    if (!mapper) return null;
    return mapper.getFunctionInfo(localizedName);
  }, [mapper]);

  // Получить все локализованные названия
  const getAllLocalizedNames = useCallback((): string[] => {
    if (!mapper) return [];
    return mapper.getAllLocalizedNames();
  }, [mapper]);

  // Проверить наличие функции
  const hasFunction = useCallback((localizedName: string): boolean => {
    if (!mapper) return false;
    return mapper.hasFunction(localizedName);
  }, [mapper]);

  return {
    mapper,
    isLoading,
    error,
    getFunctionByLocalizedName,
    getLocalizedNameByFunction,
    getFunctionInfo,
    getAllLocalizedNames,
    hasFunction,
    refresh: () => {
      // Обновление маппера
      const globalMapper = getGlobalMathFunctionMapper();
      setMapper(globalMapper);
    }
  };
};