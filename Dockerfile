# Tomcat 10 සහ JDK 21 සහිත නිල Docker Image එක
FROM tomcat:10.1-jdk21

# ඔයාගේ war file එක Tomcat server එකට copy කිරීම
# 'Airnet.war' යනු ඔයාගේ war file එකේ නමයි
COPY dist/Airnet.war /usr/local/tomcat/webapps/ROOT.war

# Port 8080 විවෘත කිරීම
EXPOSE 8080

# Tomcat Server එක run කිරීම
CMD ["catalina.sh", "run"]