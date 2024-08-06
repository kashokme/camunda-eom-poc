#!/bin/bash

SNS_TOPIC_1="my-test-topic"
SNS_TOPIC_2="my-result-topic"
SQS_QUEUE="my-test-queue"

docker run --rm -d -p 4566:4566 -p 4510:4510 localstack/localstack

echo "Esperando a que LocalStack esté listo..."
sleep 10

export AWS_ENDPOINT_URL=http://localhost:4566
export AWS_REGION=us-east-1

aws --endpoint-url=$AWS_ENDPOINT_URL sns create-topic --name $SNS_TOPIC_1
aws --endpoint-url=$AWS_ENDPOINT_URL sns create-topic --name $SNS_TOPIC_2

aws --endpoint-url=$AWS_ENDPOINT_URL sqs create-queue --queue-name $SQS_QUEUE

aws --endpoint-url=$AWS_ENDPOINT_URL sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:$SNS_TOPIC_2 --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:$SQS_QUEUE

echo "SNS topics and SQS queue created:"
aws --endpoint-url=$AWS_ENDPOINT_URL sns list-topics
aws --endpoint-url=$AWS_ENDPOINT_URL sqs list-queues

echo "Setup completed."
