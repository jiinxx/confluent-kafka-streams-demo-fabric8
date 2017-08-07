#!/bin/bash

set -e
# debug
#set -eux

function check_env_variables () {

   if [[ ( -z "KAFKA_CONNECT" ) ]]; then
       echo "KAFKA_CONNECT must be configured"
       exit 1
   fi

   if [[ ( -z "ZK_CONNECT"  ) ]]; then
       echo "ZK_CONNECT must be configured"
       exit 1
   fi

   echo "GENERATOR: Running with 'KAFKA_CONNECT' $KAFKA_CONNECT, 'ZK_CONNECT' $ZK_CONNECT"

}

cub sr-ready schema-registry 8081 30
if [ "$1" = 'generator' ]; then
  # check_env_variables
  java -jar /opt/generator/generator.jar kafka:9092 http://schema-registry:8081
#  tail -f /dev/null
else
    exec "$@"
fi