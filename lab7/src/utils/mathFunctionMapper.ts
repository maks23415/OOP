
import { MathFunctionInfo } from '../types/function.types';

/**
 * Класс для управления Map соответствий между локализованными названиями
 * и объектами MathFunction
 */
export class MathFunctionMapper {
  private map: Map<string, any> = new Map();
  private reverseMap: Map<any, string> = new Map();
  private infoMap: Map<string, MathFunctionInfo> = new Map();

  /**
   * Добавить соответствие
   * @param localizedName Локализованное название (ключ для пользователя)
   * @param functionInstance Объект MathFunction
   * @param functionInfo Дополнительная информация о функции
   */
  addMapping(
    localizedName: string,
    functionInstance: any,
    functionInfo?: MathFunctionInfo
  ): void {
    this.map.set(localizedName, functionInstance);
    this.reverseMap.set(functionInstance, localizedName);

    if (functionInfo) {
      this.infoMap.set(localizedName, functionInfo);
    }
  }

  /**
   * Получить объект функции по локализованному названию
   */
  getFunctionByLocalizedName(localizedName: string): any | null {
    return this.map.get(localizedName) || null;
  }

  /**
   * Получить локализованное название по объекту функции
   */
  getLocalizedNameByFunction(functionInstance: any): string | null {
    return this.reverseMap.get(functionInstance) || null;
  }

  /**
   * Получить информацию о функции по названию
   */
  getFunctionInfo(localizedName: string): MathFunctionInfo | null {
    return this.infoMap.get(localizedName) || null;
  }

  /**
   * Получить все локализованные названия (отсортированные по алфавиту)
   */
  getAllLocalizedNames(): string[] {
    return Array.from(this.map.keys()).sort((a, b) => a.localeCompare(b, 'ru-RU'));
  }

  /**
   * Получить все объекты функций
   */
  getAllFunctionInstances(): any[] {
    return Array.from(this.map.values());
  }

  /**
   * Проверить существование функции по названию
   */
  hasFunction(localizedName: string): boolean {
    return this.map.has(localizedName);
  }

  /**
   * Удалить функцию из Map
   */
  removeFunction(localizedName: string): boolean {
    const instance = this.map.get(localizedName);
    if (instance) {
      this.reverseMap.delete(instance);
      this.infoMap.delete(localizedName);
      return this.map.delete(localizedName);
    }
    return false;
  }

  /**
   * Очистить все соответствия
   */
  clear(): void {
    this.map.clear();
    this.reverseMap.clear();
    this.infoMap.clear();
  }

  /**
   * Получить размер Map
   */
  get size(): number {
    return this.map.size;
  }

  /**
   * Преобразовать в объект для отладки
   */
  toObject(): Record<string, any> {
    const result: Record<string, any> = {};
    this.map.forEach((value, key) => {
      result[key] = {
        instance: value,
        type: value.constructor.name,
        info: this.infoMap.get(key)
      };
    });
    return result;
  }

  /**
   * Создать Map из массива функций
   */
  static createFromArray(
    functions: Array<{localizedName: string; instance: any; info?: MathFunctionInfo}>
  ): MathFunctionMapper {
    const mapper = new MathFunctionMapper();
    functions.forEach(({ localizedName, instance, info }) => {
      mapper.addMapping(localizedName, instance, info);
    });
    return mapper;
  }
}

/**
 * Глобальный экземпляр маппера (синглтон)
 */
let globalMapper: MathFunctionMapper | null = null;

export const getGlobalMathFunctionMapper = (): MathFunctionMapper => {
  if (!globalMapper) {
    globalMapper = new MathFunctionMapper();
    // Инициализация по умолчанию
    initializeDefaultMappings(globalMapper);
  }
  return globalMapper;
};

/**
 * Инициализация дефолтных маппингов
 */
function initializeDefaultMappings(mapper: MathFunctionMapper): void {
  // Эти маппинги могут быть загружены из API или конфигурации
  const defaultMappings = [
    {
      localizedName: 'Квадратичная функция',
      instance: { calculate: (x: number) => x * x },
      info: {
        key: 'sqr',
        label: 'Квадратичная функция',
        description: 'Функция y = x²',
        example: 'f(x) = x²',
        category: 'алгебраические',
        functionType: 'SqrFunction',
        parameters: []
      }
    },
    {
      localizedName: 'Тождественная функция',
      instance: { calculate: (x: number) => x },
      info: {
        key: 'identity',
        label: 'Тождественная функция',
        description: 'Функция y = x',
        example: 'f(x) = x',
        category: 'алгебраические',
        functionType: 'IdentityFunction',
        parameters: []
      }
    },
    {
      localizedName: 'Синус',
      instance: { calculate: Math.sin },
      info: {
        key: 'sin',
        label: 'Синус',
        description: 'Тригонометрическая функция синус',
        example: 'f(x) = sin(x)',
        category: 'тригонометрические',
        functionType: 'SinFunction',
        parameters: []
      }
    },
    {
      localizedName: 'Косинус',
      instance: { calculate: Math.cos },
      info: {
        key: 'cos',
        label: 'Косинус',
        description: 'Тригонометрическая функция косинус',
        example: 'f(x) = cos(x)',
        category: 'тригонометрические',
        functionType: 'CosFunction',
        parameters: []
      }
    },
    {
      localizedName: 'Экспонента',
      instance: { calculate: Math.exp },
      info: {
        key: 'exp',
        label: 'Экспонента',
        description: 'Экспоненциальная функция',
        example: 'f(x) = e^x',
        category: 'экспоненциальные',
        functionType: 'ExpFunction',
        parameters: []
      }
    }
  ];

  defaultMappings.forEach(mapping => {
    mapper.addMapping(mapping.localizedName, mapping.instance, mapping.info);
  });
}