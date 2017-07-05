# Confluent Kafka-streams demo using fabric8
This is a test of implementing Confluents official kafka-streams-demo using fabric8
## Getting it up and running
Clone the repository:
```
git clone https://github.com/jiinxx/confluent-kafka-streams-demo-fabric8.git
```
Jump into project folder and build the workspace using:
```
mvn clean install
mvn docker:build
```
Run the build using:
```
mvn docker:start
```
and stop with:
```
mvn docker:stop
