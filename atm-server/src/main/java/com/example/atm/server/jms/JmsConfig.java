package com.example.atm.server.jms;

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
public class JmsConfig {
    private static final Logger LOG = LoggerFactory.getLogger(JmsConfig.class);

    @Autowired
    private ReplyToHolder replyToHolder;

    @Bean
    public DynamicDestinationResolver destinationResolver() {
        DynamicDestinationResolver dynamicDestinationResolver = new DynamicDestinationResolver() {
            @Override
            public Destination resolveDestinationName(Session session, String destinationName, boolean pubSubDomain) throws JMSException {
                if (destinationName.equals("REPLY_TO_DYNAMIC_QUEUE")) {
                    TemporaryQueue temporaryQueue = session.createTemporaryQueue();
                    replyToHolder.setReplyToQueue(temporaryQueue);
                    LOG.info("Created temporary queue: {}", temporaryQueue);
                    return temporaryQueue;
                }
                return super.resolveDestinationName(session, destinationName, pubSubDomain);
            }
        };
        LOG.info("DynamicDestinationResolver configured");
        return dynamicDestinationResolver;
    }

    @Bean
    public JmsListenerContainerFactory<?> queueConnectionFactory(ConnectionFactory connectionFactory,
                                                                 DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(false);
        return factory;
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
