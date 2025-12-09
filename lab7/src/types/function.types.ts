export interface MathFunctionInfo {
  key: string;
  label: string;           // Локализованное название
  description: string;
  example: string;
  category: string;
  functionType: string;    // Тип функции (SqrFunction, IdentityFunction и т.д.)
  parameters: any[];       // Параметры конструктора
}

export interface MathFunctionMap {
  [key: string]: {
    label: string;
    instance: any;         // Экземпляр функции
    factory: () => any;    // Фабрика для создания экземпляра
  };
}