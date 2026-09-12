package steps;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static hooks.Hooks.driver;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;

public class DataSteps {
    private static final Logger log = LoggerFactory.getLogger(DataSteps.class);

    private final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    @Когда("заполнить форму:")
    public void fillForm(DataTable dataTable) {
        var map = dataTable.asMaps(String.class, String.class).get(0);

        String email = map.get("Email");
        String name = map.get("Имя");
        String gender = map.get("Пол");
        String check1 = map.get("Выбор 1");
        String radio = map.get("Выбор 2");

        if (email == null || "[empty]".equals(email)) email = "";
        if (name == null || "[empty]".equals(name)) name = "";
        if (gender == null || "[empty]".equals(gender)) gender = "Мужской";

        log.info(">>> Заполняю форму: Email='{}', Имя='{}', Пол='{}', Выбор1='{}', Выбор2='{}'",
                email, name, gender, check1, radio);

        WebElement emailField = driver.findElement(By.id("dataEmail"));
        emailField.clear();
        emailField.sendKeys(email);

        WebElement nameField = driver.findElement(By.id("dataName"));
        nameField.clear();
        nameField.sendKeys(name);

        new Select(driver.findElement(By.id("dataGender"))).selectByVisibleText(gender);

        if ("1.1".equals(check1)) driver.findElement(By.id("dataCheck11")).click();
        if ("1.2".equals(check1)) driver.findElement(By.id("dataCheck12")).click();

        if ("2.1".equals(radio)) driver.findElement(By.id("dataSelect21")).click();
        if ("2.2".equals(radio)) driver.findElement(By.id("dataSelect22")).click();
        if ("2.3".equals(radio)) driver.findElement(By.id("dataSelect23")).click();

        log.info(">>> Фактическое значение поля dataName: '{}'",
                driver.findElement(By.id("dataName")).getAttribute("value"));
    }

    @Когда("нажать кнопку Добавить")
    public void clickAddButton() {
        log.info(">>> Нажимаю кнопку Добавить");
        driver.findElement(By.id("dataSend")).click();
    }

    @Когда("закрыть модальное окно")
    public void closeModal() {
        log.info(">>> Закрываю модальное окно");
        wait.until(d -> !d.findElements(By.cssSelector(".uk-modal-close")).isEmpty());
        driver.findElement(By.cssSelector(".uk-modal-close")).click();
        wait.until(d -> d.findElements(By.cssSelector(".uk-modal")).isEmpty());
        log.info(">>> Модальное окно закрыто");
    }

    @Тогда("проверить, что в таблице есть запись:")
    public void checkTable(DataTable dataTable) {
        var expected = dataTable.asMaps(String.class, String.class).get(0);
        log.info(">>> Проверяю, что в таблице есть запись: Email='{}', Имя='{}', Пол='{}'",
                expected.get("Email"), expected.get("Имя"), expected.get("Пол"));

        wait.until(d -> {
            WebElement row = d.findElement(By.cssSelector("#dataTable tbody tr:last-child"));
            return row.getText().contains(expected.get("Email"));
        });

        WebElement row = driver.findElement(By.cssSelector("#dataTable tbody tr:last-child"));
        log.info(">>> Фактическая строка в таблице: '{}'", row.getText());

        assertThat(row.getText()).contains(expected.get("Email"));
        assertThat(row.getText()).contains(expected.get("Имя"));
        assertThat(row.getText()).contains(expected.get("Пол"));
    }

    @Тогда("проверить, что отображается ошибка формы {string}")
    public void seeFormError(String expectedError) {
        log.info(">>> Проверяю ошибку формы: '{}'", expectedError);

        WebElement error = wait.until(d -> {
            if (!d.findElements(By.id("emailFormatError")).isEmpty()) {
                return d.findElement(By.id("emailFormatError"));
            }
            if (!d.findElements(By.id("blankNameError")).isEmpty()) {
                return d.findElement(By.id("blankNameError"));
            }
            return null;
        });

        log.info(">>> Фактическая ошибка: '{}'", error.getText());
        assertThat(error.getText()).contains(expectedError);
    }
}