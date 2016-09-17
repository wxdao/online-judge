#! /bin/bash

echo -n $1 > meta/result
echo -n $2 > meta/message
chmod 777 meta/*
exit
