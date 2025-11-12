package com.slib.rms.analytics.json;

import com.slib.rms.analytics.CustomFixedBackOffPolicy;
import com.slib.rms.analytics.config.RetryConfig;
import com.slib.rms.analytics.listener.ChunkLoggingListener;
import com.slib.rms.analytics.listener.JobCompletionListener;
import org.springframework.core.io.Resource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;

@Configuration
public class JSONJobConfig {
    private static final int CHUNK_SIZE = 50;
    private final JobCompletionListener jobCompletionListener;
    private final ChunkLoggingListener chunkLoggingListener;
    @Value("${batch.job.json.file}")
    private Resource inputResource;

    public JSONJobConfig(JobCompletionListener jobCompletionListener) {
        this.jobCompletionListener = jobCompletionListener;
        //this.jobCompletionListener.setFileMover(true);
        chunkLoggingListener = new ChunkLoggingListener(CHUNK_SIZE);
        this.jobCompletionListener.setChunkLoggingListener(chunkLoggingListener);
    }

    @Bean
    public JsonItemReader<kpiConfig> readerJSON() {
        return new JsonItemReader<>(inputResource, new JacksonJsonObjectReader<>(kpiConfig.class));
    }

    @Bean
    public JdbcBatchItemWriter<kpiConfig> writerJSON(@Qualifier("targetDataSource") DataSource targetDataSource) {
        return new JdbcBatchItemWriterBuilder<kpiConfig>().dataSource(targetDataSource)
                .sql("INSERT INTO raw.kpi_config (kpi_id, kpi_name, threshold_upper_value," +
                        "threshold_lower_value,target_field_window_values,target_field_window_unit,severity," +
                        "breach_on,control_ts,created_at_utc)" +
                        " VALUES (:kpiId, :kpiName, :thresholdUpperValue, :thresholdLowerValue," +
                        ":targetFieldWindowValues, :targetFieldWindowUnit, :severity," +
                        ":breachOn, :controlTs::timestamp, :createdAtUtc::timestamp)")
                .assertUpdates(false).beanMapped().build();
    }

    @Bean
    public Step dailyJSONStep(JobRepository jobJSONRepository,
                               PlatformTransactionManager transactionManager,
                              JsonItemReader<kpiConfig> readerJSON,
                               JdbcBatchItemWriter<kpiConfig> writerJSON,
                              @Qualifier("retryPolicy") RetryPolicy retryPolicy,
                              @Qualifier("backOffPolicy") BackOffPolicy backOffPolicy) {
        return new StepBuilder("dailyJSONStep", jobJSONRepository)
                .<kpiConfig, kpiConfig>chunk(CHUNK_SIZE, transactionManager)
                .faultTolerant().retryPolicy(retryPolicy).backOffPolicy(backOffPolicy)
                .listener(chunkLoggingListener).reader(readerJSON).writer(writerJSON).build();

    }

    @Bean(name = "dailyJSONJob")
    public Job dailyJSONJob(JobRepository jobJSONRepository,
                                 @Qualifier("dailyJSONStep") Step migrationStepJSON) {
        return new JobBuilder("dailyJSONJob", jobJSONRepository)
                .listener(jobCompletionListener)
                .start(migrationStepJSON).build();
    }
}