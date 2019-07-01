import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import dtos.BoardDto;

public class TestNGTrello {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeTest
    public void setup() {
        //driver = prepareFirefoxDriver();
        driver = prepareChromeDriver();

        wait = new WebDriverWait(driver, 7);
    }

    private WebDriver prepareFirefoxDriver() {
        String pathToGeckoDrive = "/home/vergil333/IdeaProjects/Selenium-Testing/selenium/geckodriver_linux"; // Change to match your path
        System.setProperty("webdriver.gecko.driver", pathToGeckoDrive);
        return new FirefoxDriver();
    }

    private WebDriver prepareChromeDriver() {
        String pathToChromeDrive = "/home/vergil333/IdeaProjects/Selenium-Testing/selenium/chromedriver_linux"; // Change to match your path
        System.setProperty("webdriver.chrome.driver", pathToChromeDrive);
        return new ChromeDriver();
    }

    @Test(priority = 0)
    private void createBoard() throws IOException {
        HashMap<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(200, "OK");

        managers.ApiTrello.deleteAllObjects("board").forEach(object -> Assert.assertEquals(object, expected));

        BoardDto newBoard;
        newBoard = managers.ApiTrello.createBoard("Board - Test 1");
        Assert.assertNotNull(newBoard, "Board was not created.");
    }

    @Test(priority = 1)
    private void createBoardAndList() throws IOException {
        BoardDto newBoard;
        String boardName = "Board w/ List- Test 2";
        String expectedName = boardName;
        String listName = "Demo List";

        newBoard = managers.ApiTrello.createBoard(boardName);
        Assert.assertNotNull(newBoard, "Board was not created.");
        Assert.assertEquals(newBoard.getName(), expectedName, "Expected name of the board does not match.");

        login();

        openBoard(boardName);

        archiveAllLists();

        createDemoList(listName);
    }

    @Test(priority = 2)
    private void createBoardAndListAndCard() throws IOException {
        String boardName = "Board w/ List and Card- Test 3";
        String expectedName = boardName;
        String listName = "Demo List";
        String cardName = "Demo Card";

        // Create new board
        BoardDto newBoard;
        newBoard = managers.ApiTrello.createBoard(boardName);
        Assert.assertNotNull(newBoard, "Board was not created.");
        Assert.assertEquals(newBoard.getName(), expectedName, "Expected name of the board does not match.");

        // Remove(archive) Lists inside just created board


        login();

        openBoard(boardName);

        //archiveAllLists();
        //createDemoList(listName);

        createDemoCard(listName, cardName);

        //fillDemoCard();
    }

    @Test(priority = 3)
    public void startMainTest() {
        login();

        openBoard("Board - Test 4");

        archiveAllLists();

        createDemoListAndCard();
    }

    private void login() {
        driver.get("https://trello.com/login");
        if (driver.findElements(By.id("user-password")).size() > 0) {
            driver.findElement(By.id("user-password")).click();
        }

        driver.findElement(By.id("user")).sendKeys("mojtestovaciucet1@gmail.com");
        driver.findElement(By.id("password")).sendKeys("BOnubsIldAdHoa6");
        driver.findElement(By.id("login")).submit();
    }

    private void openBoard(String boardName) {
        // Make sure we are on board menu
        WebElement boardHref = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/martinmachava3/boards']")));
        if (!boardHref.getAttribute("class").equals("_3C9rwrEaxzhr8w _1gsiCYfUL0OjDP")) {  // if not highlighted
            boardHref.click();
        }

        String boardHrefClass = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/martinmachava3/boards']"))).getAttribute("class");

        Assert.assertEquals(boardHrefClass, "_3C9rwrEaxzhr8w _1gsiCYfUL0OjDP");

        // Open Demo Board
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='board-tile-details-name']//div[text()='" + boardName + "']"))).click();

        // Close Menu
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class=\"board-menu-header-content\"]/a[@class=\"board-menu-header-close-button icon-lg icon-close js-hide-sidebar\"]"))).click();

        // Wait for Demo Board to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id=\"content\"]/div[@class='board-wrapper']")));
    }

    private void archiveAllLists() {
        List<WebElement> listMenus = new ArrayList<WebElement>(driver.findElements(By.xpath("//div[@class='list-header-extras']")));
        int elementSize = listMenus.size();
        listMenus.forEach(this::archiveList);

        // Make sure to remove even lists that are not visible in window
        if (elementSize >= 5) {
            archiveAllLists();
        }
    }

    private void archiveList(WebElement element) {
        element.click();
        driver.findElement(By.xpath("//a[@class='js-close-list'][text()='Archive This List']")).click();
    }

    private void createDemoListAndCard() {
        createDemoList("Demo List");
        createDemoCard("Demo List", "Demo Card");
    }

    private void createDemoList(String name) {
        driver.findElement(By.xpath("//a[@class='open-add-list js-open-add-list']//span[@class='placeholder']"))
                .click();

        driver.findElement(By.xpath("//input[@class='list-name-input']"))
                .sendKeys(name);

        driver.findElement(By.xpath("//input[@class='primary mod-list-add-button js-save-edit']"))
                .click();
    }

    private void createDemoCard(String listName, String cardName) {
        driver.findElement(By.xpath("//div[@id=\"board\"]/div[@class=\"js-list list-wrapper\"]/div[@class=\"list js-list-content\"]/div[@class=\"list-header js-list-header u-clearfix is-menu-shown\"]/textarea[text()=\"Demo List\"]/../..//a[@class='open-card-composer js-open-card-composer']"))
                .click();

        driver.findElement(By.xpath("//div[@id=\"board\"]/div[@class=\"js-list list-wrapper\"]/div[@class=\"list js-list-content\"]/div[@class=\"list-header js-list-header u-clearfix is-menu-shown\"]/textarea[text()=\"Demo List\"]/../../div[@class=\"list-cards u-fancy-scrollbar u-clearfix js-list-cards js-sortable ui-sortable\"]/div[@class=\"card-composer\"]/div[@class=\"list-card js-composer\"]/div[@class=\"list-card-details u-clearfix\"]/textarea"))
                .sendKeys(cardName);

        driver.findElement(By.xpath("//div[@id=\"board\"]/div[@class=\"js-list list-wrapper\"]/div[@class=\"list js-list-content\"]/div[@class=\"list-header js-list-header u-clearfix is-menu-shown\"]/textarea[text()=\"Demo List\"]/../../div[@class=\"list-cards u-fancy-scrollbar u-clearfix js-list-cards js-sortable ui-sortable\"]/div[@class=\"card-composer\"]/div[@class=\"cc-controls u-clearfix\"]/div[@class=\"cc-controls-section\"]/input[@value=\"Add Card\"]"))
                .click();
    }

    @AfterTest
    private void closeBrowser() {
        // removeAllBoards();
        driver.close();
    }
}
