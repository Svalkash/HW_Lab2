#!/bin/bash
export PATH=$PATH:/usr/local/hadoop/bin/
export PATH=$PATH:/apache-flume-1.9.0-bin/bin

hdfs dfs -rm -r input/*
hdfs dfs -mkdir input
flume-ng agent --name a1 --conf-file flume.conf

