package com.ims.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ims.entity.TicketMetadata;
import com.ims.repository.TicketMetadataRepository;

@Service
public class QueryBuilder {
	
	@Autowired
	private Environment env;
	
	public StringBuilder buildHiveQuery(TicketMetadataRepository ticketMetadataRepository, String systemName, String customer){
		StringBuilder queryBuilder;
		queryBuilder = new StringBuilder("insert into TICKET_DATA (");
		buildInsertQueryWithMetadata(queryBuilder, ticketMetadataRepository, systemName, customer);
		return queryBuilder;
	}
	
	
	private void buildInsertQueryWithMetadata(StringBuilder queryBuilder, TicketMetadataRepository ticketMetadataRepository, String systemName, String customer) {
		List<TicketMetadata> metadata =  ticketMetadataRepository.findBySystemNameAndCustomerOrderById(systemName, customer);
		if(!CollectionUtils.isEmpty(metadata)){
			for(TicketMetadata data : metadata){
				queryBuilder.append(data.getMappingColumn()).append(",");
			}
		}
	}
	
	
	public StringBuilder getInsertQueryWithValue(StringBuilder queryBuilder) {
		String tempQueryBuilder = queryBuilder.toString().substring(0, queryBuilder.lastIndexOf(","));
		StringBuilder query = new StringBuilder(tempQueryBuilder);
		query.append(") values (");
		return query;
	}
	
	public StringBuilder getSelectValue(StringBuilder queryBuilder) {
		String tempQueryBuilder = queryBuilder.toString().substring(0, queryBuilder.lastIndexOf(","));
		StringBuilder query = new StringBuilder(tempQueryBuilder);
		query.append(") select ");
		return query;
	}
	
	public StringBuilder getFromValue(StringBuilder queryBuilder, String tableName) {
		queryBuilder.append(" from ").append(tableName);
		return queryBuilder;
	}

}
