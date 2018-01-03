package DynamoDBMappers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "devices3")
public class Devices {

	
	private Integer DeviceId;
    private String DEviceName;
    private Payload payLoad;
    private long creationDate;
    
    @DynamoDBHashKey(attributeName="DeviceId")  
	public Integer getDeviceId() {
		return DeviceId;
	}
	public void setDeviceId(Integer deviceId) {
		DeviceId = deviceId;
	}
	
	
	
	@DynamoDBRangeKey(attributeName="creationDate")  
	public long getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
	@DynamoDBAttribute(attributeName="DEviceName")  
	public String getDEviceName() {
		return DEviceName;
	}
	public void setDEviceName(String dEviceName) {
		DEviceName = dEviceName;
	}
	@DynamoDBAttribute
	public Payload getPayLoad() {
		return payLoad;
	}
	public void setPayLoad(Payload payLoad) {
		this.payLoad = payLoad;
	}
	
	
    
    
    
    
}
