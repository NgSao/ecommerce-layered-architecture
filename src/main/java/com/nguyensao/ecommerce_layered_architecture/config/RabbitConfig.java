package com.nguyensao.ecommerce_layered_architecture.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nguyensao.ecommerce_layered_architecture.constant.RabbitMqConstant;

@Configuration
public class RabbitConfig {

    // Inventory
    @Bean
    public TopicExchange inventoryExchange() {
        return new TopicExchange(RabbitMqConstant.INVENTORY_EXCHANGE);
    }

    @Bean
    public Queue inventoryQueue() {
        return new Queue(RabbitMqConstant.INVENTORY_QUEUE, true);
    }

    @Bean
    public Binding inventoryBinding(Queue inventoryQueue, TopicExchange inventoryExchange) {
        return BindingBuilder.bind(inventoryQueue).to(inventoryExchange).with(RabbitMqConstant.INVENTORY_ROUTING_KEY);
    }

    // Notification
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(RabbitMqConstant.NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(RabbitMqConstant.NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange)
                .with(RabbitMqConstant.NOTIFICATION_ROUTING_KEY);
    }

    // Product
    @Bean
    public TopicExchange productExchange() {
        return new TopicExchange(RabbitMqConstant.PRODUCT_EXCHANGE);
    }

    @Bean
    public Queue productQueue() {
        return new Queue(RabbitMqConstant.PRODUCT_QUEUE, true);
    }

    @Bean
    public Binding productBinding(Queue productQueue, TopicExchange productExchange) {
        return BindingBuilder.bind(productQueue).to(productExchange).with(RabbitMqConstant.PRODUCT_ROUTING_KEY);
    }

    // File
    @Bean
    public TopicExchange fileExchange() {
        return new TopicExchange(RabbitMqConstant.FILE_EXCHANGE);
    }

    @Bean
    public Queue fileQueue() {
        return new Queue(RabbitMqConstant.FILE_QUEUE, true);
    }

    @Bean
    public Binding fileBinding(Queue fileQueue, TopicExchange fileExchange) {
        return BindingBuilder.bind(fileQueue).to(fileExchange).with(RabbitMqConstant.FILE_ROUTING_KEY);
    }

    // Mono
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}