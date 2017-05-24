package com.eagle.workflow.engine.config;

import java.io.IOException;

import javax.sql.DataSource;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import com.eagle.workflow.engine.job.ApplyModelTaskLet;
import com.eagle.workflow.engine.job.EnrichingDataTaskLet;
import com.eagle.workflow.engine.job.ExtractDataTaskLet;
import com.eagle.workflow.engine.job.JobCompletionNotificationListener;
import com.eagle.workflow.engine.job.PositionEngineTaskLet;
import com.eagle.workflow.engine.repository.ExtractDataJobRepository;
import com.eagle.workflow.engine.repository.InstrumentRepository;
import com.eagle.workflow.engine.repository.InstrumentRepositoryFactory;
import com.eagle.workflow.engine.repository.PositionDataJobRepository;
import com.eagle.workflow.engine.service.EmailServiceImpl;
import com.eagle.workflow.engine.store.InstrumentStoreService;
import com.eagle.workflow.engine.store.InstrumentStoreServiceImpl;
import com.eagle.workflow.engine.tws.api.EagleAPI;
import com.eagle.workflow.engine.tws.api.EagleAPILogger;
import com.eagle.workflow.engine.tws.data.providers.EagleTWSConnectionProvider;



@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
@ComponentScan("com.eagle")
public class EagleWorkFlowEngineConfiguration implements InitializingBean{
	private final static Logger LOGGER = LoggerFactory.getLogger(EagleWorkFlowEngineConfiguration.class.getName());

	private final EagleIBGatewayProperties gatewayProperties;

	private final EagleWorkFlowEngineProperties engineProperties;

	private final EagleEmailProperties emailProperties;
	
	private final EagleModelProperties modelProperties;
	
	private final EagleEnrichDataProperties enrichDataProperties;
	
	
	@Autowired
	private EagleTWSConnectionProvider connectionProvider;

	@Autowired
	private EagleAPILogger eagleAPILogger;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
    @Autowired
    public DataSource dataSource;

	private InstrumentRepositoryFactory instrumentRepositoryFactory = new InstrumentRepositoryFactory();
	
	private ExtractDataJobRepository extractDataJobRepository = new ExtractDataJobRepository();
	
	private PositionDataJobRepository positionDataRepository = new PositionDataJobRepository();

	@Autowired
	public EagleWorkFlowEngineConfiguration(@Valid EagleIBGatewayProperties gatewayProperties,
			@Valid EagleWorkFlowEngineProperties engineProperties, @Valid EagleEmailProperties emailProperties,
			@Valid EagleEnrichDataProperties enrichDataProperties, @Valid EagleModelProperties modelProperties) {
		this.gatewayProperties = gatewayProperties;
		this.engineProperties = engineProperties;
		this.emailProperties  = emailProperties;
		this.enrichDataProperties = enrichDataProperties;
		this.modelProperties = modelProperties;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOGGER.info("EagleWorkFlowEngineConfiguration done...");
	}

	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler(); //single threaded by default
	}

	@Bean
	public InstrumentRepository instrumentRepository() throws IOException{
		ClassPathResource classpathResource = new ClassPathResource("/instruments/instruments.json");
		return instrumentRepositoryFactory.createInstrumentRepository(classpathResource.getInputStream());
	}

	@Bean
	public EmailServiceImpl emailServiceImpl() {
		return new EmailServiceImpl(emailProperties); 
	}
	
	@Bean
	public InstrumentStoreService instrumentStoreService() throws IOException{
		return new InstrumentStoreServiceImpl(engineProperties);
	}

	@Bean
	public EagleAPI eagleAPI(){
		return new EagleAPI(connectionProvider, eagleAPILogger, eagleAPILogger, gatewayProperties.getHost(),
				gatewayProperties.getPort(), gatewayProperties.getClientId());
	}

	//Batch Configuration
	@Bean
	public Job extractDataJob(JobCompletionNotificationListener listener){
		return jobBuilderFactory.get("extractDataJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				//.flow(applyModel())
				.flow(extractData())
				.next(enrichingData())
				.next(applyModel())
				.next(positionEngine())
				.end()
				.build();
	}

	@Bean
	public Step extractData() {
		return stepBuilderFactory.get("extractData").tasklet(extractDataTaskLet()).build();
	}
	
	@Bean
	public Step enrichingData() {
		return stepBuilderFactory.get("enrichingData").tasklet(enrichingDataTaskLet()).build();
	}
	
	@Bean
	public Step applyModel() {
		return stepBuilderFactory.get("applyModel").tasklet(applyModelTaskLet()).build();
	}
	
	@Bean
	public Step positionEngine() {
		return stepBuilderFactory.get("positionEngine").tasklet(positionEngineTaskLet()).build();
	}
	
	@Bean
	public Tasklet extractDataTaskLet() {
		return new ExtractDataTaskLet();
	}
	
	@Bean
	public Tasklet enrichingDataTaskLet() {
		return new EnrichingDataTaskLet(enrichDataProperties,engineProperties);
	}
	
	@Bean
	public Tasklet applyModelTaskLet() {
		return new ApplyModelTaskLet(modelProperties,engineProperties);
	}
	
	@Bean
	public Tasklet positionEngineTaskLet() {
		return new PositionEngineTaskLet(modelProperties,engineProperties);
	}
	
}
