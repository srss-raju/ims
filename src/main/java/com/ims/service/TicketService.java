package com.ims.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ims.dao.HiveDao;
import com.ims.entity.Ticket;
import com.ims.entity.TicketMetadata;
import com.ims.entity.TicketStatistics;
import com.ims.repository.TicketMetadataRepository;
import com.ims.repository.TicketRepository;
import com.ims.repository.TicketStatisticsRepository;

@Service
public class TicketService {
	
	private static final Logger LOG = Logger.getLogger(TicketService.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
	private TicketMetadataRepository ticketMetadataRepository;
	
	@Autowired
	TicketStatisticsRepository ticketStatisticsRepository;
	
	@Autowired
	HiveDao hiveDao;
	
	
	public void updateTicketData(String result) throws Exception{
		StringBuilder queryBuilder = new StringBuilder("insert into ticket_data (");
		List<TicketMetadata> metadata =  ticketMetadataRepository.findBySystemNameAndCustomer("Service Now", "Deloitte");
		if(!CollectionUtils.isEmpty(metadata)){
			for(TicketMetadata data : metadata){
				queryBuilder.append(data.getMappingColumn()).append(",");
			}
		}
		
		
		
		LOG.info("Result in Service === "+result);
		JSONObject jsonObj = new JSONObject(result);
		 JSONArray records = jsonObj.getJSONArray("result");
		 if(records != null && records.length() != 0){
			 Connection con = getConnection();
			 Statement stmt = con.createStatement();
			 TicketStatistics ticketStatistics = ticketStatisticsRepository.save(getTicketStatistics());
			 for(int i=0; i < records.length(); i++){
				 JSONObject record = records.getJSONObject(i);
				 StringBuilder query = getInsertQuery(queryBuilder);
				 prepareQuery(record, query);
				 stmt.execute(query.toString());
			 }
			 stmt.close();
			 con.close();
			 ticketStatistics.setAutomationEndDate(new Date());
			 ticketStatisticsRepository.save(ticketStatistics);
		 }
		 
	}

	private void prepareQuery(JSONObject record, StringBuilder query) {
		query.append("\"");
		 query.append((String) record.get((String)env.getProperty("ticketid").trim())).append("\"").append(",");
		 query.append("\"");
		 String description = (String) record.get((String)env.getProperty("description").replace("\"", "\\\""));
		 query.append(description.trim()).append("\"").append(",");
		 query.append("\"");
		 String shortDescription = (String) record.get((String)env.getProperty("shortdescription").replace("\"", "\\\""));
		 query.append(shortDescription.trim()).append("\"").append(",");
		 query.append("\"");
		 query.append((String) record.get((String)env.getProperty("comments").trim())).append("\"").append(",");
		 query.append("\"");
		 query.append((String) record.get((String)env.getProperty("status"))).append("\"").append(",");
		 query.append("\"");
		 query.append((String) record.get((String)env.getProperty("createddate"))).append("\"").append(",");
		 query.append("\"");
		 query.append((String) record.get((String)env.getProperty("createdby"))).append("\"").append(",");
		 query.append("\"");
		 query.append((String) record.get((String)env.getProperty("updateddate"))).append("\"").append(",");
		 query.append("\"");
		 query.append((String) record.get((String)env.getProperty("category"))).append("\"").append(",");
		 query.append("\"");
		 query.append((String) record.get((String)env.getProperty("priority"))).append("\"").append(")");
		 LOG.info(" \n \n "+query.toString());
	}

	private TicketStatistics getTicketStatistics() {
		TicketStatistics ticket = new TicketStatistics();
		 ticket.setSystemName((String)env.getProperty("ticketsystem"));
		 ticket.setCustomer((String)env.getProperty("customer"));
		 ticket.setAutomationStatus("In Progress");
		 ticket.setAutomationStartDate(new Date());
		 return ticket;
	}
	
	public Connection getConnection() throws Exception {
		Class.forName((String)env.getProperty("hive.driver-class-name"));
		return DriverManager.getConnection((String)env.getProperty("hive.url"), (String)env.getProperty("hive.username"), (String)env.getProperty("hive.password"));
	}

	private StringBuilder getInsertQuery(StringBuilder queryBuilder) {
		String tempQueryBuilder = queryBuilder.toString().substring(0, queryBuilder.lastIndexOf(","));
		StringBuilder query = new StringBuilder(tempQueryBuilder);
		query.append(") values (");
		return query;
	}
	
	public List<Ticket> getTicketData(){
		return ticketRepository.findAll();
	}
	
	public void deleteData(){
		hiveDao.deleteRecords();
	}

}
