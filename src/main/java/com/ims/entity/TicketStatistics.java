package com.ims.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity
@Table(name = "ticket_statistics")
public class TicketStatistics {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long jobId;
	
	private String systemName;
	
	@Transient
	private List<String> systemNames;
	
	private String customer;
	
	private String automationStatus;
	
	private Date automationStartDate;
	
	private Date automationEndDate;
	
	private String forecastStatus;
	
	private Date forecastStartDate;
	
	private Date forecastEndDate;
	
	private String knowledgeBaseStatus;
	
	private Date knowledgeBaseStartDate;
	
	private Date knowledgeBaseEndDate;
	
	private String comments;
	
	private String fileName;
	
	private Integer versionNumber;
	
	private Long totalRecords;
	
	private Long recordsInserted;
	
	private Long recordsFailed;
	
	private String source;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "jobId")
	private List<TicketLogStatistics> ticketLogStatistics;
	
	
}
