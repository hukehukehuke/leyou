package com.leyou.goods.listener;

import com.leyou.goods.service.GoodsHtmlService;
import com.rabbitmq.http.client.domain.ExchangeType;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {

    @Autowired
    private GoodsHtmlService goodsHtmlService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.COM.QUEUE",durable = "true"),
            exchange = @Exchange(value = "LEYOU.COM.EXCHANGE",ignoreDeclarationExceptions = "ture",type = ExchangeTypes.TOPIC),
            key={"item.insert","item.update"}
     ))

    public  void  save(Long id){
        if(id == null){
            return;
        }
        this.goodsHtmlService.createHtml(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.COM.DELETE.QUEUE",durable = "true"),
            exchange = @Exchange(value = "LEYOU.COM.EXCHANGE",ignoreDeclarationExceptions = "ture",type = ExchangeTypes.TOPIC),
            key={"item.delete"}
    ))

    public  void  delete(Long id){
        if(id == null){
            return;
        }
        this.goodsHtmlService.deleteHtml(id);
    }
}
