package com.slib.rms.analytics.config;

import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RetryConfig {
    @Value("${batch.processing.retry-limit}")
    private int retryLimit;
    @Value("${batch.processing.backoff-delay-ms}")
    private long backoffDelay;

    @Bean
    public SimpleRetryPolicy retryPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionMap = new HashMap<>();
        //exceptionMap.put(Exception.class, true);
        exceptionMap.put(PSQLException.class, false);
        exceptionMap.put(DuplicateKeyException.class, false);
        return new SimpleRetryPolicy(retryLimit, exceptionMap);
    }

    @Bean
    public FixedBackOffPolicy backOffPolicy() {
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(backoffDelay);
        return backOffPolicy;
    }
}