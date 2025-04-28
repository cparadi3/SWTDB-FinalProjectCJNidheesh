import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import io.github.bonigarcia.wdm.WebDriverManager;

//Link: http://localhost:8080/onlinebookstore/
//user default login: user: shashi password: shashi
//admin login: user: Admin password: Admin
//Div for user captcha: //*[@id="CaptchaDiv"]
//logout id = logout

public class guiTests{
    
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080/onlinebookstore/";
    private static final String BASEUSERNAMEANDPASSWORD = "shashi";
    private static final String ADMINUSERNAMEANDPASSWORD = "Admin";
    private static final String LOGINSUBMITBUTTON = "//*[@id=\"theform\"]/table/tbody/tr[3]/td/input[3]";
    
    private static final By NAV_LOGIN = By.xpath("/html/body/header/nav/div/ul/li[2]/span/a");
    private static final By CUSTOMER_LOGIN_LINK = By.xpath("/html/body/table/tbody/tr[3]/td/a");
    private static final By ADMIN_LOGIN_LINK = By.xpath("/html/body/table/tbody/tr[2]/td/a");
    private static final By USER_AVAILABLE_BOOKS = By.xpath("//*[@id=\"books\"]");
    private static final By USER_CART = By.xpath("//*[@id=\"cart\"]");
    private static final By ADMIN_STORE_BOOKS = By.xpath("//*[@id=\"storebooks\"]");
    private static final By REGISTER_USER_NAV_BAR = By.xpath("//*[@id=\"navbarNav\"]/ul/li[3]/span/a");
    
    @BeforeAll
    public static void setupClass() {
        // Automatically manage ChromeDriver binaries
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testHomePageTitle() {
        driver.get(BASE_URL);
        String title = driver.getTitle();
        assertEquals("Book Store", title, "Home page title should be 'Online Bookstore'");
    }
    public void loginUser() {
        driver.findElement(NAV_LOGIN).click();
        wait.until(ExpectedConditions.elementToBeClickable(NAV_LOGIN)).click();
        WebElement customerLoginPage = driver.findElement(CUSTOMER_LOGIN_LINK);
        customerLoginPage.click();
        WebElement usernameBox = driver.findElement(By.id("userName"));
        usernameBox.click();
        usernameBox.sendKeys(BASEUSERNAMEANDPASSWORD);
        WebElement passwordBox = driver.findElement(By.id("Password"));
        passwordBox.click();
        passwordBox.sendKeys(BASEUSERNAMEANDPASSWORD);
        WebElement captchaInput = driver.findElement(By.id("CaptchaInput"));
        WebElement captcha = driver.findElement(By.id("CaptchaDiv"));
        String captchaValue = captcha.getText();
        captchaInput.sendKeys(captchaValue);
        WebElement loginSubmit = driver.findElement(By.xpath(LOGINSUBMITBUTTON));
        loginSubmit.click();
    }
    
    public void loginAdmin() {
        driver.findElement(NAV_LOGIN).click();
        wait.until(ExpectedConditions.elementToBeClickable(NAV_LOGIN)).click();
        WebElement adminLoginPage = driver.findElement(ADMIN_LOGIN_LINK);
        adminLoginPage.click();
        WebElement usernameBox = driver.findElement(By.id("userName"));
        usernameBox.click();
        usernameBox.sendKeys(ADMINUSERNAMEANDPASSWORD);
        WebElement passwordBox = driver.findElement(By.id("Password"));
        passwordBox.click();
        passwordBox.sendKeys(ADMINUSERNAMEANDPASSWORD);
        WebElement adminLoginButton = driver.findElement(By.xpath("/html/body/form/table/tbody/tr[3]/td/input[3]"));
        adminLoginButton.click();
    }
    
    public void fillCheckoutForm() {
        WebElement nameField = driver.findElement(By.xpath("//*[@id=\"fname\"]"));
        nameField.sendKeys("name");
        WebElement emailField = driver.findElement(By.xpath("//*[@id=\"email\"]"));
        emailField.sendKeys("email@email.com");
        WebElement addressField = driver.findElement(By.xpath("//*[@id=\"adr\"]"));
        addressField.sendKeys("3400 N Charles St.");
        WebElement cityField = driver.findElement(By.xpath("//*[@id=\"city\"]"));
        cityField.sendKeys("Baltimore");
        WebElement stateField = driver.findElement(By.xpath("//*[@id=\"state\"]"));
        stateField.sendKeys("MD");
        WebElement cardName = driver.findElement(By.xpath("//*[@id=\"cname\"]"));
        cardName.sendKeys("name");
        WebElement cardNumber = driver.findElement(By.xpath("//*[@id=\"ccnum\"]"));
        cardNumber.sendKeys("1111111111111111");
        WebElement expirationMonth = driver.findElement(By.xpath("//*[@id=\"expmonth\"]"));
        expirationMonth.sendKeys("July");
        WebElement expYear = driver.findElement(By.xpath("//*[@id=\"expmonth\"]"));
        expYear.sendKeys("2029");
        WebElement cvv = driver.findElement(By.xpath("//*[@id=\"cvv\"]"));
        cvv.sendKeys("000");
    }
    
    //Tests
    //Validate page navigation prior to login
    //Base user login tests: successful login, login with incorrect information, login with admin info, login with newly registered user
    
    @Test
    public void verifyLogoutMessageDisplayBaseUser() {
        driver.get(BASE_URL);
        loginUser();
        WebElement logoutButton = driver.findElement(By.id("logout"));
        logoutButton.click();
        WebElement logoutMessage = driver.findElement(By.xpath("/html/body/table/tbody/tr/td"));
        String message = "Successfully logged out!";
        assertTrue(message.equals(logoutMessage.getText()));
    }
    
    @Test
    public void verifyLogoutMessageDisplayAdminUser() {
        driver.get(BASE_URL);
        loginAdmin();
        WebElement logoutButton = driver.findElement(By.id("logout"));
        logoutButton.click();
        WebElement logoutMessage = driver.findElement(By.xpath("/html/body/table/tbody/tr/td"));
        String message = "Successfully logged out!";
        assertTrue(message.equals(logoutMessage.getText()));
    }
    
    @Test
    public void validatePageNavigationNoSignIn() {
        driver.get(BASE_URL);
        WebElement homeButton = driver.findElement(By.xpath("//*[@id=\"navbarNav\"]/ul/li[1]/span/a"));
        homeButton.click();
        String currentURL = driver.getCurrentUrl();
        assertTrue(currentURL.contains(BASE_URL + "index.html"));
        WebElement registerPageButton = driver.findElement(By.xpath("//*[@id=\"navbarNav\"]/ul/li[3]/span/a"));
        registerPageButton.click();
        currentURL = driver.getCurrentUrl();
        assertTrue(currentURL.contains(BASE_URL + "CustomerRegister.html"));
        WebElement loginPageButton = driver.findElement(By.xpath("//*[@id=\"navbarNav\"]/ul/li[2]/span/a"));
        loginPageButton.click();
        currentURL = driver.getCurrentUrl();
        assertTrue(currentURL.contains(BASE_URL + "login.html"));
    }
    
    //Fails (should)
    @Test
    public void validateNavFromLoginPageBroken() {
        driver.get(BASE_URL);
        WebElement loginPageButton = driver.findElement(By.xpath("//*[@id=\"navbarNav\"]/ul/li[2]/span/a"));
        loginPageButton.click();
        String currentURL = driver.getCurrentUrl();
        assertTrue(currentURL.contains(BASE_URL + "login.html"));
        WebElement customerLoginPageButton = driver.findElement(By.xpath("/html/body/table/tbody/tr[3]/td/a"));
        customerLoginPageButton.click();
        currentURL = driver.getCurrentUrl();
        assertTrue(currentURL.contains(BASE_URL+ "CustomerLogin.html"));
        loginPageButton.click();
        WebElement adminLoginPageButton = driver.findElement(By.xpath("/html/body/table/tbody/tr[2]/td/a"));
        adminLoginPageButton.click();
        currentURL = driver.getCurrentUrl();
        assertTrue(currentURL.contains(BASE_URL + "SellerLogin.html"));
        loginPageButton.click();
        WebElement registerUserPageButton = driver.findElement(By.xpath("/html/body/table/tbody/tr[4]/td/a"));
        registerUserPageButton.click();
        currentURL = driver.getCurrentUrl();
        assertTrue(currentURL.contains(BASE_URL + "CustomerRegister.html"));
    }
    
    @Test
    public void verifyUserWelcomeMessage() {
        driver.get(BASE_URL);
        loginUser();
        WebElement userWelcomeMessage = driver.findElement(By.xpath("/html/body/table/tbody/tr/td/p"));
        String welcomeMessage = "Welcome Shashi, Happy Learning !!";
        String actualWelcomeMessage = userWelcomeMessage.getText();
        assertTrue(actualWelcomeMessage.contains(welcomeMessage));
    }
    
   @Test
   public void validateUserNav() {
       driver.get(BASE_URL);
       loginUser();
       String currentURL = driver.getCurrentUrl();
       assertTrue(currentURL.contains(BASE_URL + "userlog"));
       driver.findElement(USER_AVAILABLE_BOOKS).click();
       currentURL = driver.getCurrentUrl();
       assertTrue(currentURL.contains(BASE_URL + "viewbook"));
       driver.findElement(USER_CART).click();
       currentURL = driver.getCurrentUrl();
       assertTrue(currentURL.contains(BASE_URL + "cart"));
   }
   
   //Fails due to user book
   @Test
   public void verifyUserCanAddBookToCart() {
       driver.get(BASE_URL);
       loginUser();
       driver.findElement(USER_AVAILABLE_BOOKS).click();
       WebElement bookCard = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]"));
       WebElement bookID = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div[2]/div[1]/p/span[1]"));
       WebElement bookAuthor = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div[1]/div/p/span"));
       WebElement bookPrice = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div[2]/div[2]/p/span"));
       WebElement bookTitle = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div[1]/div/h5"));
       String title = bookTitle.getText();
       String price = bookPrice.getText();
       String author = bookAuthor.getText();
       String bookIDNumber = bookID.getText();
       WebElement addBookToCart = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div[2]/div[2]/form/input[3]"));
       addBookToCart.click();
       driver.findElement(USER_CART).click();
       WebElement cartBookID = driver.findElement(By.xpath("/html/body/table/tbody/tr[1]/th"));
       WebElement cartBookTitle = driver.findElement(By.xpath("/html/body/table/tbody/tr[1]/td[1]"));
       WebElement cartAuthor = driver.findElement(By.xpath("/html/body/table/tbody/tr[1]/td[2]"));
       WebElement cartBookPrice = driver.findElement(By.xpath("/html/body/table/tbody/tr[1]/td[3]"));
       String cartID = cartBookID.getText();
       String cartTitle = cartBookTitle.getText();
       String cartAuth = cartAuthor.getText();
       String cartPrice = cartBookPrice.getText();
       assertTrue(title.equals(cartTitle));
       assertTrue(price.contains(cartPrice));
       assertTrue(author.equals(cartAuth));
       assertTrue(bookIDNumber.contains(cartID));
   }
   
   //Fails due to user book, passes otherwise
   @Test
   public void verifyDecreasingBookQuantityInCartToZeroRemovesFromCart() {
       driver.get(BASE_URL);
       loginUser();
       driver.findElement(USER_AVAILABLE_BOOKS).click();
       WebElement addBookToCart = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div[2]/div[2]/form/input[3]"));
       addBookToCart.click();
       driver.findElement(USER_CART).click();
       WebElement decreaseBookQuantity = driver.findElement(By.xpath("/html/body/table/tbody/tr[1]/td[4]/form/button[1]"));
       decreaseBookQuantity.click();
       WebElement noItemsText = driver.findElement(By.xpath("/html/body/table/tbody/tr/th"));
       String emptyCart = noItemsText.getText();
       assertTrue(emptyCart.equals(" No Items In the Cart "));
   }
   
   //Fails due to added books not showing up in cart
   @Test
   public void attemptCheckoutNoInfoFilledOut() {
       driver.get(BASE_URL);
       loginUser();
       driver.findElement(USER_AVAILABLE_BOOKS).click();
       WebElement addBookToCart = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div[2]/div[2]/form/input[3]"));
       addBookToCart.click();
       driver.findElement(USER_CART).click();
       WebElement proceedToPaymentBtn = driver.findElement(By.xpath("/html/body/div[2]/form/input"));
       proceedToPaymentBtn.click();
       WebElement payAndOrderBtn = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/form/input"));
       String currentURL = driver.getCurrentUrl();
       payAndOrderBtn.click();
       assertTrue(currentURL.equals(driver.getCurrentUrl()));
   }
   
   //Failing due to user book not showing up in cart
   @Test
   public void verifyCheckoutWithCorrectInfo() {
       driver.get(BASE_URL);
       loginUser();
       driver.findElement(USER_AVAILABLE_BOOKS).click();
       WebElement addBookToCart = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div[2]/div[2]/form/input[3]"));
       addBookToCart.click();
       driver.findElement(USER_CART).click();
       WebElement proceedToPaymentBtn = driver.findElement(By.xpath("/html/body/div[2]/form/input"));
       proceedToPaymentBtn.click();
       WebElement nameField = driver.findElement(By.xpath("//*[@id=\"fname\"]"));
       nameField.sendKeys("name");
       WebElement emailField = driver.findElement(By.xpath("//*[@id=\"email\"]"));
       emailField.sendKeys("email@email.com");
       WebElement addressField = driver.findElement(By.xpath("//*[@id=\"adr\"]"));
       addressField.sendKeys("3400 N Charles St.");
       WebElement cityField = driver.findElement(By.xpath("//*[@id=\"city\"]"));
       cityField.sendKeys("Baltimore");
       WebElement stateField = driver.findElement(By.xpath("//*[@id=\"state\"]"));
       stateField.sendKeys("MD");
       WebElement cardName = driver.findElement(By.xpath("//*[@id=\"cname\"]"));
       cardName.sendKeys("name");
       WebElement cardNumber = driver.findElement(By.xpath("//*[@id=\"ccnum\"]"));
       cardNumber.sendKeys("1111111111111111");
       WebElement expirationMonth = driver.findElement(By.xpath("//*[@id=\"expmonth\"]"));
       expirationMonth.sendKeys("July");
       WebElement expYear = driver.findElement(By.xpath("//*[@id=\"expmonth\"]"));
       expYear.sendKeys("2029");
       WebElement cvv = driver.findElement(By.xpath("//*[@id=\"cvv\"]"));
       cvv.sendKeys("000");
       WebElement payAndOrderBtn = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/form/input"));
       payAndOrderBtn.click();
       String currentURL = driver.getCurrentUrl();
       assertTrue(currentURL.equals(BASE_URL + "pay"));
   }
   
   //Fails due to user book, passes otherwise
   @Test
   public void buyingBookDecrementsTotalSupply() {
       driver.get(BASE_URL);
       loginUser();
       driver.findElement(USER_AVAILABLE_BOOKS).click();
       WebElement addBookToCart = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div[2]/div[2]/form/input[3]"));
       addBookToCart.click();
       WebElement totalAmountLeft = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div[2]/div[1]/p/span[2]"));
       String totalBooksLeft = totalAmountLeft.getText();
       String[] textArray = totalBooksLeft.split(" ");
       String totalBooks = textArray[1];
       int bookTotal = Integer.valueOf(totalBooks);
       driver.findElement(USER_CART).click();
       WebElement proceedBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/form/input")));
       proceedBtn.click();
       fillCheckoutForm();
       WebElement payAndOrderBtn = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/form/input"));
       payAndOrderBtn.click();
       WebElement availableBooks = driver.findElement(By.xpath("//*[@id=\"navbarNav\"]/ul/li[2]/span/a"));
       availableBooks.click();
       WebElement newTotalAmountLeft = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div[2]/div[1]/p/span[2]"));
       String newTotalBooksLeft = newTotalAmountLeft.getText();
       String[] newTextArray = newTotalBooksLeft.split(" ");
       String newTotalBooks = newTextArray[1];
       int newBookTotal = Integer.valueOf(newTotalBooks);  
       assertTrue(bookTotal == (newBookTotal + 1));
       }
   
   //Weird indexing issues here for finding book added, goes into random spot and database has many instances of same type
   //Most likely better to search all books with specific title
   @Test
   public void adminAddBookViewableOnAdminSide() {
       String title = "admin add book test book";
       String author = "me";
       String price = "100";
       String quantity = "10";

       // Admin login and add book
       driver.get(BASE_URL);
       loginAdmin();
       driver.findElement(By.id("addbook")).click();
       addBook(title, author, price, quantity);

       // Verify success message
       WebElement successMsg = wait.until(
           ExpectedConditions.visibilityOfElementLocated(
               By.xpath("/html/body/div/table/tbody/tr/td")));
       assertTrue(successMsg.getText().contains("Book Detail Updated Successfully!"));

       // Navigate to admin book list
       driver.findElement(ADMIN_STORE_BOOKS).click();
       wait.until(ExpectedConditions.visibilityOfElementLocated(
           By.xpath("//div/table/tbody/tr")));

       // Find row index for our book
       List<WebElement> rows = driver.findElements(By.xpath("//div/table/tbody/tr"));
       int rowIndex = -1;
       for (int i = 0; i < rows.size(); i++) {
           String cellTitle = rows.get(i)
               .findElement(By.xpath("td[1]"))
               .getText().trim();
           if (title.equals(cellTitle)) {
               rowIndex = i + 1; // XPath is 1-based
               break;
           }
       }
       assertTrue(rowIndex > 0, "Should find the added book in admin list");

       String basePath = "/html/body/div/table/tbody/tr[" + rowIndex + "]/td";
       String actualName = driver.findElement(
           By.xpath(basePath + "[1]")).getText().trim();
       String actualAuthor = driver.findElement(
           By.xpath(basePath + "[2]")).getText().trim();
       String actualPrice = driver.findElement(
           By.xpath(basePath + "[3]")).getText().trim();
       String actualQuantity = driver.findElement(
           By.xpath(basePath + "[4]")).getText().trim();

       assertEquals(title, actualName, "Admin view: title should match");
       assertEquals(author, actualAuthor, "Admin view: author should match");
       assertTrue(actualPrice.contains(price), "Admin view: price should contain value");
       assertEquals(quantity, actualQuantity, "Admin view: quantity should match");
   }
   
   public void addBook(String name, String author, String price, String quantity) {
       WebElement bookName = driver.findElement(By.xpath("//*[@id=\"bookName\"]"));
       bookName.sendKeys(name);
       WebElement bookAuthor = driver.findElement(By.xpath("//*[@id=\"bookAuthor\"]"));
       bookAuthor.sendKeys(author);
       WebElement bookPrice = driver.findElement(By.xpath("/html/body/div/table/tbody/tr/td/form/input[3]"));
       bookPrice.sendKeys(price);
       WebElement bookQuantity = driver.findElement(By.xpath("//*[@id=\"bookQuantity\"]"));
       bookQuantity.sendKeys(quantity);
       WebElement addBook = driver.findElement(By.xpath("/html/body/div/table/tbody/tr/td/form/input[5]"));
       addBook.click();
   }
   
   @Test
   public void baseUserCanSeeNewlyAddedBooks() {
       String title = "cool book";
       String author = "me";
       String price = "100";
       String quantity = "10";

       // Admin adds a new book
       driver.get(BASE_URL);
       loginAdmin();
       driver.findElement(By.id("addbook")).click();
       addBook(title, author, price, quantity);

       // Logout admin
       driver.findElement(By.xpath("//*[@id=\"logout\"]")).click();

       // Login as base user and navigate to available books
       loginUser();
       driver.findElement(USER_AVAILABLE_BOOKS).click();
       wait.until(ExpectedConditions.visibilityOfElementLocated(
           By.xpath("/html/body/div[2]/div[1]/div")));

       // Find the card with matching title
       List<WebElement> cards = driver.findElements(By.xpath("/html/body/div[2]/div[1]/div"));
       WebElement targetCard = null;
       for (WebElement card : cards) {
           String cardTitle = card.findElement(By.xpath("./div[1]/div/h5")).getText().trim();
           if (title.equals(cardTitle)) {
               targetCard = card;
               break;
           }
       }
       assertNotNull(targetCard, "Expected to find newly added book card");

       // Verify details within the card
       String actualTitle = targetCard.findElement(By.xpath("./div[1]/div/h5")).getText().trim();
       String actualAuthor = targetCard.findElement(By.xpath("./div[1]/div/p/span")).getText().trim();
       String actualPrice = targetCard.findElement(By.xpath("./div[2]/div[2]/p/span")).getText().trim();

       assertEquals(title, actualTitle, "Customer view: title should match");
       assertEquals(author, actualAuthor, "Customer view: author should match");
       assertTrue(actualPrice.contains(price), "Customer view: price should contain value");
   }
    @Test
    public void verifyUpdateBookButtonTakesAdminToUpdateScreen() {
        driver.get(BASE_URL);
        loginAdmin();
        WebElement bookStoreNav = driver.findElement(By.xpath("//*[@id=\"storebooks\"]"));
        bookStoreNav.click();
        WebElement updateBookBtn = driver.findElement(By.xpath("/html/body/div/table/tbody/tr[1]/td[5]/form/button"));
        updateBookBtn.click();
        String currentURL = driver.getCurrentUrl();
        assertTrue(currentURL.contains(BASE_URL + "updatebook"));
    }
    
    
    @Test
    public void adminUpdateBook() {
        driver.get(BASE_URL);
        loginAdmin();
        WebElement addBookNav = driver.findElement(By.xpath("//*[@id=\"addbook\"]"));
        addBookNav.click();
        addBook("admin update book test", "me", "100", "10");
        driver.findElement(ADMIN_STORE_BOOKS).click();
        //For first book update button in admin view
        WebElement updateBookBtn = driver.findElement(By.xpath("/html/body/div/table/tbody/tr[1]/td[5]/form/button"));
        
        List<WebElement> rows = driver.findElements(By.xpath("//div/table/tbody/tr"));
        int targetRow = -1;
        for (int i = 0; i < rows.size(); i++) {
            String title = rows.get(i)
                .findElement(By.xpath("td[1]"))
                .getText().trim();
            if ("admin update book test".equals(title)) {
                targetRow = i;
                break;
            }
        }
        assertTrue(targetRow != -1, "Expected to find book row for update");

        // Click the update button in that row
        WebElement updateBtn = rows.get(targetRow)
            .findElement(By.xpath("td[5]/form/button"));
        updateBtn.click();
        
        String newTitle = "updated title for admin update book test";
        String newAuthor = "updated author";
        String newPrice = "5000";
        String newAmount = "15";
        updateBook(newTitle, newAuthor, newPrice, newAmount);
        WebElement updateSuccessMessage = driver.findElement(By.xpath("/html/body/div/table/tbody/tr/td"));
        String message = updateSuccessMessage.getText();
        assertTrue(message.contains("Book Detail Updated Successfully!"));
        driver.findElement(ADMIN_STORE_BOOKS).click();
        
        List<WebElement> rowsUpdated = driver.findElements(By.xpath("//div/table/tbody/tr"));
        int targetRowUpdated = -1;
        for (int i = 0; i < rows.size(); i++) {
            String titleUpdated = rowsUpdated.get(i)
                .findElement(By.xpath("td[1]"))
                .getText().trim();
            if (newTitle.equals(titleUpdated)) {
                targetRowUpdated = i;
                break;
            }
        }
        assertTrue(targetRowUpdated != -1, "Expected to find book row for update");
        
        
        WebElement updatedBookTitle = rowsUpdated.get(targetRowUpdated).findElement(By.xpath("td[1]"));
        WebElement updatedBookAuthor = rowsUpdated.get(targetRowUpdated).findElement(By.xpath("td[2]"));
        WebElement updatedBookPrice = rowsUpdated.get(targetRowUpdated).findElement(By.xpath("td[3]"));
        WebElement updatedBookAmount = rowsUpdated.get(targetRowUpdated).findElement(By.xpath("td[4]"));
        String updatedTitle = updatedBookTitle.getText();
        String updatedAuthor = updatedBookAuthor.getText();
        String updatedPrice = updatedBookPrice.getText();
        String updatedAmount = updatedBookAmount.getText();
        assertEquals(newTitle, updatedTitle);
        assertEquals(newAuthor, updatedAuthor);
        assertTrue(updatedPrice.contains(newPrice));
        assertEquals(newAmount, updatedAmount);
    }
    
    public void updateBook(String title, String author, String price, String amount) {
        WebElement bookTitle = driver.findElement(By.xpath("//*[@id=\"bookName\"]"));
        WebElement bookAuthor = driver.findElement(By.xpath("//*[@id=\"bookAuthor\"]"));
        WebElement bookPrice = driver.findElement(By.xpath("/html/body/div/table/tbody/tr/td/form/input[4]"));
        WebElement bookAmount = driver.findElement(By.xpath("/html/body/div/table/tbody/tr/td/form/input[5]"));
        bookTitle.clear();
        bookAuthor.clear();
        bookPrice.clear();
        bookAmount.clear();
        bookTitle.sendKeys(title);
        bookAuthor.sendKeys(author);
        bookPrice.sendKeys(price);
        bookAmount.sendKeys(amount);
        WebElement updateBookBtn = driver.findElement(By.xpath("/html/body/div/table/tbody/tr/td/form/input[6]"));
        updateBookBtn.click();
    }
    
    @Test
    public void baseUserCanSeeUpdatedBookDetails() {
        driver.get(BASE_URL);
        loginAdmin();
        WebElement addBookNav = driver.findElement(By.xpath("//*[@id=\"addbook\"]"));
        addBookNav.click();
        addBook("base user checking updated book test", "me", "100", "10");
        driver.findElement(ADMIN_STORE_BOOKS).click();
        List<WebElement> rows = driver.findElements(By.xpath("//div/table/tbody/tr"));
        int targetRow = -1;
        for (int i = 0; i < rows.size(); i++) {
            String title = rows.get(i)
                .findElement(By.xpath("td[1]"))
                .getText().trim();
            if ("base user checking updated book test".equals(title)) {
                targetRow = i;
                break;
            }
        }
        assertTrue(targetRow != -1, "Expected to find book row for update");

        // Click the update button in that row
        WebElement updateBtn = rows.get(targetRow)
            .findElement(By.xpath("td[5]/form/button"));
        updateBtn.click();
        String newTitle = "updated title";
        String newAuthor = "updated author";
        String newPrice = "5000";
        String newAmount = "10";
        updateBook(newTitle, newAuthor, newPrice, newAmount);
        WebElement logoutButton = driver.findElement(By.id("logout"));
        logoutButton.click();
        loginUser();
        driver.findElement(USER_AVAILABLE_BOOKS).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div[1]/div")));

        List<WebElement> cards = driver.findElements(By.xpath("/html/body/div[2]/div[1]/div"));
        WebElement targetCard = null;
        for (WebElement card : cards) {
            String title = card.findElement(By.xpath("./div[1]/div/h5")).getText().trim();
            if (newTitle.equals(title)) {
                targetCard = card;
                break;
            }
        }
        assertNotNull(targetCard, "Expected to find updated book card");

        // Assert details in that card
        String actualTitle = targetCard.findElement(By.xpath("./div[1]/div/h5")).getText().trim();
        String actualAuthor = targetCard.findElement(By.xpath("./div[1]/div/p/span")).getText().trim();
        String actualPrice = targetCard.findElement(By.xpath("./div[2]/div[2]/p/span")).getText().trim();

        assertEquals(newTitle, actualTitle, "Title should be updated");
        assertEquals(newAuthor, actualAuthor, "Author should be updated");
        assertTrue(actualPrice.contains(newPrice), "Price should contain updated amount");
    }
    
    @Test
    public void verifyAdminRemoveNav() {
        driver.get(BASE_URL);
        loginAdmin();
        WebElement removeNavBtn = driver.findElement(By.xpath("//*[@id=\"removebook\"]"));
        removeNavBtn.click();
        String currentURL = driver.getCurrentUrl();
        assertTrue(currentURL.contains(BASE_URL + "removebook"));
    }
    
    @Test
    public void adminRemoveBook() {
        driver.get(BASE_URL);
        loginAdmin();
        driver.findElement(ADMIN_STORE_BOOKS).click();
        WebElement firstBookID = driver.findElement(By.xpath("/html/body/div/table/tbody/tr[1]/th"));
        String bookToRemoveID = firstBookID.getText();
        WebElement removeNavBtn = driver.findElement(By.xpath("//*[@id=\"removebook\"]"));
        removeNavBtn.click();
        WebElement bookIDForBookToRemove = driver.findElement(By.xpath("//*[@id=\"bookCode\"]"));
        bookIDForBookToRemove.sendKeys(bookToRemoveID);
        WebElement removeBookBtn = driver.findElement(By.xpath("/html/body/div/form/table/tbody/tr/td/input[2]"));
        removeBookBtn.click();
        WebElement removeSuccessMsg = driver.findElement(By.xpath("/html/body/div/table[1]/tbody/tr/td\r\n"));
        String successMsg = removeSuccessMsg.getText();
        assertTrue(successMsg.contains("Book Removed Successfully"));
        driver.findElement(ADMIN_STORE_BOOKS).click();
        WebElement newFirstBookID = driver.findElement(By.xpath("/html/body/div/table/tbody/tr[1]/th"));
        String newBookID = newFirstBookID.getText();
        assertTrue(newBookID != bookToRemoveID);
    }
    
    @Test
    public void removingWithNoIDProducesError() {
        driver.get(BASE_URL);
        loginAdmin();
        WebElement removeNavBtn = driver.findElement(By.xpath("//*[@id=\"removebook\"]"));
        removeNavBtn.click();
        WebElement removeBookBtn = driver.findElement(By.xpath("/html/body/div/form/table/tbody/tr/td/input[2]"));
        removeBookBtn.click();
        List<WebElement> elems = driver.findElements(By.xpath("/html/body/div/table[1]/tbody/tr/td\r\n"));
        assertTrue(elems.isEmpty(), "Table element should not be present");
    }
    
    @Test
    public void removingBookRemovesBookFromUserView() {
        driver.get(BASE_URL);
        loginAdmin();
        driver.findElement(ADMIN_STORE_BOOKS).click();
        WebElement firstBookID = driver.findElement(By.xpath("/html/body/div/table/tbody/tr[1]/th"));
        String bookToRemoveID = firstBookID.getText();
        WebElement removeNavBtn = driver.findElement(By.xpath("//*[@id=\"removebook\"]"));
        removeNavBtn.click();
        WebElement bookIDForBookToRemove = driver.findElement(By.xpath("//*[@id=\"bookCode\"]"));
        bookIDForBookToRemove.sendKeys(bookToRemoveID);
        WebElement removeBookBtn = driver.findElement(By.xpath("/html/body/div/form/table/tbody/tr/td/input[2]"));
        removeBookBtn.click();
        WebElement logoutButton = driver.findElement(By.id("logout"));
        logoutButton.click();
        loginUser();
        driver.findElement(USER_AVAILABLE_BOOKS).click();
        WebElement firstUserBookID = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div[2]/div[1]/p/span[1]"));
        String bookIDString = firstUserBookID.getText();
        assertFalse(bookIDString.contains(bookToRemoveID));
    }
    
    @Test
    public void validateRegisterUserNavFromNavBar() {
        driver.get(BASE_URL);
        driver.findElement(REGISTER_USER_NAV_BAR).click();
        String currentURL = driver.getCurrentUrl();
        assertTrue(currentURL.contains(BASE_URL + "CustomerRegister.html"));
    }
    
    @Test
    public void validateRegisterUserNavFromLoginMenu() {
        driver.get(BASE_URL);
        driver.findElement(NAV_LOGIN).click();
        wait.until(ExpectedConditions.elementToBeClickable(NAV_LOGIN)).click();
        WebElement registerUserBtn = driver.findElement(By.xpath("/html/body/table/tbody/tr[4]/td/a"));
        registerUserBtn.click();
        String currentURL = driver.getCurrentUrl();
        assertTrue(currentURL.contains(BASE_URL + "CustomerRegister.html"));
    }
    
    @Test
    public void createNewUser() {
        driver.get(BASE_URL);
        driver.findElement(REGISTER_USER_NAV_BAR).click();
        
        //Change these for future use
        String username = "username103";
        String password = "password";
        String firstName = "first";
        String lastName = "last";
        String address = "1111 real street";
        String phoneNumber = "1111111111";
        Boolean accept = true;
        
        registerUser(username,password,firstName,lastName,address,phoneNumber,accept);
        WebElement registerSuccessMsg = driver.findElement(By.xpath("/html/body/table/tbody/tr/td"));
        String msgTxt = registerSuccessMsg.getText();
        assertEquals("User Registered Successfully", msgTxt);
    }
    
    //Error with '-' in phoneNumber
    //Some failures here due to google pop-ups, attempted to disable, only works some of the time, passes otherwise
    @Test
    public void newRegisterUserLogin() {
        driver.get(BASE_URL);
        driver.findElement(REGISTER_USER_NAV_BAR).click();
        
        //Change these for future use
        String username = "username300";
        String password = "password";
        String firstName = "first";
        String lastName = "last";
        String address = "1111 real street";
        String phoneNumber = "1111111111";
        Boolean accept = true;
        
        registerUser(username,password,firstName,lastName,address,phoneNumber,accept);
        
        driver.findElement(NAV_LOGIN).click();
        wait.until(ExpectedConditions.elementToBeClickable(NAV_LOGIN)).click();
        WebElement customerLoginPage = driver.findElement(CUSTOMER_LOGIN_LINK);
        customerLoginPage.click();
        WebElement usernameBox = driver.findElement(By.id("userName"));
        usernameBox.click();
        usernameBox.sendKeys(username);
        WebElement passwordBox = driver.findElement(By.id("Password"));
        passwordBox.click();
        passwordBox.sendKeys(password);
        WebElement captchaInput = driver.findElement(By.id("CaptchaInput"));
        WebElement captcha = driver.findElement(By.id("CaptchaDiv"));
        String captchaValue = captcha.getText();
        captchaInput.sendKeys(captchaValue);
        WebElement loginSubmit = driver.findElement(By.xpath(LOGINSUBMITBUTTON));
        loginSubmit.click();
        WebElement loginSuccessMsg = driver.findElement(By.xpath("/html/body/table/tbody/tr/td/p"));
        String loginSuccessTxt = loginSuccessMsg.getText();
        assertTrue(loginSuccessTxt.contains(firstName));
    }
    
    @Test 
    public void registerUserMissingInfo() {
        driver.get(BASE_URL);
        driver.findElement(REGISTER_USER_NAV_BAR).click();
        WebElement userEmailInput = driver.findElement(By.xpath("//*[@id=\"Email\"]"));
        WebElement userPasswordInput = driver.findElement(By.xpath("//*[@id=\"passWord\"]"));
        String username = "newuser69";
        String password = "password";
        userEmailInput.sendKeys(username);
        userPasswordInput.sendKeys(password);
        WebElement registerBtn = driver.findElement(By.xpath("/html/body/form/table/tbody/tr[2]/td/input[7]"));
        registerBtn.click();
        List<WebElement> elems = driver.findElements(By.xpath("/html/body/table/tbody/tr/td"));
        assertTrue(elems.isEmpty(), "Table element should not be present");
    }
    
    
    //This should be allowed
    @Test
    public void registerUserMissingAddress() {
        driver.get(BASE_URL);
        driver.findElement(REGISTER_USER_NAV_BAR).click();
        
        //Change these for future use
        String username = "test100";
        String password = "password";
        String firstName = "test2";
        String lastName = "test2";
        String address = "";
        String phoneNumber = "111-111-1111";
        Boolean accept = true;
        
        registerUser(username,password,firstName,lastName,address,phoneNumber,accept);
        List<WebElement> elems = driver.findElements(By.xpath("/html/body/table/tbody/tr/td"));
        assertFalse(elems.isEmpty(), "Table element should not be present");
    }
    
    @Test
    public void registerWithoutAcceptingTermsAndConditions() {
        driver.get(BASE_URL);
        driver.findElement(REGISTER_USER_NAV_BAR).click();
        
        //Change these for future use
        String username = "test101";
        String password = "password";
        String firstName = "test2";
        String lastName = "test2";
        String address = "address";
        String phoneNumber = "1111111111";
        Boolean accept = false;
        
        registerUser(username,password,firstName,lastName,address,phoneNumber,accept);
        List<WebElement> elems = driver.findElements(By.xpath("/html/body/table/tbody/tr/td"));
        assertTrue(elems.isEmpty(), "Table element should not be present");
    }
    
    public void registerUser(String username, String password, String firstName, String lastName, String address, String phoneNumber, Boolean accept) {
        WebElement userEmailInput = driver.findElement(By.xpath("//*[@id=\"Email\"]"));
        WebElement userPasswordInput = driver.findElement(By.xpath("//*[@id=\"passWord\"]"));
        WebElement userFirstNameInput = driver.findElement(By.xpath("//*[@id=\"firstName\"]"));
        WebElement userLastNameInput = driver.findElement(By.xpath("//*[@id=\"lastName\"]"));
        WebElement userAddressInput = driver.findElement(By.xpath("//*[@id=\"address\"]"));
        WebElement userMobileNumberInput = driver.findElement(By.xpath("//*[@id=\"phno\"]"));
        WebElement acknowledgeTACCheckBox = driver.findElement(By.xpath("/html/body/form/table/tbody/tr[2]/td/input[6]"));
        WebElement registerBtn = driver.findElement(By.xpath("/html/body/form/table/tbody/tr[2]/td/input[7]"));
        userEmailInput.sendKeys(username);
        userPasswordInput.sendKeys(password);
        userFirstNameInput.sendKeys(firstName);
        userLastNameInput.sendKeys(lastName);
        userAddressInput.sendKeys(address);
        userMobileNumberInput.sendKeys(phoneNumber);
        if(accept) {
            acknowledgeTACCheckBox.click();
        }
        registerBtn.click();
    }
    
    
    @Test
    public void registerSameUserTwice() {
        driver.get(BASE_URL);
        driver.findElement(REGISTER_USER_NAV_BAR).click();
        
        //Change these for future use
        String username = "user1000";
        String password = "password";
        String firstName = "test2";
        String lastName = "test2";
        String address = "address";
        String phoneNumber = "1111111111";
        Boolean accept = true;
        
        registerUser(username,password,firstName,lastName,address,phoneNumber,accept);
        driver.findElement(REGISTER_USER_NAV_BAR).click();
        registerUser(username,password,firstName,lastName,address,phoneNumber,accept);
        WebElement userAlreadyRegistered = driver.findElement(By.xpath("/html/body/table/tbody/tr/td"));
        String msg = userAlreadyRegistered.getText();
        assertEquals("User already registered with this email !!", msg);
    }
    
    @Test
    public void loginWrongCredentialsBaseUser() {
        driver.get(BASE_URL);
        driver.findElement(NAV_LOGIN).click();
        wait.until(ExpectedConditions.elementToBeClickable(NAV_LOGIN)).click();
        WebElement customerLoginPage = driver.findElement(CUSTOMER_LOGIN_LINK);
        customerLoginPage.click();
        WebElement usernameBox = driver.findElement(By.id("userName"));
        usernameBox.click();
        usernameBox.sendKeys("VeryWrongUserName");
        WebElement passwordBox = driver.findElement(By.id("Password"));
        passwordBox.click();
        passwordBox.sendKeys("VeryWrongPassword");
        WebElement captchaInput = driver.findElement(By.id("CaptchaInput"));
        WebElement captcha = driver.findElement(By.id("CaptchaDiv"));
        String captchaValue = captcha.getText();
        captchaInput.sendKeys(captchaValue);
        WebElement loginSubmit = driver.findElement(By.xpath(LOGINSUBMITBUTTON));
        loginSubmit.click();
        WebElement incorrectLogin = driver.findElement(By.xpath("/html/body/table/tbody/tr/td"));
        String msg = incorrectLogin.getText();
        assertEquals("Incorrect UserName or PassWord", msg);
    }
    
    @Test
    public void wrongLoginCredentialsAdmin() {
        driver.get(BASE_URL);
        driver.findElement(NAV_LOGIN).click();
        wait.until(ExpectedConditions.elementToBeClickable(NAV_LOGIN)).click();
        WebElement adminLoginPage = driver.findElement(ADMIN_LOGIN_LINK);
        adminLoginPage.click();
        WebElement usernameBox = driver.findElement(By.id("userName"));
        usernameBox.click();
        usernameBox.sendKeys("WrongUser");
        WebElement passwordBox = driver.findElement(By.id("Password"));
        passwordBox.click();
        passwordBox.sendKeys("WrongPassword");
        WebElement adminLoginButton = driver.findElement(By.xpath("/html/body/form/table/tbody/tr[3]/td/input[3]"));
        adminLoginButton.click();
        WebElement incorrectLogin = driver.findElement(By.xpath("/html/body/div[2]"));
        String msg = incorrectLogin.getText();
        assertEquals("Incorrect UserName or PassWord", msg);
    }
    
}




