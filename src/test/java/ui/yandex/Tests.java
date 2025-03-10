package ui.yandex;

import io.qameta.allure.Feature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static helpers.Properties.testsProperties;
import static steps.StepsAll.*;

public class Tests extends BaseTest {

    @DisplayName("Проверка открытия сайта ЯндексМаркета")
    @ParameterizedTest(name="{displayName}: {arguments}")
    @MethodSource("helpers.DataProvider#providerFindLaptopsInCatalog")
    public void testOpen() {
        openSite(testsProperties.yandexMarketUrl(), "Яндекс Маркет", chromeDriver);
    }

    @DisplayName("Проверка поиска каталога ЯндексМаркета")
    @ParameterizedTest(name="{displayName}: {arguments}")
    @MethodSource("helpers.DataProvider#providerFindLaptopsInCatalog")
    public void testFindCatalog() {
        openSite(testsProperties.yandexMarketUrl(), "Яндекс Маркет", chromeDriver);
        findCatalog();
    }

    @DisplayName("Проверка поиска электроники в каталоге ЯндексМаркета")
    @ParameterizedTest(name="{displayName}: {arguments}")
    @MethodSource("helpers.DataProvider#providerFindLaptopsInCatalog")
    public void testFindElectronic(String electronic) {
        openSite(testsProperties.yandexMarketUrl(), "Яндекс Маркет", chromeDriver);
        findElectronic(electronic);
    }

    @DisplayName("Проверка поиска ноутбука в каталоге ЯндексМаркета")
    @ParameterizedTest(name="{displayName}: {arguments}")
    @MethodSource("helpers.DataProvider#providerFindLaptopsInCatalog")
    public void testFindLaptop(String electronic, String laptop) {
        openSite(testsProperties.yandexMarketUrl(), "Яндекс Маркет", chromeDriver);
        findcatalogSubItem(electronic,laptop);
    }

    @DisplayName("Проверка, что мы находимся в каталоге Ноутбуки ЯндексМаркета")
    @ParameterizedTest(name="{displayName}: {arguments}")
    @MethodSource("helpers.DataProvider#providerFindLaptopsInCatalog")
    public void testCheckLaptop(String catalogSubItem) {
        openSite(testsProperties.yandexMarketUrl(), "Яндекс Маркет", chromeDriver);
        checkingTitle(catalogSubItem);
    }

    @DisplayName("Ввод цен в фильтр")
    @ParameterizedTest(name="{displayName}: {arguments}")
    @MethodSource("helpers.DataProvider#providerPriceFilter")
    public void testPriceFilter(String minimumPrice, String maximumPrice) {
        openSite(testsProperties.yandexMarketUrl(), "Яндекс Маркет", chromeDriver);
        inputPriceFilter(minimumPrice, maximumPrice);
    }

    @DisplayName("Выбор брендов в фильтре")
    @ParameterizedTest(name="{displayName}: {arguments}")
    @MethodSource("helpers.DataProvider#providerBrandsFilter")
    public void testBrandFilter(String firstBrand, String secondBrand) {
        openSite(testsProperties.yandexMarketUrl(), "Яндекс Маркет", chromeDriver);
        inputBrandsFilter(firstBrand, secondBrand);
    }

    @DisplayName("Подсчет количества элементов на первой странице")
    @ParameterizedTest(name="{displayName}: {arguments}")
    @MethodSource("helpers.DataProvider#providerFilters")
    public void testСheckCountElementsOnFirstPage(String minimumPrice, String maximumPrice, String firstBrand, String secondBrand) {
        openSite(testsProperties.yandexMarketUrl(), "Яндекс Маркет", chromeDriver);
        checkCountElementsOnFirstPage(minimumPrice, maximumPrice, firstBrand, secondBrand,12);
    }

    @DisplayName("Подсчет количества элементов")
    @ParameterizedTest(name="{displayName}: {arguments}")
    @MethodSource("helpers.DataProvider#providerFilters")
    public void testCheckFilters(String minimumPrice, String maximumPrice, String firstBrand, String secondBrand) {
        openSite(testsProperties.yandexMarketUrl(), "Яндекс Маркет", chromeDriver);
        checkFilters(minimumPrice, maximumPrice, firstBrand, secondBrand);
    }





}