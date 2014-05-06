package gmapper.logic;

import gmapper.logic.ProtobufData.QData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class GMapListener implements Runnable {
/*
 * This class is responsible for:
 * 1) listen to MQ 
 * 2) pass and receive updated protobuf obj from GLogic
 * 3) send to MQ
 */
	private final String NAMEQ ="testQ1";
	private final String NAMEQ_FINAL ="testQ2";
	
	private final int SLEEPTIME = 3000;
	
	private Connection conn = null;
	private String URIQ=null;
	private String USERQ=null;
	private String PASSQ=null;
	
	public GMapListener(Connection conn,String URIQ, String USERQ, String PASSQ){
		this.conn = conn;
		this.URIQ = URIQ;
		this.USERQ = USERQ;
		this.PASSQ = PASSQ;
	}
	
	@Override
	public void run(){
		Channel chan = null;
		 QueueingConsumer consumer = null;
	
		 System.out.println("**Starting MapperPair Instance***");
		try {
			
		    
		    while (true) {
				//for later implement
					synchronized(this){
					chan = conn.createChannel();
					chan.queueDeclare(NAMEQ, false, false, false, null);
					consumer = new QueueingConsumer(chan);
				    chan.basicConsume(NAMEQ, true, consumer);
					}
				    
		      QueueingConsumer.Delivery delivery;
		      delivery = consumer.nextDelivery();
		      
		      if (delivery != null){
		    	  String  message = new String(delivery.getBody());
			      System.out.println(" [x] "+NAMEQ+" Received " + message);
			      
			      ByteArrayInputStream iStream = new ByteArrayInputStream(delivery.getBody());
			      QData data = QData.parseFrom(iStream);
			      GLogic ml = new GLogic(data);
			      String logic = ml.generateLogic();
			      
			      QData.Builder processedData = QData.newBuilder();
			      processedData.mergeFrom(data);
			      processedData.setMapperLogic(logic);
			      QData pData = processedData.build();
			      
			    //  synchronize this too for later more than 1 threads
			      synchronized(this){
			   //   QProducer qp = new QProducer(NAMEQ_FINAL,pData,conn);
			      QProducer qp = new QProducer(NAMEQ_FINAL,pData,conn, URIQ, USERQ, PASSQ);
			      qp.sendtoQ();
			      }
			 
		    	}
		 
		      if (chan!=null)
		    	  chan.close();

		      
		      Thread.sleep(SLEEPTIME);
		    }
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		catch (ShutdownSignalException | ConsumerCancelledException
				| InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
	} 
	}
}
