
export const mathFunctionApi = {
  // Получение всех доступных математических функций с информацией
  getAllMathFunctions: async (): Promise<MathFunctionInfo[]> => {
    const response = await apiClient.get('/math-functions/all');
    return response.data;
  },

  // Получение Map функций для фронтенда
  getMathFunctionMap: async (): Promise<Record<string, {
    label: string;
    instance: any;
  }>> => {
    const response = await apiClient.get('/math-functions/map');
    return response.data;
  },

  // Создание конкретной функции по ключу
  createMathFunctionInstance: async (functionKey: string): Promise<any> => {
    const response = await apiClient.post('/math-functions/create', { functionKey });
    return response.data.instance;
  },

  // Предпросмотр функции
  previewMathFunction: async (
    mathFunctionKey: string,
    pointsCount: number,
    leftBound: number,
    rightBound: number
  ): Promise<Point[]> => {
    const response = await apiClient.post('/math-functions/preview', {
      mathFunctionKey,
      pointsCount,
      leftBound,
      rightBound,
    });
    return response.data.points;
  },

  // Получение функции по локализованному названию
  getFunctionByLocalizedName: async (localizedName: string): Promise<any> => {
    const response = await apiClient.get(`/math-functions/by-name/${encodeURIComponent(localizedName)}`);
    return response.data;
  },

  // Получение локализованного названия по ключу
  getLocalizedName: async (functionKey: string): Promise<string> => {
    const response = await apiClient.get(`/math-functions/localized-name/${functionKey}`);
    return response.data.name;
  },

  // Получение ключа по локализованному названию
  getKeyByLocalizedName: async (localizedName: string): Promise<string> => {
    const response = await apiClient.get(`/math-functions/key-by-name/${encodeURIComponent(localizedName)}`);
    return response.data.key;
  },
};