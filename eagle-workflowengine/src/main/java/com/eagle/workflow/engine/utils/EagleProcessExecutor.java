package com.eagle.workflow.engine.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.eagle.boot.config.exception.EagleError;
import com.eagle.boot.config.exception.EagleException;

/**
 * @author ppasupuleti
 *
 */
@Component
public class EagleProcessExecutor {
	private static final Logger LOGGER = LoggerFactory.getLogger(EagleProcessExecutor.class);
	
	public EagleProcessExecutorResult execute(String command) throws EagleException{
		EagleProcessExecutorResult result = new EagleProcessExecutorResult();
		try {
			LOGGER.info("Process Command:"+ command);
			Process process = Runtime.getRuntime().exec(command);//processBuilder.start();
			result.setResponse(processResponse(process.getInputStream()));
			result.setErrorMessage(processResponse(process.getErrorStream()));
			int errorCode = process.waitFor();
			if(errorCode == 0){
				result.setExecStatus(true);
			} else {
				result.setExecStatus(false);
			}
			result.setErrorCode(errorCode);
		} catch (IOException | InterruptedException e) {
			throw new EagleException(EagleError.FAILED_TO_EXECUTE_PROCESS, command);
		}
		return result;
	}
	
	//----------Helpers------------
	private static String processResponse(InputStream inputStream) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + System.getProperty("line.separator"));
			}
		} finally {
			br.close();
		}
		return sb.toString();
	}
}
