package com.eagle.workflow.engine.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

import com.eagle.contract.constants.EagleContractConstants;
import com.eagle.contract.constants.InstrumentHistoricalDataProvider;

/**
 * @author ppasupuleti
 *
 */
@Component
public class EagleEngineDateUtils {
	
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	
	public String getExtractDataRunDate(){
		LocalDate todayDate = LocalDate.now();
		return dateFormatter.format(todayDate);
	}
	
	public String getEnrichDataToolRunDate(){
		LocalDate todayDate = LocalDate.now();
		if(todayDate.getDayOfWeek()==DayOfWeek.SATURDAY){
			todayDate = todayDate.minusDays(1);
		}else if(todayDate.getDayOfWeek()==DayOfWeek.SUNDAY){
			todayDate = todayDate.minusDays(2);
		}
		return dateFormatter.format(todayDate);
	}
	
	public int getHistoricalDataDuration(LocalDate lastRecordDate, String dataProvider) {
		int duration = 0;
		LocalDate today = LocalDate.now();
		int addDays = 1; // Need to fetch data from LastRecordDay after
		int weeks = (int) ChronoUnit.WEEKS.between(lastRecordDate.plusDays(addDays), today);
		if (dataProvider.equalsIgnoreCase(InstrumentHistoricalDataProvider.IB.name())) {
			int days = (int) ChronoUnit.DAYS.between(lastRecordDate.plusDays(addDays), today);
			duration = days - (weeks * 2);
			// if the lastRecordDate is a Friday and
			if ((weeks != 0) && (days % weeks != 0) && (lastRecordDate.getDayOfWeek() == DayOfWeek.FRIDAY)) {
				duration = duration - 2;
			}
			if (duration > EagleContractConstants.MAX_IB_SUPPORTED_HISTORICAL_DAYS) {
				duration = EagleContractConstants.MAX_IB_SUPPORTED_HISTORICAL_DAYS;
			}
		} else {
			duration = (int) ChronoUnit.DAYS.between(lastRecordDate, today);
		}
		return duration;
	}
}
