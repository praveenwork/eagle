package com.eagle.workflow.engine.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eagle.boot.config.exception.EagleError;
import com.eagle.boot.config.exception.EagleException;
import com.eagle.contract.model.Instrument;
import com.eagle.workflow.engine.config.EagleEnrichDataProperties;
import com.eagle.workflow.engine.config.EagleWorkFlowEngineProperties;
import com.eagle.workflow.engine.repository.InstrumentRepository;
import com.eagle.workflow.engine.utils.EagleEngineFileUtils;
import com.eagle.workflow.engine.utils.EagleProcessExecutor;
import com.eagle.workflow.engine.utils.EagleProcessExecutorResult;

/**
 * @author ppasupuleti
 *
 */
@Component
public class EnrichingDataTaskLet implements Tasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(EnrichingDataTaskLet.class);

	private EagleEnrichDataProperties enrichDataProperties;
	
	@Autowired
	private InstrumentRepository instrumentRepository;
	
	@Autowired
	private EagleProcessExecutor eagleProcessExecutor;
	
	@Autowired
	private EagleEngineFileUtils eagleEngineFileUtils;
	
	private static final String DATA_FILE_EXTENSION = ".csv";
	
	private static final String EMPTY_SPACE = " ";
	
	private static final String MODEL_DATA_SUFFIX = "_enrichModel.csv";
	
	private static final String ENRICH_DATA_SUFFIX = "_enrichData.csv";
	
	private static final String PYTHON_PATH = "/Users/ppasupuleti/anaconda/bin/python";
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	public EnrichingDataTaskLet(EagleEnrichDataProperties enrichDataProperties, EagleWorkFlowEngineProperties engineProperties) {
		this.enrichDataProperties = enrichDataProperties;
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute
	 * (org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 * 
	 * Python Command: 
	 * 			python run_all.py raw_data_ess.csv Final_Feature_ess.csv Final_f_0410.csv 12/30/2016 ess
	 * 			raw_data_ess.csv 		: Current date rawData
	 * 			Final_Feature_ess.csv 	: Fixed Model File (?)
	 * 			Final_f_0410.csv		: 
	 * 
	 * 
	 * ES_enrichdata.csv
	 * AMZN_enrichdata.csv
	 * 
	 *
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		LOGGER.debug("*** Enriching Data Step is in progress... ***");
		try {
			
			String rawDataDirectory = eagleEngineFileUtils.getRawDataPath();
			String enrichDataDirectory = eagleEngineFileUtils.getEnrichDataPath();
			String enrichDataModelDirectory = eagleEngineFileUtils.getEnrichModelPath();
			
			
			// Tools
			String enrichDataToolsDirectory = eagleEngineFileUtils.getToolDataPath();
			String enrichAppName = enrichDataProperties.getEnrichApp();
			String configPath = enrichDataProperties.getConfigPath();
			
			List<Instrument> instrumentsList = instrumentRepository.getInstruments();
			
			StringBuilder baseCommand = new StringBuilder(PYTHON_PATH);
			baseCommand.append(EMPTY_SPACE);
			baseCommand.append(enrichDataToolsDirectory+enrichAppName).append(EMPTY_SPACE);
			
			StringBuilder command = null;
			String rawFilePath =  null;
			String enrichModelFilePath = null;
			String enrichDataFilePath = null;
			String dateString = dateFormat.format(new Date());
			
			for (Instrument instrument : instrumentsList) {
				if("es".equalsIgnoreCase(instrument.getSymbol())){ //FIXME: delete this condition
					command = new StringBuilder();
					command.append(baseCommand).append(EMPTY_SPACE);
					
					rawFilePath = rawDataDirectory + instrument.getSymbol() + DATA_FILE_EXTENSION;
					enrichModelFilePath = enrichDataModelDirectory + instrument.getSymbol()+ MODEL_DATA_SUFFIX;
					enrichDataFilePath = enrichDataDirectory + instrument.getSymbol()+ ENRICH_DATA_SUFFIX;
					
					command.append(rawFilePath).append(EMPTY_SPACE);
					command.append(enrichModelFilePath).append(EMPTY_SPACE);
					command.append(enrichDataFilePath).append(EMPTY_SPACE);
					command.append(dateString).append(EMPTY_SPACE);
					
					//command.append(instrument.getSymbol()).append(EMPTY_SPACE);
					command.append("ess").append(EMPTY_SPACE); //FIXME: ??
					command.append(enrichDataToolsDirectory+configPath);
					LOGGER.info("Enrich Data Command:"+ command);
					EagleProcessExecutorResult executeResult = eagleProcessExecutor.execute(command.toString());
					if (executeResult.isExecStatus()) {
						LOGGER.info("Enrich Data process command executed succesfully.");
					} else {
						LOGGER.error("Enrich Data process command execution failed. Reason:"+executeResult.getErrorMessage());
					}
					command = null;
					rawFilePath =  null;
					enrichModelFilePath = null;
					enrichDataFilePath = null;
				}
			}
		} catch (Exception e) {
			throw new EagleException(EagleError.FAILED_TO_EXECUTE_ENRICHDATA_STEP, e, e.getMessage());
		}
		LOGGER.debug("*** Enriching Data Step completed ***");
		return RepeatStatus.FINISHED;
	}
}
