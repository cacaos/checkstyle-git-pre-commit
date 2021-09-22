#!/bin/sh

echo "Generate uber jar (checkstyle-all-X.XX.jar)"
mvn clean
mvn -P assembly package
