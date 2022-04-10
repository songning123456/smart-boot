#!/bin/bash

#
# Copyright (c) 2001-2019 kingtroldata, Ltd.
# All rights reserved.
#
# author: liyuanyuan (mailto:yuanyuan.li@kingtroldata.com)
#

## resolve links
PRG="$0"

# need this for relative symlinks
while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG="`dirname "$PRG"`/$link"
  fi
done

saveddir=`pwd`

SMARTBOOT_COMPONENT_HOME=`dirname "$PRG"`/..

# make it fully qualified
SMARTBOOT_COMPONENT_HOME=`cd "$SMARTBOOT_COMPONENT_HOME" && pwd`

cd "$saveddir"


SERVICE_NAME=smart-boot

export MODE=service
export PID_FOLDER="${SMARTBOOT_COMPONENT_HOME}"/logs
export LOG_FOLDER="${SMARTBOOT_COMPONENT_HOME}"/logs
export identity=${SERVICE_NAME}
# export CONF_FOLDER="${SMARTBOOT_COMPONENT_HOME}"/conf

# If exists private jdk
if [ -d "${SMARTBOOT_COMPONENT_HOME}/jdk" ] || [ -h "${SMARTBOOT_COMPONENT_HOME}/jdk" ] || [ -L "${SMARTBOOT_COMPONENT_HOME}/jdk" ]; then
	export JAVA_HOME="${SMARTBOOT_COMPONENT_HOME}/jdk"
fi

BOOT_JAR=`echo "${SMARTBOOT_COMPONENT_HOME}"/*.jar`
if [ ! -x "${BOOT_JAR}" ]; then
  chmod + "${BOOT_JAR}"
fi

$BOOT_JAR stop "$@"



