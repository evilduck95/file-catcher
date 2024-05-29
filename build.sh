echo "Running with env file $1"
./gradlew clean bootJar
docker-compose --env-file "$1" up -d --force-recreate