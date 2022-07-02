package com.sonin.uuid.service.impl;

import com.sonin.uuid.core.SnowWorkerM1;
import com.sonin.uuid.core.SnowWorkerM2;
import com.sonin.uuid.entity.IdGeneratorOptions;
import com.sonin.uuid.enums.MethodEnum;
import com.sonin.uuid.exception.IdGeneratorException;
import com.sonin.uuid.service.IIdGenerator;
import com.sonin.uuid.service.ISnowWorker;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/2 15:49
 */
public class DefaultIdGenerator implements IIdGenerator {

    private static ISnowWorker snowWorker = null;

    public DefaultIdGenerator(IdGeneratorOptions options) throws IdGeneratorException {
        if (options == null) {
            throw new IdGeneratorException("options error.");
        }

        // 1. baseTime
        if (options.baseTime < 315504000000L || options.baseTime > System.currentTimeMillis()) {
            throw new IdGeneratorException("baseTime error.");
        }

        // 2. workerIdBitLength
        if (options.workerIdBitLength <= 0) {
            throw new IdGeneratorException("WorkerIdBitLength error.(range:[1, 21])");
        }
        if (options.workerIdBitLength + options.seqBitLength > 22) {
            throw new IdGeneratorException("error：WorkerIdBitLength + SeqBitLength <= 22");
        }

        // 3. workerId
        int maxWorkerIdNumber = (1 << options.workerIdBitLength) - 1;
        if (maxWorkerIdNumber == 0) {
            maxWorkerIdNumber = 63;
        }
        if (options.workerId < 0 || options.workerId > maxWorkerIdNumber) {
            throw new IdGeneratorException("workerId error. (range:[0, " + (maxWorkerIdNumber > 0 ? maxWorkerIdNumber : 63) + "]");
        }

        // 4. seqBitLength
        if (options.seqBitLength < 2) {
            throw new IdGeneratorException("SeqBitLength error. (range:[2, 21])");
        }

        // 5.maxSeqNumber
        int maxSeqNumber = (1 << options.seqBitLength) - 1;
        if (maxSeqNumber == 0) {
            maxSeqNumber = 63;
        }
        if (options.maxSeqNumber < 0 || options.maxSeqNumber > maxSeqNumber) {
            throw new IdGeneratorException("MaxSeqNumber error. (range:[1, " + maxSeqNumber + "]");
        }

        // 6.minSeqNumber
        if (options.minSeqNumber < 5 || options.minSeqNumber > maxSeqNumber) {
            throw new IdGeneratorException("MinSeqNumber error. (range:[5, " + maxSeqNumber + "]");
        }

        // 雪花算法
        if (options.method == MethodEnum.DRIFT) {
            snowWorker = new SnowWorkerM1(options);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // 传统算法
            snowWorker = new SnowWorkerM2(options);
        }

    }

    @Override
    public long newLong() {
        return snowWorker.nextId();
    }

}
