#! /bin/bash

echo -n "jack" > meta/input.txt
echo -n "I'm jack" > meta/expect.txt
cat meta/input.txt | su judge -c "perl ./memlimit -c -m 10240 timeout 5 meta/app" 1> meta/output.txt 2> meta/output.err
ERR=$?
if [ $ERR -ge 120 ]; then
	cat meta/output.err | grep MEM
	if [ $? == 0 ]; then
		source result.sh MLE
	else
		source result.sh TLE
	fi
elif [ $ERR != 0 ]; then
	source result.sh RE
else
	cmp --silent meta/output.txt meta/expect.txt
	if [ $? == 0 ]; then
		source result.sh AC "Very good!"
	else
		source result.sh WA	"Keep going!"
	fi
fi

