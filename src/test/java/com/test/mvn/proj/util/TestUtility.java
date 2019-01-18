package com.test.mvn.proj.util;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.log4j.Logger;

import com.test.mvn.proj.exception.EmergartSeleniumException;
import com.test.mvn.proj.vo.ShippingDataVO;

/**
 * The Class TestUtility.
 */
public class TestUtility {
	
	/** The Constant LOG. */
	private final static Logger LOG = Logger.getLogger(TestUtility.class);
	
	/** The Constant SHP_COST_NOT_FOUND. */
	public static final String SHP_COST_NOT_FOUND = "Not Available";

	/** The Constant SHP_QUOTATION_BY_DEFAULT. */
	public static final String SHP_QUOTATION_BY_DEFAULT = "Fedex";
	
	/**
	 * Find latest downloaded file.
	 *
	 * @param inputFileName the input file name
	 * @return the file
	 * @throws EmergartSeleniumException the emergart selenium exception
	 */
	public static File findLatestDownloadedFile(String inputFileName) throws EmergartSeleniumException {
		LOG.debug("findLatestDownloadedFile : start");
		File returnFile = null;
    	File folder = new File(getDownloadFolderPath().toString());
		if(folder.exists()){
			File[] files = folder.listFiles(new FilenameFilter() {
			@Override
				public boolean accept(File dir, String name) {
					return name.matches(inputFileName+".*\\.xlsx" );
			    }
			});
			
			if(null != files && files.length>0){
				Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
				returnFile = files[0];
			} else {
				LOG.error("No file found with filename - "+inputFileName);
				throw new EmergartSeleniumException("No file found with filename - "+inputFileName);
			}
		} else {
			LOG.error("Not a valid download folder path : "+getDownloadFolderPath());
			throw new EmergartSeleniumException("Not a valid download folder path : "+getDownloadFolderPath());
		}
		LOG.debug("findLatestDownloadedFile : end");
		return returnFile;
	}

	/**
	 * Gets the download folder path.
	 *
	 * @return the download folder path
	 */
	public static StringBuilder getDownloadFolderPath() {
		return new StringBuilder().append(System.getProperty("user.home")).append("/Downloads/");
	}

	/**
	 * Checks if is shp cost to be calculated.
	 *
	 * @param shippingCostVOKeyList the shipping cost VO key list
	 * @param idStr the id str
	 * @param shippingDataVO the shipping data VO
	 * @return the boolean
	 */
	public static Boolean isShpCostToBeCalculated(List<String> shippingCostVOKeyList, String idStr,
			ShippingDataVO shippingDataVO) {
		LOG.debug("isShpCostToBeCalculated : start");
		Boolean returnFlag = Boolean.FALSE;
		String searchKeyTxt = getSearchText(idStr, shippingDataVO);
		int index = Collections.binarySearch(shippingCostVOKeyList, searchKeyTxt);
		if(index<0){
			returnFlag = Boolean.TRUE;
		}
		LOG.debug("isShpCostToBeCalculated : end");
		return returnFlag;
	}

	/**
	 * Gets the search text.
	 *
	 * @param idStr the id str
	 * @param shippingDataVO the shipping data VO
	 * @return the search text
	 */
	private static String getSearchText(String idStr, ShippingDataVO shippingDataVO) {
		return new StringBuilder().append(idStr).append(shippingDataVO.getZipCode())
				.append(shippingDataVO.getCity()).append(shippingDataVO.getCountry()).toString();
	}

	/**
	 * Gets the current date time as str.
	 *
	 * @return the current date time as str
	 */
	public static String getCurrentDateTimeAsStr() {
		return new SimpleDateFormat("dd-MMMM-yyyy kk:mm").format(new Date());
	}
}
