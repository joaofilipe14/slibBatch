#!/bin/bash

cd /home/data{{shared.env.code}}/slib-rms-analytics-batch/ || exit 1

if [ -e *archive.zip ]; then
  setup_bash=(*setup.bash)
  bash "$setup_bash" reinstall
fi
