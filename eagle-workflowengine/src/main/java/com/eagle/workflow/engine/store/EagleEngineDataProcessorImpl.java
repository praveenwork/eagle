package com.eagle.workflow.engine.store;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import com.eagle.boot.config.exception.EagleError;
import com.eagle.boot.config.exception.EagleException;
import com.eagle.contract.constants.EagleCellProcessor;
import com.eagle.contract.model.BaseInstrumentPredictionData;
import com.eagle.contract.model.InstrumentHistoricalData;
import com.eagle.contract.model.InstrumentPredictionData;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author ppasupuleti
 *
 */
@Component
public class EagleEngineDataProcessorImpl<T> implements EagleEngineDataProcessor<T> {

	@Override
	public List<T> readData(String path) {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean writeData(String path, T data) {
		ICsvMapWriter csvMapWriter = null;
		try {
			if (StringUtils.isEmpty(path) || data == null) {
				throw new EagleException(EagleError.EMPTY_OBJECT, path);
			}
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String,Object> beanProperties = objectMapper.convertValue(data, Map.class);
			// Header
			String[] header  =  new String[beanProperties.size()];
			int headerIndex = 0;
			for (Map.Entry<String, Object> entry : beanProperties.entrySet()) {
				header[headerIndex] = new String(entry.getKey());
				headerIndex++;
				
			}
			Path inputPath = Paths.get(path);
			if(Files.exists(inputPath)){
				csvMapWriter = new CsvMapWriter(new FileWriter(path,true),CsvPreference.STANDARD_PREFERENCE);
			} else {
				csvMapWriter = new CsvMapWriter(new FileWriter(path),CsvPreference.STANDARD_PREFERENCE);
				csvMapWriter.writeHeader(header);
			}
			csvMapWriter.write(beanProperties, header, CsvDataProcessor.getCellProcessor(data.getClass().getName()));  
			return true;
		} catch (IOException e) {
			throw new EagleException(EagleError.FAILD_TO_WRITE_DATA_INFILE,path, e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EagleException(EagleError.FAILD_TO_WRITE_DATA_INFILE,path, e.getMessage());
		} finally{
			if(csvMapWriter!=null){
				try {
					csvMapWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public T getLastRecord(Class<T> clazz, String path) {
		ICsvBeanReader csvBeanReader = null;
		try {
			if (StringUtils.isEmpty(path)) {
				throw new EagleException(EagleError.EMPTY_OBJECT, path);
			}
			Path inputPath = Paths.get(path);
			if(!Files.exists(inputPath)){
				throw new EagleException(EagleError.INVALID_PATH, path);
			}
			csvBeanReader = new CsvBeanReader(new FileReader(path),CsvPreference.STANDARD_PREFERENCE);
			final String[] header = csvBeanReader.getHeader(true);
			convertHeaderNameToBeanPropertyName(header);
			T data = null;
			T result = null;
			while((data  = (csvBeanReader.read(clazz, header, CsvDataProcessor.getCellProcessor(clazz.getName())))) != null){	
				result = data;
			} 
			return result;
		} catch (IOException e) {
			throw new EagleException(EagleError.FAILD_TO_READ_THE_LAST_RECORD,path, e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EagleException(EagleError.FAILD_TO_READ_THE_LAST_RECORD,path, e.getMessage());
		} finally{
			if(csvBeanReader!=null){
				try {
					csvBeanReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getLastRecord(Class<T> clazz, String path, boolean customSection) {
		if (!customSection) {
			return getLastRecord(clazz, path);
		}
		ICsvBeanReader csvBeanReader = null;
		try {
			if (StringUtils.isEmpty(path)) {
				throw new EagleException(EagleError.EMPTY_OBJECT, path);
			}
			Path inputPath = Paths.get(path);
			if(!Files.exists(inputPath)){
				throw new EagleException(EagleError.INVALID_PATH, path);
			}
			csvBeanReader = new CsvBeanReader(new FileReader(path),CsvPreference.STANDARD_PREFERENCE);
			final String[] fileHeaders = csvBeanReader.getHeader(true);
			String[] customHeader = new String[fileHeaders.length];
			CellProcessor[] beanCustomerProcessors = CsvDataProcessor.getCellProcessor(clazz.getName());
			CellProcessor[] customProcessors = new CellProcessor[fileHeaders.length];
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String,Object> beanProperties = objectMapper.convertValue(clazz.newInstance(), Map.class);
			
			for (int i = 0, j=0; i < fileHeaders.length; i++) {
				if (beanProperties.get(fileHeaders[i]) != null) {
					customHeader[i] = fileHeaders[i];
					customProcessors[i] = EagleCellProcessor.getCellProcessor(fileHeaders[i]);//beanCustomerProcessors[j];
					j++;
				} else {
					customHeader[i] = null;
					customProcessors[i] = null;
				}
			}
			convertHeaderNameToBeanPropertyName(customHeader);
			T data = null;
			T result = null;
			while((data  = (csvBeanReader.read(clazz, customHeader, customProcessors))) != null){	
				result = data;
				data = null;
			} 
			return result;
		} catch (IOException e) {
			throw new EagleException(EagleError.FAILD_TO_READ_THE_LAST_RECORD,path, e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EagleException(EagleError.FAILD_TO_READ_THE_LAST_RECORD,path, e.getMessage());
		} finally{
			if(csvBeanReader!=null){
				try {
					csvBeanReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//-------Helpers---------
	private void convertHeaderNameToBeanPropertyName(String[] header){ 
		for (int i=0;i<header.length;i++) {
			if(header[i]!=null){
				header[i] = header[i].replaceAll(" ", "");
				if(header[i].startsWith("1")){
					header[i] = header[i].replaceFirst("1", "One");
				} else if(header[i].startsWith("2")){
					header[i] = header[i].replaceFirst("2", "Two");
				} else if(header[i].startsWith("3")){
					header[i] = header[i].replaceFirst("3", "Three");
				} else if(header[i].startsWith("4")){
					header[i] = header[i].replaceFirst("4", "Four");
				} else if(header[i].startsWith("5")){
					header[i] = header[i].replaceFirst("5", "Five");
				}
				header[i] = WordUtils.capitalizeFully(header[i], new char[]{'_'}).replaceAll("_", "");
			}
		}
	}
	
	//-------Remove following---------
	public static void main1(String[] args) {
		String historicalDataPath = "/Users/ppasupuleti/Praveen/Projects/Anil/test/test.csv";
		String pridictionData = "/Users/ppasupuleti/Praveen/Projects/Anil/eagle_home/model/output/ES_predictions.csv";
		
		Map<String, String> headerMap = new HashMap();
		headerMap.put("nextdret_predicted", "nextdretPredicted");
		headerMap.put("nextdret_predicted_labels", "nextdretPredictedLabels");
		headerMap.put("nextdret_relative_probability", "nextdretRelativeProbability");
		
		
		/*
		// Instrument HistoricalData
		EagleEngineDataProcessor<InstrumentHistoricalData> processor = new EagleEngineDataProcessorImpl<>();
		//writing InstrumentHistoricalData
		processor.writeData(historicalDataPath, getInstrumentHistoricalData());
		//Reading Last Historical Data record
		InstrumentHistoricalData instrumentHistoricalData = processor.getLastRecord(InstrumentHistoricalData.class, historicalDataPath);
		System.out.println(instrumentHistoricalData.toString());
		*/
		//Instrument Prediction Data
		EagleEngineDataProcessor<InstrumentPredictionData> predictionProcessor = new EagleEngineDataProcessorImpl<>();
		//Writing Instrument Prediction Data
		//predictionProcessor.writeData(pridictionData, getInstrumentPredictionData());
		//Reading Instrument Prediction Data
		InstrumentPredictionData instrumentPredictionData = predictionProcessor.getLastRecord(InstrumentPredictionData.class, pridictionData,true);
		System.out.println(instrumentPredictionData.toString());
	}                                                                                                     
	
	private static InstrumentHistoricalData getInstrumentHistoricalData(){
		InstrumentHistoricalData instrumentHistoricalData = new InstrumentHistoricalData();
		instrumentHistoricalData.setAdjClose(0.1);
		instrumentHistoricalData.setClose(0.2);
		instrumentHistoricalData.setCount(20);
		instrumentHistoricalData.setDate("01/02/2019");
		instrumentHistoricalData.setHasGaps(true);
		instrumentHistoricalData.setHigh(0.3);
		instrumentHistoricalData.setId(1);
		instrumentHistoricalData.setLow(0.4);
		instrumentHistoricalData.setOpen(0.5);
		instrumentHistoricalData.setVolume(10000);
		instrumentHistoricalData.setWap(0.6);
		return instrumentHistoricalData;
	}
	private static BaseInstrumentPredictionData getInstrumentPredictionData(){
		BaseInstrumentPredictionData instrumentPredictionData = new BaseInstrumentPredictionData();
		instrumentPredictionData.setCci3(0.1);
		instrumentPredictionData.setCci5(0.5);
		instrumentPredictionData.setClose(0.1);
		instrumentPredictionData.setCloByHighMin95(0.2);
		instrumentPredictionData.setCloByLowMin95(0.3);
		instrumentPredictionData.setDate("10/10/2017");
		instrumentPredictionData.setFiveDr(0.4);
		instrumentPredictionData.setFiveDrCat("FiveDrCat");
		instrumentPredictionData.setId(1);
		instrumentPredictionData.setMacd531(0.5);
		instrumentPredictionData.setMacd532(0.6);
		instrumentPredictionData.setNextdret(0.7);
		instrumentPredictionData.setNextdretPredicted(0.8);
		instrumentPredictionData.setNextdretPredictedLabels("nextdretPredictedLabels");
		instrumentPredictionData.setNextdretRelativeProbability(0.9);
		instrumentPredictionData.setOneDr(1.0);
		instrumentPredictionData.setRsi3(1.1);
		instrumentPredictionData.setRsi3Category("rsi3Category");
		instrumentPredictionData.setRsi3(1.3);
		instrumentPredictionData.setRsi3Category("rsi3Category");
		instrumentPredictionData.setStock3kCat("stock3kCat");
		instrumentPredictionData.setStoc3k(1.4);
		instrumentPredictionData.setStock7kd(1.5);
		instrumentPredictionData.setThreeDr(1.6);
		instrumentPredictionData.setThreeDrCat("threeDrCat");
		instrumentPredictionData.setTwoDr(1.7);
		instrumentPredictionData.setTwoDrCat("twoDrCat");
		return instrumentPredictionData;
	}
}
