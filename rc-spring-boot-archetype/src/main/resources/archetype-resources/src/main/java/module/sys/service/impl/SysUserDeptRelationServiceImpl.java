#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.service.impl;

import cc.rc.framework.crud.service.impl.RcBaseServiceImpl;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ${package}.module.sys.entity.SysUserDeptRelationEntity;
import ${package}.module.sys.mapper.SysUserDeptRelationMapper;


/**
 * 后台用户-部门关联
 * @author linweijian
 */
@Slf4j
@Service
public class SysUserDeptRelationServiceImpl extends RcBaseServiceImpl<SysUserDeptRelationMapper, SysUserDeptRelationEntity> {

    /**
     * 先清理用户ID所有关联关系, 再绑定用户ID与部门ID
     */
    public void cleanAndBind(Long userId, Long deptId) {
        this.remove(
                new QueryWrapper<SysUserDeptRelationEntity>()
                        .lambda()
                        .eq(SysUserDeptRelationEntity::getUserId, userId)
        );

        if (ObjectUtil.isNotNull(deptId)) {
            // 需要绑定部门
            this.save(
                    SysUserDeptRelationEntity.builder()
                            .userId(userId)
                            .deptId(deptId)
                            .build()
            );
        }

    }

}