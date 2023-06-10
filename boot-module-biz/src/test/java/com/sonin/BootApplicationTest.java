package com.sonin;

import com.sonin.modules.base.service.IBaseService;
import com.sonin.modules.xsinsert.quartz.job.ScheduleJob;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * <pre>
 * Spring Test
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/25 16:30
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class BootApplicationTest {

    @Autowired
    private IBaseService baseService;
    @Autowired
    private ScheduleJob scheduleJob;


    @Test
    public void test() {
    }

    @Test
    public void createTableTest() {
        scheduleJob.createTableFunc();
        scheduleJob.deleteTableFunc();
    }

}
