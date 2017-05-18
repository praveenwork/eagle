package com.eagle.workflow.engine.job;

import java.io.File;
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
import com.eagle.workflow.engine.config.EagleModelProperties;
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
public class ApplyModelTaskLet implements Tasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplyModelTaskLet.class);

	private static final String EMPTY_SPACE = " ";
	
	private static final String FINAL_ENRICH_DATA = "_final_";
	
	private static final String PREDICTIONS = "predictions_";
	
	private EagleModelProperties eagleModelProperties;
	
	private EagleWorkFlowEngineProperties engineProperties;
	
	@Autowired
	private InstrumentRepository instrumentRepository;
	
	@Autowired
	private EagleProcessExecutor eagleProcessExecutor;
	
	@Autowired
	private EagleEngineFileUtils eagleEngineFileUtils;
	
	private static final String ENRICH_DATA_SUFFIX = "_enrichData.csv";
	
	private static final String PKL_FILE_SUFFIX = "_predictive.pkl";
	
	private static final String OUTPUT_FILE_SUFFIX = "_predictions.csv";
	
	private static final String PYTHON_PATH = "/Users/ppasupuleti/anaconda/bin/python";
	
	
	public ApplyModelTaskLet(EagleModelProperties eagleModelProperties, EagleWorkFlowEngineProperties engineProperties) {
		this.eagleModelProperties = eagleModelProperties;
		this.engineProperties = engineProperties;
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute
	 * (org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 * 
	 * python command:
	 * 		python clientModelApply.py --input=..\Data\es_testds_daily_16.csv --picklefile=..\Model\ess_predictive.pkl --output=..\Output\predictions_es_0410.csv
	 * 		es_testds_daily_16 : latest enrichData
	 * 		picklefile: static pkl file (?)
	 * 		output: Prediction File		
	 * 
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		LOGGER.debug("*** Apply Model Data Step in progress... ***");
		try {
			
			String enrichDataDirectory = eagleEngineFileUtils.getEnrichDataPath();
			String modelPKLDirectory = eagleEngineFileUtils.getModelPKLPath();
			String modelOutputDirectory = eagleEngineFileUtils.getModelOutputPath();
			
			//Tools 
			String modelDataToolCodeDirectory = eagleEngineFileUtils.getModelDataToolCodePath();
			String modelAppName = eagleModelProperties.getModelAppName();
			
			//Base python Command
			StringBuilder baseCommand = new StringBuilder(PYTHON_PATH);
			baseCommand.append(EMPTY_SPACE);
			baseCommand.append(modelDataToolCodeDirectory+modelAppName);
			
			// Iterate Instrument Repository and apply model for each instrument.
			List<Instrument> instrumentsList = instrumentRepository.getInstruments();
			StringBuilder command = null;
			String enrichDataFilePath = null;
			String picklefile = null;
			for (Instrument instrument : instrumentsList) {
				if ("es".equalsIgnoreCase(instrument.getSymbol())) { //FIXME: delete this condition
					command = new StringBuilder();
					command.append(baseCommand).append(EMPTY_SPACE);

					enrichDataFilePath = enrichDataDirectory + instrument.getSymbol() + ENRICH_DATA_SUFFIX;
					picklefile = modelPKLDirectory + instrument.getSymbol() + PKL_FILE_SUFFIX;

					command.append("--input=").append(enrichDataFilePath).append(EMPTY_SPACE);
					command.append("--picklefile=").append(picklefile).append(EMPTY_SPACE);
					command.append("--output=")
							.append(modelOutputDirectory + instrument.getSymbol() + OUTPUT_FILE_SUFFIX);

					LOGGER.info("Apply Model Command:" + command);
					
					EagleProcessExecutorResult executeResult = eagleProcessExecutor.execute(command.toString());
					if (executeResult.isExecStatus()) {
						LOGGER.info("Apply Model process command executed succesfully.");
					} else {
						LOGGER.error("pply Model process command execution failed. Reason:"+executeResult.getErrorMessage());
					}
					command = null;
				}
			}
		} catch (Exception e) {
			throw new EagleException(EagleError.FAILED_TO_EXECUTE_ENRICHDATA_STEP, e, e.getMessage());
		}
		LOGGER.debug("Enriching Data Tasklet Step completed");
		return RepeatStatus.FINISHED;
	}
}
