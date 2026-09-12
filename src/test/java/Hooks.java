package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

public class Hooks {
    private static final Logger log = LoggerFactory.getLogger(Hooks.class);
    public static WebDriver driver;

    @Before
    public void setUp() {
        log.info(">>> Запускаю браузер");
        System.setProperty("webdriver.chrome.silentOutput", "true");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.OFF);

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--log-level=3");
        options.addArguments("--silent");
        driver = new ChromeDriver(options);
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed() && driver instanceof TakesScreenshot) {
            log.info(">>> Тест упал, прикрепляю скриншот");
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Скриншот при падении", new ByteArrayInputStream(screenshot));
        }
        if (driver != null) {
            log.info(">>> Закрываю браузер");
            driver.quit();
        }
    }
}