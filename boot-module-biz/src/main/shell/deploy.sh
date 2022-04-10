#java虚拟机启动参数
JAVA_OPTS=" -Xmx128m -Xms128m -server -D64 -XX:+UseG1GC "

JAVA_DEBUG="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=10073"
export JAVA_DEBUG

BASE_APP_DIR=/home/kingtrol/apps/flowable-design-gzcs
BASE_LOG_DIR=/home/kingtrol/logs/flowable-design-gzcs

# GC日志
LOG_DIR=$BASE_LOG_DIR
LogFilePath=`pwd`/logback-spring.xml
JAVA_GC_PRINT="-XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationConcurrentTime -XX:+PrintHeapAtGC -XX:+UseGCLogFileRotation -XX:+HeapDumpOnOutOfMemoryError -Xloggc:$LOG_DIR/gc.log -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=5M -XX:HeapDumpPath=$LOG_DIR/HeapDumpOnOutOfMemoryError/ -Dlogging.config=$LogFilePath "
 
#需要启动的jar包
APP=Flowable-Design.jar

#启动参数
ARGS=""

#进行jar包路径添加
if test -n "$2"
  then APP=$2
fi

#进行启动参数添加
if test -n "$3"
  then ARGS=$3
fi

echo "The path of jar is $APP"
echo "The args is $ARGS"

#初始化psid
psid=0

checkpid() {
  javaps=`jps -l | grep $BASE_APP_DIR/$APP`
  
  if [ -n "$javaps" ]; then
     psid=`echo $javaps | awk '{print $1}'`
  else
     psid=0
  fi
}

start() {
  checkpid
  
  if [ $psid -ne 0 ]; then
    echo "================================================"
    echo "info: $APP already started! pid=$psid"
    echo "================================================"
  else
    echo -n "Starting $APP ..."
    `nohup java $JAVA_OPTS $JAVA_GC_PRINT -jar $BASE_APP_DIR/$APP $ARGS > $BASE_LOG_DIR/out.log 2>&1 &`
    checkpid
    if [ $psid -ne 0 ]; then
      echo "success! pid=$psid [OK]"
    else
      echo "[Failed]"
    fi
  fi
}

stop() {
  checkpid
  if [ $psid -ne 0 ]; then
    echo "info: Stoping $APP... pid=$psid"
    `kill -15 $psid`
    if [ $? -eq 0 ]; then
       echo "[OK]"
    else
       echo "[Failed]"
    fi
    
    checkpid
    if [ $psid -ne 0 ]; then
       stop
    fi
  else
    echo "================================================"
    echo "info: $APP already stoped!"
    echo "================================================"
  fi
}

case "$1" in
  'start')
    start
    ;;
  'stop')
    stop
    ;;
  'restart')
    stop
    start
    ;;
  *)
echo "help: $0 {start|stop|restart}"
echo "例子: ./deploy.sh start $APP"
exit 1
esac
exit 0
