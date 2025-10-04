package com.github.adetiamarhadi.xyz_auth_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Core pool size - number of threads to keep alive
        executor.setCorePoolSize(5);

        // Maximum pool size - maximum number of threads
        executor.setMaxPoolSize(10);

        // Queue capacity - capacity for the queue
        executor.setQueueCapacity(25);

        // Thread name prefix
        executor.setThreadNamePrefix("async-task-");

        // Keep alive time for threads above core pool size (in seconds)
        executor.setKeepAliveSeconds(60);

        // Allow core threads to timeout
        executor.setAllowCoreThreadTimeOut(true);

        // Rejection policy when queue is full
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // Timeout for shutdown (in seconds)
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();

        log.info("Async task executor initialized with core pool size: {}, max pool size: {}, queue capacity: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());

        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            log.error("Exception occurred in async method: {} with arguments: {}",
                    method.getName(), objects, throwable);
        };
    }
}
