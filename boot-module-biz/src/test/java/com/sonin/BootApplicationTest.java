package com.sonin;

import com.sonin.core.mpp.BaseFactory;
import com.sonin.modules.sys.entity.SysRole;
import com.sonin.modules.sys.entity.SysUserRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

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

    @Test
    public void testLambda() {
        List<Map<String, Object>> mapList = BaseFactory.JOIN()
                .select(SysRole::getId)
                .select(SysUserRole::getRoleId)
                .from(SysRole.class)
                .innerJoin(SysUserRole.class, SysUserRole::getRoleId, SysRole::getId)
                .where()
                .eq(true, SysUserRole::getRoleId, "074fb9ddf4f24478bf06da0f5619dc78")
                .eq(true, SysRole::getId, "074fb9ddf4f24478bf06da0f5619dc78")
                .queryForList();
        System.out.println(mapList);
    }

}
