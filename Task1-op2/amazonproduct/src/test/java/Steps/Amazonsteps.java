package stepDefinitions;

import io.cucumber.java.en.*;
import io.cucumber.java.After;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;
import java.util.List;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Amazonsteps {
    WebDriver driver;
    String priceOnDetailsPage; // Store the price for validation

    @Given("I navigate to {string}")
    public void i_navigate_to(String url) {

        WebDriverManager.chromedriver().setup(); 
    
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");
    
        driver = new ChromeDriver(chromeOptions);
        driver.get(url);
    }
    @When("I search for {string}")
    public void i_search_for(String searchTerm) {
        WebElement searchBox = driver.findElement(By.id("twotabsearchtextbox"));
        searchBox.sendKeys(searchTerm);
        searchBox.submit();
        
        // Wait for the results to load
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".s-result-item")));
    }

    @When("I select two random products and add them to the cart")
    public void i_select_two_random_products_and_add_them_to_the_cart() throws InterruptedException {
        List<WebElement> products = driver.findElements(By.cssSelector(".s-result-item .sg-col-inner"));
        
        // Select the first two available products
        for (int i = 0; i < 2; i++) {
            WebElement product = products.get(i);
            product.findElement(By.cssSelector(".a-link-normal.a-text-normal")).click();
            
            // Wait for the product price to load
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.id("price_inside_buybox")));
            priceOnDetailsPage = driver.findElement(By.id("price_inside_buybox")).getText();

            // Add product to cart
            driver.findElement(By.id("add-to-cart-button")).click();

            // Go back to search results for the second product
            if (i < 1) {
                driver.navigate().back();
                // Wait until the search results are visible again
                new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
            }
        }
    }

    @Then("I validate the price on the Search Results page, Product Details page, and Total Summary page")
    public void i_validate_price_on_search_details_summary_page() {
        // Access cart to validate price on Total Summary page
        driver.findElement(By.id("nav-cart")).click();
        
        List<WebElement> cartItems = driver.findElements(By.cssSelector(".sc-list-item"));
        for (WebElement item : cartItems) {
            String priceInCart = item.findElement(By.cssSelector(".sc-product-price")).getText();
            
            // Validate price (compare priceOnDetailsPage with priceInCart)
            Assert.assertEquals("Price mismatch!", priceOnDetailsPage, priceInCart);
        }
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit(); // Ensure the driver is closed after tests
        }
    }
}
