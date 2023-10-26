
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.sql.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MainAnalyzer {
    public static void main(String[] args){
        //Относительная ссылка, работает из IDE
        //System.setProperty("webdriver.chrome.driver", "./src/main/resources/chromedriver.exe");
        //Абсолютная ссылка, работает из jar файла
        System.setProperty("webdriver.chrome.driver", "C:/Users/Ruslan1/IdeaProjects/AutoMarketAnalyze/src/main/resources/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        WebDriverWait wait = new WebDriverWait(driver, 20);
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(50000, TimeUnit.MILLISECONDS);
        driver.manage().timeouts().implicitlyWait(30000, TimeUnit.MILLISECONDS);
        //Открывает каталог видеокарт
        driver.get("https://www.dns-shop.ru/catalog/17a89aab16404e77/videokarty/");
        //driver.get("https://www.dns-shop.ru/catalog/17a89aab16404e77/videokarty/?p=4");
        //driver.get("https://www.dns-shop.ru/catalog/17a89aab16404e77/videokarty/?stock=now-today-tomorrow-later&p=9");
        //Список имен видеокарт со страницы каталогоа

        //driver.findElement(By.cssSelector("[class=\"catalog-product__name ui-link ui-link_black\"]")).click();
        //Actions action = new Actions(driver);

        do {
            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("[class=\"catalog-product__name ui-link ui-link_black\"]"),"идеокарт"));
            List<WebElement> videocardItems = driver.findElements(By.cssSelector("[class=\"catalog-product__name ui-link ui-link_black\"]"));
            //Список цен со страницы каталого
            //wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("[class=\"product-buy__price\"]")," ₽"));
            //List<WebElement> priceItems = driver.findElements(By.cssSelector("[class=\"product-buy__price\"]"));
            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("div[class*=\"product-buy__price\"]")," ₽"));
            List<WebElement> priceItems = driver.findElements(By.cssSelector("div[class*=\"product-buy__price\"]"));
            //Перебор одной страницы каталога
            for (int i = 0; i < videocardItems.size(); i++)
            {
                wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("[class=\"catalog-product__name ui-link ui-link_black\"]"),"идеокарт"));
                videocardItems = driver.findElements(By.cssSelector("[class=\"catalog-product__name ui-link ui-link_black\"]"));
                wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("div[class*=\"product-buy__price\"]")," ₽"));
                priceItems = driver.findElements(By.cssSelector("div[class*=\"product-buy__price\"]"));
                for(int g=0; g< priceItems.size();g++)
                {
                    if(priceItems.get(g).getText().contains("мес"))
                    {
                    priceItems.remove(g);
                    g--;
                    }
                }
                wait.until(ExpectedConditions.textToBePresentInElement(videocardItems.get(i),"идеокарт"));
                String nameCard = videocardItems.get(i).getText();
                try {
                    //Поиск видеокарты в БД по названию
                    int idVideocard = -1;
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/videocards", "root", "1234");
                    Statement statement = conn.createStatement();
                    String selectCommand = "Select id_videocard from videocard where name=\"" + nameCard + "\"";
                    ResultSet resultSet = statement.executeQuery(selectCommand);
                    while (resultSet.next()) {
                        idVideocard = resultSet.getInt("id_videocard");
                    }
                    conn.close();
                    //Если видеокарты еще нет в БД, то переход на ее страницу и добавление
                    if (idVideocard == -1)
                    {
                        wait.until(ExpectedConditions.elementToBeClickable(videocardItems.get(i)));
                        videocardItems.get(i).click();
                        String memoryVol = "";
                        String techProcces = "";
                        String memoryType = "";
                        String power = "";
                        while(memoryVol==""||techProcces==""||memoryType==""||power=="")
                        {
                            wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.cssSelector("[class=\"button-ui button-ui_white product-characteristics__expand\"]"))));
                            driver.findElement(By.cssSelector("[class=\"button-ui button-ui_white product-characteristics__expand\"]")).click();
                            List<WebElement> characteristics = driver.findElements(By.cssSelector("[class=\"product-characteristics__spec-value\"]"));
                            for (int j = 0; j < characteristics.size(); j++) {
                                if ((characteristics.get(j).getText().contains(" ГБ")) & (characteristics.get(j).getText().length() <= 6)) {
                                    memoryVol = characteristics.get(j).getText().substring(0, characteristics.get(j).getText().indexOf(" "));
                                    j = characteristics.size() + 1;
                                }
                            }
                            for (int j = 0; j < characteristics.size(); j++) {
                                if ((characteristics.get(j).getText().contains(" нм")) & (characteristics.get(j).getText().length() <= 6)) {
                                    techProcces = characteristics.get(j).getText().substring(0, characteristics.get(j).getText().indexOf(" "));
                                    j = characteristics.size() + 1;
                                }
                            }
                            for (int j = 0; j < characteristics.size(); j++) {
                                if ((characteristics.get(j).getText().contains("DDR")) & (characteristics.get(j).getText().length() <= 8)) {
                                    memoryType = characteristics.get(j).getText();
                                    j = characteristics.size() + 1;
                                }
                            }
                            for (int j = 0; j < characteristics.size(); j++) {
                                if ((characteristics.get(j).getText().contains(" Вт")) & (characteristics.get(j).getText().length() <= 8)) {
                                    power = characteristics.get(j).getText().substring(0, characteristics.get(j).getText().indexOf(" "));
                                    j = characteristics.size() + 1;
                                }
                            }
                        }
                        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/videocards", "root", "1234");
                        statement = conn.createStatement();
                        String insertCommand = "INSERT videocard(name, memory_volume, memory_type, tech_procces, power) VALUES ('";
                        insertCommand = insertCommand + nameCard + "',";
                        insertCommand = insertCommand + memoryVol + ",'";
                        insertCommand = insertCommand + memoryType + "',";
                        insertCommand = insertCommand + techProcces + ",";
                        insertCommand = insertCommand + power + ")";
                        statement.executeUpdate(insertCommand);
                        conn.close();
                        driver.navigate().back();
                        driver.navigate().back();
                        i--;
                    }
                    //Если видеокарта есть в БД, то записываем ее цену на текущий момент без перехода на ее страницу
                    else {
                        wait.until(ExpectedConditions.textToBePresentInElement(priceItems.get(i)," ₽"));
                        String price="";
                        //price = priceItems.get(i).getText().substring(0, priceItems.get(i).getText().length() - 2);
                        price = priceItems.get(i).getText().substring(0, priceItems.get(i).getText().indexOf('₽') - 1);
                        price = price.replace(" ", "");
                        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/videocards", "root", "1234");
                        statement = conn.createStatement();
                        String insertCommand = "INSERT price(id_videocard, price) VALUES ('";
                        insertCommand = insertCommand + idVideocard + "',";
                        insertCommand = insertCommand + price + ")";
                        statement.executeUpdate(insertCommand);
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                videocardItems = driver.findElements(By.cssSelector("[class=\"catalog-product__name ui-link ui-link_black\"]"));
                //videocardItems.get(i).sendKeys(Keys.CONTROL + "\t");
            }
            List<WebElement> pages = driver.findElements(By.cssSelector("[class=\"pagination-widget__page-link pagination-widget__page-link_next \"]"));
            if(pages.size()>0)
            {
                pages.get(0).click();
                jse.executeScript("window.scrollTo(0, -document.body.scrollHeight);");
                driver.navigate().refresh();
            }
            else break;
        }
        while(true);
    }
}
