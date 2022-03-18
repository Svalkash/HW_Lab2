#!/bin/bash
hdfs dfs -rm -r output
#hdfs dfs -rm -r typeID.csv
#hdfs dfs -put filein.txt input/file1.txt
#hdfs dfs -put ../src/main/resources/typeID_example.csv typeID.csv

export SPARK_HOME=./spark-2.3.1-bin-hadoop2.7
export HADOOP_CONF_DIR=/opt/hadoop-2.10.1/etc/hadoop
export PATH=$PATH:./spark-2.3.1-bin-hadoop2.7/bin

hdfs dfs -rm -r output
spark-submit --class bdtc.Lab2.SparkApplication --master local --deploy-mode client --executor-memory 256m --name actioncount --conf "spark.app.id=SparkApplication" ../target/Lab2-1.0-SNAPSHOT-jar-with-dependencies.jar input output
hdfs dfs -cat hdfs://127.0.0.1:9000/user/root/output/part-00000

hdfs://127.0.0.1:9000/user/root/typeID.csv

spark-submit --class bdtc.lab2.SparkSQLApplication --master local --deploy-mode client --executor-memory 1g --name

yarn jar ../target/Lab2-1.0-SNAPSHOT-jar-with-dependencies.jar filein.txt output hdfs://127.0.0.1:9000/user/root/typeID.csv
hdfs dfs -get output output
cat output/part-00000


yarn jar ./target/L1App-jar-with-dependencies.jar input output metricNames.csv 1m avg
hdfs dfs -get output output
java -jar ./target/L1Reader-jar-with-dependencies.jar ./output/part-r-00000 output-decoded.txt
cat output-decoded.txt
#java -jar ./target/L1App-jar-with-dependencies.jar input output metricNames.csv 1m avg
#java -jar ./target/L1Reader-jar-with-dependencies.jar ./output/part-r-00000 output-decoded.txt

spark-submit --class bdtc.lab2.SparkApplication --master local --deploy-mode client --executor-memory 256m --name actioncount --conf "spark.app.id=SparkApplication" ../target/Lab2-1.0-SNAPSHOT-jar-with-dependencies.jar hdfs://127.0.0.1:9000/user/root/logs/ filein.txtout
