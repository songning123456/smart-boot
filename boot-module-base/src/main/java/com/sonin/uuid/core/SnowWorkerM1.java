package com.sonin.uuid.core;

import com.sonin.uuid.entity.IdGeneratorOptions;
import com.sonin.uuid.exception.IdGeneratorException;
import com.sonin.uuid.service.ISnowWorker;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/2 15:42
 */
public class SnowWorkerM1 implements ISnowWorker {

    /**
     * 基础时间
     */
    private long baseTime;

    /**
     * 机器码
     */
    short workerId;

    /**
     * 自增序列数位长
     */
    byte seqBitLength;

    /**
     * 最大序列数（含）
     */
    int maxSeqNumber;

    /**
     * 最小序列数（含）
     */
    short minSeqNumber;

    /**
     * 最大漂移次数（含）
     */
    private int topOverCostCount;

    byte timestampShift;

    final byte[] syncLock = new byte[0];

    short currentSeqNumber;

    long lastTimeTick = 0;

    private long turnBackTimeTick = 0;

    private byte turnBackIndex = 0;

    private boolean isOverCost = false;

    private int overCostCountInOneTerm = 0;

    private int termIndex = 0;

    public SnowWorkerM1(IdGeneratorOptions options) {
        baseTime = options.baseTime != 0 ? options.baseTime : 1582136402000L;
        // 机器码位长
        byte workerIdBitLength = options.workerIdBitLength == 0 ? 6 : options.workerIdBitLength;
        workerId = options.workerId;
        seqBitLength = options.seqBitLength == 0 ? 6 : options.seqBitLength;
        maxSeqNumber = options.maxSeqNumber <= 0 ? (1 << seqBitLength) - 1 : options.maxSeqNumber;
        minSeqNumber = options.minSeqNumber;
        topOverCostCount = options.topOverCostCount == 0 ? 2000 : options.topOverCostCount;
        timestampShift = (byte) (workerIdBitLength + seqBitLength);
        currentSeqNumber = minSeqNumber;
    }

    private void endOverCostAction() {
        if (termIndex > 10000) {
            termIndex = 0;
        }
    }

    private long nextOverCostId() {
        long currentTimeTick = getCurrentTimeTick();
        if (currentTimeTick > lastTimeTick) {
            endOverCostAction();
            lastTimeTick = currentTimeTick;
            currentSeqNumber = minSeqNumber;
            isOverCost = false;
            overCostCountInOneTerm = 0;
            return calcId(lastTimeTick);
        }

        if (overCostCountInOneTerm >= topOverCostCount) {
            endOverCostAction();
            lastTimeTick = getNextTimeTick();
            currentSeqNumber = minSeqNumber;
            isOverCost = false;
            overCostCountInOneTerm = 0;
            return calcId(lastTimeTick);
        }

        if (currentSeqNumber > maxSeqNumber) {
            lastTimeTick++;
            currentSeqNumber = minSeqNumber;
            isOverCost = true;
            overCostCountInOneTerm++;
            return calcId(lastTimeTick);
        }
        return calcId(lastTimeTick);
    }

    private long nextNormalId() throws IdGeneratorException {
        long currentTimeTick = getCurrentTimeTick();
        if (currentTimeTick < lastTimeTick) {
            if (turnBackTimeTick < 1) {
                turnBackTimeTick = lastTimeTick - 1;
                turnBackIndex++;
                // 每毫秒序列数的前5位是预留位，0用于手工新值，1-4是时间回拨次序
                // 支持4次回拨次序（避免回拨重叠导致ID重复），可无限次回拨（次序循环使用）。
                if (turnBackIndex > 4) {
                    turnBackIndex = 1;
                }
            }
            return calcTurnBackId(turnBackTimeTick);
        }

        // 时间追平时，turnBackTimeTick清零
        if (turnBackTimeTick > 0) {
            turnBackTimeTick = 0;
        }
        if (currentTimeTick > lastTimeTick) {
            lastTimeTick = currentTimeTick;
            currentSeqNumber = minSeqNumber;
            return calcId(lastTimeTick);
        }
        if (currentSeqNumber > maxSeqNumber) {
            termIndex++;
            lastTimeTick++;
            currentSeqNumber = minSeqNumber;
            isOverCost = true;
            overCostCountInOneTerm = 1;
            return calcId(lastTimeTick);
        }
        return calcId(lastTimeTick);
    }

    private long calcId(long useTimeTick) {
        long result = ((useTimeTick << timestampShift) + ((long) workerId << seqBitLength) + (int) currentSeqNumber);
        currentSeqNumber++;
        return result;
    }

    private long calcTurnBackId(long useTimeTick) {
        long result = ((useTimeTick << timestampShift) + ((long) workerId << seqBitLength) + turnBackIndex);
        turnBackTimeTick--;
        return result;
    }

    long getCurrentTimeTick() {
        long millis = System.currentTimeMillis();
        return millis - baseTime;
    }

    long getNextTimeTick() {
        long tempTimeTicker = getCurrentTimeTick();
        while (tempTimeTicker <= lastTimeTick) {
            tempTimeTicker = getCurrentTimeTick();
        }
        return tempTimeTicker;
    }

    @Override
    public long nextId() {
        synchronized (syncLock) {
            return isOverCost ? nextOverCostId() : nextNormalId();
        }
    }

}
