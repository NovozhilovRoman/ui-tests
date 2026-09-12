package steps;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static hooks.Hooks.driver;
import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;

public class AuthSteps {
    private static final Logger log = LoggerFactory.getLogger(AuthSteps.class);

    private final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    private final String basePath = System.getProperty("user.dir")
            + "/src/test/resources/qa-test.html";

    @Дано("открыть страницу авторизации")
    public void openAuthPage() {
        log.info(">>> Открываю страницу: {}", basePath);
        driver.get("file://" + basePath);
    }

    @Когда("ввести логин {string} и пароль {string}")
    public void enterCredentials(String email, String password) {
        log.info(">>> Ввожу логин: '{}' и пароль: '{}'", email, password);
        WebElement emailField = wait.until(d -> d.findElement(By.id("loginEmail")));
        emailField.clear();
        emailField.sendKeys(email);

        WebElement passwordField = driver.findElement(By.id("loginPassword"));
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    @Когда("нажать кнопку Вход")
    public void clickAuthButton() {
        log.info(">>> Нажимаю кнопку Вход");
        driver.findElement(By.id("authButton")).click();
    }

    @Тогда("проверить, что отображается страница с формой данных")
    public void seeDataPage() {
        log.info(">>> Проверяю страницу с формой данных");
        wait.until(d -> d.findElement(By.id("inputsPage")).isDisplayed());
        assertThat(driver.findElement(By.id("inputsPage")).isDisplayed()).isTrue();
        log.info(">>> Страница с данными отобразилась");
    }

    @Тогда("проверить, что отображается ошибка {string}")
    public void seeError(String expectedError) {
        log.info(">>> Проверяю ошибку: '{}'", expectedError);
        WebElement error = wait.until(d -> {
            if (!d.findElements(By.id("emailFormatError")).isEmpty()) {
                return d.findElement(By.id("emailFormatError"));
            }
            return d.findElement(By.id("invalidEmailPassword"));
        });
        log.info(">>> Фактическая ошибка: '{}'", error.getText());
        assertThat(error.getText()).contains(expectedError);
    }
}