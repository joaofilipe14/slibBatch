package com.slib.rms.analytics.dbt;

import com.slib.rms.analytics.listener.JobCompletionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
public class DBTJobConfig {
    private static final Logger log = LoggerFactory.getLogger(DBTJobConfig.class);
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobCompletionListener jobCompletionListener;
    private final RetryTemplate retryTemplate;
    @Value("${batch.job.dbt.url}")
    private String bdtURL;

    public DBTJobConfig(JobRepository jobRepository,
                        PlatformTransactionManager transactionManager,
                        JobCompletionListener jobCompletionListener) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.jobCompletionListener = jobCompletionListener;
        retryTemplate =  new RetryTemplate();
    }

    @Bean
    public Step dbtBronzeStep() {
        List<DbtDTO> bodies = List.of(
                new DbtDTO("snapshot", "kpi_config"),
                new DbtDTO("currency_ref"),
                new DbtDTO("ds_account_to_risk"),
                new DbtDTO("ds_account"),
                new DbtDTO("connector_param"),
                new DbtDTO("ds_client_entity"),
                new DbtDTO("ds_exposition_vacation"),
                new DbtDTO("ds_portfolio"),
                new DbtDTO("ds_portfolio_to_risk"),
                new DbtDTO("ds_risk_unit"),
                new DbtDTO("ds_super_client"),
                new DbtDTO("portfolio_mr_ccp"),
                new DbtDTO("ds_algo_external_results")
        );
        return new StepBuilder("dbtBronzeStep", jobRepository)
                .tasklet(dbtTasklet(bodies), transactionManager).build();
    }

    @Bean(name = "dailyJobDBTBronze")
    public Job dailyJobDBTBronze(@Qualifier("dbtBronzeStep")  Step dbtBronzeStep) {
        return new JobBuilder("dailyJobDBTBronzeJob", jobRepository)
                .listener(jobCompletionListener)
                .start(dbtBronzeStep).build();
    }

    @Bean
    public Step dbtSilverStep() {
        List<DbtDTO> bodies = List.of(
                new DbtDTO("slv_dim_client"),
                new DbtDTO("slv_dim_currency"),
                new DbtDTO("slv_dim_portfolio"),
                new DbtDTO("slv_dim_portfolio_risk_unit"),
                new DbtDTO("slv_dim_risk_unit"),
                new DbtDTO("slv_fct_portfolio_margin_eod_ccp"),
                new DbtDTO("slv_fct_portfolio_margin_eod_rms"),
                new DbtDTO("slv_fct_portfolio_margin_eod"),
                new DbtDTO("slv_param_alert_config"),
                new DbtDTO("slv_kpi_04_mr_discrepancy"),
                new DbtDTO("slv_kpi_05_mr_rollingavg"),
                new DbtDTO("slv_kpi_06_mr_rollingavg_py"),
                new DbtDTO("slv_kpi_09_mr_observ_exceed"),
                new DbtDTO("slv_kpi_10_mr_zscore"),
                new DbtDTO("slv_fct_portfolio_alert_events"),
                new DbtDTO("slv_dim_kpi"),
                new DbtDTO("slv_dim_ccp")
        );
        return new StepBuilder("dbtSilverStep", jobRepository)
                .tasklet(dbtTasklet(bodies), transactionManager).build();
    }

    @Bean(name = "dailyJobDBTSilver")
    public Job dailyJobDBTSilver(@Qualifier("dbtSilverStep") Step dbtSilverStep) {
        return new JobBuilder("dailyJobDBTSilverJob", jobRepository)
                .listener(jobCompletionListener)
                .start(dbtSilverStep).build();
    }

    @Bean
    public Step dbtGoldStep() {
        List<DbtDTO> bodies = List.of(
                new DbtDTO("gld_dim_client"),
                new DbtDTO("gld_dim_currency"),
                new DbtDTO("gld_dim_portfolio"),
                new DbtDTO("gld_dim_portfolio_risk_unit"),
                new DbtDTO("gld_dim_risk_unit"),
                new DbtDTO("gld_fct_portfolio_margin_eod"),
                new DbtDTO("gld_fct_portfolio_margin_eod_ccp"),
                new DbtDTO("gld_dim_ccp"),
                new DbtDTO("gld_dim_kpi"),
                new DbtDTO("gld_fct_portfolio_alert_events")
        );
        return new StepBuilder("dbtGoldStep", jobRepository)
                .tasklet(dbtTasklet(bodies), transactionManager).build();
    }

    @Bean(name = "dailyJobDBTGold")
    public Job dailyJobDBTGold(@Qualifier("dbtGoldStep") Step dbtGoldStep) {
        return new JobBuilder("dailyJobDBTGoldJob", jobRepository)
                .listener(jobCompletionListener)
                .start(dbtGoldStep).build();
    }

    private Tasklet dbtTasklet(List<DbtDTO>  commands) {
        return (contribution, chunkContext) -> {
            RestTemplate restTemplate = new RestTemplate();
            for (DbtDTO dto : commands) {
                log.info("Calling DBT: {} with body {}", bdtURL, dto.toString());
                retryTemplate.execute(context -> {
                    try {
                        ResponseEntity<String> response = restTemplate.postForEntity(bdtURL, dto, String.class);
                        log.info("Calling DBT successfully: {}", response);
                    } catch (Exception e) {
                        log.error("DBT call failed: {}", e.getMessage());
                    }
                    return null;
                });
            }
            return RepeatStatus.FINISHED;
        };
    }
}