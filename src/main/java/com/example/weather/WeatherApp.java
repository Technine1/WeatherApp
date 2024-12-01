package com.example.weather;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject; // Библиотека для работы с JSON

public class WeatherApp {

    // Ваш реальный API-ключ
    private static final String API_KEY = "261238b3-8a7d-47f6-8ad4-067df98384a4";

    public static void main(String[] args) {
        double lat = 52.37125;
        double lon = 4.89388;

        // Формируем URL с координатами
        String url = String.format("https://api.weather.yandex.ru/v2/forecast?lat=52.37125&lon=4.89388", lat, lon);

        // Создаем HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Создаем HttpRequest с заголовком для API-ключа
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))  // URL с координатами
                .header("X-Yandex-Weather-Key", API_KEY)
                .build();

        try {
            // Отправляем запрос и получаем ответ
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Проверяем, что код ответа 200 (OK)
            if (response.statusCode() == 200) {
                String responseBody = response.body();

                // Печатаем полный ответ в формате JSON
                System.out.println("Полный ответ от API:");
                System.out.println(responseBody);

                // Обрабатываем JSON ответ
                JSONObject jsonResponse = new JSONObject(responseBody);

                // Извлекаем информацию о текущей температуре (temp)
                int currentTemp = jsonResponse.getJSONObject("fact").getInt("temp");
                System.out.println("Текущая температура: " + currentTemp + "°C");

                // Пример получения средней температуры (если нужно)
                double avgTemp = calculateAverageTemperature(jsonResponse, 3);
                System.out.println("Средняя температура за первые 3 часа: " + avgTemp + "°C");

            } else {
                System.out.println("Ошибка запроса: " + response.statusCode());
                System.out.println("Ответ от сервиса: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static double calculateAverageTemperature(JSONObject jsonResponse, int hoursLimit) {
        double sum = 0;
        int count = 0;

        try {
            // Получаем массив прогноза по часам
            var forecasts = jsonResponse.getJSONArray("forecasts").getJSONObject(0);
            var hours = forecasts.getJSONArray("hours");

            // Берем температуру за первые несколько часов
            for (int i = 0; i < Math.min(hours.length(), hoursLimit); i++) {
                sum += hours.getJSONObject(i).getInt("temp");
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count > 0 ? sum / count : 0;
    }
}