package com.test.mvn.proj.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.test.mvn.proj.exception.EmergartSeleniumException;
import com.test.mvn.proj.util.TestUtility;
import com.test.mvn.proj.vo.ShippingCostDetailsVO;
import com.test.mvn.proj.vo.ShippingDataVO;

/**
 * The Class ApachePOIHelper.
 */
public class ApachePOIHelper {
	
	private final static Logger LOG = Logger.getLogger(ApachePOIHelper.class);
	
	/**
	 * Gets the excel file data.
	 *
	 * @param inputFileName the input file name
	 * @return the excel file data
	 * @throws EmergartSeleniumException 
	 */
	public List<ShippingDataVO> getExcelInputFileData(String inputFileName) throws EmergartSeleniumException{
		LOG.debug("getExcelInputFileData : start");
		List<ShippingDataVO> shippingVOList = new ArrayList<ShippingDataVO>();
		File downloadedFile = TestUtility.findLatestDownloadedFile(inputFileName);
		
		try {
			Workbook workbook = new XSSFWorkbook(downloadedFile);
			Sheet sheet = workbook.getSheetAt(0);
			for(Row row : sheet){
				if(row.getRowNum()> 0 && !isRowEmpty(row)){
					Integer inputId = null;
					String country = StringUtils.EMPTY;
					String state = StringUtils.EMPTY;
					String city = StringUtils.EMPTY;
					String zipCode = StringUtils.EMPTY;
					for(Cell cell : row){
						if(null == inputId){
							inputId = Double.valueOf(cell.getNumericCellValue()).intValue();
						} else if(StringUtils.isEmpty(country)){
							country = cell.getStringCellValue().trim();
						} else if(StringUtils.isEmpty(state)){
							state = cell.getStringCellValue().trim();
						} else if(StringUtils.isEmpty(city)){
							city = cell.getStringCellValue().trim();
						} else if(StringUtils.isEmpty(zipCode)){
							if(CellType.NUMERIC.equals(cell.getCellTypeEnum())){
								zipCode = String.valueOf(Double.valueOf(cell.getNumericCellValue()).intValue());
							} else {
								zipCode = cell.getStringCellValue().trim();
							}
						}
					}
					shippingVOList.add(new ShippingDataVO(inputId, country, state, city, zipCode));
				}
			}
			workbook.close();
		} catch (IOException e) {
			LOG.error("IOException while accessing file :"+inputFileName, e);
			throw new EmergartSeleniumException(e);
		} catch (InvalidFormatException e) {
			LOG.error("InvalidFormatException while accessing file :"+inputFileName, e);
			throw new EmergartSeleniumException(e);
		}
		
		LOG.debug("getExcelInputFileData : end");
		return shippingVOList;
	}
	
	/**
	 * Gets the excel output file key list.
	 *
	 * @param outputFileName the output file name
	 * @return the excel output file key list
	 * @throws EmergartSeleniumException 
	 */
	public List<String> getExcelOutputFileKeyList(String outputFileName) throws EmergartSeleniumException {
		LOG.debug("getExcelOutputFileKeyList : start");
		List<String> outputDataKeyList = new ArrayList<String>();
		File downloadedFile = TestUtility.findLatestDownloadedFile(outputFileName);
		try {
			Workbook workbook = new XSSFWorkbook(downloadedFile);
			Sheet sheet = workbook.getSheetAt(0);
			for(Row row : sheet){
				if(row.getRowNum()> 0 && !isRowEmpty(row)){
					Long productId = Double.valueOf(row.getCell(0)
							.getNumericCellValue()).longValue();
					String destinationCity = row.getCell(13).getStringCellValue();
					String destinationCountry = row.getCell(15).getStringCellValue();
					String destinationZipcode = StringUtils.EMPTY;
					if(CellType.NUMERIC.equals(row.getCell(16).getCellTypeEnum())){
						destinationZipcode = String.valueOf(Double.
								valueOf(row.getCell(16).getNumericCellValue()).intValue());
					} else {
						destinationZipcode = row.getCell(16).getStringCellValue().trim();
					}
					ShippingCostDetailsVO shpCostVO = new ShippingCostDetailsVO(productId, destinationCity, 
							destinationCountry, destinationZipcode);
					
					outputDataKeyList.add(shpCostVO.toString());
				}
			}
			Collections.sort(outputDataKeyList);
			workbook.close();
		} catch (IOException e) {
			LOG.error("IOException while accessing file :"+outputFileName, e);
			throw new EmergartSeleniumException(e);
		} catch (InvalidFormatException e) {
			LOG.error("InvalidFormatException while accessing file :"+outputFileName, e);
			throw new EmergartSeleniumException(e);
		}
		LOG.debug("getExcelOutputFileKeyList : end");
		return outputDataKeyList;
	}

	/**
	 * Write data to output file.
	 *
	 * @param outputFileName the output file name
	 * @param outputCostDetailsVOList the output cost details VO list
	 * @throws EmergartSeleniumException 
	 */
	public void writeDataToOutputFile(String outputFileName, 
			List<ShippingCostDetailsVO> outputCostDetailsVOList) throws EmergartSeleniumException {
		LOG.debug("writeDataToOutputFile : start");
		if(CollectionUtils.isNotEmpty(outputCostDetailsVOList)){
			File downloadedFile = TestUtility.findLatestDownloadedFile(outputFileName);
			try {
				FileInputStream inputStream = new FileInputStream(downloadedFile);
				OPCPackage opc = OPCPackage.open(inputStream);
				Workbook workbook = WorkbookFactory.create(opc);
				Sheet sheet = workbook.getSheetAt(0);
				int rownum = getEmptyRowNum(sheet);
				for(ShippingCostDetailsVO costDetailsVO : outputCostDetailsVOList){
					Row row = sheet.createRow(++rownum);
					row.createCell(0).setCellValue(costDetailsVO.getProductId());
					row.createCell(1).setCellValue(costDetailsVO.getProductName());
					row.createCell(2).setCellValue(costDetailsVO.getProductType());
					row.createCell(3).setCellValue(costDetailsVO.getProductPrice());
					row.createCell(4).setCellValue(costDetailsVO.getProductURL());
					row.createCell(5).setCellValue(costDetailsVO.getProductHeight());
					row.createCell(6).setCellValue(costDetailsVO.getProductWidth());
					row.createCell(7).setCellValue(costDetailsVO.getProductDepth());
					row.createCell(8).setCellValue(costDetailsVO.getProductWeight());
					row.createCell(9).setCellValue(costDetailsVO.getSourceCity());
					row.createCell(10).setCellValue(costDetailsVO.getSourceState());
					row.createCell(11).setCellValue(costDetailsVO.getSourceCountry());
					row.createCell(12).setCellValue(costDetailsVO.getSourceZipcode());
					row.createCell(13).setCellValue(costDetailsVO.getDestinationCity());
					row.createCell(14).setCellValue(costDetailsVO.getDestinationState());
					row.createCell(15).setCellValue(costDetailsVO.getDestinationCountry());
					row.createCell(16).setCellValue(costDetailsVO.getDestinationZipcode());
					if(null == costDetailsVO.getQuotationBy()) {
						row.createCell(17).setCellType(CellType.BLANK);
					} else {
						row.createCell(17).setCellValue(costDetailsVO.getQuotationBy());
					}
					row.createCell(18).setCellValue(costDetailsVO.getQuotationPrice());
					row.createCell(19).setCellValue(costDetailsVO.getRecordedAt());
					row.createCell(20).setCellValue(costDetailsVO.getInputRowId());
				}
				FileOutputStream outStream = new FileOutputStream(TestUtility.findLatestDownloadedFile(outputFileName));
				workbook.write(outStream);
				workbook.close();
				outStream.close();
			} catch (IOException e) {
				LOG.error("IOException while accessing file :"+outputFileName, e);
				throw new EmergartSeleniumException(e);
			} catch (InvalidFormatException e) {
				LOG.error("InvalidFormatException while accessing file :"+outputFileName, e);
				throw new EmergartSeleniumException(e);
			}
			LOG.debug("writeDataToOutputFile : end");
		} else {
			LOG.info("No output data to write in file");
		}
		
	}
	
	/**
	 * Gets the empty row num.
	 *
	 * @param sheet the sheet
	 * @return the empty row num
	 */
	private int getEmptyRowNum(Sheet sheet) {
		int rownum = 0;
		for(Row row : sheet){
			if(isRowEmpty(row)){
				rownum = row.getRowNum() - 1;
				break;
			}
		}
		return rownum;
	}

	/**
	 * Checks if is row empty.
	 *
	 * @param row the row
	 * @return the boolean
	 */
	private Boolean isRowEmpty(Row row) {
		Boolean rowEmptyInd = Boolean.TRUE;
		Cell cell = row.getCell(0);
		if(null != cell && CellType.BLANK != cell.getCellTypeEnum()){
			rowEmptyInd = Boolean.FALSE;
		}
		return rowEmptyInd;
	}
}
