#!/usr/bin/env bash
if [ -z "$1" ] ; then
  echo "Usage: ./run-test.sh <path-to-tutorial> E.g.: ./run-test.sh ./1-networking/101-basic-vpc/TEST-display-vpc-created.sh" && exit 1;
fi

source ./backend-env.conf
source "$1"
