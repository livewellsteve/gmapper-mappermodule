package gmapper.logic;

import gmapper.logic.ProtobufData.QData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;



import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


public class QProducer {
private String qName;
private QData data;
private String URIQ;
private String USERQ;
private String PASSQ;
private Connection conn;
	
	public QProducer(String qName, QData data,Connection conn, String URIQ, String USERQ, String PASSQ){
		this.qName = qName;
		this.data = data;
		this.conn = conn;
		this.URIQ = URIQ;
		this.URIQ = USERQ;
		this.PASSQ = PASSQ;
	}
	
	public boolean sendtoQ(){
		ConnectionFactory factory = new ConnectionFactory();
		 
		 Channel chan = null;
		 try {
				URI uri = new URI(URIQ);
				factory.setUri(uri);
				factory.setUsername(USERQ);
				factory.setPassword(PASSQ);
				
				chan = conn.createChannel();
				chan.queueDeclare(qName, false, false, false, null);
				
				ByteArrayOutputStream oStream = new ByteArrayOutputStream();
				data.writeTo(oStream);
				chan.basicPublish("", qName, null, oStream.toByteArray());
				
				if(chan!=null)
					chan.close();
				
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return false;
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		 return true;
	}
}

