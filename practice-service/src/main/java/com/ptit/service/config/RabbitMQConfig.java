package com.ptit.service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQConfig {

    @Value("${user.exchange}")
    private String userExchange;

    @Value("${user.created.queue}")
    private String userCreatedQueue;

    @Value("${user.created.routing.key}")
    private String userCreatedRoutingKey;
    
    @Value("${user.login.exchange}")
    private String userLoginExchange;
    
    @Value("${user.login.queue}")
    private String userLoginQueue;
    
    @Value("${user.login.routing.key}")
    private String userLoginRoutingKey;

    // User Exchange và Queue
    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange(userExchange);
    }

    @Bean
    public Queue userCreatedQueue() {
        return new Queue(userCreatedQueue);
    }

    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder.bind(userCreatedQueue())
                .to(userExchange())
                .with(userCreatedRoutingKey);
    }
    
    // User Login Exchange và Queue
    @Bean
    public DirectExchange userLoginExchange() {
        return new DirectExchange(userLoginExchange);
    }

    @Bean
    public Queue userLoginQueue() {
        return new Queue(userLoginQueue);
    }

    @Bean
    public Binding userLoginBinding() {
        return BindingBuilder.bind(userLoginQueue())
                .to(userLoginExchange())
                .with(userLoginRoutingKey);
    }

    // Comment out beans that require ConnectionFactory when RabbitMQ is disabled
    /*
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
    */
}