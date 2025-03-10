package steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.YandexAfterSearch;
import pages.YandexBeforeSearch;

import java.time.Duration;
import java.util.List;

import static helpers.Properties.testsProperties;

public class StepsAll {

    private static WebDriverWait wait;
    private static WebDriver driver;

    @Step("Переходим на сайт: {url}")
    public static void openSite(String url, String title, WebDriver currentDriver) {
        driver = currentDriver;
        driver.get(url);
        wait = new WebDriverWait(driver, testsProperties.defaultTimeout());
        try {
            wait.until(ExpectedConditions.titleContains(title));
        }catch(TimeoutException ex){
            Assertions.fail("Тайтл " + title + " не содержится на сайте " + url);
        }
    }

    @Step("Перехожу в Каталог")
    public static void findCatalog(){
        YandexBeforeSearch yandexBeforeSearch = new YandexBeforeSearch(driver);
        yandexBeforeSearch.buttonClick();
    }

    @Step("Навожу курсор на раздел {catalogContent}")
    public static void findElectronic(String catalogContent){
        YandexBeforeSearch yandexBeforeSearch = new YandexBeforeSearch(driver);
        yandexBeforeSearch.buttonClick();
        yandexBeforeSearch.electronicMouseOver(catalogContent);
    }

    @Step("Перехожу в раздел {catalogContent}")
    public static void findcatalogSubItem(String catalogContent, String catalogSubItem){
        YandexBeforeSearch yandexBeforeSearch = new YandexBeforeSearch(driver);
        yandexBeforeSearch.buttonClick();
        yandexBeforeSearch.electronicMouseOver(catalogContent);
        yandexBeforeSearch.laptopClick(catalogSubItem);
    }

    @Step("Проверка, что мы находимся в каталоге {laptop}")
    public static void checkingTitle(String catalogSubItem){
        YandexAfterSearch yandexAfterSearch = new YandexAfterSearch(driver, "Ноутбуки");
        yandexAfterSearch.checkingTitleByText(catalogSubItem);
    }

    @Step("Задаем параметр цена от {minimumPrice} до {maximumPrice}")
    public static void inputPriceFilter(String minimumPrice, String maximumPrice){
        YandexAfterSearch yandexAfterSearch = new YandexAfterSearch(driver, "Ноутбуки");
        yandexAfterSearch.inputPriceInterval(minimumPrice, maximumPrice);
    }

    @Step("Выбираем 2-х производителей: {firstBrand} до {SecondBrand}")
    public static void inputBrandsFilter(String firstBrand, String secondBrand){
        YandexAfterSearch yandexAfterSearch = new YandexAfterSearch(driver, "Ноутбуки");
        //yandexAfterSearch.inputBrands(firstBrand, secondBrand);
        yandexAfterSearch.inputBrands(List.of(firstBrand,secondBrand));
    }

    @Step("Подсчет количества элементов на первой странице")
    public static void checkCountElementsOnFirstPage(String minimumPrice, String maximumPrice, String firstBrand, String secondBrand, int elementsCount){
        YandexAfterSearch yandexAfterSearch = new YandexAfterSearch(driver, "Ноутбуки");
        yandexAfterSearch.inputPriceInterval(minimumPrice, maximumPrice);
        yandexAfterSearch.inputBrands(List.of(firstBrand, secondBrand));
        Assertions.assertTrue(yandexAfterSearch.CountOfElementsOnFirstPage()>elementsCount);
    }

    @Step("Проверка, что все предложения соответсвуют фильтру")
    public static void checkFilters(String minimumPrice, String maximumPrice, String firstBrand, String secondBrand){
        YandexAfterSearch yandexAfterSearch = new YandexAfterSearch(driver, "Ноутбуки");
        yandexAfterSearch.inputPriceInterval(minimumPrice, maximumPrice);
        yandexAfterSearch.inputBrands(List.of(firstBrand, secondBrand));
        double num1 = Double.parseDouble(minimumPrice);
        double num2 = Double.parseDouble(maximumPrice);
       Assertions.assertTrue(yandexAfterSearch.checkFilters(num1,num2,List.of(firstBrand, secondBrand)));
    }




}
