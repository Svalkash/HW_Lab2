#!/usr/bin/env bash
if [[ $# -eq 0 ]] ; then
    echo 'You should specify container name!'
    exit 1
fi

echo "Creating .jar file..."
mvn package -f ../pom.xml #compile my trash

docker cp ../target/Lab2-1.0-SNAPSHOT-jar-with-dependencies.jar "$1":/ #copy it
docker cp docker/. "$1":/ #copy it