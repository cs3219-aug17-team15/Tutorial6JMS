package cs3219.jms.order;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Matric 1:A0138474X
 * Name   1:Jong Xue Min Shermine
 *
 * Matric 2:
 * Name   2:
 *
 * This file implements a pipe that transfer messages using JMS.
 */

public class JmsPipe implements IPipe,MessageListener {

	private String factoryName, queueName;
	private QueueConnectionFactory qconFactory;
    private QueueConnection qcon;
    private QueueSession qsession;
    private QueueSender qsender;
    private QueueReceiver qreceiver;
    private Queue queue;
    private TextMessage msg;

    public JmsPipe(String factoryName, String queueName) throws Exception{
    	this.factoryName = factoryName;
    	this.queueName = queueName;
        init();
    }
    private static InitialContext getInitialContext()
            throws NamingException {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
        props.put(Context.PROVIDER_URL, "jnp://localhost:1099");
        props.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
        return new InitialContext(props);
    }
    public void init()
            throws NamingException, JMSException {
    	InitialContext ctx = getInitialContext();
        qconFactory = (QueueConnectionFactory) ctx.lookup(factoryName);
        qcon = qconFactory.createQueueConnection();
        qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        queue = (Queue) ctx.lookup(queueName);
        qreceiver = qsession.createReceiver(queue);
        qsender = qsession.createSender(queue);
        msg = qsession.createTextMessage();
        qcon.start();
    }
	public void write(Order s) {
        try {
        	msg.setText(s.toString());
			qsender.send(msg);
		} catch (JMSException e) {
			System.err.println("An exception has occurred: " + e.getMessage());
		}
	}

	public Order read() {
		return Order.fromString(msg.toString());
	}

	public void close() {
        try {
        	qsender.close();
            qsession.close();
			qcon.close();
		} catch (JMSException e) {
			System.err.println("An exception has occurred: " + e.getMessage());
		}
	}
	public void onMessage(Message arg0) {
	try{
		if (arg0 instanceof TextMessage) {
            msg.setText(((TextMessage) arg0).getText());
        } else {
            msg.setText(arg0.toString());
        }
    } catch (JMSException jmse) {
        System.err.println("An exception occurred: " + jmse.getMessage());
    }

	}

}
