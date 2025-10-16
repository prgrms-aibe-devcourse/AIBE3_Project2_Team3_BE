FROM amazoncorretto:21-alpine
WORKDIR /app

# Gradle 빌드 산출물 복사 (Maven이면 target/*.jar로 바꾸세요)
COPY build/libs/*.jar app.jar

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -Duser.timezone=Asia/Seoul"
EXPOSE 8080

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
