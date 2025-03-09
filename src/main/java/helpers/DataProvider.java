package helpers;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class DataProvider {

    public static Stream<Arguments> providerFindLaptopsInCatalog(){
        return Stream.of(
                Arguments.of( "Электроника", "Ноутбуки")
        );
    }

    public static Stream<Arguments> providerPriceFilter(){
        return Stream.of(
                Arguments.of( "10000", "30000")
        );
    }

    public static Stream<Arguments> providerBrandsFilter(){
        return Stream.of(
                Arguments.of( "Lenovo", "HP")
        );
    }

    public static Stream<Arguments> providerFilters() {
        return providerPriceFilter().flatMap(priceArgs ->
                providerBrandsFilter().map(brandArgs ->
                        Arguments.of(priceArgs.get()[0], priceArgs.get()[1], brandArgs.get()[0], brandArgs.get()[1])
                )
        );
    }





}
