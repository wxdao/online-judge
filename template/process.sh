#! /bin/bash

source build.sh
if [ ${COMPILER_ERR} == 0 ]; then
	source judge.sh
else
	source result.sh CE
fi
