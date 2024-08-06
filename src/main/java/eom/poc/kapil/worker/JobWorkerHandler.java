package eom.poc.kapil.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eom.poc.kapil.model.WorkflowVariables;
import eom.poc.kapil.service.SnsPublisher;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@AllArgsConstructor
public class JobWorkerHandler {

  @Autowired
  private SnsPublisher snsPublisher;

  @Autowired
  private ObjectMapper objectMapper;

  private final WorkflowVariables workflowVariables = WorkflowVariables.getInstance();

  @JobWorker(type = "run-job")
  public WorkflowVariables runDummyJob() throws JsonProcessingException {
    Map<String, String> job = new HashMap<>();
    job.put("message", "dummyMessage");
    String jsonString = objectMapper.writeValueAsString(job);
    snsPublisher.publishMessage(jsonString);
    log.info("SNS message published");
    workflowVariables.setDummyJobId("dummyJobId");
    workflowVariables.setWaitForJobEnabled(true);
    return workflowVariables;
  }
}