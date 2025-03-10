package pages;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static helpers.Properties.testsProperties;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

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

    public void inputBrands(List<String> brands) {
        String brandSearchLocator = "//div[contains(@data-zone-data, 'Бренд')]//input";
        String brandShownLocator = "//div[contains(@data-zone-data, 'Бренд')]//div[contains(@data-zone-name, 'FilterValue')]";
        List<String> someBrands = new ArrayList<>(brands);
        wait.ignoring(StaleElementReferenceException.class)
                .until(
                        driver -> {
                            List<WebElement> brandElements = driver.findElements(By.xpath(brandShownLocator));
                            for (WebElement element : brandElements) {
                                String text = element.getText();
                                for (String brand : brands) {
                                    if (text.contains(brand)) {
                                        WebElement checkbox = element.findElement(By.xpath(".//label"));
                                        wait.until(ExpectedConditions.attributeToBeNotEmpty(checkbox, "aria-checked"));
                                        while ("false".equals(checkbox.getAttribute("aria-checked"))) {
                                            checkbox.click();
                                            someBrands.remove(brand);
                                        }
                                    }
                                }
                            }
                            return true;
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
            this.brandSearch = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
                    brandSearchLocator))));
            brandSearch.clear();
            brandSearch.click();
            brandSearch.sendKeys(element);
            By elementLocator = By.xpath(brandShownLocator +
                    "//span[contains(text(),'" + element + "' )]");
            WebElement brandObject = wait.until(ExpectedConditions.visibilityOf(driver.findElement(elementLocator)));
            brandObject.click();
        }
//
//        for(String element:someBrands) {
//            wait.ignoring(StaleElementReferenceException.class)
//                    .until(
//                            driver -> {
//                                this.brandSearch = driver.findElement(By.xpath(
//                                        brandSearchLocator));
//                                brandSearch.clear();
//                                brandSearch.click();
//                                brandSearch.sendKeys(element);
//                                By elementLocator = By.xpath(brandShownLocator +
//                                        "//span[contains(text(),'" + element + "' )]");
//                                WebElement brandObject = driver.findElement(elementLocator);
//                                brandObject.click();
//                                wait.until(driver1 -> "true".equals(brandObject.findElement(By.xpath("./ancestor::label")).getAttribute("aria-checked")));
//
//                                return true;
//                            }
//                    );
//        }

    }
//
//    public void inputBrandsShowMore(String brandShown, String brandSearchLocation, List<String> someBrands) {
//        List<String> someL = new ArrayList<>(someBrands);
//        for (String element : someL) {
//            while (!someL.isEmpty()) {
//                List<WebElement> brandElements = driver.findElements(By.xpath(brandShown));
//                if (brandElements.isEmpty()) {
//                    break;
//                }
//                WebElement lastElement = brandElements.get(brandElements.size() - 1);
//                lastElement.click();
//                //wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(By.xpath(brandShown))));
//                //wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(By.xpath(brandSearchLocation))));
//                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(brandSearchLocation)));
//                this.brandSearch = driver.findElement(By.xpath(brandSearchLocation));
//                //wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(By.xpath(brandShown))));
//                brandSearch.click();
//                brandSearch.sendKeys(element);
//                By elementLocator = By.xpath(brandShown +
//                        "//span[contains(text(),'" + element + "' )]");
//                //wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(elementLocator)));
//                brandElements = driver.findElements(By.xpath(brandShown));
//                List<WebElement> foundElements = driver.findElements(elementLocator);
//                if (foundElements.isEmpty()) {
//                    System.out.println("Пока не нашел" + element);
//                    continue;
//                }
//                WebElement brandObject = foundElements.get(0);
//                brandObject.click();
//                //wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(By.xpath(brandSearchLocation + "//button[text()='Очистить']"))));
//                this.brandSearchDel = driver.findElement(By.xpath(brandSearchLocation + "//button[contains(text(),'Очистить')]"));
//                brandSearchDel.click();
//                someL.remove(element);
//            }
//        }
//    }

//
//    public void inputBrands(List<String> brands) {
//        String brandSearchLocation = "//div[contains(@data-zone-data, 'Бренд')]//input";
//        String brandShown = "//div[contains(@data-zone-data, 'Бренд')]//div[contains(@data-zone-name, 'FilterValue')]";
//        //wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(By.xpath(brandShown))));
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(brandShown)));
//        List<String> someBrands = new ArrayList<>(brands);
//        List<WebElement> brandElements = driver.findElements(By.xpath(brandShown));
//        for (WebElement element : brandElements) {
//            //wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(element)));
//            element = wait.until(ExpectedConditions.elementToBeClickable(element));
//            String text = element.getText();
//            for (String brand : brands) {
//                if (text.contains(brand)) {
//                    WebElement checkbox = element.findElement(By.xpath(".//label"));
//                    wait.until(ExpectedConditions.attributeToBeNotEmpty(checkbox, "aria-checked"));
//                    while ("false".equals(checkbox.getAttribute("aria-checked"))) {
//                        checkbox.click();
//                        someBrands.remove(brand);
//                    }
//                }
//            }
//        }
//        if (!someBrands.isEmpty()) {
//            inputBrandsShowMore(brandShown, brandSearchLocation, someBrands);
//        }
//    }

//    public void inputBrandsShowMore(String brandShown, String brandSearchLocation, List<String> someBrands) {
//        List<String> someL = new ArrayList<>(someBrands);
//        for (String element : someL) {
//            while (!someL.isEmpty()) {
//                List<WebElement> brandElements = driver.findElements(By.xpath(brandShown));
//                if (brandElements.isEmpty()) {
//                    break;
//                }
//                WebElement lastElement = brandElements.get(brandElements.size() - 1);
//                lastElement.click();
//                //wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(By.xpath(brandShown))));
//                //wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(By.xpath(brandSearchLocation))));
//                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(brandSearchLocation)));
//                this.brandSearch = driver.findElement(By.xpath(brandSearchLocation));
//                //wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(By.xpath(brandShown))));
//                brandSearch.click();
//                brandSearch.sendKeys(element);
//                By elementLocator = By.xpath(brandShown +
//                        "//span[contains(text(),'" + element + "' )]");
//                //wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(elementLocator)));
//                brandElements = driver.findElements(By.xpath(brandShown));
//                List<WebElement> foundElements = driver.findElements(elementLocator);
//                if (foundElements.isEmpty()) {
//                    System.out.println("Пока не нашел" + element);
//                    continue;
//                }
//                WebElement brandObject = foundElements.get(0);
//                brandObject.click();
//                //wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(By.xpath(brandSearchLocation + "//button[text()='Очистить']"))));
//                this.brandSearchDel = driver.findElement(By.xpath(brandSearchLocation + "//button[contains(text(),'Очистить')]"));
//                brandSearchDel.click();
//                someL.remove(element);
//            }
//        }
//    }


//    public void inputBrands(String brandNameOne, String brandNameTwo) {
//        By brandShowMoreButtonLocator = By.xpath("//div[contains(@data-zone-data, 'Бренд')]//div[contains(@data-zone-name, 'LoadFilterValue')]//button");
//        By brandSearchLocation = By.xpath("//div[contains(@data-zone-data, 'Бренд')]//div[contains(@data-zone-name, 'filterSearchValueField')]//input");
//        By firstBrandLocator = By.xpath("//div[contains(@data-zone-data, 'Бренд')]//div[contains(@data-zone-name, 'FilterValue')]//span[contains(text(),'" + brandNameOne + "' )]");
//        By secondBrandLocator = By.xpath(" //div[contains(@data-zone-data, 'Бренд')]//div[contains(@data-zone-name, 'FilterValue')]//span[contains(text(),'" + brandNameTwo + "')]");
//
//        boolean isFirstVisible = isLocatorVisible(firstBrandLocator);
//        boolean isSecondVisible = isLocatorVisible(secondBrandLocator);
//
//        if (isFirstVisible || isSecondVisible) {
//            if (isFirstVisible) {
//                wait.until(visibilityOfElementLocated(firstBrandLocator));
////                this.selectorLenovo = driver.findElement(firstBrandLocator);
////                selectorLenovo.click();
//            }
//            if (!isSecondVisible) {
//                isSecondVisible = isLocatorVisible(secondBrandLocator);
//            }
//            if (isSecondVisible) {
//                wait.until(visibilityOfElementLocated(secondBrandLocator));
//                this.selectorHP = driver.findElement(secondBrandLocator);
//                selectorHP.click();
//            }
//        } else {
//            long startTime = System.currentTimeMillis();
//            long maxTime = 60000;
//            while (!isLocatorVisible(brandSearchLocation) && (System.currentTimeMillis() - startTime) < maxTime) {
//                try {
//                    wait.until(ExpectedConditions.elementToBeClickable(brandShowMoreButtonLocator));
//                    this.brandShowMoreButton = driver.findElement(brandShowMoreButtonLocator);
//                    brandShowMoreButton.click();
//                } catch (Exception e) {
//                    System.out.println("Кнопка 'Показать всё' не кликабельна: " + e.getMessage());
//                    break;
//                }
//            }
//            wait.until(visibilityOfElementLocated(brandSearchLocation));
//            this.brandSearch = driver.findElement(brandSearchLocation);
//            brandSearch.click();
//            if (!isFirstVisible) {
//                brandSearch.sendKeys(brandNameOne);
//                wait.until(visibilityOfElementLocated(firstBrandLocator));

    /// /                this.selectorLenovo = driver.findElement(firstBrandLocator);
    /// /                selectorLenovo.click();
//            }
//            brandSearch.clear();
//            if (!isSecondVisible) {
//                brandSearch.sendKeys(brandNameTwo);
//                wait.until(visibilityOfElementLocated(secondBrandLocator));
//                this.selectorHP = driver.findElement(secondBrandLocator);
//                selectorHP.click();
//            }
//        }
//    }
    public int CountOfElementsOnFirstPage() {
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

    public void CountOfElementsOnAllPages() {
        List<WebElement> foundElements = new ArrayList<>();
        Actions actions = new Actions(driver);
        long startTime = System.currentTimeMillis();
        long timeLimit = 60000;

        while (System.currentTimeMillis() - startTime < timeLimit) {
            if (driver.findElements(By.xpath("//div[contains(@data-baobab-name, 'pager')]" +
                    "//div[contains(@data-baobab-name, 'next')]")).isEmpty()) {
                List<WebElement> elements = driver.findElements(By.xpath(
                        "//div[contains(@data-auto, 'SerpList')]" +
                                "//div[contains(@data-auto-themename, 'listDetailed')]"));
                foundElements.addAll(elements);
                break;
            } else {
                actions.sendKeys(org.openqa.selenium.Keys.PAGE_DOWN).perform();
                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                            "//div[contains(@data-auto, 'SerpList')]" +
                                    "//div[contains(@data-auto-themename, 'listDetailed')]")));
                } catch (TimeoutException e) {
                    System.out.println("Элемент не появился вовремя.");
                    break;
                }
            }
        }

        list = foundElements;
    }

    //div[contains(@data-auto-themename, 'listDetailed')]//div[contains(@data-baobab-name, 'price')]//span[contains(@data-auto, 'snippet-price-current')]//*[1][name()="span"]
//div[contains(@data-auto-themename, 'listDetailed')]//span[contains(@data-auto, 'snippet-price-current')]//*[1][name()="span"]
    public boolean checkFilters(Double minimum, Double maximum, List<String> namesOfBrand) {
        CountOfElementsOnAllPages();
        List<WebElement> find = list.stream()
                .filter(product -> {
                    String titleText = product.findElement(By.xpath(
                            ".//div[contains(@data-baobab-name, 'title')]//span")).getText().toLowerCase();
                    boolean isBrandMatch = namesOfBrand.stream()
                            .map(String::toLowerCase)
                            .anyMatch(titleText::contains);
                    String priceText = product.findElement(By.xpath(
                            ".//span[contains(@data-auto, 'snippet-price-current')]//*[1][name()='span']")).getText().replaceAll("[^\\d.]", "");
                    double price = Double.parseDouble(priceText);
                    boolean isPriceInRange = price > minimum && price < maximum;
                    return isBrandMatch && isPriceInRange;
                })
                .collect(Collectors.toList());

        System.out.println(find.size());
        System.out.println(list.size());
        List<WebElement> remaining = list.stream()
                .filter(product -> !find.contains(product))
                .collect(Collectors.toList());
        if (!remaining.isEmpty()) {
            for (WebElement element : remaining) {
                element.click();
                ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
                driver.switchTo().window(tabs.get(tabs.size() - 1));

                WebElement brand = driver.findElement(By.xpath("//div[contains(@data-zone-name," +
                        " 'fullSpecs')]//div[contains(@aria-label, 'Характеристики')]//span[contains(text(),'Бренд')]" +
                        "/../.. /following-sibling::div[1]//span"));
                if (brand != null) {
                    String brandText = brand.getText().trim();

                    boolean containsBrand = namesOfBrand.stream().anyMatch(brandText::equalsIgnoreCase);
                    if (containsBrand) {
                        System.out.println(element);
                        find.add(element);
                        driver.close();
                        driver.switchTo().window(tabs.get(0));
                    }
                }

            }
        }
        System.out.println("---------------------------------------------------------");
        System.out.println(find.size());
        System.out.println(list.size());
        return find.size() == list.size();
    }

}
