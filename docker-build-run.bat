@echo off
set IMAGE_NAME=westudy-app
set CONTAINER_NAME=westudy-container

echo =====================================
echo Container stop and remove...
echo =====================================


docker stop %CONTAINER_NAME%
docker rm %CONTAINER_NAME%

echo =====================================
echo Building project with Gradle...
echo =====================================

call gradlew.bat build

echo =====================================
echo Docker image build...
echo =====================================

docker build -t %IMAGE_NAME% .

echo =====================================
echo Docker container run...
echo =====================================

docker run -d -p 8080:8080 --name %CONTAINER_NAME% %IMAGE_NAME%

echo =====================================
echo Success! Open: http://localhost:8080
echo =====================================

pause
