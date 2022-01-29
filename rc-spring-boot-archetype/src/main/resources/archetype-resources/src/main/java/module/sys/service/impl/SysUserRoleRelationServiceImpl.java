#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.service.impl;

import cc.rc.framework.crud.service.impl.RcBaseServiceImpl;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ${package}.module.sys.entity.SysUserRoleRelationEntity;
import ${package}.module.sys.mapper.SysUserRoleRelationMapper;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 后台用户-角色关联
 * @author linweijian
 */
@Slf4j
@Service
public class SysUserRoleRelationServiceImpl extends RcBaseServiceImpl<SysUserRoleRelationMapper, SysUserRoleRelationEntity> {

    /**
     * 先清理用户ID所有关联关系, 再绑定用户ID与角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void cleanAndBind(Long userId, List<Long> roleIds) {
        this.remove(
                new QueryWrapper<SysUserRoleRelationEntity>()
                        .lambda()
                        .eq(SysUserRoleRelationEntity::getUserId, userId)
        );

        if (CollUtil.isNotEmpty(roleIds)) {
            // 需要绑定角色
            roleIds.forEach(
                    roleId -> this.save(
                            SysUserRoleRelationEntity.builder()
                                    .userId(userId)
                                    .roleId(roleId)
                                    .build()
                    )
            );
        }
    }

    /**
     * 根据角色Ids取用户Ids
     */
    public List<Long> listUserIdByRoleIds(List<Long> roleIds) throws IllegalArgumentException {
        if (CollUtil.isEmpty(roleIds)) {
            throw new IllegalArgumentException("roleIds不能为空");
        }

        return this.list(
                new QueryWrapper<SysUserRoleRelationEntity>()
                        .select(" user_id ")
                        .lambda()
                        .in(SysUserRoleRelationEntity::getRoleId, roleIds)
        ).stream().map(SysUserRoleRelationEntity::getUserId).collect(Collectors.toList());
    }

    /**
     * 取拥有角色Ids
     * @param userId 用户ID
     * @return 失败返回空列表
     */
    public List<Long> listRoleIdByUserId(Long userId) throws IllegalArgumentException {
        if (userId == null) {
            throw new IllegalArgumentException("userId不能为空");
        }

        return this.list(
                new QueryWrapper<SysUserRoleRelationEntity>()
                        .select(" role_id ")
                        .lambda()
                        .eq(SysUserRoleRelationEntity::getUserId, userId)
        ).stream().map(SysUserRoleRelationEntity::getRoleId).collect(Collectors.toList());
    }
}