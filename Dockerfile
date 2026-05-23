# සැහැල්ලු Tomcat 9 සංස්කරණය (Java 8 සමඟ)
FROM tomcat:9.0-jdk8

# ඔයාගේ war ෆයිල් එක Tomcat හි webapps ෆෝල්ඩර් එකට දැමීම
# නම 'ROOT.war' ලෙස වෙනස් කිරීමෙන් API ලින්ක් ගැටලුව සම්පූර්ණයෙන්ම විසඳේ
COPY dist/Airnet.war /usr/local/tomcat/webapps/ROOT.war

# Render සර්වර් එකට Port එක හඳුන්වා දීම
EXPOSE 8080