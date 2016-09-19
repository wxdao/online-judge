#! /bin/bash

chmod chmod -R 777 .
echo -n $2 > meta/message
echo -n $1 > meta/result
exit
