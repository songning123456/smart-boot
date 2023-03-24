package com.sonin.modules.data.service.impl;

import com.sonin.modules.data.entity.DataLog;
import com.sonin.modules.data.mapper.DataLogMapper;
import com.sonin.modules.data.service.DataLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 数据日志 服务实现类
 * </p>
 *
 * @author sonin
 * @since 2023-03-24
 */
@Service
public class DataLogServiceImpl extends ServiceImpl<DataLogMapper, DataLog> implements DataLogService {

}
