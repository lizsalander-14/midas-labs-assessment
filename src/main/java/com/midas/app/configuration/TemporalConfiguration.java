package com.midas.app.configuration;

import com.midas.app.activities.AccountActivityImpl;
import com.midas.app.workflows.CreateAccountWorkflow;
import com.midas.app.workflows.CreateAccountWorkflowImpl;
import com.midas.app.workflows.UpdateAccountWorkflow;
import com.midas.app.workflows.UpdateAccountWorkflowImpl;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfiguration {

  @Autowired private WorkerFactory workerFactory;

  @Autowired AccountActivityImpl accountActivity;

  @Bean
  public List<Worker> startTemporalWorker() {
    var worker1 = workerFactory.newWorker(CreateAccountWorkflow.QUEUE_NAME);
    worker1.registerWorkflowImplementationTypes(CreateAccountWorkflowImpl.class);
    worker1.registerActivitiesImplementations(accountActivity);
    var worker2 = workerFactory.newWorker(UpdateAccountWorkflow.QUEUE_NAME);
    worker2.registerWorkflowImplementationTypes(UpdateAccountWorkflowImpl.class);
    worker2.registerActivitiesImplementations(accountActivity);
    workerFactory.start();
    return Arrays.asList(worker1, worker2);
  }
}
