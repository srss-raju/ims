package com.ims.jobs;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import com.ims.constant.StatusType;
import com.ims.entity.TicketStatistics;
import com.ims.repository.TicketStatisticsRepository;

public class ImsForecastAutomationJob implements Job {
	
	private static final Logger LOG = Logger.getRootLogger();
	
	@Autowired
	private Environment env;
	
	@Autowired
	TicketStatisticsRepository ticketStatisticsRepository;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		List<String> customers = ticketStatisticsRepository.findDistinctForecastCustomers();
		List<TicketStatistics> statistics = ticketStatisticsRepository.findAllByCustomerIn(customers);
		if(!CollectionUtils.isEmpty(statistics)){
			String forecastUrl = env.getProperty("forecast.url");
			
			
			RestTemplate restTemplate = new RestTemplate();
			for(TicketStatistics stats:statistics){
				StringBuilder url = new StringBuilder(forecastUrl);
				LOG.info("Forecast URL ==>> "+url);
				url.append(stats.getCustomer());
				runForecast(url, restTemplate, stats);
				
			}
		}
		
		
	}

	private void runForecast(StringBuilder url, RestTemplate restTemplate, TicketStatistics statistics) {
		try{
			if(StatusType.COMPLETED.getDescription().equalsIgnoreCase(statistics.getAutomationStatus()) && StatusType.OPEN.getDescription().equalsIgnoreCase(statistics.getForecastStatus())){
				LOG.info("In Run Forecast method");
				statistics.setForecastStatus(StatusType.INPROGRESS.getDescription());
				ticketStatisticsRepository.save(statistics);
				LOG.info("Triggered URL ===>> "+url.toString());
				String result = restTemplate.getForObject(url.toString(), String.class);
				LOG.info("Forecast status ==>> "+result);
				if("Success".equalsIgnoreCase(result)){
					statistics.setForecastStatus(StatusType.COMPLETED.getDescription());
					ticketStatisticsRepository.save(statistics);
				}else{
					statistics.setForecastStatus(StatusType.FAILED.getDescription());
					ticketStatisticsRepository.save(statistics);
				}
			}
			
		}catch(Exception ex){
			LOG.info("Exception ===>> "+ex);
			statistics.setForecastStatus(StatusType.FAILED.getDescription());
			ticketStatisticsRepository.save(statistics);
		}
	}
}
