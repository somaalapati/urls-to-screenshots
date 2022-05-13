package demo.deque.urlToScreenshots;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

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
import org.testng.annotations.Test;

import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;
import com.google.common.net.InternetDomainName;

import io.github.bonigarcia.wdm.WebDriverManager;

public class URLtoScreenshot {

	//public WebDriver driver;

		@SuppressWarnings({ "resource" })
		public void readExcel(String filePath, String fileName, String sheetName, int fileSeq, String failList, int index)
				throws IOException {
			int width = 1920;
			int height = 1080;
			PrintWriter failedList = new PrintWriter(failList);
			WebDriverManager.chromedriver().setup();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--headless");
			WebDriver driver = new ChromeDriver(options);
			driver.manage().window().setSize(new Dimension(800, 800));
			JavascriptExecutor js = (JavascriptExecutor) driver;
			String windowSize = js.executeScript("return (window.outerWidth - window.innerWidth + " + width
					+ ") + ',' + (window.outerHeight - window.innerHeight + " + height + "); ").toString();
			width = Integer.parseInt(windowSize.split(",")[0]);
			height = Integer.parseInt(windowSize.split(",")[1]);
			System.out.println("Width is :" + width + "and height is :" + height);
			driver.manage().window().setSize(new Dimension(width, height));
			File file = new File(filePath + "\\" + fileName);
			FileInputStream inputStream = new FileInputStream(file);
			Workbook myWorkbook = null;
			myWorkbook = new XSSFWorkbook(inputStream);
			Sheet mySheet = myWorkbook.getSheet(sheetName);
			int rowCount = mySheet.getLastRowNum() - mySheet.getFirstRowNum();
			String targetFilePath = System.getProperty("user.dir") + "\\target\\screenshots\\";
			String skippedFilePath = System.getProperty("user.dir") + "\\target\\skipped_screenshots\\";
			//index is nothing but the row number in the excel file. Sometimes screenshots are generated for only some rows. So
			//this index number is useful while rerunning the same file by excluding completed URLs.		
			for (int i = index; i < rowCount + 1; i++) {
				Row row = mySheet.getRow(i);
				String url = row.getCell(0).getStringCellValue();// 1st column of the excel contains all the URLs
				if (!(url.contains("http://") || url.contains("https://"))) {
					url = "https://" + url;
					}			
				try {
					driver.get(url);// Selenium command to launch URL.
					Thread.sleep(20000);
					BufferedImage image = Shutterbug.shootPage(driver, ScrollStrategy.WHOLE_PAGE, 5).getImage();
					int seqNo = fileSeq + i;				
					int imageHeight = image.getHeight();
					//Skipping all the screenshots with height more than 5000px.
					String domainName = extractDomain(url);
					if(imageHeight > 5000) {
						System.out.println(seqNo + " - Skipping it. Height of this screenshot is more thatn 5000 px. Height is : "+imageHeight);
						ImageIO.write(image, "png", new File(skippedFilePath + seqNo + "-" + imageHeight +"-"+domainName+".png"));
					}
					else {
						ImageIO.write(image, "png", new File(targetFilePath + seqNo+"-"+domainName+ ".png"));
						System.out.println(seqNo+" - Successfully genereated screenshot for the URL:" + url);
					}				
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Page 404 issue is observed with the URL : " + url);
					driver.navigate().refresh();
					failedList.println(i + "-" + url);
				}
			}
			failedList.close();
			driver.close();

		}
		
		public String extractDomain(String url) throws MalformedURLException {
		     URL myUrl = new URL(url);
		         String host = myUrl.getHost();
		         InternetDomainName name1 = InternetDomainName.from(host).topPrivateDomain();
		         String domain = name1.toString();
		         return domain;
		}

		@Test
		public void script1() throws IOException {		
			String filePath = System.getProperty("user.dir") + "\\src\\resources";
			readExcel(filePath, "URLsDocument.xlsx", "urls", 0, "failedURLs_1.txt", 1);		
		}	
		
		//Uncomment this test when you want to run a couple of URL files parallelly. Run testng.xml to kick start the parallel execution.
		//@Test
		public void script2() throws IOException {		
			String filePath = System.getProperty("user.dir") + "\\src\\resources";
			readExcel(filePath, "122021URLs_1001_to_2000.xlsx", "April06-2022", 1000, "failedURLs_2.txt", 816);		
		}

	}