package com.eagle.workflow.engine.job;

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
import com.eagle.workflow.engine.service.DataExtractorService;

/**
 * @author ppasupuleti
 *
 */
@Component
public class ExtractDataTaskLet implements Tasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExtractDataTaskLet.class);


	@Autowired
	private DataExtractorService dataExtractorService;

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		LOGGER.debug("In tasklet");
		try {
			dataExtractorService.extractData(); 
		} catch (EagleException e) {
			throw e;
		}  catch (Exception e) {
			throw new EagleException(EagleError.FAILED_TO_EXTRACT_DATA, e.getMessage(),e);
		}
		return RepeatStatus.FINISHED;
	}

}
