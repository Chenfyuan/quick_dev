/**
 * Copyright 2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.rc.framework.rocketmq.core.factory.execution;

import cc.rc.framework.rocketmq.annotation.RocketMessage;
import cc.rc.framework.rocketmq.core.strategy.PutProducerStrategy;
import cc.rc.framework.rocketmq.props.AliyunRocketProperties;
import cc.rc.framework.rocketmq.thread.AbstractProducerThread;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * ClassName: ProducerFactoryExecution
 * Description:
 * date: 2019/5/3 13:25
 *
 * @author ThierrySquirrel
 * @since JDK 1.8
 */
public class ProducerFactoryExecution extends AbstractProducerThread {

	public ProducerFactoryExecution(Map<String, Object> producerConsumer, RocketMessage rocketMessage, Object bean, AliyunRocketProperties rocketProperties, ApplicationContext applicationContext) {
		super(producerConsumer, rocketMessage, bean, rocketProperties, applicationContext);
	}

	/**
	 * 开始向容器装填
	 *
	 * @param producerConsumer   producerConsumer
	 * @param rocketMessage      rocketMessage
	 * @param bean               bean
	 * @param rocketProperties   rocketProperties
	 * @param applicationContext applicationContext
	 */
	@Override
	protected void statsPutProducer(Map<String, Object> producerConsumer, RocketMessage rocketMessage, Object bean, AliyunRocketProperties rocketProperties, ApplicationContext applicationContext) {
		PutProducerStrategy.putProducer(producerConsumer, rocketMessage, bean, rocketProperties, applicationContext);
	}
}
