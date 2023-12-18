package com.sonin;

import com.sonin.core.constant.BaseConstant;
import com.sonin.modules.schedule.serivce.ICustomBusiness;
import com.sonin.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

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
    private ICustomBusiness customBusiness;

    @Test
    public void demoTest() {
        String startTime = DateUtils.date2Str(DateUtils.prevHour(new Date()), BaseConstant.dateFormat);
        customBusiness.generateHourDataFunc(startTime, startTime);
    }

}
