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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

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
public class EmergartSeleniumTest {

	/** The Constant LOG. */
	private final static Logger LOG = Logger.getLogger(EmergartSeleniumTest.class);

	/** The Constant PROD_URL_KEY. */
	private static final String PROD_URL_KEY = "prodURL";

	/** The Constant PROD_ID_STR_KEY. */
	private static final String PROD_ID_STR_KEY = "prodIdStr";

	/** The Constant PROD_TITLE_KEY. */
	private static final String PROD_TITLE_KEY = "prodTitle";

	/** The Constant PROD_COST_KEY. */
	private static final String PROD_COST_KEY = "prodCost";

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

				// Retain original window handle
				String originalHandle = driver.getWindowHandle();

				// Fetching all the menus
				Set<String> menuTextSet = fetchNavigationMenus(driver);
				for (String menuText : menuTextSet) {
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

							LOG.debug("::::: Testing for Product Type :" + menuText + " : Product Id : " + productId
									+ " : Product Title : " + productTitle);
							try {
								uiOutputElements = addProductToCartAndCheckout(prodElement, uiOutputElements);
								ShippingCostDetailsVO restApiRetunObj = RestAPIHelper
										.updateProductAndArtistDetails(uiOutputElements.get(PROD_ID_STR_KEY), props);
								for (ShippingDataVO shippingDataVO : shippingDataVOList) {
									if (TestUtility.isShpCostToBeCalculated(shippingCostVOKeyList,
											uiOutputElements.get(PROD_ID_STR_KEY), shippingDataVO)) {
										ShippingCostDetailsVO outputCostVO = createOutputVO(shippingDataVO,
												uiOutputElements, restApiRetunObj);
										outputCostVO = calculateShippingCostForProduct(shippingDataVO, outputCostVO);
										outputCostDetailsVOList.add(outputCostVO);
										if (shippingDataVO != shippingDataVOList.get(shippingDataVOList.size() - 1)) {
											WebElement editEle = driver.findElement(
													By.xpath("//*[@id='opc-billing']//a[contains(text(),'Edit')]"));
											editEle.click();
											testHelper.waitForSpecificTimePeriod(2000);
										}
									} else {
										LOG.info(
												"Data already found for input row id - " + shippingDataVO.getInputId());
									}
								}
								removeProductFromCart();
								testHelper.switchToOriginalHandle(driver, originalHandle);
								WebElement prodModalElement = driver
										.findElement(By.id("image-gallery"));
								if(prodModalElement.isDisplayed()){
									prodModalElement.sendKeys(Keys.ESCAPE);
								}
								/*JavascriptExecutor js = (JavascriptExecutor) driver;
								js.executeScript("arguments[0].click();", fadeElement);*/
								testHelper.waitForSpecificTimePeriod(5000);
							} catch (WebDriverException | EmergartSeleniumException e) {
								LOG.error("Exception while executing scenario for Product : " + productTitle + "("
										+ productId + ")", e);
								executeAlternateFlow(originalHandle);
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
	 * Adds the product to cart and checkout.
	 *
	 * @param prodElement
	 *            the prod element
	 * @param uiOutputElements
	 *            the ui output elements
	 * @return the map
	 * @throws EmergartSeleniumException
	 */
	private Map<String, String> addProductToCartAndCheckout(WebElement prodElement,
			Map<String, String> uiOutputElements) throws EmergartSeleniumException {
		LOG.debug("Add Product To Cart and Checkout : start");
		try {
			WebElement link = prodElement.findElement(By.xpath(".//a"));
			link.click();
			testHelper.waitForSpecificTimePeriod(2000);
			elementName = "Product Detail Page Element";
			String byElement = "viewDetailPage";
			testHelper.waitElementVisibilityById(driver, byElement);
			WebElement detailsPageEle = driver.findElement(By.id(byElement));
			uiOutputElements.put(PROD_URL_KEY, detailsPageEle.getAttribute("href"));

			elementName = "Product Price Element";
			byElement = "modal-product-price";
			testHelper.waitElementVisibilityById(driver, byElement);
			WebElement productCost = driver.findElement(By.id(byElement));
			uiOutputElements.put(PROD_COST_KEY, new StringBuilder(productCost.getText()).deleteCharAt(0).toString());
			detailsPageEle.click();

			testHelper.waitForSpecificTimePeriod(5000);
			testHelper.switchTab(driver);

			elementName = "Add To Cart Link";
			byElement = "//button[@class='button btn-cart']";
			testHelper.waitElementVisibilityByXPath(driver, byElement);
			WebElement addToCartEle = driver.findElement(By.xpath(byElement));
			addToCartEle.click();

			testHelper.waitForSpecificTimePeriod(5000);
			elementName = "Cart Link";
			byElement = "//*[@id='header']//li/a[text()='Cart']";
			testHelper.waitElementVisibilityByXPath(driver, byElement);
			WebElement cartEle = driver.findElement(By.xpath(byElement));
			cartEle.click();

			testHelper.waitForSpecificTimePeriod(5000);

			elementName = "Proceed And Checkout Button";
			byElement = "//ul[@class='checkout-types bottom']//button[@class='button btn-proceed-checkout btn-checkout']";
			testHelper.waitElementVisibilityByXPath(driver, byElement);
			WebElement proceedButtonEle = driver.findElement(By.xpath(byElement));
			proceedButtonEle.click();

			testHelper.waitForSpecificTimePeriod(5000);
		} catch (WebDriverException e) {
			LOG.error("WebDriverException while accessing Web Element : " + elementName, e);
			throw new EmergartSeleniumException(e);
		}
		LOG.debug("Add Product To Cart and Checkout : end");
		return uiOutputElements;
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
		try {
			elementName = "Billing : Street";
			String byElement = "billing:street1";
			testHelper.waitElementVisibilityById(driver, byElement);
			WebElement addressEle = driver.findElement(By.id(byElement));
			addressEle.clear();
			addressEle.sendKeys("Street 1");

			elementName = "Billing : Country";
			byElement = "billing:country_id";
			testHelper.waitElementVisibilityById(driver, byElement);
			Select countryEle = new Select(driver.findElement(By.id(byElement)));
			countryEle.selectByVisibleText(shippingDataVO.getCountry());
			testHelper.waitForSpecificTimePeriod(1000);

			elementName = "Billing : State";
			byElement = "billing:region_id";
			testHelper.waitElementVisibilityById(driver, byElement);
			WebElement stateEle = driver.findElement(By.id(byElement));
			if (!stateEle.isDisplayed()) {
				WebElement regionEle = driver.findElement(By.id("billing:region"));
				regionEle.clear();
				regionEle.sendKeys(shippingDataVO.getState());
			} else {
				Select regionEle = new Select(stateEle);
				regionEle.selectByVisibleText(shippingDataVO.getState());
			}

			elementName = "Billing : City";
			byElement = "billing:city";
			testHelper.waitElementVisibilityById(driver, byElement);
			WebElement cityEle = driver.findElement(By.id(byElement));
			cityEle.clear();
			cityEle.sendKeys(shippingDataVO.getCity());

			elementName = "Billing : Postcode";
			byElement = "billing:postcode";
			testHelper.waitElementVisibilityById(driver, byElement);
			WebElement postCodeEle = driver.findElement(By.id(byElement));
			postCodeEle.clear();
			postCodeEle.sendKeys(shippingDataVO.getZipCode());

			elementName = "Billing : Phone";
			byElement = "billing:telephone";
			testHelper.waitElementVisibilityById(driver, byElement);
			WebElement phoneEle = driver.findElement(By.id(byElement));
			phoneEle.clear();
			phoneEle.sendKeys("123456789");

			elementName = "Billing : Continue button";
			byElement = "//*[@id='billing-buttons-container']/button[contains(@title, 'Continue')]";
			testHelper.waitElementVisibilityByXPath(driver, byElement);
			WebElement continueEle = driver.findElement(By.xpath(byElement));
			continueEle.click();

			testHelper.waitElementVisibilityById(driver, "checkout-step-shipping_method");
			testHelper.waitForSpecificTimePeriod(2000);

			elementName = "Billing : Continue button";
			byElement = "//div[@id='checkout-shipping-method-load']";
			testHelper.waitElementVisibilityByXPath(driver, byElement);
			WebElement shippingEle = driver.findElement(By.xpath(byElement));
			List<WebElement> childElements = shippingEle.findElements(By.xpath("*"));
			Boolean priceFound = Boolean.FALSE;
			for (WebElement child : childElements) {
				if ("dl".equalsIgnoreCase(child.getTagName())) {
					priceFound = Boolean.TRUE;
					break;
				}
			}
			if (BooleanUtils.isTrue(priceFound)) {
				WebElement priceEle = driver.findElement(By.xpath("//span[@class='price']"));
				outputCostVO.setQuotationPrice(priceEle.getText());
				WebElement shpMethodEle = driver.findElement(By.xpath("//input[@name='shipping_method']"));
				String shpMethod = shpMethodEle.getAttribute("value");
				String[] parts = shpMethod.split("_", 2);
				outputCostVO.setQuotationBy(parts[0]);
			} else {
				outputCostVO.setQuotationPrice(TestUtility.SHP_COST_NOT_FOUND);
			}

			outputCostVO.setRecordedAt(TestUtility.getCurrentDateTimeAsStr());
			testHelper.waitForSpecificTimePeriod(3000);
		} catch (WebDriverException e) {
			LOG.error("WebDriverException while accessing Web Element : " + elementName, e);
			throw new EmergartSeleniumException(e);
		}
		LOG.debug("Calculate Shipping Cost : end");
		return outputCostVO;
	}

	/**
	 * Removes the product from cart.
	 * 
	 * @throws EmergartSeleniumException
	 */
	private void removeProductFromCart() throws EmergartSeleniumException {
		LOG.debug("Remove Product From Cart : start");

		try {
			testHelper.waitForSpecificTimePeriod(2000);
			String xPath = "//*[@id='header']//li/a[text()='Cart']";
			elementName = "Cart Link";
			testHelper.waitElementVisibilityByXPath(driver, xPath);
			WebElement cartEle = driver.findElement(By.xpath(xPath));
			cartEle.click();
			testHelper.waitForSpecificTimePeriod(3000);
			if(isCartNotEmpty()){
				xPath = "//a[@class='remove' and contains(@title,'Remove item')]";
				elementName = "Remove Element Link";
				testHelper.waitElementVisibilityByXPath(driver, xPath);
				WebElement removeItemEle = driver.findElement(By.xpath(xPath));
				removeItemEle.click();
			}
			testHelper.waitForSpecificTimePeriod(2000);
		} catch (WebDriverException e) {
			LOG.error("WebDriverException while accessing Web Element : " + elementName, e);
			throw new EmergartSeleniumException(e);
		}
		LOG.debug("Remove Product From Cart : end");
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
				uiOutputElements.get(PROD_TYPE_KEY), uiOutputElements.get(PROD_COST_KEY),
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
			if(CollectionUtils.isEmpty(prodTypeExecList)){
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
	
	/**
	 * Execute alternate flow.
	 *
	 * @param originalHandle the original handle
	 * @throws EmergartSeleniumException the emergart selenium exception
	 */
	private void executeAlternateFlow(String originalHandle) throws EmergartSeleniumException {
		testHelper.waitForLoad(driver);
		removeProductFromCart();
		testHelper.switchToOriginalHandle(driver, originalHandle);
		WebElement prodModalElement = driver
				.findElement(By.id("image-gallery"));
		if(prodModalElement.isDisplayed()){
			prodModalElement.sendKeys(Keys.ESCAPE);
		}
/*		// //*[@id = 'image-gallery']
		WebElement fadeElement = driver
				.findElement(By.xpath("//div[@class='modal-backdrop fade in']"));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", fadeElement);*/
		testHelper.waitForSpecificTimePeriod(3000);
	}
	
	/**
	 * Checks if is cart not empty.
	 *
	 * @return the boolean
	 */
	private Boolean isCartNotEmpty() {
		Boolean cartNotEmptyInd = Boolean.TRUE;
		String xPath = "//*//*[@id='header']//li/a[text()='Cart']/span";
		testHelper.waitElementVisibilityByXPath(driver, xPath);
		String cartProdCount = driver.findElement(By.xpath(xPath)).getText();
		if(cartProdCount.equals("0")){
			cartNotEmptyInd = Boolean.FALSE;
		}
		return cartNotEmptyInd;
	}
}
