package com.oneandone.iocunitejb.example5;

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

    @Resource(mappedName = "java:/queue/incoming")
    private Queue incomingQueue;

    @Resource(mappedName = "java:/queue/callback")
    private Queue callbackQueue;

    AtomicLong atomicLong = new AtomicLong(0);


    void send(Map<String, ?> message, Queue destination) throws JMSException {
        try (Connection connection = cf.createConnection();
             Session session = connection.createSession();
             MessageProducer mp = session.createProducer(destination)) {
            MapMessage mapMessage = session.createMapMessage();
            for (Map.Entry<String, ?> e : message.entrySet()) {
                mapMessage.setObject(e.getKey(), e.getValue());
            }
            mp.send(mapMessage);
        }
    }

    HashMap<String, Object> initializeMessage(String operationName) {
        return initializeMessage(operationName, atomicLong.incrementAndGet());
    }

    static HashMap<String, Object> initializeMessage(String operationName, long correlationIdLong) {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("operation", operationName);
        map.put("correlationId", correlationIdLong);
        return map;
    }

    AsynchronousServiceIntf.CorrelationId postMessageToJms(HashMap<String, Object> message, Queue queue) {
        try {
            send(message, queue);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        return new AsynchronousServiceIntf.CorrelationId((Long) message.get("correlationId"));
    }

    public HashMap<String, Object> initializeMessage(MapMessage original) throws JMSException {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("operation", original.getString("operation"));
        map.put("correlationId", original.getLong("correlationId"));
        return map;
    }

    public Queue getIncomingQueue() {
        return incomingQueue;
    }


    public Queue getCallbackQueue() {
        return callbackQueue;
    }

}
