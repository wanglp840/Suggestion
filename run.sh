#!/bin/bash
mkdir -p /tmp/suggestion/
export MAVEN_OPTS="-Xms400m -Xmx400m"
mvn clean clean
mvn jetty:run
