# ***项目需要修改的参数
# 1. 文件目录(此处必须修改, 执行脚本时参数可以不写)
BASE_APP_DIR=/home/cloud/apps/smart-boot
# 2. 需要启动的jar包(此处必须修改, 执行脚本时参数可以不写)
APP=$BASE_APP_DIR/smart-boot.jar
# 3. 启动参数(此处可以不写, 执行脚本时参数也可以不写)
ARGS=""
# 4. 日志文件所在位置(此处可以不修改, 默认)
BASE_LOG_DIR=$BASE_APP_DIR/logs
# 5. 是否开启远程调试(默认false)
JAVA_DEBUG_ENABLE=false
# 6. 远程调试端口(此处可以不修改, 默认)
JAVA_DEBUG_PORT=8787

# 参数2: 进行jar包全路径添加, e.g: /home/.../smart-boot.jar
if test -n "$2"
  then APP=$2
fi
echo "The path of jar is $APP"

# 参数3: 进行启动参数添加
if test -n "$3"
  then ARGS=$3
fi
echo "The args is $ARGS"

# 创建日志文件夹及堆内存溢出文件夹
mkdir -p $BASE_LOG_DIR/HeapDumpOnOutOfMemoryError
# 创建Tomcat临时缓存目录
mkdir -p $BASE_LOG_DIR/tmp

# 判断是否开启远程调试
if [ 'trueX' == "${JAVA_DEBUG_ENABLE}X" ]; then
  JAVA_OPTS="${JAVA_OPTS} -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=${JAVA_DEBUG_PORT}"
fi

# ***java虚拟机启动参数
# 仅支持IP4
JAVA_OPTS="-Djava.net.preferIPv4Stack=true"
# 北京时间
JAVA_OPTS="$JAVA_OPTS -Duser.timezone=Asia/Beijing"
# UTF-8编码
JAVA_OPTS="$JAVA_OPTS -Dclient.encoding.override=UTF-8"
# 强行设置系统文件编码格式为utf-8
JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8"
# 加快随机数产生过程
JAVA_OPTS="$JAVA_OPTS -Djava.security.egd=file:/dev/./urandom "
# 最大堆、最小堆
JAVA_OPTS="$JAVA_OPTS -Xmx128m -Xms128m"
# 服务端
JAVA_OPTS="$JAVA_OPTS -server"
# 64位环境
JAVA_OPTS="$JAVA_OPTS -D64"
# 使用G1垃圾回收器
JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC"
# 打印GC日志信息
JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationConcurrentTime -XX:+PrintHeapAtGC -XX:+UseGCLogFileRotation -Xloggc:$BASE_LOG_DIR/gc.log"
# 打印堆溢出日志信息
JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$BASE_LOG_DIR/HeapDumpOnOutOfMemoryError/"
# 强制JVM始终抛出含堆栈的异常
JAVA_OPTS="$JAVA_OPTS -XX:-OmitStackTraceInFastThrow"
# 日志打印路径
JAVA_OPTS="$JAVA_OPTS -Dlog.path=$BASE_LOG_DIR"
# Tomcat临时缓存目录
JAVA_OPTS="$JAVA_OPTS -Dserver.tomcat.basedir=$BASE_LOG_DIR/tmp"

# 初始化psid
psid=0

# 检测PID
checkpid() {
  javaps=`jps -l | grep $APP`
  if [ -n "$javaps" ]; then
     psid=`echo $javaps | awk '{print $1}'`
  else
     psid=0
  fi
}

# 启动
start() {
  checkpid
  if [ $psid -ne 0 ]; then
    echo "================================================"
    echo "info: $APP already started! pid=$psid"
    echo "================================================"
  else
    echo -n "Starting $APP ..."
    `nohup java $JAVA_OPTS -jar $APP $ARGS > $BASE_LOG_DIR/out.log 2>&1 &`
    checkpid
    if [ $psid -ne 0 ]; then
      echo "success! pid=$psid [OK]"
    else
      echo "[Failed]"
    fi
  fi
}

# 结束
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
