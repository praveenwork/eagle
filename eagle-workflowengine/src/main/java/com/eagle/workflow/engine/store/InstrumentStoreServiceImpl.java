/**
 * 
 */
package com.eagle.workflow.engine.store;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.eagle.contract.model.InstrumentHistoricalData;
import com.eagle.workflow.engine.config.EagleWorkFlowEngineProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ppasupuleti
 *
 */
@Service
public class InstrumentStoreServiceImpl implements InstrumentStoreService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentStoreServiceImpl.class);
	
	@Autowired
	private EagleWorkFlowEngineProperties engineProperties;

	public InstrumentStoreServiceImpl(EagleWorkFlowEngineProperties engineProperties) {
		this.engineProperties = engineProperties;
	}
	
	/* (non-Javadoc)
	 * @see com.eagle.workflow.engine.store.InstrumentStoreService#store(com.eagle.contract.model.InstrumentHistoricalData)
	 */
	@Override
	public Boolean store(InstrumentHistoricalData historicalData) {
		
		if (historicalData == null || historicalData.getInstrument() == null) {
			LOGGER.error("Nothing to save. historicalData is null");
			return null;
		}
		LOGGER.debug("storing data in Excel for Instrument ["+historicalData.getInstrument().getSymbol()+"]");
		ClassPathResource classpathResource = new ClassPathResource(engineProperties.getInstrument().getStoreDataDirectory());
		
		String instrumentStorePath = classpathResource.getPath() 
				+ historicalData.getInstrument().getSymbol()+".xls";
		
		LOGGER.debug("Instrument Store Path:"+instrumentStorePath);
		
		// Store the data in excel file
		Boolean storeStatus = storeInExcel(instrumentStorePath,historicalData);
		
		return storeStatus;
	}
	
	//---------Helpers--------
	private static Boolean storeInExcel(String instrumentStorePath,InstrumentHistoricalData historicalData){
		Workbook workbook = null;
		Sheet sheet = null;
		Row row = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			if(StringUtils.isBlank(instrumentStorePath)){
				LOGGER.error("Invalid Store path:"+instrumentStorePath);
				return null;
			}
			Map<String,Object> props = objectMapper.convertValue(historicalData, Map.class);
			
			File instrumentStoreFile = new File(instrumentStorePath);
			int rowCount = 0;

			// Check File already exist or not 
			if (instrumentStoreFile.exists()) { 
				FileInputStream inputStream = new FileInputStream(instrumentStoreFile);
				workbook = new HSSFWorkbook(inputStream);
				sheet = workbook.getSheet("historicalData");
				rowCount = sheet.getPhysicalNumberOfRows();

			} else {	
				workbook = new HSSFWorkbook();
				sheet = workbook.createSheet("historicalData");
				rowCount = 0;
				// header Row
				row = sheet.createRow(rowCount++);
				int headerIndex = 0;
				for (Map.Entry<String, Object> entry : props.entrySet()) {
					Cell cell = row.createCell(headerIndex);
					cell.setCellValue(entry.getKey());
					headerIndex++;
				}
			}

			// append the historical data in next row
			row = sheet.createRow(rowCount++);
			int index = 0;
			for (Map.Entry<String, Object> entry : props.entrySet()) {
				Cell cell = row.createCell(index);
				if (entry.getValue() instanceof Integer) {
					cell.setCellValue((Integer) entry.getValue());
				} else if (entry.getValue() instanceof Double) {
					cell.setCellValue((Double) entry.getValue());
				} else if (entry.getValue() instanceof Date) {
					cell.setCellValue((Date) entry.getValue());
				} else {
					cell.setCellValue((String) entry.getValue());
				}
				index++;
			}
			FileOutputStream outStream = new FileOutputStream(instrumentStorePath);
			workbook.write(outStream);
			outStream.close();
			return new Boolean(true);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new Boolean(false);
	}
}