package gmapper.logic;

import gmapper.logic.ProtobuftoMapperQMsg.ToMapperQMsg;

import java.io.ByteArrayInputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;

public class GMapperListener implements Runnable{
	private String incomingQName;
	private String outgoingQName;
	private Connection toMapperConn;
	private Connection fromMapperConn;
	private java.sql.Connection DbConn;
	
	public GMapperListener(String incomingQName,String outgoingQName,Connection toMapperConn,Connection fromMapperConn, java.sql.Connection DbConn ){
		this.incomingQName = incomingQName;
		this.outgoingQName = outgoingQName;
		this.toMapperConn = toMapperConn;
		this.fromMapperConn = fromMapperConn;
		this.DbConn = DbConn;
	}
	@Override
	public void run() {
		Channel toMapperChan = null;
		QueueingConsumer toMapperConsumer = null;
		Channel fromMapperChan = null;
		QueueingConsumer fromMapperConsumer = null;
		
		try{
			  QueueingConsumer.Delivery delivery = toMapperConsumer.nextDelivery();
			  
			  if(delivery!=null){
				  ByteArrayInputStream iStream = new ByteArrayInputStream(delivery.getBody());
				  ToMapperQMsg toMapperQMsg = ToMapperQMsg.parseFrom(iStream);
				  getDatafromDB(toMapperQMsg);
			  }
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void getDatafromDB(ToMapperQMsg toMapperQMsg){
		//target_object, target_pk,target_adaptor_id(from here get access credentials) from mapper_config
		//action,  from mapper_field_config
		//mapper_id from here
		
		int mapperId = toMapperQMsg.getMapperId(); //need to pass to Q
		List<String> fieldName = toMapperQMsg.getFieldNameList();
		List<String> fieldValue = toMapperQMsg.getFieldValueList();
		List<String> fieldType = toMapperQMsg.getFieldTypeList();
		
		
		Statement stmt = DbConn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		
		
		
		
	}

}
