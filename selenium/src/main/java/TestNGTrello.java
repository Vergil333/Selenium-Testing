import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestNGTrello {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeTest
    public void setup() {
        //String pathToGeckoDrive = "/home/vergil333/IdeaProjects/Selenium-Testing/selenium/geckodriver_linux"; // Change to match your path
        //System.setProperty("webdriver.gecko.driver", pathToGeckoDrive);
        //driver = new FirefoxDriver();

        String pathToChromeDrive = "/home/vergil333/IdeaProjects/Selenium-Testing/selenium/chromedriver_linux"; // Change to match your path
        System.setProperty("webdriver.chrome.driver", pathToChromeDrive);
        driver = new ChromeDriver();

        wait = new WebDriverWait(driver, 7);
    }

    @Test
    private void login() {
        driver.get("https://trello.com/login");
        if (driver.findElements(By.id("user-password")).size() > 0) {
            driver.findElement(By.id("user-password")).click();
        }

        driver.findElement(By.id("user")).sendKeys("mojtestovaciucet1@gmail.com");
        driver.findElement(By.id("password")).sendKeys("BOnubsIldAdHoa6");
        driver.findElement(By.id("login")).submit();

        // Make sure we are on board menu
        WebElement boardHref = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/martinmachava3/boards']")));
        if (!boardHref.getAttribute("class").equals("_3C9rwrEaxzhr8w _1gsiCYfUL0OjDP")) {  // if not highlighted
            boardHref.click();
        }

        String boardHrefClass = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/martinmachava3/boards']"))).getAttribute("class");

        Assert.assertEquals(boardHrefClass, "_3C9rwrEaxzhr8w _1gsiCYfUL0OjDP");
    }

    @AfterTest
    private void closeBrowser() {
        driver.close();
    }
}
