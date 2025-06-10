# 1단계: Build Stage
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# 전체 프로젝트 복사 (이게 가장 안전)
COPY . .

# 빌드 실행
RUN ./gradlew clean build -x test --no-daemon

# 2단계: Runtime Stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# 빌드 결과물만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["sh", "-c", "sleep 5 && java -jar app.jar --spring.profiles.active=docker"]
