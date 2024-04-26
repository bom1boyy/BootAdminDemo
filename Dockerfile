# 베이스 이미지 설정
FROM openjdk:21-jdk

# 작업 디렉토리 설정
WORKDIR /app

# 호스트 머신에서 빌드된 JAR 파일을 현재 디렉토리의 /app 디렉토리로 복사 ( 어플리케이션명은 빌드된 파일 이름으로 변경 )
COPY ./build/libs/BootAdminDemo-0.0.1-SNAPSHOT.jar /app/BootAdmin-1.0.jar

# 컨테이너가 실행될 때 실행될 명령어 설정
ENTRYPOINT ["java", "-jar", "BootAdmin-1.0.jar"]
