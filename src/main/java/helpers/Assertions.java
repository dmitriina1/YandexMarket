package helpers;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Assertions {


    public static void assertTimeOut(boolean a, String error) {
        try {
            org.junit.jupiter.api.Assertions.assertTrue(a, error);
        }
        catch (TimeoutException exception) {
            org.junit.jupiter.api.Assertions.fail("Ошибка");
        }
    }
}
