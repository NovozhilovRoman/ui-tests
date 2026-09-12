package steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ru.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static org.assertj.core.api.Assertions.assertThat;
import static hooks.Hooks.driver;

public class DataSteps {

    private final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    @Когда("заполнить форму:")
    public void fillForm(DataTable dataTable) {
        var map = dataTable.asMap(String.class, String.class);
        driver.findElement(By.id("dataEmail")).sendKeys(map.get("Email"));
        driver.findElement(By.id("dataName")).sendKeys(map.get("Имя"));
        new Select(driver.findElement(By.id("dataGender"))).selectByVisibleText(map.get("Пол"));

        String check1 = map.get("Выбор 1");
        if ("1.1".equals(check1)) driver.findElement(By.id("dataCheck11")).click();
        if ("1.2".equals(check1)) driver.findElement(By.id("dataCheck12")).click();

        String radio = map.get("Выбор 2");
        if ("2.1".equals(radio)) driver.findElement(By.id("dataSelect21")).click();
        if ("2.2".equals(radio)) driver.findElement(By.id("dataSelect22")).click();
        if ("2.3".equals(radio)) driver.findElement(By.id("dataSelect23")).click();
    }

    @Когда("нажать кнопку Добавить")
    public void clickAddButton() {
        driver.findElement(By.id("dataSend")).click();
    }

    @Тогда("проверить, что в таблице есть запись:")
    public void checkTable(DataTable dataTable) {
        var expected = dataTable.asMap(String.class, String.class);

        wait.until(d -> {
            WebElement row = d.findElement(By.cssSelector("#dataTable tbody tr:last-child"));
            return row.getText().contains(expected.get("Email"));
        });

        WebElement row = driver.findElement(By.cssSelector("#dataTable tbody tr:last-child"));
        assertThat(row.getText()).contains(expected.get("Email"));
        assertThat(row.getText()).contains(expected.get("Имя"));
        assertThat(row.getText()).contains(expected.get("Пол"));
    }

    @Тогда("проверить, что отображается ошибка формы {string}")
    public void seeFormError(String expectedError) {
        WebElement error = wait.until(d -> {
            if (d.findElements(By.id("emailFormatError")).size() > 0) {
                return d.findElement(By.id("emailFormatError"));
            }
            return d.findElement(By.id("blankNameError"));
        });
        assertThat(error.getText()).contains(expectedError);
    }
}