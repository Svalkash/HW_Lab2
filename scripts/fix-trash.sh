#!/bin/bash

#Скрипт для исправления косяков HDFS, связанных с магией. Перезапускает гадость, форматирует датаноду и пересоздаёт папки.
/opt/hadoop-2.10.1/sbin/stop-all.sh
sudo rm -R /tmp/*
hdfs namenode -format
/opt/hadoop-2.10.1/sbin/start-all.sh
hdfs dfs -mkdir /user
hdfs dfs -mkdir /user/root