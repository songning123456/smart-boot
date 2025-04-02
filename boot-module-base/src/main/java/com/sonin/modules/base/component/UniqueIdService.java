package com.sonin.modules.base.component;

import cn.hutool.core.lang.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author：sonin
 * @Date：2025/4/2 9:42
 */
@Component
public class UniqueIdService {

    private final Snowflake snowflake;

    @Autowired
    public UniqueIdService(Snowflake snowflake) {
        this.snowflake = snowflake;
    }

    public long nextId() {
        return snowflake.nextId();
    }

}
