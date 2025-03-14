package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static helpers.Properties.testsProperties;

public class YandexBeforeSearch {
    private WebElement electronicPlace;
    private WebElement catalogButton;
    private WebElement laptopLink;
    private WebDriver driver;

    private WebDriverWait wait;

    public YandexBeforeSearch(WebDriver driver) {
        this.driver = driver;
        this.wait=new WebDriverWait(driver, testsProperties.defaultTimeout());
        wait.until(visibilityOfElementLocated(By.xpath("//div[(@data-baobab-name='catalog')]")));
        this.catalogButton = driver.findElement(By.xpath("//div[(@data-baobab-name='catalog')]"));
    }

    @Step("Нажатие на каталог")
    public void buttonClick(){
        wait.until(this::isJsReady);
        wait.until(visibilityOfElementLocated(By.xpath("//div[(@data-baobab-name='catalog')]")));
        catalogButton.click();
    }

    @Step("Поиск каталога по слову {catalogContent}")
    public void electronicMouseOver(String catalogContent){
        wait.until(this::isJsReady);
        wait.until(visibilityOfElementLocated(By.xpath("//div[contains(@data-zone-name, 'catalog-content')]//ul//a//span[contains(text(), '"+ catalogContent +"')]")));
        this.electronicPlace = driver.findElement(By.xpath("//div[contains(@data-zone-name, 'catalog-content')]//ul//a//span[contains(text(), '"+ catalogContent +"')]"));
        Actions actions = new Actions(driver);
        actions.moveToElement(electronicPlace).perform();
    }

    @Step("Поиск подкаталога по слову {catalogSubItem}")
    public void laptopClick(String catalogSubItem){
        wait.until(visibilityOfElementLocated(By.xpath("//li//a[contains(text(), '"+catalogSubItem+"')]")));
        this.laptopLink = driver.findElement(By.xpath("//li//a[contains(text(), '"+catalogSubItem+"')]"));
        laptopLink.click();
    }

    private boolean isJsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete");
    }
}
