package com.example.atm.server.jms;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.spring.boot.MQConnectionFactoryCustomizer;
import com.ibm.mq.spring.boot.MQConnectionFactoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.MessageListener;
import java.util.List;

import static com.example.atm.server.jms.DestinationConfig.REPLY_TO_DYNAMIC_QUEUE;

@Configuration
@EnableJms
public class JmsConfig implements JmsListenerConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(JmsConfig.class);

    private static final String JMS_TEMPLATE_PREFIX = "jmsTemplate-";
    private static final String JMS_ENDPOINT_PREFIX = "jmsEndpoint-";

    @Autowired
    private ObjectProvider<List<MQConnectionFactoryCustomizer>> factoryCustomizers;

    @Autowired
    private DefaultJmsListenerContainerFactoryConfigurer configurer;

    @Autowired
    private QmProperties qmProperties;

    @Autowired
    private MessageListener messageListener;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();

        for (ExtendedMQConfigurationProperties properties : qmProperties.getList()) {
            String queueManager = properties.getQueueManager();
            LOG.info("Configuring queue manager " + queueManager);

            // Cria ConnectionFactory
            MQConnectionFactory connectionFactory = new MQConnectionFactoryFactory(properties, factoryCustomizers.getIfAvailable()).createConnectionFactory(MQConnectionFactory.class);

            // Cria ContainerFactory
            DefaultJmsListenerContainerFactory containerFactory = new DefaultJmsListenerContainerFactory();
            configurer.configure(containerFactory, connectionFactory);

            // Cria JmsListener
            SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
            endpoint.setId(JMS_ENDPOINT_PREFIX + queueManager);
            endpoint.setDestination(REPLY_TO_DYNAMIC_QUEUE);
            endpoint.setConcurrency(properties.getConcurrency());
            endpoint.setMessageListener(message -> messageListener.onMessage(message));
            registrar.registerEndpoint(endpoint, containerFactory);
            LOG.debug("Registered bean {}", endpoint.getId());

            // Cria JmsTemplate
            BeanDefinition beanDef = BeanDefinitionBuilder.genericBeanDefinition(JmsTemplate.class)
                    .addPropertyValue("connectionFactory", connectionFactory)
                    .getBeanDefinition();
            beanDefinitionRegistry.registerBeanDefinition(JMS_TEMPLATE_PREFIX + queueManager, beanDef);
            LOG.debug("Registered bean {}", JMS_TEMPLATE_PREFIX + queueManager);
        }
    }

    public JmsTemplate getJmsTemplate(String id) {
        return applicationContext.getBean(JMS_TEMPLATE_PREFIX + id, JmsTemplate.class);
    }

}
