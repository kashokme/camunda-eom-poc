# Camunda Job Workers with Events

This project demostrates how a BPMN works Camunda with a Service Task that triggers an SNS notification (in the background, the SNS will trigger a Lambda function and the lambda will trigger the Airflow DAG, but we are not doing that as part of this POC).
Once the "job is triggered", the SQS Polling will start polling for a new message as completion result message, the JobResult will be processed and propagated to Camunda to go into a Manual Task step if
the job failed or move to the next step if succeeded.

## Pre-requisites
We need to run the service considering that:
- Java 17 required
- Installed Localstack in your local: ```docker pull localstack/localstack```

## How to run
1. Create new Zeebe client and configure credentials in [Camunda Twilio Stage Org](https://console.camunda.io/org/fecc1cd1-bff0-42cc-9982-53aaf9794c6b/cluster/5953bcad-bdc4-4887-966d-4e02b41436cd/api) and add the credentials in ```application.properties```
2. Run ```setup_localstack.sh```. This script will create:
   * SNS topic to trigger job
   * SNS topic to send Completion result
   * SQS queue to poll for completion result
   * SQS subscribed to the SNS topic to send Completion result
3. Deploy BPMN in Camunda and Run it (Go to Operate Tab to see execution)
4. Manually send the Job Completion Result sending the SNS:
```
aws --endpoint-url=http://localhost:4566 sns publish \
--topic-arn arn:aws:sns:us-east-1:000000000000:my-result-topic \
--message "SUCCESS" \
--subject "Test"
```

## How it works
The current BPMN process created:
1. Run Job Service Task: Will send an SNS notification to trigger the job and enable SQS polling.
2. Polling for Job Result: Will poll the SQS queue for the completion result message and disable the polling once the message is received.
3. Process Job Result: Will process the job result and move to the next step if succeeded or go to Manual Task if failed.
   