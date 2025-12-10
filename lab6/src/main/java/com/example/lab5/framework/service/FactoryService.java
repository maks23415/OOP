// FactoryService.java
package com.example.lab5.framework.service;

import com.example.lab5.framework.dto.FactoryResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FactoryService {

    private String currentFactory = "array"; // По умолчанию массив

    public FactoryResponse getCurrentFactory() {
        FactoryResponse response = new FactoryResponse();
        response.setCurrentFactory(currentFactory);
        response.setMessage("Текущая фабрика: " +
                (currentFactory.equals("array") ? "Массив" : "Связный список"));
        return response;
    }

    public FactoryResponse setFactory(String factoryType) {
        if (!factoryType.equals("array") && !factoryType.equals("linked_list")) {
            throw new IllegalArgumentException("Неверный тип фабрики. Допустимые значения: array, linked_list");
        }

        this.currentFactory = factoryType;

        FactoryResponse response = new FactoryResponse();
        response.setCurrentFactory(currentFactory);
        response.setMessage("Фабрика изменена на: " +
                (currentFactory.equals("array") ? "Массив" : "Связный список"));
        return response;
    }

    public Map<String, Object> getFactoryInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("currentFactory", currentFactory);

        Map<String, String> arrayFactory = new HashMap<>();
        arrayFactory.put("type", "array");
        arrayFactory.put("name", "Массив");
        arrayFactory.put("description", "Хранит точки в массиве. Быстрый доступ по индексу.");

        Map<String, String> linkedListFactory = new HashMap<>();
        linkedListFactory.put("type", "linked_list");
        linkedListFactory.put("name", "Связный список");
        linkedListFactory.put("description", "Хранит точки в связном списке. Быстрая вставка/удаление.");

        info.put("availableFactories", new Map[]{arrayFactory, linkedListFactory});
        info.put("defaultFactory", "array");

        return info;
    }
}