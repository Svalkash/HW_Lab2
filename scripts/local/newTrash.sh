#!/bin/bash
python generate.py filein.txt
java -jar ../target/Lab2-1.0-SNAPSHOT-jar-with-dependencies.jar filein.txt output ../src/main/resources/typeID_example.csv
cat output/part-00000

wget https://dlcdn.apache.org/flume/1.9.0/apache-flume-1.9.0-bin.tar.gz
tar xvzf apache-flume-1.9.0-bin.tar.gz

export PATH=$PATH:/apache-flume-1.9.0-bin/bin

wget https://archive.apache.org/dist/spark/spark-2.3.1/spark-2.3.1-bin-hadoop2.7.tgz
tar xvzf spark-2.3.1-bin-hadoop2.7.tgz
export SPARK_HOME=./spark-2.3.1-bin-hadoop2.7
export HADOOP_CONF_DIR=/opt/hadoop-2.10.1/etc/hadoop
export PATH=$PATH:./spark-2.3.1-bin-hadoop2.7/bin


flume-ng agent -n FlumeAgent -c conf -f flume.conf
flume-ng agent --name a1 --conf flume.conf

flume-ng agent --name a1 --conf-file flume.conf


export HADOOP_CONF_DIR=$HADOOP_PREFIX/etc/hadoop
