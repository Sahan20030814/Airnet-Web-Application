# Java 11 සමඟ ක්‍රියාත්මක වන Tomcat 9 සංස්කරණය
FROM tomcat:9.0-jdk11

# ඔයාගේ war ෆයිල් එක ROOT.war ලෙස සර්වර් එකට දැමීම
COPY dist/Airnet.war /usr/local/tomcat/webapps/ROOT.war

# Port 8080 විවෘත කිරීම
EXPOSE 8080