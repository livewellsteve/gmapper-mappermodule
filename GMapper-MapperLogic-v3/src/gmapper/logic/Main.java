package gmapper.logic;

import gmapper.logic.ProtobuftoMapperQMsg.ToMapperQMsg;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;




public class Main {
	
	public static void main(String[] args){
	
		Properties prop = new Properties();
		InputStream input = null;
		try{
			input = new FileInputStream("config.properties");
			
			String qUriIncoming = prop.getProperty("Q_URI_INCOMING");
			String qUserIncoming = prop.getProperty("Q_USER_INCOMING");
			String qPwdIncoming = prop.getProperty("Q_PASSWORD_INCOMING");
			String qNameIncoming = prop.getProperty("Q_NAME_INCOMING");

			// load a properties file
			prop.load(input);
//			System.out.println(qUriIncoming); //why null?
//			System.out.println(prop.getProperty("Q_URI_INCOMING"));
	
			
		
		ConnectionFactory factory = new ConnectionFactory();
		URI uri = new URI(prop.getProperty("Q_URI_INCOMING"));
		
		factory.setUri(uri);
		factory.setUsername(prop.getProperty("Q_USER_INCOMING"));
		factory.setPassword(prop.getProperty("Q_PASSWORD_INCOMING"));
		
		Connection conn = factory.newConnection();
		while (true){
			Channel chan = conn.createChannel();
		//	chan.queueDeclare(prop.getProperty("Q_NAME_INCOMING"), false, false, false, null);
			QueueingConsumer consumer = new QueueingConsumer(chan);
		    chan.basicConsume(prop.getProperty("Q_NAME_INCOMING"), true, consumer);
		    
		    QueueingConsumer.Delivery delivery;

			delivery = consumer.nextDelivery();

			if (delivery != null) {
				ByteArrayInputStream iStream = new ByteArrayInputStream(
						delivery.getBody());
				ToMapperQMsg toMapperQMsg = ToMapperQMsg.parseFrom(iStream);
				System.out.println(toMapperQMsg.getFieldValueList().toString());
			}
		
			if (chan!=null)
				chan.close();
		}
		//testing
		
		
//		ExecutorService esMapper = Executors.newFixedThreadPool(1);
//		Runnable tMapper = new GMapListener(conn,URIQ, USERQ, PASSQ);
//		esMapper.execute(tMapper);
		
		
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
