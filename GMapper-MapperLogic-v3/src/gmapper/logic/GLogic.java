package gmapper.logic;

import java.util.List;

import gmapper.logic.ProtobufData.QData;

public class GLogic {
/*
 * This class responsible for:
 * 1) deserialize protobuf obj and create sql statement depends data
 * 2) returns serialized protobuf obj
 */
	private QData data;
	private int mapperFieldConfigId;
	private String action;
	
	private String targetType;
	
	private List<String> sourceFieldName;
	private List<String> sourceFieldType;
	private List<String> sourceFieldValue;
	
	private List<String> sourcePkValue;
	
	private String targetObject;
	private List<String> targetFieldName;
	private List<String> targetFieldType;

	private String time;
	
	


		public GLogic(QData data){
			this.data = data;
			this.mapperFieldConfigId = data.getMapperFieldConfigId();
			this.action = data.getAction();
			this.targetType = data.getTargetType();
			this.sourceFieldName = data.getSourceFieldNameList();
			this.sourceFieldType = data.getSourceFieldTypeList();
			this.sourceFieldValue = data.getSourceFieldValueList();
			this.sourcePkValue = data.getSourcePkValList();
			this.targetObject = data.getTargetObjName();
			this.targetFieldName = data.getTargetFieldNameList();
			this.targetFieldType = data.getTargetFieldTypeList();
			this.time = data.getTime();
			
			
		}
		// \"dumbTable\" postgres
		
		public String generateLogic(){
		
			StringBuilder sb = new StringBuilder();
			if (action.equalsIgnoreCase("insert")){
				if (targetType.equalsIgnoreCase("postgres"))
					targetObject ="\""+targetObject+"\"";
				
			sb.append("INSERT INTO ");
			sb.append(targetObject+" (");
			for (int i =0; i<targetFieldName.size()-1; i++){
				sb.append(targetFieldName.get(i)+",");
			}
			sb.append(targetFieldName.get(targetFieldName.size()-1));
			sb.append(") ");
			sb.append("VALUES (");
			for (int i =0; i<sourceFieldName.size()-1; i++){
				sb.append("'"+sourceFieldName.get(i)+"',");
			}
			sb.append("'"+sourceFieldName.get(sourceFieldName.size()-1)+"'");
			sb.append(")");
			}
			
			else if (action.equalsIgnoreCase("update")){
				
			}
			
			else if (action.equalsIgnoreCase("delete")){
				
			}
			return sb.toString();
		}
}
