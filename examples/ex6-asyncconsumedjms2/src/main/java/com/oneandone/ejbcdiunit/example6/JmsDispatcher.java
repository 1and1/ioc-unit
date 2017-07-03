package com.oneandone.ejbcdiunit.example6;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class JmsDispatcher {

    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory cf;

    @Resource(mappedName = "java:/queue/example6")
    private Queue queue;

    AtomicLong atomicLong = new AtomicLong(0);

    void send(Map<String, ?> message) throws JMSException {
        try (Connection connection = cf.createConnection();
             Session session = connection.createSession();
             MessageProducer mp = session.createProducer(queue)) {
            MapMessage mapMessage = session.createMapMessage();
           for (Map.Entry<String, ?> e: message.entrySet()) {
               if (e.getKey().equals("destination")) {
                   mapMessage.setStringProperty("destination", (String)e.getValue());
               }
               mapMessage.setObject(e.getKey(), e.getValue());
           }
           mp.send(mapMessage);
        }
    }

    HashMap<String, Object> initializeMessageForSender(String operationName) {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("operation", operationName);
        map.put("correlationId", atomicLong.incrementAndGet());
        map.put("destination", "sender");
        return map;
    }



    public HashMap<String, Object> initializeMessageForCallback(MapMessage original) throws JMSException {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("operation", original.getString("operation"));
        map.put("correlationId", original.getLong("correlationId"));
        map.put("destination", "callback");
        return map;
    }

}
