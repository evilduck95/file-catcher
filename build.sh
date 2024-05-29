./gradlew clean bootJar
docker build -t file-catcher .
docker tag file-catcher evilduck95.net/file-catcher
docker push evilduck95.net/file-catcher