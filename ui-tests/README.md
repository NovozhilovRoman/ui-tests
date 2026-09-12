# UI Автотесты для qa-test.html

Автотесты на Java + Selenium + Cucumber для HTML-страницы `qa-test.html`.

## Стек

- Java 17
- Selenium WebDriver 4.15
- Cucumber 7 (Gherkin)
- JUnit 4 (Vintage Engine)
- AssertJ
- Allure
- Logback + SLF4J
- WebDriverManager
- Maven

## Запуск тестов

```bash
mvn clean test
```

Ожидаемый результат: `Tests run: 17, Failures: 0, Errors: 0`

## Просмотр отчёта Allure

```bash
mvn allure:serve
```

После запуска в консоли появится ссылка вида `http://127.0.0.1:xxxx`.

Открой её: **Ctrl + Click** по ссылке в терминале Codespace.