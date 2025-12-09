export interface ValidationResult {
  isValid: boolean;
  type?: string;
  message?: string;
}

export const validateSize = (size: string): ValidationResult => {
  // Проверка на пустоту
  if (!size.trim()) {
    return {
      isValid: false,
      type: 'EMPTY_FIELD',
      message: 'Пожалуйста, введите количество точек'
    };
  }

  // Проверка на число
  const num = parseInt(size);
  if (isNaN(num)) {
    return {
      isValid: false,
      type: 'INVALID_NUMBER',
      message: 'Количество точек должно быть числом'
    };
  }

  // Проверка на отрицательное значение
  if (num < 0) {
    return {
      isValid: false,
      type: 'NEGATIVE_SIZE',
      message: 'Количество точек не может быть отрицательным'
    };
  }

  // Проверка минимального значения
  if (num < 2) {
    return {
      isValid: false,
      type: 'INVALID_NUMBER',
      message: 'Минимальное количество точек - 2'
    };
  }

  // Проверка на слишком большое значение (предупреждение, не ошибка)
  if (num > 10000) {
    return {
      isValid: true, // Все равно разрешаем, но предупреждаем
      type: 'TOO_LARGE_SIZE',
      message: `Вы ввели ${num} точек. Это может замедлить работу.`
    };
  }

  return { isValid: true };
};

export const validatePoints = (points: Array<{x: number, y: number}>): ValidationResult => {
  if (points.length < 2) {
    return {
      isValid: false,
      type: 'INVALID_NUMBER',
      message: 'Функция должна содержать минимум 2 точки'
    };
  }

  // Проверка на уникальность X
  const xValues = points.map(p => p.x);
  const uniqueX = new Set(xValues);

  if (uniqueX.size !== xValues.length) {
    return {
      isValid: false,
      type: 'DUPLICATE_X',
      message: 'Значения X должны быть уникальными'
    };
  }

  // Проверка на сортировку X (не обязательно, но желательно)
  for (let i = 1; i < xValues.length; i++) {
    if (xValues[i] < xValues[i - 1]) {
      return {
        isValid: false,
        type: 'INVALID_NUMBER',
        message: 'Значения X должны быть упорядочены по возрастанию'
      };
    }
  }

  return { isValid: true };
};

export const validatePointsCount = (count: string): ValidationResult => {
  const num = parseInt(count);

  if (isNaN(num)) {
    return {
      isValid: false,
      type: 'INVALID_NUMBER',
      message: 'Количество точек должно быть числом'
    };
  }

  if (num < 2) {
    return {
      isValid: false,
      type: 'INVALID_NUMBER',
      message: 'Минимум 2 точки'
    };
  }

  if (num > 100000) {
    return {
      isValid: false,
      type: 'TOO_LARGE_SIZE',
      message: 'Слишком большое количество точек (максимум 100000)'
    };
  }

  return { isValid: true };
};

export const validateInterval = (left: string, right: string): ValidationResult => {
  const leftNum = parseFloat(left);
  const rightNum = parseFloat(right);

  if (isNaN(leftNum) || isNaN(rightNum)) {
    return {
      isValid: false,
      type: 'INVALID_NUMBER',
      message: 'Границы интервала должны быть числами'
    };
  }

  if (leftNum >= rightNum) {
    return {
      isValid: false,
      type: 'INVALID_INTERVAL',
      message: 'Левая граница должна быть меньше правой'
    };
  }

  return { isValid: true };
};