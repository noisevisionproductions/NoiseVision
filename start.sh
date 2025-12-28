#!/bin/sh
# Uruchamianie Javy z odpowiednimi opcjami
java -Dserver.port=${PORT} \
     -XX:+UseContainerSupport \
     -XX:MaxRAMPercentage=80.0 \
     -XX:+UseG1GC \
     -jar app.jar