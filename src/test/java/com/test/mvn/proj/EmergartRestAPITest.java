package com.test.mvn.proj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.log.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.test.mvn.proj.download.OAuthGoogleFilesDownload;
import com.test.mvn.proj.exception.EmergartSeleniumException;
import com.test.mvn.proj.helper.ApachePOIHelper;
import com.test.mvn.proj.helper.RestAPIHelper;
import com.test.mvn.proj.helper.TestHelper;
import com.test.mvn.proj.util.TestUtility;
import com.test.mvn.proj.vo.ShippingCostDetailsVO;
import com.test.mvn.proj.vo.ShippingDataVO;

/**
 * The Class EmergartSeleniumTest.
 */
public class EmergartRestAPITest {

	/** The Constant LOG. */
	private final static Logger LOG = Logger.getLogger(EmergartRestAPITest.class);

	/** The Constant PROD_URL_KEY. */
	private static final String PROD_URL_KEY = "prodURL";

	/** The Constant PROD_ID_STR_KEY. */
	private static final String PROD_ID_STR_KEY = "prodIdStr";

	/** The Constant PROD_TITLE_KEY. */
	private static final String PROD_TITLE_KEY = "prodTitle";

	/** The Constant PROD_TYPE_KEY. */
	private static final String PROD_TYPE_KEY = "prodType";

	/** The test helper. */
	private static TestHelper testHelper = new TestHelper();

	/** The props. */
	private static Properties props;

	/** The driver. */
	private static WebDriver driver;

	/** The poi helper. */
	private ApachePOIHelper poiHelper = new ApachePOIHelper();

	private String elementName = StringUtils.EMPTY;

	/**
	 * Prepare prerequisites.
	 *
	 * @throws EmergartSeleniumException
	 *             the emergart selenium exception
	 */
	@BeforeClass
	public static void preparePrerequisites() throws EmergartSeleniumException {
		props = testHelper.initializePropertiesObj();
		String path = props.getProperty("chromeDriverPath");
		System.setProperty("webdriver.chrome.driver", path);
		driver = new ChromeDriver();
	}

	/**
	 * Download from google.
	 *
	 * @throws EmergartSeleniumException
	 *             the emergart selenium exception
	 */
	@Before
	public void downloadFromGoogle() throws EmergartSeleniumException {
		OAuthGoogleFilesDownload fileDownloadImpl = new OAuthGoogleFilesDownload();
		fileDownloadImpl.downloadFiles(driver, props);
	}

	/**
	 * Compute shipping cost.
	 *
	 * @throws EmergartSeleniumException
	 *             the emergart selenium exception
	 */
	@Test
	public void computeShippingCost() throws EmergartSeleniumException {

		// Get Existing Output File Data
		String outputFileName = props.getProperty("outputFileName");
		// Output Details List
		List<ShippingCostDetailsVO> outputCostDetailsVOList = new ArrayList<>();
		try {
			// Get input Shipping Address Data
			String inputFileName = props.getProperty("inputFileName");
			List<ShippingDataVO> shippingDataVOList = poiHelper.getExcelInputFileData(inputFileName);
			List<String> shippingCostVOKeyList = poiHelper.getExcelOutputFileKeyList(outputFileName);

			if (CollectionUtils.isNotEmpty(shippingDataVOList)) {
				LOG.info("Input Data count :" + shippingDataVOList.size());

				if (CollectionUtils.isNotEmpty(shippingCostVOKeyList)) {
					LOG.info("Existing output data : " + shippingCostVOKeyList.size());
				}

				driver.manage().window().maximize();
				driver.get(props.getProperty("testURL"));
				testHelper.waitForLoad(driver);

				// Login to test URL
				loginToTestURL();

				// Fetching all the menus
				Set<String> menuTextSet = fetchNavigationMenus(driver);
				for (String menuText :  menuTextSet) {
					LOG.debug("Starting with traversing Product Type : " + menuText);
					WebElement menuItemEle = driver
							.findElement(By.xpath("//*[@id='nav']//a[@class='level0 ' and text()='" + menuText + "']"));
					menuItemEle.click();
					testHelper.waitForLoad(driver);
					// Fetch all the products for Menu
					List<WebElement> productsElement = scrollToFetchAllProdForMenu();
					if (CollectionUtils.isNotEmpty(productsElement)) {
						// For each Product
						Map<String, String> uiOutputElements = null;
						for (WebElement prodElement : productsElement) {

							uiOutputElements = new HashMap<String, String>();
							uiOutputElements.put(PROD_TYPE_KEY, menuText);
							WebElement prodHRefEle = prodElement.findElement(By.xpath(".//a"));
							String productId = prodHRefEle.getAttribute("id");
							String productTitle = prodHRefEle.getAttribute("title").replaceAll("^\"|\"$", "");
							uiOutputElements.put(PROD_ID_STR_KEY, productId);
							uiOutputElements.put(PROD_TITLE_KEY, productTitle);
							WebElement hiddenInputEle = prodHRefEle.findElement(By.xpath(".//input"));
							uiOutputElements.put(PROD_URL_KEY, hiddenInputEle.getAttribute("value"));

							LOG.debug("::::: Testing for Product Type :" + menuText + " : Product Id : " + productId
									+ " : Product Title : " + productTitle);
							try {
								ShippingCostDetailsVO restApiRetunObj = RestAPIHelper
										.updateProductAndArtistDetails(uiOutputElements.get(PROD_ID_STR_KEY), props);
								for (ShippingDataVO shippingDataVO : shippingDataVOList) {
									try{
										if (TestUtility.isShpCostToBeCalculated(shippingCostVOKeyList,
												uiOutputElements.get(PROD_ID_STR_KEY), shippingDataVO)) {
											ShippingCostDetailsVO outputCostVO = createOutputVO(shippingDataVO,
													uiOutputElements, restApiRetunObj);
											outputCostVO = calculateShippingCostForProduct(shippingDataVO, outputCostVO);
											outputCostDetailsVOList.add(outputCostVO);
										} else {
											LOG.info(
													"Data already found for input row id - " + shippingDataVO.getInputId());
										}
									} catch (WebDriverException | EmergartSeleniumException e) {
										LOG.error("Exception while executing scenario for Product : " + productTitle + "("
												+ productId + ")", e);
										// executeAlternateFlow(originalHandle);
									}
								}
								
								testHelper.waitForSpecificTimePeriod(2000);
							} catch (WebDriverException | EmergartSeleniumException e) {
								LOG.error("Exception while executing scenario for Product : " + productTitle + "("
										+ productId + ")", e);
								// executeAlternateFlow(originalHandle);
							}
						}
					}
					LOG.debug("Ending with traversing Product Type : " + menuText);
				}
				// Log out user
				WebElement logoutEle = driver.findElement(By.xpath("//*[@id='header']//a[contains(text(),'Logout')]"));
				logoutEle.click();
				// Write data to the output file
				poiHelper.writeDataToOutputFile(outputFileName, outputCostDetailsVOList);
			} else {
				Log.info("No input data found for shipping cost calculation");
			}
		} catch (EmergartSeleniumException e) {
			LOG.error("EmergartSelenium Exception during execution : e", e);
			poiHelper.writeDataToOutputFile(outputFileName, outputCostDetailsVOList);
			throw e;
		} catch (Exception e) {
			LOG.error("Exception during execution : e", e);
			poiHelper.writeDataToOutputFile(outputFileName, outputCostDetailsVOList);
			throw e;
		}
	}

	/**
	 * Upload file to google.
	 *
	 * @throws EmergartSeleniumException
	 *             the emergart selenium exception
	 */
	@After
	public void uploadFileToGoogle() throws EmergartSeleniumException {
		OAuthGoogleFilesDownload fileDownloadImpl = new OAuthGoogleFilesDownload();
		fileDownloadImpl.uploadOutputFile(driver, props);
	}

	/**
	 * Test complete.
	 */
	@AfterClass
	public static void testComplete() {
		driver.quit();
	}

	/**
	 * Login to test URL.
	 * 
	 * @throws EmergartSeleniumException
	 */
	private void loginToTestURL() throws EmergartSeleniumException {
		LOG.debug("Logging in : start");
		String xPath = StringUtils.EMPTY;
		try {
			xPath = "//*[@id='header']//a[@class='ajaxlogin-login' and text()='Sign In']";
			elementName = "Sign In Link";
			testHelper.waitElementVisibilityByXPath(driver, xPath);
			WebElement signInElement = driver.findElement(By.xpath(xPath));
			signInElement.click();

			String userName = props.getProperty("loginUserName");
			String password = props.getProperty("loginPassword");

			elementName = "Email Element";
			testHelper.waitElementVisibilityById(driver, "email");
			WebElement loginNameElement = driver.findElement(By.id("email"));
			elementName = "Password Element";
			testHelper.waitElementVisibilityById(driver, "pass");
			WebElement loginPwdElement = driver.findElement(By.id("pass"));

			loginNameElement.clear();
			loginNameElement.sendKeys(userName);

			loginPwdElement.clear();
			loginPwdElement.sendKeys(password);

			elementName = "Submit Element";
			testHelper.waitElementVisibilityById(driver, "send2");
			driver.findElement(By.id("send2")).click();
		} catch (WebDriverException e) {
			LOG.error("WebDriverException while accessing Web Element : " + elementName, e);
			throw new EmergartSeleniumException(e);
		}

		testHelper.waitForSpecificTimePeriod(3000);
	}

	/**
	 * Scroll to fetch all prod for menu.
	 *
	 * @return the list
	 */
	private List<WebElement> scrollToFetchAllProdForMenu() {
		int prevChildCount = 0;
		List<WebElement> childElements = driver.findElements(By.xpath("//*[@id='container']/ul/*"));
		int currentChildCount = childElements.size();
		while (BooleanUtils.isFalse(prevChildCount == currentChildCount)) {
			prevChildCount = currentChildCount;
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("window.scrollBy(0,2000)");
			// Wait for the DOM to be updated.
			testHelper.waitForSpecificTimePeriod(5000);
			childElements = driver.findElements(By.xpath("//*[@id='container']/ul/*"));
			currentChildCount = childElements.size();
		}

		return childElements;
	}

	/**
	 * Calculate shipping cost for product.
	 *
	 * @param shippingDataVO
	 *            the shipping data VO
	 * @param outputCostVO
	 *            the output cost VO
	 * @return the shipping cost details VO
	 * @throws EmergartSeleniumException
	 */
	private ShippingCostDetailsVO calculateShippingCostForProduct(ShippingDataVO shippingDataVO,
			ShippingCostDetailsVO outputCostVO) throws EmergartSeleniumException {
		LOG.debug("Calculate Shipping Cost : start");
		String shippingCost = RestAPIHelper.findShippingCostForProduct(outputCostVO, props);
		if(StringUtils.isNotBlank(shippingCost)){
			outputCostVO.setQuotationPrice(shippingCost);
			if(!TestUtility.SHP_COST_NOT_FOUND.equals(shippingCost)){
				outputCostVO.setQuotationBy(TestUtility.SHP_QUOTATION_BY_DEFAULT);
			}
		}
		outputCostVO.setRecordedAt(TestUtility.getCurrentDateTimeAsStr());
		LOG.debug("Calculate Shipping Cost : end");
		return outputCostVO;
	}

	/**
	 * Creates the output VO.
	 *
	 * @param shippingDataVO
	 *            the shipping data VO
	 * @param uiOutputElements
	 *            the ui output elements
	 * @param restApiRetunObj
	 *            the rest api retun obj
	 * @return the shipping cost details VO
	 */
	private ShippingCostDetailsVO createOutputVO(ShippingDataVO shippingDataVO, Map<String, String> uiOutputElements,
			ShippingCostDetailsVO restApiRetunObj) {

		ShippingCostDetailsVO outputCostVO = new ShippingCostDetailsVO(
				Long.valueOf(uiOutputElements.get(PROD_ID_STR_KEY)), uiOutputElements.get(PROD_TITLE_KEY),
				uiOutputElements.get(PROD_TYPE_KEY), restApiRetunObj.getProductPrice(),
				uiOutputElements.get(PROD_URL_KEY), restApiRetunObj.getProductHeight(),
				restApiRetunObj.getProductWidth(), restApiRetunObj.getProductDepth(),
				restApiRetunObj.getProductWeight(), restApiRetunObj.getSourceCity(), restApiRetunObj.getSourceState(),
				restApiRetunObj.getSourceCountry(), restApiRetunObj.getSourceZipcode(), shippingDataVO.getCity(),
				shippingDataVO.getState(), shippingDataVO.getCountry(), shippingDataVO.getZipCode(),
				shippingDataVO.getInputId());

		return outputCostVO;
	}

	/**
	 * Fetch navigation menus.
	 *
	 * @param driver
	 *            the driver
	 * @return the sets the
	 * @throws EmergartSeleniumException
	 */
	private Set<String> fetchNavigationMenus(WebDriver driver) throws EmergartSeleniumException {
		LOG.debug("fetchNavigationMenus: start");
		Set<String> menuSet = new HashSet<>();
		try {
			List<String> prodTypeExecList = Arrays.asList(props.getProperty("productTypeExec"));
			if (CollectionUtils.isEmpty(prodTypeExecList)) {
				elementName = "Product Menu Link";
				String xPath = "//*[@id='nav']//a[@class='level0 ']";
				testHelper.waitElementVisibilityByXPath(driver, xPath);
				List<WebElement> menuItemsEle = driver.findElements(By.xpath(xPath));
				if (CollectionUtils.isNotEmpty(menuItemsEle)) {
					for (WebElement menuItem : menuItemsEle) {
						menuSet.add(menuItem.getText());
					}
				}
			} else {
				menuSet.addAll(prodTypeExecList);
			}
		} catch (WebDriverException e) {
			LOG.error("WebDriverException while accessing Web Element : " + elementName, e);
			throw new EmergartSeleniumException(e);
		}
		LOG.debug("fetchNavigationMenus: end");
		return menuSet;
	}

}
