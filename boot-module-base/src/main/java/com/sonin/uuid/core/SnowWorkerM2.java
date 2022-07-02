
package com.sonin.uuid.core;

import com.sonin.uuid.entity.IdGeneratorOptions;
import com.sonin.uuid.exception.IdGeneratorException;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/2 15:42
 */
public class SnowWorkerM2 extends SnowWorkerM1 {

    public SnowWorkerM2(IdGeneratorOptions options) {
        super(options);
    }

    @Override
    public long nextId() {
        synchronized (syncLock) {
            long currentTimeTick = getCurrentTimeTick();
            if (lastTimeTick == currentTimeTick) {
                if (currentSeqNumber++ > maxSeqNumber) {
                    currentSeqNumber = minSeqNumber;
                    currentTimeTick = getNextTimeTick();
                }
            } else {
                currentSeqNumber = minSeqNumber;
            }
            if (currentTimeTick < lastTimeTick) {
                throw new IdGeneratorException("Time error for {0} milliseconds", lastTimeTick - currentTimeTick);
            }
            lastTimeTick = currentTimeTick;
            return ((currentTimeTick << timestampShift) + ((long) workerId << seqBitLength) + (int) currentSeqNumber);
        }
    }

}
