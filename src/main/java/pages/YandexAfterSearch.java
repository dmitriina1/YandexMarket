package pages;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
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
    private WebElement selectorHP;
    private WebElement selectorLenovo;
    private List<WebElement> list;
    private WebElement pageTitle;

    public YandexAfterSearch(WebDriver driver) {
        this.driver = driver;
        this.wait=new WebDriverWait(driver, testsProperties.defaultTimeout());
        this.list = new ArrayList<>();
        wait.until(visibilityOfElementLocated(By.xpath("//div[contains(@data-zone-name, 'searchTitle')]//h1")));
        this.pageTitle = driver.findElement(By.xpath("//div[contains(@data-zone-name, 'searchTitle')]//h1"));
    }

    public YandexAfterSearch(WebDriver driver, String searchQuery) {
        this.driver = driver;
        this.wait=new WebDriverWait(driver, testsProperties.defaultTimeout());
        this.list = new ArrayList<>();
        driver.get("https://market.yandex.ru/search?text="+searchQuery + "&hid=91013");
    }

    public void checkingTitleByText(String link){
        wait.until(visibilityOfElementLocated(By.xpath("//div[contains(@data-zone-name, 'searchTitle')]//h1")));
        this.pageTitle = driver.findElement(By.xpath("//div[contains(@data-zone-name, 'searchTitle')]//h1"));
        Assertions.assertFalse( driver.findElements(By.xpath("//div[contains(@data-zone-name, 'searchTitle')]//h1")).size()==0,
                "Не найдено тайтла с текстом: '"+link);
    }

    public void inputPriceInterval(String minimum, String maximum){
        wait.until(visibilityOfElementLocated(By.xpath("//span[contains(@data-auto, 'filter-range-min')]//label[contains(text(), 'Цена')]/..//input")));
        this.minimumPriceInterval = driver.findElement(By.xpath("//span[contains(@data-auto, 'filter-range-min')]//label[contains(text(), 'Цена')]/..//input"));
        minimumPriceInterval.click();
        minimumPriceInterval.sendKeys(minimum + Keys.ENTER);


        wait.until(visibilityOfElementLocated(By.xpath("//span[contains(@data-auto, 'filter-range-max')]//label[contains(text(), 'Цена')]/..//input")));
        this.maximumPriceInterval = driver.findElement(By.xpath("//span[contains(@data-auto, 'filter-range-max')]//label[contains(text(), 'Цена')]/..//input"));
        maximumPriceInterval.click();
        maximumPriceInterval.sendKeys(maximum + Keys.ENTER);
    }

    private boolean isLocatorVisible(By inputFieldLocator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(inputFieldLocator)) != null;
        } catch (Exception e) {
            return false;
        }
    }

        public void inputBrands(String brandNameOne, String brandNameTwo){
        By brandShowMoreButtonLocator = By.xpath("//div[contains(@data-zone-data, 'Бренд')]//div[contains(@data-zone-name, 'LoadFilterValue')]//button");
        By brandSearchLocation = By.xpath("//div[contains(@data-zone-data, 'Бренд')]//div[contains(@data-zone-name, 'filterSearchValueField')]//input");
        By firstBrandLocator = By.xpath("//div[contains(@data-zone-data, 'Бренд')]//div[contains(@data-zone-name, 'FilterValue')]//span[contains(text(),'" + brandNameOne + "' )]");
        By secondBrandLocator = By.xpath(" //div[contains(@data-zone-data, 'Бренд')]//div[contains(@data-zone-name, 'FilterValue')]//span[contains(text(),'" + brandNameTwo + "')]");

        boolean isFirstVisible = isLocatorVisible(firstBrandLocator);
        boolean isSecondVisible = isLocatorVisible(secondBrandLocator);

        if (isFirstVisible || isSecondVisible) {
            if (isFirstVisible) {
                wait.until(visibilityOfElementLocated(firstBrandLocator));
                this.selectorLenovo = driver.findElement(firstBrandLocator);
                selectorLenovo.click();
            }
            if (!isSecondVisible) {
                isSecondVisible = isLocatorVisible(secondBrandLocator);
            }
            if (isSecondVisible) {
                wait.until(visibilityOfElementLocated(secondBrandLocator));
                this.selectorHP = driver.findElement(secondBrandLocator);
                selectorHP.click();
            }
        } else {
            long startTime = System.currentTimeMillis();
            long maxTime = 60000;
            while (!isLocatorVisible(brandSearchLocation) && (System.currentTimeMillis() - startTime) < maxTime) {
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(brandShowMoreButtonLocator));
                    this.brandShowMoreButton = driver.findElement(brandShowMoreButtonLocator);
                    brandShowMoreButton.click();
                } catch (Exception e) {
                    System.out.println("Кнопка 'Показать всё' не кликабельна: " + e.getMessage());
                    break;
                }
            }
            wait.until(visibilityOfElementLocated(brandSearchLocation));
            this.brandSearch = driver.findElement(brandSearchLocation);
            brandSearch.click();
            if(!isFirstVisible){
            brandSearch.sendKeys(brandNameOne);
                wait.until(visibilityOfElementLocated(firstBrandLocator));
                this.selectorLenovo = driver.findElement(firstBrandLocator);
                selectorLenovo.click();}
            brandSearch.clear();
            if (!isSecondVisible) {
                brandSearch.sendKeys(brandNameTwo);
                wait.until(visibilityOfElementLocated(secondBrandLocator));
                this.selectorHP = driver.findElement(secondBrandLocator);
                selectorHP.click();
            }
        }
    }

    public int CountOfElementsOnFirstPage() {
        List<WebElement> foundElements = new ArrayList<>();
        Actions actions = new Actions(driver);
        long startTime = System.currentTimeMillis();
        long maxTime = 60000;
        while ((System.currentTimeMillis() - startTime) < maxTime) {
            List<WebElement> elements = driver.findElements(By.xpath("//div[contains(@data-auto, 'SerpList')]//div[contains(@data-auto-themename, 'listDetailed')]"));
            foundElements.addAll(elements);

            if (driver.findElements(By.xpath("//div[contains(@data-baobab-name, 'pager')]")).size() > 0) {
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
                    return  namesOfBrand.stream()
                            .map(String::toLowerCase)
                            .anyMatch(titleText::contains);
//                    String priceText = product.findElement(By.xpath(
//                            ".//span[contains(@data-auto, 'snippet-price-current')]//*[1][name()='span']")).getText();
//                    double price = Double.parseDouble(priceText);
//                    boolean isPriceInRange = price > minimum && price < maximum;
//                    return isBrandMatch && isPriceInRange;
                })
                .collect(Collectors.toList());

        System.out.println(find.size());
        System.out.println(list.size());
        List<WebElement> remaining = list.stream()
                .filter(product -> !find.contains(product))
                .collect(Collectors.toList());
        if(!remaining.isEmpty()){
            for (WebElement element : remaining) {
                element.click();
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
                driver.switchTo().window(tabs.get(tabs.size() - 1));

                WebElement brand = driver.findElement(By.xpath("//div[contains(@data-zone-name," +
                        " 'fullSpecs')]//div[contains(@aria-label, 'Характеристики')]//span[contains(text(),'Бренд')]" +
                        "/../.. /following-sibling::div[1]//span"));
                String brandText = brand.getText().trim(); // Убираем лишние пробелы

                boolean containsBrand = namesOfBrand.stream().anyMatch(brandText::equalsIgnoreCase);
                if(containsBrand){
                    System.out.println(element);
                    find.add(element);
                }
                driver.close();
                driver.switchTo().window(tabs.get(0));
            }
        }
        System.out.println("---------------------------------------------------------");
        System.out.println(find.size());
        System.out.println(list.size());
        return find.size() == list.size();
    }

}
