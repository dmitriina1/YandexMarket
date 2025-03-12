package pages;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static helpers.Properties.testsProperties;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class YandexAfterSearch {

    private WebDriver driver;

    private WebDriverWait wait;

    private WebElement minimumPriceInterval;
    private WebElement maximumPriceInterval;
    private WebElement brandShowMoreButton;
    private WebElement brandSearch;
    private WebElement brandSearchDel;
    private WebElement selectorHP;
    private List<WebElement> list;
    private WebElement pageTitle;

    public YandexAfterSearch(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, testsProperties.defaultTimeout());
        this.list = new ArrayList<>();
        wait.until(visibilityOfElementLocated(By.xpath("//div[contains(@data-zone-name, 'searchTitle')]//h1")));
        this.pageTitle = driver.findElement(By.xpath("//div[contains(@data-zone-name, 'searchTitle')]//h1"));
    }

    public YandexAfterSearch(WebDriver driver, String searchQuery) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, testsProperties.defaultTimeout());
        this.list = new ArrayList<>();
        driver.get("https://market.yandex.ru/search?text=" + searchQuery + "&hid=91013");
    }

    public void checkingTitleByText(String link) {
        wait.until(visibilityOfElementLocated(By.xpath("//div[contains(@data-zone-name, 'searchTitle')]//h1")));
        this.pageTitle = driver.findElement(By.xpath("//div[contains(@data-zone-name, 'searchTitle')]//h1"));
        Assertions.assertFalse(driver.findElements(By.xpath("//div[contains(@data-zone-name, 'searchTitle')]//h1")).size() == 0,
                "Не найдено тайтла с текстом: '" + link);
    }

    public void inputPriceInterval(String minimum, String maximum) {
        wait.until(this::isJsReady);
        this.minimumPriceInterval = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
                "//span[contains(@data-auto, 'filter-range-min')]//label[contains(text(), 'Цена')]" +
                        "/..//input"))));
        minimumPriceInterval.clear();
        minimumPriceInterval.click();
        minimumPriceInterval.sendKeys(minimum + Keys.ENTER);

        this.maximumPriceInterval = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
                "//span[contains(@data-auto, 'filter-range-max')]//label[contains(text(), 'Цена')]" +
                        "/..//input"))));
        maximumPriceInterval.click();
        maximumPriceInterval.clear();
        maximumPriceInterval.sendKeys(maximum + Keys.ENTER);
    }

    private boolean isJsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete");
    }

    List<String> someBrands;


    //Учитывай что при применении фильтров и других действий КАРТОЧКИ СТАНОВЯТСЯ НЕДОСТУПНЫ!!!!

    public void inputBrands(List<String> brands) {
        String brandSearchLocator = "//div[contains(@data-zone-data, 'Бренд')]//input";
        String brandShownLocator = "//div[contains(@data-zone-data, 'Бренд')]//div[contains(@data-zone-name, 'FilterValue')]";
        someBrands = new ArrayList<>(brands);
        Actions actions = new Actions(driver);

        wait.ignoring(StaleElementReferenceException.class)
                .until(driver -> {
                    wait.until(visibilityOf(driver.findElement(By.xpath("//div[contains(@data-auto, 'SerpList')]//div[contains(@data-auto-themename, 'listDetailed')]"))));
                    wait.until(elementToBeClickable(driver.findElement(By.xpath("//div[contains(@data-auto, 'SerpList')]//div[contains(@data-auto-themename, 'listDetailed')]"))));
                    {
                        boolean allChecked = true;
                        List<WebElement> brandElements = driver.findElements(By.xpath(brandShownLocator));

                        for (WebElement element : brandElements) {
                            String text = element.getText().trim();
                            for (String brand : brands) {
                                if (text.equalsIgnoreCase(brand)) {
                                    WebElement checkbox = element.findElement(By.xpath(".//label"));
                                    wait.until(ExpectedConditions.elementToBeClickable(checkbox));

                                    if (!"true".equals(checkbox.getAttribute("aria-checked"))) {
                                        actions.moveToElement(checkbox).click().perform(); // Кликаем через Actions
                                        someBrands.remove(brand);
                                        allChecked = false;
                                        wait.until(this::isJsReady);
                                    }
                                }
                            }
                        }
                        return allChecked;
                    }
                });

        if (!someBrands.isEmpty()) {
            inputBrandsShowMore(brandShownLocator, brandSearchLocator, someBrands);
        }
     }
     
    public void inputBrandsShowMore(String brandShownLocator, String brandSearchLocator, List<String> someBrands) {
        String additionalLocator = "[contains(@data-baobab-name, 'showMoreFilters')]";
        wait.until(this::isJsReady);
        this.brandShowMoreButton = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
                brandShownLocator + additionalLocator))));
        brandShowMoreButton.click();
        for (String element : someBrands) {
            this.brandSearch = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(brandSearchLocator))));
            brandSearch.clear();
            brandSearch.click();
            brandSearch.sendKeys(element);
            By elementLocator = By.xpath(brandShownLocator +
                    "//span[contains(text(),'" + element + "' )]");
            WebElement brandObject = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(elementLocator)));
            brandObject.click();
        }
    }

    public int CountOfElementsOnFirstPage() {
        wait.until(this::isJsReady);
        List<WebElement> foundElements = new ArrayList<>();
        Actions actions = new Actions(driver);
        long startTime = System.currentTimeMillis();
        long maxTime = 60000;
        while ((System.currentTimeMillis() - startTime) < maxTime) {
            List<WebElement> elements = driver.findElements(By.xpath("//div[contains(@data-auto, 'SerpList')]//div[contains(@data-auto-themename, 'listDetailed')]"));
            foundElements.addAll(elements);
            if (!driver.findElements(By.xpath("//div[contains(@data-baobab-name, 'pager')]")).isEmpty()) {
                break;
            }
            actions.sendKeys(org.openqa.selenium.Keys.PAGE_DOWN).perform();
        }
        return foundElements.size();
    }

    public void countOfElementsOnAllPages() {
        List<WebElement> foundElements = new ArrayList<>();
        Actions actions = new Actions(driver);
        long startTime = System.currentTimeMillis();
        long timeLimit = 60000;

        while (System.currentTimeMillis() - startTime < timeLimit) {
            if (isLastPage()) {
                foundElements.addAll(driver.findElements(By.xpath("//div[contains(@data-auto, 'SerpList')]//div[contains(@data-auto-themename, 'listDetailed')]")));
                break;
            } else {
                actions.sendKeys(Keys.PAGE_DOWN).perform();
                try {
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@data-auto, 'SerpList')]")));
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@data-auto, 'SerpList')]//div[contains(@data-auto-themename, 'listDetailed')]")));
                } catch (TimeoutException e) {
                    System.out.println("Элемент не появился вовремя.");
                }
            }
        }

        list = foundElements;
    }

    private boolean isLastPage() {
        return driver.findElements(By.xpath("//div[contains(@data-baobab-name, 'pager')]//div[contains(@data-baobab-name, 'next')]")).isEmpty();
    }

    public boolean checkFilters(Double minimum, Double maximum, List<String> namesOfBrand) {
        countOfElementsOnAllPages();
        List<WebElement> filteredProducts = filterProducts(minimum, maximum, namesOfBrand);

        List<WebElement> remainingProducts = list.stream()
                .filter(product -> !filteredProducts.contains(product))
                .collect(Collectors.toList());

        processRemainingProducts(remainingProducts, namesOfBrand, filteredProducts);

        System.out.println("---------------------------------------------------------");
        System.out.println(filteredProducts.size());
        System.out.println(list.size());
        return filteredProducts.size() == list.size();
    }

    private List<WebElement> filterProducts(Double minimum, Double maximum, List<String> namesOfBrand) {
        return list.stream()
                .filter(product -> {
                    String titleText = product.findElement(By.xpath(".//div[contains(@data-baobab-name, 'title')]//span")).getText().toLowerCase();
                    boolean isBrandMatch = namesOfBrand.stream().map(String::toLowerCase).anyMatch(titleText::contains);
                    double price = getPrice(product);
                    boolean isPriceInRange = price > minimum && price < maximum;
                    return isBrandMatch && isPriceInRange;
                })
                .collect(Collectors.toList());
    }

    private double getPrice(WebElement product) {
        String priceText = product.findElement(By.xpath(".//span[contains(@data-auto, 'snippet-price-current')]//*[1][name()='span']")).getText().replaceAll("[^\\d.]", "");
        return Double.parseDouble(priceText);
    }

    private void processRemainingProducts(List<WebElement> remainingProducts, List<String> namesOfBrand, List<WebElement> filteredProducts) {
        for (WebElement element : remainingProducts) {
            element.click();
            wait.until(this::isJsReady);
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(tabs.size() - 1));

            List<WebElement> brandElements = driver.findElements(By.xpath("//div[contains(@data-zone-name, 'fullSpecs')]//div[contains(@aria-label, 'Характеристики')]//span[text()='Бренд']/../.. /following-sibling::div[1]//span"));
            if (!brandElements.isEmpty()) {
                String brandText = brandElements.get(0).getText().trim();
                if (namesOfBrand.stream().anyMatch(brandText::equalsIgnoreCase)) {
                    filteredProducts.add(element);
                }
            }
            driver.close();
            driver.switchTo().window(tabs.get(0));
        }
    }

//    public void countOfElementsOnAllPages() {
//        List<WebElement> foundElements = new ArrayList<>();
//        Actions actions = new Actions(driver);
//        long startTime = System.currentTimeMillis();
//        long timeLimit = 60000;
//
//        while (System.currentTimeMillis() - startTime < timeLimit) {
//            if (driver.findElements(By.xpath("//div[contains(@data-baobab-name, 'pager')]" +
//                    "//div[contains(@data-baobab-name, 'next')]")).isEmpty()) {
//                List<WebElement> elements = driver.findElements(By.xpath(
//                        "//div[contains(@data-auto, 'SerpList')]" +
//                                "//div[contains(@data-auto-themename, 'listDetailed')]"));
//                foundElements.addAll(elements);
//                break;
//            } else {
//                actions.sendKeys(org.openqa.selenium.Keys.PAGE_DOWN).perform();
//                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@data-auto, 'SerpList')]")));
//                try {
//                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
//                            "//div[contains(@data-auto, 'SerpList')]" +
//                                    "//div[contains(@data-auto-themename, 'listDetailed')]")));
//                } catch (TimeoutException e) {
//                    System.out.println("Элемент не появился вовремя.");
//                    break;
//                }
//            }
//        }
//
//        list = foundElements;
//    }
//
//    //div[contains(@data-auto-themename, 'listDetailed')]//div[contains(@data-baobab-name, 'price')]//span[contains(@data-auto, 'snippet-price-current')]//*[1][name()="span"]
////div[contains(@data-auto-themename, 'listDetailed')]//span[contains(@data-auto, 'snippet-price-current')]//*[1][name()="span"]
//    public boolean checkFilters(Double minimum, Double maximum, List<String> namesOfBrand) {
//        countOfElementsOnAllPages();
//        List<WebElement> find = list.stream()
//                .filter(product -> {
//                    String titleText = product.findElement(By.xpath(
//                            ".//div[contains(@data-baobab-name, 'title')]//span")).getText().toLowerCase();
//                    boolean isBrandMatch = namesOfBrand.stream()
//                            .map(String::toLowerCase)
//                            .anyMatch(titleText::contains);
//                    String priceText = product.findElement(By.xpath(
//                            ".//span[contains(@data-auto, 'snippet-price-current')]//*[1][name()='span']")).getText().replaceAll("[^\\d.]", "");
//                    double price = Double.parseDouble(priceText);
//                    boolean isPriceInRange = price > minimum && price < maximum;
//                    return isBrandMatch && isPriceInRange;
//                })
//                .collect(Collectors.toList());
//
//        List<WebElement> remaining = list.stream()
//                .filter(product -> !find.contains(product))
//                .collect(Collectors.toList());
//        if (!remaining.isEmpty()) {
//            for (WebElement element : remaining) {
//                element.click();
//                wait.until(this::isJsReady);
//                ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
//                driver.switchTo().window(tabs.get(tabs.size() - 1));
//
//                List<WebElement> brandElements = driver.findElements(By.xpath("//div[contains(@data-zone-name, 'fullSpecs')]//div[contains(@aria-label, 'Характеристики')]//span[text()='Бренд']/../.. /following-sibling::div[1]//span"));
//                if (!brandElements.isEmpty()) {
//                    String brandText = brandElements.get(0).getText().trim();
//
//                    if (namesOfBrand.stream().anyMatch(brandText::equalsIgnoreCase)) {
//                        find.add(element);
//                        driver.close();
//                        driver.switchTo().window(tabs.get(0));
//                    }
//                }
//                else{
//                    driver.close();
//                    driver.switchTo().window(tabs.get(0));
//                }
//
//            }
//        }
//        System.out.println("---------------------------------------------------------");
//        System.out.println(find.size());
//        System.out.println(list.size());
//        return find.size() == list.size();
//    }

}
