package cc.rc.framework.crud.service.impl;

import cc.rc.framework.crud.entity.RcBaseEntity;
import cc.rc.framework.crud.service.RcBaseService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NoArgsConstructor;

/**
 * @author Linweijian
 */
@NoArgsConstructor
public class RcBaseServiceImpl<MAPPER extends BaseMapper<ENTITY>, ENTITY extends RcBaseEntity<?>> extends ServiceImpl<MAPPER, ENTITY> implements RcBaseService<ENTITY> {

}
