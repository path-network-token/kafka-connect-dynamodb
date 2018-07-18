FROM confluentinc/cp-kafka-connect:latest

ADD kafka-connect-dynamodb-standalone.jar /etc/kafka-connect/jars

