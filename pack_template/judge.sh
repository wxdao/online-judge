#! /bin/bash

#memory limit in kilobytes
MEM_LIMIT=1
#time limit in seconds
TIME_LIMIT=0.1

for i in input*.txt; do
	cp "$i" meta/input.txt
	cp "expect${i#input}" meta/expect.txt
	cat meta/input.txt | su judge -c "perl ./memlimit -c -m ${MEM_LIMIT} -t ${TIME_LIMIT} meta/app" 1> meta/output.txt 2> meta/output.err
	ERR=$?
	if [ $ERR == 233 ]; then
		source result.sh TLE
	elif [ $ERR == 234 ]; then
		source result.sh MLE
	elif [ $ERR != 0 ]; then
		source result.sh RE
	else
		diff --ignore-space-change meta/output.txt meta/expect.txt
		if [ $? != 0 ]; then
			source result.sh WA "Keep going!"
		fi
	fi
done

source result.sh AC "cnss{good_you_got_an_AC}"

