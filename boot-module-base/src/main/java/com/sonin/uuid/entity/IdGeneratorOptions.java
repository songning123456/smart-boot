package com.sonin.uuid.entity;

import com.sonin.uuid.enums.MethodEnum;

/**
 * <pre>
 * 雪花算法使用的参数
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/2 15:38
 */
public class IdGeneratorOptions {


    /**
     * 雪花计算方法
     * （漂移算法|传统算法），默认 漂移
     */
    public MethodEnum method = MethodEnum.DRIFT;

    /**
     * 基础时间（ms单位）
     * 不能超过当前系统时间
     */
    public long baseTime = 1582136402000L;

    /**
     * 机器码
     * 必须由外部设定，最大值 2^WorkerIdBitLength-1
     */
    public short workerId = 0;

    /**
     * 机器码位长
     * 默认值6，取值范围 [1, 15]（要求：序列数位长+机器码位长不超过22）
     */
    public byte workerIdBitLength = 6;

    /**
     * 序列数位长
     * 默认值6，取值范围 [3, 21]（要求：序列数位长+机器码位长不超过22）
     */
    public byte seqBitLength = 6;

    /**
     * 最大序列数（含）
     * 设置范围 [MinSeqNumber, 2^SeqBitLength-1]，默认值0，表示最大序列数取最大值（2^SeqBitLength-1]）
     */
    public short maxSeqNumber = 0;

    /**
     * 最小序列数（含）
     * 默认值5，取值范围 [5, MaxSeqNumber]，每毫秒的前5个序列数对应编号是0-4是保留位，其中1-4是时间回拨相应预留位，0是手工新值预留位
     */
    public short minSeqNumber = 5;

    /**
     * 最大漂移次数（含）
     * 默认2000，推荐范围500-10000（与计算能力有关）
     */
    public short topOverCostCount = 2000;

    public IdGeneratorOptions() {
    }

    public IdGeneratorOptions(short workerId) {
        this.workerId = workerId;
    }

}
