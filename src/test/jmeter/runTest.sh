#!/usr/bin/env bash

FILE=$(echo $1 | cut -d'.' -f 1)

# TODO: generate sample test data and store it in datastore

# TODO: overwrite instead of append
jmeter -n -t $1 -l ./results/"$FILE"_result.jtl -Lorg.apache.jmeter.protocol.http.control=DEBUG -j ./results/jmeter.log

# TODO: parse test result data

jmeter -g ./results/"$FILE"_result.jtl -o ./reports/"$FILE"/ -j ./results/jmeter-report.log
