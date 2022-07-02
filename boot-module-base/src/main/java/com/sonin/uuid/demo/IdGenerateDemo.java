package com.sonin.uuid.demo;

import com.sonin.uuid.entity.IdGeneratorOptions;
import com.sonin.uuid.enums.MethodEnum;
import com.sonin.uuid.helper.UniqueIdHelper;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/2 15:54
 */
public class IdGenerateDemo {

    public static void main(String[] args) throws InterruptedException {
        IdGeneratorOptions options = new IdGeneratorOptions();
        options.method = MethodEnum.DRIFT;
        options.baseTime = System.currentTimeMillis();
        options.workerId = 1;
        options.workerIdBitLength = 10;
        options.seqBitLength = 10;
        // UniqueIdHelper.setIdGenerator(options);
        while (true) {
            System.out.println(UniqueIdHelper.nextId());
            TimeUnit.SECONDS.sleep(1);
        }
    }

}
