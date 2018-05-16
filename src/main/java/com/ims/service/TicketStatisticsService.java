package com.ims.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ims.constant.StatusType;
import com.ims.entity.TicketStatistics;
import com.ims.repository.TicketStatisticsRepository;

@Service
public class TicketStatisticsService {

	@Autowired
    private TicketStatisticsRepository ticketStatisticsRepository;
	
	public TicketStatistics create(TicketStatistics ticketStatistics) {
		return ticketStatisticsRepository.save(ticketStatistics);
	}

	public TicketStatistics update(TicketStatistics ticketStatistics) {
		return ticketStatisticsRepository.save(ticketStatistics);
	}
	
	public TicketStatistics findById(Long ticketStatisticsId) {
		return ticketStatisticsRepository.findOne(ticketStatisticsId);
	}

	public void delete(Long ticketStatisticsId) {
		ticketStatisticsRepository.delete(ticketStatisticsId);
		
	}

	public List<TicketStatistics> findAll() {
		return ticketStatisticsRepository.findAll();
	}
	
	public List<TicketStatistics> findAllByFileNameOrderByJobId(String fileName){
		return ticketStatisticsRepository.findAllByFileNameOrderByJobIdDesc(fileName);
	}
	
	public TicketStatistics findMostRecentRecord(String fileName){
		TicketStatistics ticketStatistics = null;
		List<TicketStatistics> ticketStatisticsList = ticketStatisticsRepository.findAllByFileNameOrderByJobIdDesc(fileName);
		if(!CollectionUtils.isEmpty(ticketStatisticsList)){
			ticketStatistics = ticketStatisticsList.get(0);
		}
		return ticketStatistics;
	}

	public List<TicketStatistics> getCurrentRecords() {
		return ticketStatisticsRepository.findAllByAutomationStatusOrderByJobIdDesc(StatusType.INPROGRESS.getDescription());
	}

	public TicketStatistics getCurrentRecordStatus(Long id) {
		return ticketStatisticsRepository.findByJobId(id);
	}
	
}
