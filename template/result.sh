#! /bin/bash

echo -n $1 > meta/result
echo -n $2 > meta/result.msg
chmod 777 meta/*
exit
