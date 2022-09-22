#!/usr/bin/env bash

(cd sls; yarn; AWS_PROFILE=aws-workout yarn build)

