package com.slib.rms.analytics.bd;

import javax.sql.DataSource;
import com.slib.rms.analytics.CustomFixedBackOffPolicy;
import com.slib.rms.analytics.bd.entity.*;
import com.slib.rms.analytics.listener.ChunkLoggingListener;
import com.slib.rms.analytics.listener.JobCompletionListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
public class DatabaseJobConfig {
    private static final int CHUNK_SIZE = 1000;
    private final DataSource sourceCoreDataSource;
    private final DataSource sourceDashboardDataSource;
    private final DataSource targetDataSource;
    private final JobRepository jobDatabaseRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobCompletionListener jobCompletionListener;
    @Value("${batch.processing.retry-limit}")
    private int retryLimit;

    // Restoring explicit injection via constructor
    public DatabaseJobConfig(
            @Qualifier("sourceCoreDataSource") DataSource sourceCoreDataSource,
            @Qualifier("sourceDashboardDataSource") DataSource sourceDashboardDataSource,
            @Qualifier("targetDataSource") DataSource targetDataSource,
            JobRepository jobDatabaseRepository,
            PlatformTransactionManager transactionManager,
            JobCompletionListener jobCompletionListener) {
        this.sourceCoreDataSource = sourceCoreDataSource;
        this.sourceDashboardDataSource = sourceDashboardDataSource;
        this.targetDataSource = targetDataSource;
        this.jobDatabaseRepository = jobDatabaseRepository;
        this.transactionManager = transactionManager;
        this.jobCompletionListener = jobCompletionListener;
    }

    @Bean
    public JdbcBatchItemWriter<ConnectorParamDTO> writerConnectorParam() {
        String[] sql = generateSql(ConnectorParamDTO.class, "raw.connector_param");
        return new JdbcBatchItemWriterBuilder<ConnectorParamDTO>().dataSource(targetDataSource)
                .sql(sql[1]).beanMapped().assertUpdates(false).build();
    }

    @Bean
    public Step connectorParamStep(JdbcBatchItemWriter<ConnectorParamDTO> writerConnectorParam) {
        String[] sql = generateSql(ConnectorParamDTO.class, "rms.connector_param");
        String selectSql = sql[0];
        JdbcCursorItemReader<ConnectorParamDTO> reader = new JdbcCursorItemReaderBuilder<ConnectorParamDTO>()
                .dataSource(sourceCoreDataSource).name("jdbcReader")
                .sql(selectSql).rowMapper(new BeanPropertyRowMapper<>(ConnectorParamDTO.class)).build();
        return new StepBuilder("connectorParamStep", jobDatabaseRepository)
                .<ConnectorParamDTO, ConnectorParamDTO>chunk(CHUNK_SIZE, transactionManager)
                .faultTolerant().retryLimit(retryLimit)
                .backOffPolicy(new CustomFixedBackOffPolicy()).retry(Exception.class)
                .listener(new ChunkLoggingListener(CHUNK_SIZE))
                .reader(reader).writer(writerConnectorParam).build();
    }

    @Bean
    public JdbcBatchItemWriter<CurrencyRefDTO> writerCurrencyRef() {
        String[] sql = generateSql(CurrencyRefDTO.class, "raw.currency_ref");
        return new JdbcBatchItemWriterBuilder<CurrencyRefDTO>().dataSource(targetDataSource)
                .sql(sql[1]).beanMapped().assertUpdates(false).build();
    }

    @Bean
    public Step currencyRefStep(JdbcBatchItemWriter<CurrencyRefDTO> writerCurrencyRef) {
        String[] sql = generateSql(CurrencyRefDTO.class, "rms.currency_ref");
        String selectSql = sql[0];
        JdbcCursorItemReader<CurrencyRefDTO> reader = new JdbcCursorItemReaderBuilder<CurrencyRefDTO>()
                .dataSource(sourceCoreDataSource).name("jdbcReader")
                .sql(selectSql).rowMapper(new BeanPropertyRowMapper<>(CurrencyRefDTO.class)).build();
        return new StepBuilder("currencyRefStep", jobDatabaseRepository)
                .<CurrencyRefDTO, CurrencyRefDTO>chunk(CHUNK_SIZE, transactionManager)
                .faultTolerant().retryLimit(retryLimit)
                .backOffPolicy(new CustomFixedBackOffPolicy()).retry(Exception.class)
                .listener(new ChunkLoggingListener(CHUNK_SIZE))
                .reader(reader).writer(writerCurrencyRef).build();
    }

    @Bean
    public JdbcBatchItemWriter<DsAccountToRiskDTO> writerDsAccountToRisk() {
        String[] sql = generateSql(DsAccountToRiskDTO.class, "raw.ds_account_to_risk");
        return new JdbcBatchItemWriterBuilder<DsAccountToRiskDTO>().dataSource(targetDataSource)
                .sql(sql[1]).beanMapped().assertUpdates(false).build();
    }

    @Bean
    public Step dsAccountToRiskStep(JdbcBatchItemWriter<DsAccountToRiskDTO> writerDsAccountToRisk) {
        String[] sql = generateSql(DsAccountToRiskDTO.class, "rmt_dashboards.ds_account_to_risk");
        String selectSql = sql[0];
        JdbcCursorItemReader<DsAccountToRiskDTO> reader = new JdbcCursorItemReaderBuilder<DsAccountToRiskDTO>()
                .dataSource(sourceDashboardDataSource).name("jdbcReader")
                .sql(selectSql).rowMapper(new BeanPropertyRowMapper<>(DsAccountToRiskDTO.class)).build();
        return new StepBuilder("dsAccountToRiskStep", jobDatabaseRepository)
                .<DsAccountToRiskDTO, DsAccountToRiskDTO>chunk(CHUNK_SIZE, transactionManager)
                .faultTolerant().retryLimit(retryLimit)
                .backOffPolicy(new CustomFixedBackOffPolicy()).retry(Exception.class)
                .listener(new ChunkLoggingListener(CHUNK_SIZE))
                .reader(reader).writer(writerDsAccountToRisk).build();
    }

    @Bean
    public JdbcBatchItemWriter<DsClientEntityDTO> writerDsClientEntity() {
        String[] sql = generateSql(DsClientEntityDTO.class, "raw.ds_client_entity");
        return new JdbcBatchItemWriterBuilder<DsClientEntityDTO>().dataSource(targetDataSource)
                .sql(sql[1]).beanMapped().assertUpdates(false).build();
    }

    @Bean
    public Step dsClientEntityStep(JdbcBatchItemWriter<DsClientEntityDTO> writerDsClientEntity) {
        String[] sql = generateSql(DsClientEntityDTO.class, "rmt_dashboards.ds_client_entity");
        String selectSql = sql[0];
        JdbcCursorItemReader<DsClientEntityDTO> reader = new JdbcCursorItemReaderBuilder<DsClientEntityDTO>()
                .dataSource(sourceDashboardDataSource).name("jdbcReader")
                .sql(selectSql).rowMapper(new BeanPropertyRowMapper<>(DsClientEntityDTO.class)).build();
        return new StepBuilder("dsClientEntityStep", jobDatabaseRepository)
                .<DsClientEntityDTO, DsClientEntityDTO>chunk(CHUNK_SIZE, transactionManager)
                .faultTolerant().retryLimit(retryLimit)
                .backOffPolicy(new CustomFixedBackOffPolicy()).retry(Exception.class)
                .listener(new ChunkLoggingListener(CHUNK_SIZE))
                .reader(reader).writer(writerDsClientEntity).build();
    }

    @Bean
    public JdbcBatchItemWriter<DsPortfolioDTO> writerDsPortfolio() {
        String[] sql = generateSql(DsPortfolioDTO.class, "raw.ds_portfolio");
        return new JdbcBatchItemWriterBuilder<DsPortfolioDTO>().dataSource(targetDataSource)
                .sql(sql[1]).beanMapped().assertUpdates(false).build();
    }

    @Bean
    public Step dsPortfolioStep(JdbcBatchItemWriter<DsPortfolioDTO> writerDsPortfolio) {
        String[] sql = generateSql(DsPortfolioDTO.class, "rmt_dashboards.ds_portfolio");
        String selectSql = sql[0];
        JdbcCursorItemReader<DsPortfolioDTO> reader = new JdbcCursorItemReaderBuilder<DsPortfolioDTO>()
                .dataSource(sourceDashboardDataSource).name("jdbcReader")
                .sql(selectSql).rowMapper(new BeanPropertyRowMapper<>(DsPortfolioDTO.class)).build();
        return new StepBuilder("dsPortfolioStep", jobDatabaseRepository)
                .<DsPortfolioDTO, DsPortfolioDTO>chunk(CHUNK_SIZE, transactionManager)
                .faultTolerant().retryLimit(retryLimit)
                .backOffPolicy(new CustomFixedBackOffPolicy()).retry(Exception.class)
                .listener(new ChunkLoggingListener(CHUNK_SIZE))
                .reader(reader).writer(writerDsPortfolio).build();
    }

    @Bean
    public JdbcBatchItemWriter<DsPortfolioToRiskDTO> writerDsPortfolioToRisk() {
        String[] sql = generateSql(DsPortfolioToRiskDTO.class, "raw.ds_portfolio_to_risk");
        return new JdbcBatchItemWriterBuilder<DsPortfolioToRiskDTO>().dataSource(targetDataSource)
                .sql(sql[1]).beanMapped().assertUpdates(false).build();
    }

    @Bean
    public Step dsPortfolioToRiskStep(JdbcBatchItemWriter<DsPortfolioToRiskDTO> writerDsPortfolioToRisk) {
        String[] sql = generateSql(DsPortfolioToRiskDTO.class, "rmt_dashboards.ds_portfolio_to_risk");
        String selectSql = sql[0];
        JdbcCursorItemReader<DsPortfolioToRiskDTO> reader = new JdbcCursorItemReaderBuilder<DsPortfolioToRiskDTO>()
                .dataSource(sourceDashboardDataSource).name("jdbcReader")
                .sql(selectSql).rowMapper(new BeanPropertyRowMapper<>(DsPortfolioToRiskDTO.class)).build();
        return new StepBuilder("dsPortfolioToRiskStep", jobDatabaseRepository)
                .<DsPortfolioToRiskDTO, DsPortfolioToRiskDTO>chunk(CHUNK_SIZE, transactionManager)
                .faultTolerant().retryLimit(retryLimit)
                .backOffPolicy(new CustomFixedBackOffPolicy()).retry(Exception.class)
                .listener(new ChunkLoggingListener(CHUNK_SIZE))
                .reader(reader).writer(writerDsPortfolioToRisk).build();
    }

    @Bean
    public JdbcBatchItemWriter<DsRiskUnitDTO> writerDsRiskUnit() {
        String[] sql = generateSql(DsRiskUnitDTO.class, "raw.ds_risk_unit");
        return new JdbcBatchItemWriterBuilder<DsRiskUnitDTO>().dataSource(targetDataSource)
                .sql(sql[1]).beanMapped().assertUpdates(false).build();
    }

    @Bean
    public Step dsRiskUnitStep(JdbcBatchItemWriter<DsRiskUnitDTO> writerDsRiskUnit) {
        String[] sql = generateSql(DsRiskUnitDTO.class, "rmt_dashboards.ds_risk_unit");
        String selectSql = sql[0];
        JdbcCursorItemReader<DsRiskUnitDTO> reader = new JdbcCursorItemReaderBuilder<DsRiskUnitDTO>()
                .dataSource(sourceDashboardDataSource).name("jdbcReader")
                .sql(selectSql).rowMapper(new BeanPropertyRowMapper<>(DsRiskUnitDTO.class)).build();
        return new StepBuilder("dsRiskUnitStep", jobDatabaseRepository)
                .<DsRiskUnitDTO, DsRiskUnitDTO>chunk(CHUNK_SIZE, transactionManager)
                .faultTolerant().retryLimit(retryLimit)
                .backOffPolicy(new CustomFixedBackOffPolicy()).retry(Exception.class)
                .listener(new ChunkLoggingListener(CHUNK_SIZE))
                .reader(reader).writer(writerDsRiskUnit).build();
    }

    @Bean
    public JdbcBatchItemWriter<DsExpositionVacationDTO> writerDsExpositionVacation() {
        String[] sql = generateSql(DsExpositionVacationDTO.class, "raw.ds_exposition_vacation");
        return new JdbcBatchItemWriterBuilder<DsExpositionVacationDTO>().dataSource(targetDataSource)
                .sql(sql[1]).beanMapped().assertUpdates(false).build();
    }

    @Bean
    public Step dsExpositionVacationStep(JdbcBatchItemWriter<DsExpositionVacationDTO> writerDsExpositionVacation) {
        String[] sql = generateSql(DsExpositionVacationDTO.class, "rmt_dashboards.ds_exposition_vacation");
        String selectSql = sql[0];
        JdbcCursorItemReader<DsExpositionVacationDTO> reader = new JdbcCursorItemReaderBuilder<DsExpositionVacationDTO>()
                .dataSource(sourceDashboardDataSource).name("jdbcReader")
                .sql(selectSql).rowMapper(new BeanPropertyRowMapper<>(DsExpositionVacationDTO.class)).build();
        return new StepBuilder("dsExpositionVacationStep", jobDatabaseRepository)
                .<DsExpositionVacationDTO, DsExpositionVacationDTO>chunk(CHUNK_SIZE, transactionManager)
                .faultTolerant().retryLimit(retryLimit)
                .backOffPolicy(new CustomFixedBackOffPolicy()).retry(Exception.class)
                .listener(new ChunkLoggingListener(CHUNK_SIZE))
                .reader(reader).writer(writerDsExpositionVacation).build();
    }

    @Bean
    public JdbcBatchItemWriter<DsAlgoExternalResultsDTO> writerDsAlgoExternalResults() {
        String[] sql = generateSql(DsAlgoExternalResultsDTO.class, "raw.ds_algo_external_results");
        return new JdbcBatchItemWriterBuilder<DsAlgoExternalResultsDTO>().dataSource(targetDataSource)
                .sql(sql[1]).beanMapped().assertUpdates(false).build();
    }

    @Bean
    public Step dsAlgoExternalResultsStep(JdbcBatchItemWriter<DsAlgoExternalResultsDTO> writerDsAlgoExternalResults) {
        String[] sql = generateSql(DsAlgoExternalResultsDTO.class, "rmt_dashboards.ds_algo_external_results");
        String selectSql = sql[0];
        JdbcCursorItemReader<DsAlgoExternalResultsDTO> reader = new JdbcCursorItemReaderBuilder<DsAlgoExternalResultsDTO>()
                .dataSource(sourceDashboardDataSource).name("jdbcReader")
                .sql(selectSql).rowMapper(new BeanPropertyRowMapper<>(DsAlgoExternalResultsDTO.class)).build();
        return new StepBuilder("dsAlgoExternalResultsStep", jobDatabaseRepository)
                .<DsAlgoExternalResultsDTO, DsAlgoExternalResultsDTO>chunk(CHUNK_SIZE, transactionManager)
                .faultTolerant().retryLimit(retryLimit)
                .backOffPolicy(new CustomFixedBackOffPolicy()).retry(Exception.class)
                .listener(new ChunkLoggingListener(CHUNK_SIZE))
                .reader(reader).writer(writerDsAlgoExternalResults).build();
    }

    @Bean
    public JdbcBatchItemWriter<DsSuperClientDTO> writerDsSuperClient() {
        String[] sql = generateSql(DsSuperClientDTO.class, "raw.ds_super_client");
        return new JdbcBatchItemWriterBuilder<DsSuperClientDTO>().dataSource(targetDataSource)
                .sql(sql[1]).beanMapped().assertUpdates(false).build();
    }

    @Bean
    public Step dsSuperClientStep(JdbcBatchItemWriter<DsSuperClientDTO> writerDsSuperClient) {
        String[] sql = generateSql(DsSuperClientDTO.class, "rmt_dashboards.ds_super_client");
        String selectSql = sql[0];
        JdbcCursorItemReader<DsSuperClientDTO> reader = new JdbcCursorItemReaderBuilder<DsSuperClientDTO>()
                .dataSource(sourceDashboardDataSource).name("jdbcReader")
                .sql(selectSql).rowMapper(new BeanPropertyRowMapper<>(DsSuperClientDTO.class)).build();
        return new StepBuilder("dsAlgoExternalResultsStep", jobDatabaseRepository)
                .<DsSuperClientDTO, DsSuperClientDTO>chunk(CHUNK_SIZE, transactionManager)
                .faultTolerant().retryLimit(retryLimit)
                .backOffPolicy(new CustomFixedBackOffPolicy()).retry(Exception.class)
                .listener(new ChunkLoggingListener(CHUNK_SIZE))
                .reader(reader).writer(writerDsSuperClient).build();
    }

    @Bean(name = "dailyDatabaseJob")
    public Job dailyDatabaseJob(JobRepository jobDatabaseRepository,
                                @Qualifier("connectorParamStep") Step connectorParamStep,
                                @Qualifier("currencyRefStep") Step currencyRefStep,
                                @Qualifier("dsAccountToRiskStep") Step dsAccountToRiskStep,
                                @Qualifier("dsClientEntityStep") Step dsClientEntityStep,
                                @Qualifier("dsPortfolioStep") Step dsPortfolioStep,
                                @Qualifier("dsPortfolioToRiskStep") Step dsPortfolioToRiskStep,
                                @Qualifier("dsRiskUnitStep") Step dsRiskUnitStep,
                                @Qualifier("dsExpositionVacationStep") Step dsExpositionVacationStep,
                                @Qualifier("dsAlgoExternalResultsStep") Step dsAlgoExternalResultsStep,
                                @Qualifier("dsSuperClientStep") Step dsSuperClientStep) {
        return new JobBuilder("dailyDatabaseJob", jobDatabaseRepository)
                .listener(jobCompletionListener).start(connectorParamStep).next(currencyRefStep).next(dsAccountToRiskStep).next(dsClientEntityStep)
                .next(dsPortfolioStep).next(dsPortfolioToRiskStep).next(dsRiskUnitStep)
                .next(dsExpositionVacationStep).next(dsAlgoExternalResultsStep).next(dsSuperClientStep).build();
    }

    private <T> String[] generateSql(Class<T> dtoClass, String schemaAndTableName) {
        String columnList = Arrays.stream(dtoClass.getDeclaredFields())
                .map(Field::getName)
                .map(name -> name.replaceAll("(?<=[a-z0-9])([A-Z])", "_$1").toLowerCase())
                .collect(Collectors.joining(","));
        String valueList = Arrays.stream(dtoClass.getDeclaredFields())
                .map(Field::getName).map(name -> ":" + name).collect(Collectors.joining(","));
        String selectSql = "SELECT " + columnList + " FROM " + schemaAndTableName;
        String insertSql = "INSERT INTO " + schemaAndTableName + " (" + columnList +
                ") VALUES (" + valueList + ")";
        return new String[]{selectSql, insertSql};
    }
}