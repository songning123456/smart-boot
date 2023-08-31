package com.sonin.modules.quartz.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.modules.base.service.IBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * 定时任务
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/6/9 18:13
 */
@EnableScheduling
@Service
@Slf4j
public class ScheduleJob {

    @Autowired
    private IBaseService baseService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * <pre>
     * 数据备份
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Scheduled(cron = "${biz.scheduled.backup}")
    public void backupJob() {
        log.info(">>> 开始执行{}定时任务 <<<", "数据备份");
        try {
            backupFunc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backupFunc() {
        QueryWrapper<?> queryWrapper = new QueryWrapper<>();
        List<String> reitIdList = new ArrayList<String>() {{
            add("1e8581b0f76f434f9ba64512f10c7ac5");
            add("b2779371e591463f9fe5bfab16059cd1");
            add("546aabdac5f040fba0c3c456b471aa4a");
            add("089413dad59f4006af425911c1df20a2");
        }};
        queryWrapper.eq("factory_id", "8023d91517a2443a87c7683545c17745")
                .in("reit_id", reitIdList);
        List<Map<String, Object>> queryMapList = baseService.queryForList("select * from f_report_itemv", queryWrapper);
        AtomicInteger insertCount = new AtomicInteger();
        AtomicInteger deleteCount = new AtomicInteger();
        transactionTemplate.execute(transactionStatus -> {
            insertCount.set(baseService.insertBatch("f_report_itemv_backup", queryMapList));
            deleteCount.set(baseService.delete("f_report_itemv", queryWrapper));
            return 1;
        });
        log.info("插入{}条, 删除{}条", insertCount.get(), deleteCount.get());
    }

}
