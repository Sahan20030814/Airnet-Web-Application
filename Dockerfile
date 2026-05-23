# Payara (GlassFish) Java 8 හි අවසාන සහ ස්ථාවර සංස්කරණය
FROM payara/server-full:5.2022.5

# ඔයාගේ ව්‍යාපෘතියේ dist ෆෝල්ඩර් එකේ ඇති Airnet.war ෆයිල් එක සර්වර් එකට ලබාදීම
COPY dist/Airnet.war $DEPLOY_DIR/ROOT.war

# Render සර්වර් එකට Port එක හඳුන්වා දීම
EXPOSE 8080