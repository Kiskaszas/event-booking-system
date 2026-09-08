#!/bin/bash
echo "🔄 LocalStack AWS erőforrások inicializálása..."

# 1. S3 Bucket létrehozása a posztereknek
awslocal s3 mb s3://event-posters

# 2. DynamoDB tábla létrehozása a katalógusnak
awslocal dynamodb create-table \
    --table-name event \
    --attribute-definitions AttributeName=eventId,AttributeType=S \
    --key-schema AttributeName=eventId,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST

# 3. SNS Topic (Pub/Sub) a rendelés eseményeknek
awslocal sns create-topic --name order-events-topic

# 4. SQS Queue az értesítő szerviznek
awslocal sqs create-queue --queue-name notification-queue

# 5. SQS feliratkoztatása az SNS Topic-ra
TOPIC_ARN=$(awslocal sns list-topics --query "Topics[0].TopicArn" --output text)
QUEUE_ARN=$(awslocal sqs get-queue-attributes --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/notification-queue --attribute-names QueueArn --query "Attributes.QueueArn" --output text)

awslocal sns subscribe \
    --topic-arn $TOPIC_ARN \
    --protocol sqs \
    --notification-endpoint $QUEUE_ARN
    --attributes RawMessageDelivery=true

echo "✅ AWS erőforrások sikeresen létrehozva!"