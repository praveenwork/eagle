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

	private static final String EMPTY_SPACE = " ";
	
	private static final String MODEL_DATA = "_model";
	
	private static final String FINAL_ENRICH_DATA = "_final_";
	
	private EagleEnrichDataProperties enrichDataProperties;
	
	private EagleWorkFlowEngineProperties engineProperties;
	
	@Autowired
	private InstrumentRepository instrumentRepository;
	
	@Autowired
	private EagleProcessExecutor eagleProcessExecutor;
	
	@Autowired
	private EagleEngineFileUtils eagleEngineFileUtils;
	
	public EnrichingDataTaskLet(EagleEnrichDataProperties enrichDataProperties, EagleWorkFlowEngineProperties engineProperties) {
		this.enrichDataProperties = enrichDataProperties;
		this.engineProperties = engineProperties;
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		LOGGER.debug("Enriching Data Tasklet Step in progress...");
		try {
			String enrichAppName = enrichDataProperties.getEnrichApp();
			String rawDataDirectory = eagleEngineFileUtils.getRawDataPath();
			String enrichDataDirectory = eagleEngineFileUtils.getEnrichDataPath();
			String enrichDataToolsDirectory = eagleEngineFileUtils.getToolDataPath();
			String enrichDataModelDirectory = eagleEngineFileUtils.getEnrichDataModelPath();
			
			String rawDataFileType = engineProperties.getRawDataFileType();
			
			List<Instrument> instrumentsList = instrumentRepository.getInstruments();
			
			StringBuilder baseCommand = new StringBuilder("/Users/ppasupuleti/anaconda/bin/python");
			baseCommand.append(EMPTY_SPACE);
			baseCommand.append(enrichDataToolsDirectory+enrichAppName).append(EMPTY_SPACE);
			
			StringBuilder command = null;
			String rawInstrumentData =  null;
			String modelDataFileName = null;
			String finalEnrichDataFileName = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			String dateString = dateFormat.format(new Date());
			dateFormat = new SimpleDateFormat("MMdd");
			String dString = "0423";//dateFormat.format(new Date()); //FIXME
			for (Instrument instrument : instrumentsList) {
				if("es".equalsIgnoreCase(instrument.getSymbol())){ //FIXME: delete this condition
					command = new StringBuilder();
					command.append(baseCommand).append(EMPTY_SPACE);
					rawInstrumentData = rawDataDirectory + instrument.getSymbol() + "."+ rawDataFileType;
					modelDataFileName = instrument.getSymbol()+ MODEL_DATA+"."+ rawDataFileType;
					finalEnrichDataFileName = instrument.getSymbol()+ FINAL_ENRICH_DATA + dString + "."+ rawDataFileType;
					command.append(rawInstrumentData).append(EMPTY_SPACE);
					command.append(enrichDataModelDirectory+modelDataFileName).append(EMPTY_SPACE);
					command.append(enrichDataDirectory+finalEnrichDataFileName).append(EMPTY_SPACE);
					command.append(dateString).append(EMPTY_SPACE);
					//command.append(instrument.getSymbol());
					command.append("ess"); //FIXME: ??
					LOGGER.info("Enrich Data Command:"+ command);
					EagleProcessExecutorResult executeResult = eagleProcessExecutor.execute(command.toString());
					if (executeResult.isExecStatus()) {
						LOGGER.info("Enrich Data process command executed succesfully.");
					} else {
						LOGGER.error("Enrich Data process command execution failed. Reason:"+executeResult.getErrorMessage());
					}
					command = null;
					rawInstrumentData =  null;
					modelDataFileName = null;
					finalEnrichDataFileName = null;
				}
			}
		} catch (Exception e) {
			throw new EagleException(EagleError.FAILED_TO_EXECUTE_ENRICHDATA_STEP, e, e.getMessage());
		}
		LOGGER.debug("Enriching Data Tasklet Step completed");
		return RepeatStatus.FINISHED;
	}
}
