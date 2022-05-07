# ***项目需要修改的参数
# 启动堆大小
HEAP_MIN=1024m
HEAP_MAX=1024m
# 是否开启远程调试(默认false)
JAVA_DEBUG_ENABLE=false
# 远程调试端口
JAVA_DEBUG_PORT=8787


# 执行脚本的绝对路径
SHELL_DIR=$(cd $(dirname "${BASH_SOURCE[0]}");pwd)
# 部署路径，删除执行脚本绝对路径的'/bin'
BOOT_JAR_DIR=${SHELL_DIR%/bin*}
# 启动的jar包全路径/*.jar，此路径下只能存在一个jar包
BOOT_JAR=`echo "${BOOT_JAR_DIR}"/*.jar`
# 日志文件所在位置
JAR_LOG_DIR=$BOOT_JAR_DIR/logs
# 使用config文件夹下的logback-spring.xml文件
LOGGING_CONFIG=$BOOT_JAR_DIR/config/logback-spring.xml

# 启动时要先进入到目录中，否则读取不到config文件夹下的配置文件
cd $BOOT_JAR_DIR
# 创建日志文件夹及堆内存溢出文件夹
if [ ! -d "$JAR_LOG_DIR/HeapDumpOnOutOfMemoryError" ]; then
 mkdir -p $JAR_LOG_DIR/HeapDumpOnOutOfMemoryError
fi
# 创建Tomcat临时缓存目录
if [ ! -d "$JAR_LOG_DIR/tmp" ]; then
 mkdir -p $JAR_LOG_DIR/tmp
fi
# 创建日志文件夹及GC日志文件夹
if [ ! -d "$JAR_LOG_DIR/gc" ]; then
 mkdir -p $JAR_LOG_DIR/gc
fi
# config文件夹授权
if [ -d "$BOOT_JAR_DIR/config" ]; then
 chmod -R 755 $BOOT_JAR_DIR/config
fi
# jar包授权
chmod -R 755 $BOOT_JAR

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
JAVA_OPTS="$JAVA_OPTS -Djava.security.egd=file:/dev/./urandom"
# 最大堆、最小堆
JAVA_OPTS="$JAVA_OPTS -Xmx${HEAP_MAX} -Xms${HEAP_MIN}"
# 服务端
JAVA_OPTS="$JAVA_OPTS -server"
# 64位环境
JAVA_OPTS="$JAVA_OPTS -D64"
# 使用G1垃圾回收器
JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC"
# 打印GC日志信息
JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationConcurrentTime -XX:+PrintHeapAtGC -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=5M -Xloggc:$JAR_LOG_DIR/gc/gc.log"
# 打印堆溢出日志信息
JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$JAR_LOG_DIR/HeapDumpOnOutOfMemoryError/"
# 强制JVM始终抛出含堆栈的异常
JAVA_OPTS="$JAVA_OPTS -XX:-OmitStackTraceInFastThrow"
# 日志打印路径
JAVA_OPTS="$JAVA_OPTS -Dlog.path=$JAR_LOG_DIR"
# Tomcat临时缓存目录
JAVA_OPTS="$JAVA_OPTS -Dserver.tomcat.basedir=$JAR_LOG_DIR/tmp"
# 判断是否开启远程调试
if [ 'trueX' == "${JAVA_DEBUG_ENABLE}X" ]; then
  JAVA_OPTS="${JAVA_OPTS} -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=${JAVA_DEBUG_PORT}"
fi
# 使用指定位置config的日志配置文件
if [ -f "$LOGGING_CONFIG" ]; then
  JAVA_OPTS="$JAVA_OPTS -Dlogging.config=$LOGGING_CONFIG"
fi

# 初始化psid
psid=0

# 检测PID
checkpid() {
  javaps=`jps -l | grep $BOOT_JAR`
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
    echo "info: $BOOT_JAR already started! pid=$psid"
    echo "================================================"
  else
    echo -n "Starting $BOOT_JAR ..."
    `nohup java $JAVA_OPTS -jar $BOOT_JAR > $JAR_LOG_DIR/out.log 2>&1 &`
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
    echo "info: Stoping $BOOT_JAR... pid=$psid"
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
    echo "info: $BOOT_JAR already stoped!"
    echo "================================================"
  fi
}

# 查看运行状态
status() {
  checkpid
  if [ $psid -ne 0 ]; then
    echo "info: $BOOT_JAR is running: pid=$psid"
  else
    echo "info: $BOOT_JAR is not running!"
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
  'status')
    status
    ;;
  *)
   echo "例子: sh deploy.sh {start|stop|restart|status}"
exit 1
esac
exit 0
