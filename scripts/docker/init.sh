#!/bin/bash

#Скачиваем Python - для генератора
sudo apt-get update -y
sudo apt-get install -y python

# Скачиваем Spark
if [ ! -f spark-2.3.1-bin-hadoop2.7.tgz ]; then
    wget https://archive.apache.org/dist/spark/spark-2.3.1/spark-2.3.1-bin-hadoop2.7.tgz
    tar xvzf spark-2.3.1-bin-hadoop2.7.tgz
else
    echo "Spark already exists, skipping..."
fi

# Скачиваем Flume
if [ ! -f apache-flume-1.9.0-bin.tar.gz ]; then
    wget --no-check-certificate https://dlcdn.apache.org/flume/1.9.0/apache-flume-1.9.0-bin.tar.gz
    tar xvzf apache-flume-1.9.0-bin.tar.gz
else
    echo "Flume already exists, skipping..."
fi