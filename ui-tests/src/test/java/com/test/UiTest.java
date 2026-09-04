package com.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UiTest {

    static WebDriver driver;
    static WebDriverWait wait;
    static String basePath;

    @BeforeAll
    static void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Путь к файлу
        String path = System.getProperty("user.dir") + "/src/test/java/com/test/resources/qa-test (1).html";
        basePath = "file://" + path;
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) driver.quit();
    }

    // ================================
    // ХЕЛПЕРЫ (чтобы не дублировать код)
    // ================================

    private void openPage() {
        driver.get(basePath);
    }

    private void login(String email, String password) {
        wait.until(d -> d.findElement(By.id("loginEmail"))).sendKeys(email);
        driver.findElement(By.id("loginPassword")).sendKeys(password);
        driver.findElement(By.id("authButton")).click();
    }

    private void waitForDataPage() {
        wait.until(d -> d.findElement(By.id("inputsPage")).isDisplayed());
    }

    private void addData(String email, String name, String gender, 
                         boolean check11, boolean check12, String radio) {
        driver.findElement(By.id("dataEmail")).clear();
        driver.findElement(By.id("dataEmail")).sendKeys(email);
        
        driver.findElement(By.id("dataName")).clear();
        driver.findElement(By.id("dataName")).sendKeys(name);
        
        new Select(driver.findElement(By.id("dataGender"))).selectByVisibleText(gender);
        
        if (check11) driver.findElement(By.id("dataCheck11")).click();
        if (check12) driver.findElement(By.id("dataCheck12")).click();
        
        if (radio != null) {
            driver.findElement(By.id(radio)).click();
        }
        
        driver.findElement(By.id("dataSend")).click();
    }

    // ================================
    // ТЕСТЫ: АВТОРИЗАЦИЯ
    // ================================

    @Test
    @Order(1)
    void testValidLogin() {
        openPage();
        login("test@protei.ru", "test");
        waitForDataPage();
        
        assertThat(driver.findElement(By.id("inputsPage")).isDisplayed()).isTrue();
        System.out.println("✅ testValidLogin пройден");
    }

    @Test
    @Order(2)
    void testEmptyEmail() {
        openPage();
        login("", "test");
        
        WebElement error = wait.until(d -> d.findElement(By.id("emailFormatError")));
        assertThat(error.getText()).contains("Неверный формат E-Mail");
        System.out.println("✅ testEmptyEmail пройден");
    }

    @Test
    @Order(3)
    void testInvalidEmailFormat() {
        openPage();
        login("wrong-email", "test");
        
        WebElement error = wait.until(d -> d.findElement(By.id("emailFormatError")));
        assertThat(error.getText()).contains("Неверный формат E-Mail");
        System.out.println("✅ testInvalidEmailFormat пройден");
    }

    @Test
    @Order(4)
    void testWrongPassword() {
        openPage();
        login("test@protei.ru", "wrong");
        
        WebElement error = wait.until(d -> d.findElement(By.id("invalidEmailPassword")));
        assertThat(error.getText()).contains("Неверный E-Mail или пароль");
        System.out.println("✅ testWrongPassword пройден");
    }

    @Test
    @Order(5)
    void testEmptyPassword() {
        openPage();
        login("test@protei.ru", "");
        
        WebElement error = wait.until(d -> d.findElement(By.id("invalidEmailPassword")));
        assertThat(error.getText()).contains("Неверный E-Mail или пароль");
        System.out.println("✅ testEmptyPassword пройден");
    }

    // ================================
    // ТЕСТЫ: ФОРМА ДАННЫХ
    // ================================

    @Test
    @Order(6)
    void testAddValidData() {
        openPage();
        login("test@protei.ru", "test");
        waitForDataPage();
        
        addData("test@protei.ru", "Иван Петров", "Мужской", true, false, "dataSelect22");
        
        wait.until(d -> {
            WebElement row = d.findElement(By.cssSelector("#dataTable tbody tr:last-child"));
            return row.getText().contains("test@protei.ru");
        });
        
        WebElement row = driver.findElement(By.cssSelector("#dataTable tbody tr:last-child"));
        assertThat(row.getText()).contains("test@protei.ru");
        assertThat(row.getText()).contains("Иван Петров");
        System.out.println("✅ testAddValidData пройден");
    }

    @Test
    @Order(7)
    void testEmptyEmailInForm() {
        openPage();
        login("test@protei.ru", "test");
        waitForDataPage();
        
        addData("", "Иван", "Мужской", false, false, null);
        
        WebElement error = wait.until(d -> d.findElement(By.id("emailFormatError")));
        assertThat(error.getText()).contains("Неверный формат E-Mail");
        System.out.println("✅ testEmptyEmailInForm пройден");
    }

    @Test
    @Order(8)
    void testInvalidEmailInForm() {
        openPage();
        login("test@protei.ru", "test");
        waitForDataPage();
        
        addData("wrong-email", "Иван", "Мужской", false, false, null);
        
        WebElement error = wait.until(d -> d.findElement(By.id("emailFormatError")));
        assertThat(error.getText()).contains("Неверный формат E-Mail");
        System.out.println("✅ testInvalidEmailInForm пройден");
    }

    @Test
    @Order(9)
    void testEmptyName() {
        openPage();
        login("test@protei.ru", "test");
        waitForDataPage();
        
        addData("test@protei.ru", "", "Мужской", false, false, null);
        
        WebElement error = wait.until(d -> d.findElement(By.id("blankNameError")));
        assertThat(error.getText()).contains("Поле имя не может быть пустым");
        System.out.println("✅ testEmptyName пройден");
    }

    @Test
    @Order(10)
    void testNoCheckboxes() {
        openPage();
        login("test@protei.ru", "test");
        waitForDataPage();
        
        addData("test@protei.ru", "Сергей", "Женский", false, false, "dataSelect21");
        
        WebElement row = wait.until(d -> {
            WebElement r = d.findElement(By.cssSelector("#dataTable tbody tr:last-child"));
            return r.getText().contains("Сергей") ? r : null;
        });
        
        assertThat(row.getText()).contains("Нет"); // Выбор 1 — "Нет"
        System.out.println("✅ testNoCheckboxes пройден");
    }

    @Test
    @Order(11)
    void testMultipleCheckboxes() {
        openPage();
        login("test@protei.ru", "test");
        waitForDataPage();
        
        addData("test@protei.ru", "Анна", "Женский", true, true, "dataSelect23");
        
        WebElement row = wait.until(d -> {
            WebElement r = d.findElement(By.cssSelector("#dataTable tbody tr:last-child"));
            return r.getText().contains("Анна") ? r : null;
        });
        
        assertThat(row.getText()).contains("1.1, 1.2");
        assertThat(row.getText()).contains("2.3");
        System.out.println("✅ testMultipleCheckboxes пройден");
    }
}