#!/bin/sh
#  hops.sh
#  
#
#  Created by Ron Huebler on 27.06.19.
#
PROG_PATH=${BASH_SOURCE[0]}      # this script's name
PROG_NAME=${PROG_PATH##*/}       # basename of script (strip path)
PROG_DIR="$(cd "$(dirname "${PROG_PATH:-$PWD}")" 2>/dev/null 1>&2 && pwd)"
parameters=""
while [ $# != "0" ] ; do
    parameters="$parameters $1"
    shift
done
java -jar $PROG_DIR/hops.jar $parameters
