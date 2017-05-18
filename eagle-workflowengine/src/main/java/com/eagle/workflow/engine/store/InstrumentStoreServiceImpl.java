package com.eagle.workflow.engine.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import com.eagle.contract.model.InstrumentHistoricalData;
import com.eagle.workflow.engine.config.EagleWorkFlowEngineProperties;
import com.eagle.workflow.engine.utils.EagleEngineFileUtils;
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
	
	@Autowired
	private EagleEngineFileUtils eagleEngineFileUtils;
	
	@Autowired
	private EagleEngineDataProcessor<InstrumentHistoricalData> dataProcessor;

	public InstrumentStoreServiceImpl(EagleWorkFlowEngineProperties engineProperties) {
		this.engineProperties = engineProperties;
	}
	
	/* (non-Javadoc)
	 * @see com.eagle.workflow.engine.store.InstrumentStoreService#store(com.eagle.contract.model.InstrumentHistoricalData)
	 */
	@Override
	public Boolean storeRawData(InstrumentHistoricalData historicalData) {
		
		if (historicalData == null || historicalData.getInstrument() == null) {
			LOGGER.error("Nothing to save. historicalData is null");
			return null;
		}
		String rawDataDirectory = eagleEngineFileUtils.getRawDataPath();
		String rawDataFileType = engineProperties.getRawDataFileType();
		
		LOGGER.debug("storing data in "+rawDataFileType+" for Instrument ["+historicalData.getInstrument().getSymbol()+"]");
		
		ClassPathResource classpathResource = new ClassPathResource(rawDataDirectory);

		String instrumentStorePath = classpathResource.getPath() + historicalData.getInstrument().getSymbol() + "."
				+ rawDataFileType;
		
		LOGGER.debug("Instrument Store Path:"+instrumentStorePath);
		
		Boolean storeStatus = false;
		
		if ("csv".equalsIgnoreCase(rawDataFileType)) {
			// Store the data in csv file
			//storeStatus = storeInCSV(instrumentStorePath,historicalData);
			storeStatus = saveCsv(instrumentStorePath,historicalData);
		} else {
			// Store the data in excel file
			storeStatus = storeInExcel(instrumentStorePath,historicalData);
		}
		return storeStatus;
	}
	
	//---------Helpers--------
	private Boolean saveCsv(String path, InstrumentHistoricalData historicalData){
		Path inputPath = Paths.get(path);
		if(Files.exists(inputPath)){
			InstrumentHistoricalData lastHistoricalRecord = dataProcessor.getLastRecord(InstrumentHistoricalData.class, path);
			if (lastHistoricalRecord != null) {
				int lastRecordId = lastHistoricalRecord.getId();
				historicalData.setId(lastRecordId+1);
			} else {
				historicalData.setId(1);
			}
		} else {
			historicalData.setId(1);
		}
		return dataProcessor.writeData(path, historicalData);
	}
	
	private  Boolean storeInCSV(String instrumentStorePath,InstrumentHistoricalData historicalData){
		ICsvMapWriter mapWriter  = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			Map<String,Object> props = objectMapper.convertValue(historicalData, Map.class);
			File instrumentStoreFile = new File(instrumentStorePath);
			
			//Header Row
			final CellProcessor[] processors = getProcessors();
			String[] header  =  new String[props.size()];
			int headerIndex = 0;
			for (Map.Entry<String, Object> entry : props.entrySet()) {
				header[headerIndex] = new String(entry.getKey());
				headerIndex++;
			}
			if (instrumentStoreFile.exists()) { 
				
				mapWriter = new CsvMapWriter(new FileWriter(instrumentStorePath,true), CsvPreference.STANDARD_PREFERENCE);
			} else {
				mapWriter = new CsvMapWriter(new FileWriter(instrumentStorePath), CsvPreference.STANDARD_PREFERENCE);
				// write the header
				mapWriter.writeHeader(header);
			}
			mapWriter.write(props, header, processors);   
			return new Boolean(true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (mapWriter != null) {
					mapWriter.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new Boolean(false);
	}
	
	private  CellProcessor[] getProcessors() {
        final CellProcessor[] processors = new CellProcessor[] { 
        		new NotNull(), // ID
        		new NotNull(), // Date
                new NotNull(), // open
                new NotNull(), // high
                new NotNull(), // close
                new NotNull(), // low
                new NotNull(), // volume
                new NotNull(), // AdjClose
        };
        return processors;
	}
	
	private  Boolean storeInExcel(String instrumentStorePath,InstrumentHistoricalData historicalData){
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
			int id = 1;
			for (Map.Entry<String, Object> entry : props.entrySet()) {
				Cell cell = row.createCell(index);
				if(index == 0){
					cell.setCellValue(id);
				}
				if (entry.getValue() instanceof Integer) {
					cell.setCellValue((Integer) entry.getValue());
				} else if (entry.getValue() instanceof Double) {
					cell.setCellValue((Double) entry.getValue());
				} else if (entry.getValue() instanceof Date) {
					cell.setCellValue((Date) entry.getValue());
				} else if (entry.getValue() instanceof Long) {
					cell.setCellValue((Long) entry.getValue());
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