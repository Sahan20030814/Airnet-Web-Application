# Payara (GlassFish) Java 8 සංස්කරණය ලබාගැනීම
FROM payara/server-full:5-jdk8

# ඔයාගේ ව්‍යාපෘතියේ dist ෆෝල්ඩර් එකේ ඇති Airnet.war ෆයිල් එක සර්වර් එකට ලබාදීම
# මෙහිදී ROOT.war ලෙස නම වෙනස් කිරීමෙන් API ලින්ක් ගැටලුව විසඳේ
COPY dist/Airnet.war $DEPLOY_DIR/ROOT.war

# Render සර්වර් එකට Port එක හඳුන්වා දීම (අත්‍යවශ්‍යයි)
EXPOSE 8080