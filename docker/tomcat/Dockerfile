FROM tomcat:8.5

COPY LearningMongoDB.war $CATALINA_HOME/webapps/

WORKDIR /$CATALINA_HOME/webapps/
RUN mv ROOT ROOT_TOMCAT
RUN unzip LearningMongoDB.war -d ROOT

EXPOSE 8080
CMD ["catalina.sh", "run"]
