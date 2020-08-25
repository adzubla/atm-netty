package com.example.atm.server.jms;

import com.ibm.mq.jms.MQSession;
import com.ibm.msg.client.wmq.WMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.*;

@Configuration
public class DestinationConfig {
    private static final Logger LOG = LoggerFactory.getLogger(DestinationConfig.class);

    public static final String REPLY_TO_DYNAMIC_QUEUE = "REPLY_TO_DYNAMIC_QUEUE";

    @Autowired
    private ReplyToHolder replyToHolder;

    @Bean
    public DynamicDestinationResolver destinationResolver() {
        DynamicDestinationResolver dynamicDestinationResolver = new DynamicDestinationResolver() {
            @Override
            public Destination resolveDestinationName(Session session, String destinationName, boolean pubSubDomain) throws JMSException {
                if (destinationName.equals(REPLY_TO_DYNAMIC_QUEUE)) {
                    TemporaryQueue temporaryQueue = session.createTemporaryQueue();

                    String qm = getQm(session);
                    replyToHolder.setReplyToQueue(qm, temporaryQueue);
                    LOG.info("Created temporary queue: {} {}", qm, temporaryQueue);
                    return temporaryQueue;
                }
                return super.resolveDestinationName(session, destinationName, pubSubDomain);
            }

            private String getQm(Session session) throws JMSException {
                MQSession mqSession = (MQSession) session;
                return mqSession.getStringProperty(WMQConstants.WMQ_QUEUE_MANAGER);
            }
        };
        LOG.info("DynamicDestinationResolver configured");
        return dynamicDestinationResolver;
    }

    @Bean
    public JmsListenerContainerFactory<?> topicConnectionFactory(ConnectionFactory connectionFactory,
                                                                 DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(true);
        return factory;
    }

}
