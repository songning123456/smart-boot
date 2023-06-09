package com.sonin.modules.init;

import com.sonin.core.javassist.JavassistFactory;
import com.sonin.modules.xsinsert.entity.Xsinsert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/6/9 18:26
 */
@Slf4j
@Component
public class InitApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        initClassFunc();
    }

    private void initClassFunc() {
        log.info(">>> 初始化基础类 <<<");
        try {
            JavassistFactory.create(Xsinsert.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
