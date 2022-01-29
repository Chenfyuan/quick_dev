package cc.rc.framework.crud.handler;

import cc.rc.framework.core.enums.IdGeneratorStrategyEnum;
import cc.rc.framework.core.props.RcProperties;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 自定义雪花ID生成器
 * @author Linweijian
 */
@Slf4j
public class RcIdentifierGeneratorHandler implements IdentifierGenerator {

    private Snowflake snowflakeInstance;


    public RcIdentifierGeneratorHandler(RcProperties rcProperties) {
        long workerId;

        try {
            // 当前机器的局域网IP
            workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
        } catch (Exception e) {
            workerId = NetUtil.getLocalhost().hashCode();
        }

        if (IdGeneratorStrategyEnum.SNOWFLAKE.equals(rcProperties.getCrud().getIdGenerator().getStrategy())) {
            // Hutool的雪花生成器仅支持0-31
            workerId = workerId % 32;
            Long datacenterId = rcProperties.getCrud().getIdGenerator().getDatacenterId();
            Date epochDate = DateUtil.parseDate(rcProperties.getCrud().getIdGenerator().getEpochDate());

            log.info("[主键ID生成器] >> strategy=[{}], workerId=[{}], datacenterId=[{}], epochDate=[{}]",
                    rcProperties.getCrud().getIdGenerator().getStrategy().getLabel(),
                    workerId,
                    datacenterId,
                    epochDate
            );

            snowflakeInstance = getSnowflake(workerId, datacenterId, epochDate);
        }
    }

    @Override
    public Number nextId(Object entity) {
        return snowflakeInstance.nextId();
    }

    @Override
    public String nextUUID(Object entity) {
        return IdUtil.randomUUID();
    }

    private static Snowflake getSnowflake(long workerId, long datacenterId, Date epochDate) {
        return Singleton.get(Snowflake.class, epochDate, workerId, datacenterId, false);
    }

}
