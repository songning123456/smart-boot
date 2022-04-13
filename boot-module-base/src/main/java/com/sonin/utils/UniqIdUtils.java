package com.sonin.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author sonin
 * @date 2021/10/17 13:37
 * 32位整形的主键
 */
public class UniqIdUtils {

    private static final Log log = LogFactory.getLog(UniqIdUtils.class);

    private static UniqIdUtils uniqIdUtils = new UniqIdUtils();

    private String hostAddr;

    private final Random random = new SecureRandom();

    private final UniqTimer uniqTimer = new UniqTimer();

    private boolean isOutputInfo = false;

    private UniqIdUtils() {
        try {
            final InetAddress inetAddress = InetAddress.getLocalHost();
            hostAddr = inetAddress.getHostAddress();
        } catch (Exception e) {
            log.error("[UniqID] Get HostAddr Error: " + e);
            hostAddr = String.valueOf(System.currentTimeMillis());
        }
        if (null == hostAddr || hostAddr.trim().length() == 0 || "127.0.0.1".equals(hostAddr)) {
            hostAddr = String.valueOf(System.currentTimeMillis());
        }
        hostAddr = hostAddr.substring(hostAddr.length() - 2).replace(".", "0");
        if (isOutputInfo) {
            log.info("[UniqID]hostAddr is:" + hostAddr + "----length:" + hostAddr.length());
        }
    }

    /**
     * 获取UniqID实例
     *
     * @return UniqId
     */

    public static UniqIdUtils getInstance() {
        uniqIdUtils.isOutputInfo = false;
        return uniqIdUtils;
    }

    /**
     * 获得不会重复的毫秒数
     *
     * @return 不会重复的时间
     */
    private String getUniqTime() {
        String time = uniqTimer.getCurrentTime();
        if (isOutputInfo) {
            log.info("[UniqID.getUniqTime]" + time + "----length:" + time.length());
        }
        return time;
    }

    /**
     * 获得UniqId
     *
     * @return uniqTime-randomNum-hostAddr-threadId
     */
    public String getUniqID() {
        final StringBuffer stringBuffer = new StringBuffer();
        int randomNumber = random.nextInt(8999999) + 1000000;
        stringBuffer.append(getUniqTime());
        stringBuffer.append(hostAddr);
        stringBuffer.append(getUniqThreadCode());
        stringBuffer.append(randomNumber);
        if (isOutputInfo) {
            log.info("[UniqID.randomNumber]" + randomNumber + "----length:" + String.valueOf(randomNumber).length());
            log.info("[UniqID.getUniqID]" + stringBuffer.toString() + "----length:" + String.valueOf(stringBuffer).length());
        }
        return stringBuffer.toString();
    }

    private String getUniqThreadCode() {
        String threadCode = StringUtils.left(String.valueOf(Thread.currentThread().hashCode()), 9);
        if (isOutputInfo) {
            log.info("[UniqID.getUniqThreadCode]" + threadCode + "----length:" + threadCode.length());
        }
        return StringUtils.leftPad(threadCode, 9, "0");
    }

    /**
     * 实现不重复的时间
     */
    private class UniqTimer {

        private final AtomicLong lastTime = new AtomicLong(System.currentTimeMillis());

        String getCurrentTime() {
            if (!timestamp2Date(this.lastTime.incrementAndGet()).equals(timestamp2Date(System.currentTimeMillis()))) {
                lastTime.set(System.currentTimeMillis() + random.nextInt(10000));
            }
            return timestamp2DateTimes(this.lastTime.incrementAndGet());
        }

    }

    /**
     * 规范化日期，规范成yyyy-MM-dd
     *
     * @param timestamp
     * @return
     */
    private static String timestamp2Date(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date(timestamp * 1000));
    }

    private static String timestamp2DateTimes(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleDateFormat.format(new Date(timestamp));
    }


    /**
     * demo案例
     *
     * @param args
     */
    public static void main(String[] args) {
        String uid = UniqIdUtils.getInstance().getUniqID();
    }

}

