package com.sonin.modules.sequence.service.impl;

import com.sonin.modules.sequence.entity.Sequence;
import com.sonin.modules.sequence.service.ISequenceService;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/2/24 9:40
 */
@Service("sequenceService")
public class SequenceService implements ISequenceService {

    private Sequence sequence = new Sequence();

    public SequenceService() {
    }

    public long nextId() {
        return this.sequence.nextId();
    }

}
