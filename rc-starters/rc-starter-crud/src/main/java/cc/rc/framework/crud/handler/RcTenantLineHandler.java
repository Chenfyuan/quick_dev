package cc.rc.framework.crud.handler;

import cc.rc.framework.core.constant.RcConstant;
import cc.rc.framework.core.context.UserContextHolder;
import cc.rc.framework.core.props.RcProperties;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

import javax.annotation.Resource;

/**
 * 行级租户拦截器
 * @author Linweijian
 */
public class RcTenantLineHandler implements TenantLineHandler {

    @Resource
    private RcProperties rcProperties;


    @Override
    public Expression getTenantId() {
        return new LongValue(UserContextHolder.getRelationalTenant().getTenantId());
    }

    @Override
    public String getTenantIdColumn() {
        return RcConstant.CRUD.COLUMN_TENANT_ID;
    }

    @Override
    public boolean ignoreTable(String tableName) {
        if (RcConstant.CRUD.PRIVILEGED_TENANT_ID.equals(UserContextHolder.getRelationalTenant().getTenantId())) {
            return true;
        }

        return rcProperties.getCrud().getTenant().getIgnoredTables().contains(tableName);
    }

}
