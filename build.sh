./gradlew clean bootJar
docker-compose --env-file /home/franky/environment/file-catcher.env up --build -d --force-recreate