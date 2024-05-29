./gradlew clean bootJar
docker-compose --env-file "$1" up -d --force-recreate