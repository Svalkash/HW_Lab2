#!/bin/bash
export PATH=$PATH:/usr/local/hadoop/bin/

export SPARK_HOME=/spark-2.3.1-bin-hadoop2.7
export HADOOP_CONF_DIR=$HADOOP_PREFIX/etc/hadoop
export PATH=$PATH:/spark-2.3.1-bin-hadoop2.7/bin

hdfs dfs -rm -r output
#hdfs dfs -rm -r typeID.csv
#hdfs dfs -put ../src/main/resources/typeID_example.csv typeID.csv

spark-submit --class bdtc.Lab2.SparkApplication --master local --deploy-mode client --executor-memory 256m --name actioncount --conf "spark.app.id=SparkApplication" ./Lab2-1.0-SNAPSHOT-jar-with-dependencies.jar input output
hdfs dfs -cat hdfs://127.0.0.1:9000/user/root/output/part-00000