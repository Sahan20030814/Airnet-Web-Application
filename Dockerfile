FROM tomcat:9.0-jdk8
COPY dist/Airnet.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080