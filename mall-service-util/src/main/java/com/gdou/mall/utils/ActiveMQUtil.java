package com.gdou.mall.utils;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.pool.PooledConnectionFactory;

import javax.jms.*;
import java.util.Iterator;
import java.util.Map;

public class ActiveMQUtil {
    PooledConnectionFactory pooledConnectionFactory = null;

    public ConnectionFactory init(String brokerUrl) {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        //加入连接池
        pooledConnectionFactory = new PooledConnectionFactory(factory);
        //出现异常时重新连接
        pooledConnectionFactory.setReconnectOnException(true);
        //
        pooledConnectionFactory.setMaxConnections(5);
        pooledConnectionFactory.setExpiryTimeout(10000);
        return pooledConnectionFactory;
    }

    public ConnectionFactory getConnectionFactory() {
        return pooledConnectionFactory;
    }

    //生产者发送MapMessage
    public void producerSendMap(Connection connection, String queueName, Map<String, String> message) {
        this.producerSendMap(connection, queueName, message, false, 0);
    }

    //生产者发送延迟MapMessage
    public void producerSendMap(Connection connection, String queueName, Map<String, String> message, boolean isScheduleDelay, long delayTime) {
        Session session = null;
        Iterator<Map.Entry<String, String>> iterator = message.entrySet().iterator();
        try {
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(queue);
            MapMessage mapMessage = new ActiveMQMapMessage();

            if (isScheduleDelay == true) {
                mapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delayTime);
            }

            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String key = entry.getKey();
                String value = entry.getValue();
                mapMessage.setString(key, value);
            }

            producer.send(mapMessage);
            session.commit();
        } catch (JMSException e) {
            e.printStackTrace();
            if (session != null) {
                try {
                    session.rollback();
                } catch (JMSException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //生产者发送TextMessage
    public void producerSendText(Connection connection, String queueName, String message) {
        Session session = null;
        try {
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(queue);
            TextMessage texyMessage = new ActiveMQTextMessage();
            texyMessage.setText(message);

            producer.send(texyMessage);
            session.commit();
        } catch (JMSException e) {
            e.printStackTrace();
            try {
                session.rollback();
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
