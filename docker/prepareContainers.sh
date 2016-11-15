#!/bin/sh
echo "preparing tomcat container"

cd ../ && mvn clean package -Dmaven.test.skip=true && cd - && cp ../target/LearningMongoDB.war tomcat/
cd tomcat/
docker build -t tomcat .
rm LearningMongoDB.war
cd ../

echo "preparing mongo container"
cd mongo/
pwd
unzip ../../primer-dataset.json.zip -d .

docker build -t mongo .
rm primer-dataset.json
cd ../
