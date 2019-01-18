package com.test.mvn.proj.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.test.mvn.proj.exception.EmergartSeleniumException;

/**
 * The Class TestHelper.
 */
public class TestHelper {
	
	/** The Constant LOG. */
	private final static Logger LOG = Logger.getLogger(TestHelper.class);

	/**
	 * Initialize properties obj.
	 *
	 * @return the properties
	 * @throws EmergartSeleniumException 
	 */
	public Properties initializePropertiesObj() throws EmergartSeleniumException {
		LOG.debug("initializePropertiesObj: start");
		Properties props = new Properties();
		InputStream resourceStream = getClass().getClassLoader()
    			.getResourceAsStream("resourcesFile.properties");
    	try {
			props.load(resourceStream);
		} catch (IOException e) {
			LOG.error("initializePropertiesObj : Unable to read properties file",e);
			throw new EmergartSeleniumException(e);
		}
    	LOG.debug("initializePropertiesObj: end");
		return props;
	}
	
	/**
	 * Wait for specific time period.
	 *
	 * @param timeInMillis the time in millis
	 */
	public void waitForSpecificTimePeriod(long timeInMillis) {
		LOG.debug("waitForSpecificTimePeriod: start");
		try {
			Thread.sleep(timeInMillis);
		} catch (InterruptedException e) {
			LOG.error("waitForSpecificTimePeriod : Thread sleep : InterruptedException", e);
		}
		LOG.debug("waitForSpecificTimePeriod: end");
	}
	
	/**
	 * Wait element visibility by id.
	 *
	 * @param driver the driver
	 * @param elementId the element id
	 */
	public void waitElementVisibilityById(WebDriver driver, String elementId) {
		LOG.debug("waitElementVisibilityById: start");
		WebDriverWait wait = new WebDriverWait(driver, 15);
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
    	LOG.debug("waitElementVisibilityById: end");
		
	}
	
	/**
	 * Wait element visibility by X path.
	 *
	 * @param driver the driver
	 * @param xpath the xpath
	 */
	public void waitElementVisibilityByXPath(WebDriver driver, String xPath) {
		LOG.debug("waitElementVisibilityByXPath: start");
		WebDriverWait wait = new WebDriverWait(driver, 10);
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xPath)));
    	LOG.debug("waitElementVisibilityByXPath: end");
		
	}

	/**
	 * Switch tab.
	 *
	 * @param driver the driver
	 */
	public void switchTab(WebDriver driver) {
    	LOG.debug("switchTab: start");
		String parentHandle = driver.getWindowHandle();
    	List<String> windowHandles = new ArrayList<String>(driver.getWindowHandles());
    	for(String tab : windowHandles){
    		if(!StringUtils.equals(parentHandle, tab)){
    			driver.switchTo().window(tab);
    			break;
    		}
    	}
    	LOG.debug("switchTab: end");
	}

	/**
	 * Wait for load.
	 *
	 * @param driver the driver
	 */
	public void waitForLoad(WebDriver driver) {
		LOG.debug("waitForLoad: start");
        ExpectedCondition<Boolean> pageLoadCondition = new
                ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
                    }
                };
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(pageLoadCondition);
        LOG.debug("waitForLoad: end");
    }

	/**
	 * Find latest downloaded file.
	 *
	 * @param driver the driver
	 * @param originalHandle the original handle
	 * @return the file
	 */
	

	public void switchToOriginalHandle(WebDriver driver, String originalHandle) {
		LOG.debug("switchToOriginalHandle: start");
		for(String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalHandle)) {
                driver.switchTo().window(handle);
                driver.close();
            }
        }
        driver.switchTo().window(originalHandle);
        LOG.debug("switchToOriginalHandle: end");
	}

}
