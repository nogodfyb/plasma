package com.fyb.plasma.service.impl;

import com.fyb.plasma.entity.Rec;
import com.fyb.plasma.mapper.RecMapper;
import com.fyb.plasma.service.IRecService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Plasma记录 服务实现类
 * </p>
 *
 * @author fyb
 * @since 2020-09-09
 */
@Service
public class RecServiceImpl extends ServiceImpl<RecMapper, Rec> implements IRecService {

}
