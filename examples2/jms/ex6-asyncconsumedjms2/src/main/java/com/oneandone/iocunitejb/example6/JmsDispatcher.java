package com.oneandone.iocunitejb.example6;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;

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
