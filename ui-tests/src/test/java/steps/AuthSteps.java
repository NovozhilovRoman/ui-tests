package steps;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import static hooks.Hooks.driver;
import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;

public class AuthSteps {

    private final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    private final String basePath = System.getProperty("user.dir")
        + "/src/test/resources/qa-test.html";

    @Дано("открыть страницу авторизации")
    public void openAuthPage() {
        driver.get("file://" + basePath);
    }

    @Когда("ввести логин {string} и пароль {string}")
    public void enterCredentials(String email, String password) {
        wait.until(d -> d.findElement(By.id("loginEmail"))).sendKeys(email);
        driver.findElement(By.id("loginPassword")).sendKeys(password);
    }

    @Когда("нажать кнопку Вход")
    public void clickAuthButton() {
        driver.findElement(By.id("authButton")).click();
    }

    @Тогда("проверить, что отображается страница с формой данных")
    public void seeDataPage() {
        wait.until(d -> d.findElement(By.id("inputsPage")).isDisplayed());
        assertThat(driver.findElement(By.id("inputsPage")).isDisplayed()).isTrue();
    }

    @Тогда("проверить, что отображается ошибка {string}")
    public void seeError(String expectedError) {
        WebElement error = wait.until(d -> {
            if (d.findElements(By.id("emailFormatError")).size() > 0) {
                return d.findElement(By.id("emailFormatError"));
            }
            return d.findElement(By.id("invalidEmailPassword"));
        });
        assertThat(error.getText()).contains(expectedError);
    }
}