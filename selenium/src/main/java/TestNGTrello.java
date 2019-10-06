import java.io.File;
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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dtos.BoardDto;
import dtos.ListDto;

public class TestNGTrello {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    private void setup() {
        //driver = prepareFirefoxDriver();
        driver = prepareChromeDriver();

        wait = new WebDriverWait(driver, 7);

        login();
    }

    @AfterMethod
    private void closeBrowser() {
        driver.close();
    }

    @Test(priority = 0)
    private void createBoardWithSelenium() throws IOException {
        String boardName = "Board - Test 1";

        HashMap<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(200, "OK");

        managers.ApiTrello.deleteAllBoards("board").forEach(object -> Assert.assertEquals(object, expected));

        checkBoardPage();
        createBoard(boardName);
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

        // Remove lists in newly created Board
        HashMap<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(200, "OK");
        managers.ApiTrello.archiveAllLists("lists", newBoard.getId()).forEach(object -> Assert.assertEquals(object, expected));

        checkBoardPage();
        openBoard(boardName);
        archiveAllLists();
        createDemoList(listName);
    }

    @Test(priority = 2)
    private void createEverything() throws IOException {
        String boardName = "Board w/ List and Card- Test 3";
        String expectedBoardName = boardName;
        String listName = "Demo List";
        String expectedListName = listName;
        String cardName = "Demo Card";

        // Create new board via API
        BoardDto newBoard;
        newBoard = managers.ApiTrello.createBoard(boardName);
        Assert.assertNotNull(newBoard, "Board was not created.");
        Assert.assertEquals(newBoard.getName(), expectedBoardName, "Expected name of the board does not match.");


        // Remove lists in newly created Board via API
        HashMap<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(200, "OK");
        managers.ApiTrello.archiveAllLists("lists", newBoard.getId()).forEach(object -> Assert.assertEquals(object, expected));

        // Create new list via API
        ListDto newList;
        newList = managers.ApiTrello.createList(newBoard.getId(), listName);
        Assert.assertNotNull(newList, "List was not created.");
        Assert.assertEquals(newList.getName(), expectedListName, "Expected name of the list does not match.");

        checkBoardPage();
        openBoard(boardName);
        closeBoardMenu();
        createDemoCard(listName, cardName);
        fillDemoCard(cardName);
    }

    @Test(priority = 3)
    private void createEverythingWithSelenium() throws IOException {
        String boardName = "Board Everything with Selenium - Test 4";
        String listName = "Demo List";
        String cardName = "Demo Card";

        checkBoardPage();
        createBoard(boardName); // board is opened automatically after creation
        closeBoardMenu();
        archiveAllLists();
        createDemoList(listName);
        createDemoCard(listName, cardName);
        fillDemoCard(cardName);
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

    private WebDriver prepareFirefoxDriver() {
        File driverFile = new File("src/main/resources/geckodriver_linux");
        String pathToGeckoDrive = driverFile.getAbsolutePath();
        System.setProperty("webdriver.gecko.driver", pathToGeckoDrive);
        return new FirefoxDriver();
    }

    private WebDriver prepareChromeDriver() {
        File driverFile = new File("src/main/resources/chromedriver_linux");
        String pathToChromeDrive = driverFile.getAbsolutePath();
        System.setProperty("webdriver.chrome.driver", pathToChromeDrive);
        return new ChromeDriver();
    }

    private void checkBoardPage() {
        // Make sure we are on board menu
        WebElement boardHref = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/martinmachava3/boards']")));
        if (!boardHref.getAttribute("class").equals("_3C9rwrEaxzhr8w _1gsiCYfUL0OjDP")) {  // if not highlighted
            boardHref.click();
        }

        String boardHrefClass = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/martinmachava3/boards']"))).getAttribute("class");
    }

    private void createBoard(String name) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//ul[@class=\"boards-page-board-section-list\"]" +
                        "/li/div/p/span[text()=\"Create new board\"]" +
                        "/.." +
                        "/.."
        ))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//form[@class=\"create-board-form\"]" +
                        "/div[@class=\"form-container\"]" +
                        "/div[contains(@class,\"board-tile create-board-tile\")]" +
                        "/div/input[@class=\"subtle-input\"]"
        ))).sendKeys(name);

        driver.findElement(By.xpath(
                "//form[@class=\"create-board-form\"]" +
                        "/button[@class=\"button primary\"]" +
                        "/span[text()=\"Create Board\"]"
        )).click();
    }

    private void openBoard(String boardName) {
        // Open Demo Board
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='board-tile-details-name']//div[text()='" + boardName + "']"))).click();
    }

    private void closeBoardMenu() {
        // Close Menu
        if (wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id=\"content\"]/div[contains(@class,\"board-wrapper\")]")))
                .getAttribute("class")
                .contains("is-show-menu")) {
            driver.findElement(By.xpath("//div[@class=\"board-menu-header-content\"]/a[@class=\"board-menu-header-close-button icon-lg icon-close js-hide-sidebar\"]")).click();
        }

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
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='open-add-list js-open-add-list']//span[@class='placeholder']")))
                .click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@class='list-name-input']")))
                .sendKeys(name);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@class='primary mod-list-add-button js-save-edit']")))
                .click();
    }

    private void createDemoCard(String listName, String cardName) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id=\"board\"]/div[@class=\"js-list list-wrapper\"]/div[@class=\"list js-list-content\"]/div[contains(@class,\"list-header js-list-header\")]/textarea[text()=\"" + listName + "\"]/../../a[@class=\"open-card-composer js-open-card-composer\"]/span[@class=\"icon-sm icon-add\"]")))
                .click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id=\"board\"]/div[@class=\"js-list list-wrapper\"]/div[@class=\"list js-list-content\"]/div[contains(@class,\"list-header js-list-header\")]/textarea[text()=\"" + listName + "\"]/../../div[@class=\"list-cards u-fancy-scrollbar u-clearfix js-list-cards js-sortable ui-sortable\"]/div[@class=\"card-composer\"]/div[@class=\"list-card js-composer\"]/div[@class=\"list-card-details u-clearfix\"]/textarea")))
                .sendKeys(cardName);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id=\"board\"]/div[@class=\"js-list list-wrapper\"]/div[@class=\"list js-list-content\"]/div[contains(@class,\"list-header js-list-header\")]/textarea[text()=\"" + listName + "\"]/../../div[@class=\"list-cards u-fancy-scrollbar u-clearfix js-list-cards js-sortable ui-sortable\"]/div[@class=\"card-composer\"]/div[@class=\"cc-controls u-clearfix\"]/div[@class=\"cc-controls-section\"]/input[@value=\"Add Card\"]")))
                .click();
    }

    private void fillDemoCard(String cardName) throws IOException {
        File image = new File("src/main/resources/mot_black_logo.png");
        String imagePath = image.getAbsolutePath();

        // Open card
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id=\"board\"]/div[@class=\"js-list list-wrapper\"]/div[@class=\"list js-list-content\"]/div/a/div/span[@class=\"list-card-title js-card-name\"][text()=\"" + cardName + "\"]")))
                .click();

        // Add checklist
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class=\"window\"]/div/div/div[@class=\"window-sidebar\"]/div[contains(@class,\"window-module\")]/div/a[@class=\"button-link js-add-checklist-menu\"]")))
                .click();
        driver.findElement(By.xpath("//div[@class=\"pop-over is-shown\"]/div/div/div[contains(@class,\"pop-over-content js-pop-over-content\")]/div/div/form/input[@id=\"id-checklist\"]"))
                .sendKeys("Selenium part of the task");
        driver.findElement(By.xpath("//div[@class=\"pop-over is-shown\"]/div/div/div[contains(@class,\"pop-over-content js-pop-over-content\")]/div/div/form/input[@class=\"primary wide confirm js-add-checklist\"]"))
                .click();
        // Add checkbox - description
        driver.findElement(By.xpath("//div[@class=\"editable checklist-new-item u-gutter js-new-checklist-item editing\"]/textarea[@class=\"edit field checklist-new-item-text js-new-checklist-item-input\"]"))
                .sendKeys("Add description");
        driver.findElement(By.xpath("//div[@class=\"editable checklist-new-item u-gutter js-new-checklist-item editing\"]/div[@class=\"checklist-add-controls u-clearfix\"]/input[@class=\"primary confirm mod-submit-edit js-add-checklist-item\"]"))
                .click();
        // Add checkbox - image
        driver.findElement(By.xpath("//div[@class=\"editable checklist-new-item u-gutter js-new-checklist-item editing\"]/textarea[@class=\"edit field checklist-new-item-text js-new-checklist-item-input\"]"))
                .sendKeys("Add image");
        driver.findElement(By.xpath("//div[@class=\"editable checklist-new-item u-gutter js-new-checklist-item editing\"]/div[@class=\"checklist-add-controls u-clearfix\"]/input[@class=\"primary confirm mod-submit-edit js-add-checklist-item\"]"))
                .click();
        // Add checkbox - comment
        driver.findElement(By.xpath("//div[@class=\"editable checklist-new-item u-gutter js-new-checklist-item editing\"]/textarea[@class=\"edit field checklist-new-item-text js-new-checklist-item-input\"]"))
                .sendKeys("Add comment");
        driver.findElement(By.xpath("//div[@class=\"editable checklist-new-item u-gutter js-new-checklist-item editing\"]/div[@class=\"checklist-add-controls u-clearfix\"]/input[@class=\"primary confirm mod-submit-edit js-add-checklist-item\"]"))
                .click();

        // Add description
        driver.findElement(By.xpath("//a[@class=\"description-fake-text-area hide-on-edit js-edit-desc js-hide-with-draft\"][text()=\"Add a more detailed description…\"]"))
                .click();
        driver.findElement(By.xpath("//div[@class=\"description-content js-desc-content\"]/div[@class=\"description-edit edit\"]/textarea[@class=\"field field-autosave js-description-draft description card-description\"]"))
                .sendKeys("Takže vyplnené description by sme mali...");
        driver.findElement(By.xpath("//div[@class=\"description-content js-desc-content\"]/div[@class=\"description-edit edit\"]/div[@class=\"edit-controls u-clearfix\"]/input[@class=\"primary confirm mod-submit-edit js-save-edit\"]"))
                .click();
        // Check description checkbox
        driver.findElement(By.xpath("//div[contains(@class,\"checklist-list\")]/div[@class=\"checklist\"]/div[contains(@class,\"checklist-items-list\")]/div[@class=\"checklist-item\"]/div[contains(@class,\"checklist-item-details\")]/div[contains(@class,\"checklist-item-row\")]/span[text()=\"Add description\"]/../../../div[@class=\"checklist-item-checkbox enabled js-toggle-checklist-item\"]"))
                .click();

        // Add image
        driver.findElement(By.xpath("//form/div[@class=\"comment-frame\"]/div[@class=\"comment-box\"]/textarea[@class=\"comment-box-input js-new-comment-input\"]"))
                .click();
        driver.findElement(By.xpath("//form/div[@class=\"comment-frame\"]/div[@class=\"comment-box\"]/div[@class=\"comment-box-options\"]/a[@class=\"comment-box-options-item js-comment-add-attachment\"]/span[@class=\"icon-sm icon-attachment\"]"))
                .click();
        driver.findElement(By.xpath("//div[@class=\"pop-over is-shown\"]/div[@class=\"no-back\"]/div/div[@class=\"pop-over-content js-pop-over-content u-fancy-scrollbar js-tab-parent\"]/div/div/ul[@class=\"pop-over-list\"]/li[@class=\"uploader\"]/form[@class=\"realfile\"]/input[@class=\"js-attach-file\"]"))
                .sendKeys(imagePath);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class=\"window-module js-attachments-section u-clearfix\"]/div[@class=\"u-gutter\"]/div[@class=\"u-clearfix js-attachment-list ui-sortable\"]/div[@class=\"attachment-thumbnail\"]/a[@class=\"attachment-thumbnail-preview js-open-viewer attachment-thumbnail-preview-is-cover\"]")));
        // Check image checkbox
        driver.findElement(By.xpath("//div[contains(@class,\"checklist-list\")]/div[@class=\"checklist\"]/div[contains(@class,\"checklist-items-list\")]/div[@class=\"checklist-item\"]/div[contains(@class,\"checklist-item-details\")]/div[contains(@class,\"checklist-item-row\")]/span[text()=\"Add image\"]/../../../div[@class=\"checklist-item-checkbox enabled js-toggle-checklist-item\"]"))
                .click();

        // Add comment
        driver.findElement(By.xpath("//form/div[@class=\"comment-frame\"]/div[@class=\"comment-box\"]/textarea[@class=\"comment-box-input js-new-comment-input\"]"))
                .sendKeys("Tak a to by malo byť všetko...");
        driver.findElement(By.xpath("//div[contains(@class,\"new-comment js-new-comment\")]/form/div[@class=\"comment-frame\"]/div[@class=\"comment-box\"]/div[contains(@class,\"comment-controls\")]/input[@class=\"primary confirm mod-no-top-bottom-margin js-add-comment\"]"))
                .click();
        // Check comment checkbox
        driver.findElement(By.xpath("//div[contains(@class,\"checklist-list\")]/div[@class=\"checklist\"]/div[contains(@class,\"checklist-items-list\")]/div[@class=\"checklist-item\"]/div[contains(@class,\"checklist-item-details\")]/div[contains(@class,\"checklist-item-row\")]/span[text()=\"Add comment\"]/../../../div[@class=\"checklist-item-checkbox enabled js-toggle-checklist-item\"]"))
                .click();

        // Wait till checkbox is checked
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,\"checklist-list\")]/div[@class=\"checklist\"]/div[contains(@class,\"checklist-items-list\")]/div[@class=\"checklist-item checklist-item-state-complete\"]/div[contains(@class,\"checklist-item-details\")]/div[contains(@class,\"checklist-item-row\")]/span[text()=\"Add comment\"]/../../../div[@class=\"checklist-item-checkbox enabled js-toggle-checklist-item\"]")));
    }
}
