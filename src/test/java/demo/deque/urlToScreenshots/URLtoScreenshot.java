package demo.deque.urlToScreenshots;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;

import io.github.bonigarcia.wdm.WebDriverManager;

public class URLtoScreenshot {

	public static WebDriver driver;

	@SuppressWarnings({ "resource"})
	public void readExcel(String filePath, String fileName, String sheetName) throws IOException {
		
		int width = 1920;
        int height = 1080;
		
		//Storing failed urls in a separate file for tracking
		PrintWriter failedList = new PrintWriter("failedURLs.txt");		
		//WebDriverManager.firefoxdriver().setup();
		//driver = new FirefoxDriver();
		WebDriverManager.chromedriver().setup();
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		//WebDriver driver = new ChromeDriver(options);
		
		driver = new ChromeDriver(options);		
		//driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);		
		
		
        //Remove the window from fullscreen (optional), if it s in fullscreen the outerHeight is not accurate
        driver.manage().window().setSize(new Dimension(800,800));
        JavascriptExecutor js= (JavascriptExecutor)driver;

        String windowSize = js.executeScript("return (window.outerWidth - window.innerWidth + "+width+") + ',' + (window.outerHeight - window.innerHeight + "+height+"); ").toString();

        //Get the values
        width = Integer.parseInt(windowSize.split(",")[0]);
        height = Integer.parseInt(windowSize.split(",")[1]);
        System.out.println("Width is :"+width+ "and height is :"+height);

        //Set the window
        driver.manage().window().setSize(new Dimension(width, height));       
        
        //Dimension dimension = new Dimension(1920, 1080);
        //driver.manage().window().setSize(dimension);

		// Create an object of File class to open xlsx file
		File file = new File(filePath + "\\" + fileName);

		// Create an object of FileInputStream class to read excel file
		FileInputStream inputStream = new FileInputStream(file);

		Workbook myWorkbook = null;

		myWorkbook = new XSSFWorkbook(inputStream);

		// Read sheet inside the workbook by its name
		Sheet mySheet = myWorkbook.getSheet(sheetName);

		// Find number of rows in excel file - this will be passed in the below for loop
		int rowCount = mySheet.getLastRowNum() - mySheet.getFirstRowNum();

		String targetFilePath = System.getProperty("user.dir") + "\\target\\screenshots\\";

		// Create a loop over all the rows of excel file to read it
		//for (int i = 1; i < rowCount + 1; i++) {
		for (int i = 2; i < rowCount + 1; i++) {
			Row row = mySheet.getRow(i);
			String url = row.getCell(0).getStringCellValue();//1st column of the excel contains all the URLs
			//String tcName = (row.getCell(0).getStringCellValue()).replaceAll("\\s", "");
			if(!(url.contains("http://") || url.contains("https://"))) {
				url = "https://"+url;			
			}
			System.out.println("Updated URL is :" + url);
			try {
				driver.get(url);//Selenium command to launch URL.
				Thread.sleep(30000);
				BufferedImage image = Shutterbug.shootPage(driver, ScrollStrategy.WHOLE_PAGE, 5).getImage();				
				//BufferedImage image = Shutterbug.shootPage(driver, ScrollStrategy.VIEWPORT_ONLY).getImage();
				int seqNo = 0 + i;
				ImageIO.write(image, "png", new File(targetFilePath + seqNo + ".png"));				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Page 404 issue is observed with the URL : " + url);
				failedList.println(i + "-" + url);			
			}
		}
		failedList.close();

	}

	// Main function is calling readExcel function to read data from excel file
	public static void main(String... strings) throws IOException {
		// Create an object of the excelFile class
		URLtoScreenshot objExcelFile = new URLtoScreenshot();
		// Prepare the path of the excel file
		String filePath = System.getProperty("user.dir") + "\\src\\resources";
		// Call read file method of the class to read data
		objExcelFile.readExcel(filePath, "apex_batch7_part2.xlsx", "Sheet1");
		driver.close();
	}

}

/*
 * AShot implementation - take screenshot of the entire page Screenshot
 * 
 * import ru.yandex.qatools.ashot.AShot; 
 * import ru.yandex.qatools.ashot.Screenshot;
 * import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
 * 
 * screenshot=new
 * AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).
 * takeScreenshot(driver); try { ImageIO.write(screenshot.getImage(),"PNG",new
 * File(targetFilePath + i + ".png")); } catch (IOException e) { // TODO
 * Auto-generated catch block e.printStackTrace(); }
 * 
 * //driver.quit();
 * 
				
//WebDriverWait wait = new WebDriverWait(driver, 120);
//wait.until(WebDriver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete"));
//js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
 */