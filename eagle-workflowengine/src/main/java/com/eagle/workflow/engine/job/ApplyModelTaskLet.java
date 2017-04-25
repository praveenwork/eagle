package com.eagle.workflow.engine.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	
	public ApplyModelTaskLet(EagleModelProperties eagleModelProperties, EagleWorkFlowEngineProperties engineProperties) {
		this.eagleModelProperties = eagleModelProperties;
		this.engineProperties = engineProperties;
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		LOGGER.debug("Apply Model Data Tasklet Step in progress...");
		try {
			String modelAppName = eagleModelProperties.getModelAppName();
			String modelDataToolsPath = eagleEngineFileUtils.getModelDataToolsPath();
			String modelOutputFilePath = eagleEngineFileUtils.getModelOutputPath();
			
			String enrichDataPath = eagleEngineFileUtils.getEnrichDataPath();
			String rawDataFileType = engineProperties.getRawDataFileType();
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd");
			String dString = dateFormat.format(new Date());
			
			
			StringBuilder baseCommand = new StringBuilder("/Users/ppasupuleti/anaconda/bin/python");
			baseCommand.append(EMPTY_SPACE).append(modelDataToolsPath+"Code/"+modelAppName);

			
			List<Instrument> instrumentsList = instrumentRepository.getInstruments();
			StringBuilder command = null;
			String finalEnrichDataFileName = null;
			String picklefile = null;
			for (Instrument instrument : instrumentsList) {
				if ("es".equalsIgnoreCase(instrument.getSymbol())) { //FIXME: delete this condition
					command = new StringBuilder();
					command.append(baseCommand).append(EMPTY_SPACE);
					finalEnrichDataFileName = enrichDataPath+instrument.getSymbol()+ FINAL_ENRICH_DATA + dString + "."+ rawDataFileType;
					picklefile = modelDataToolsPath+"Model"+File.separator+eagleModelProperties.getPklFilePath();
					command.append("--input=").append(finalEnrichDataFileName).append(EMPTY_SPACE);
					command.append("--picklefile=").append(picklefile).append(EMPTY_SPACE);
					command.append("--output=").append(modelOutputFilePath+PREDICTIONS+instrument.getSymbol()+"_"+dString+"."+ rawDataFileType);
					
					LOGGER.info("Enrich Data Command:"+ command);
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
