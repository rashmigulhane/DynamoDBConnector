package DynamoDBMappers;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

public class MapperMain {

	public static void main(String[] args) throws Exception  {
		// TODO Auto-generated method stub
		AmazonDynamoDB dynamodb = establishNewConenction();
		DynamoDBMapper mapper = new DynamoDBMapper(dynamodb);
	//	createTableRequest(mapper,dynamodb);
		insertData(mapper);
	//	query(mapper);
		subQuery(mapper);
	}

	private static void subQuery(DynamoDBMapper mapper) {
		// TODO Auto-generated method stub
		
		Map<String, com.amazonaws.services.dynamodbv2.model.AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withN("1"));
		eav.put(":v2",new AttributeValue().withN(Long.toString(Instant.now().toEpochMilli())));
		eav.put(":v3",new AttributeValue().withN(Long.toString(Instant.now().toEpochMilli()-111118000)));
		eav.put(":v4", new AttributeValue().withN("22"));
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
			    .withFilterExpression("DeviceId = :v1 and creationDate between :v3 and  :v2 and payLoad.speed = :v4")
			    .withExpressionAttributeValues(eav);
		
		List<Devices> scanResult = mapper.scan(Devices.class, scanExpression);
		
		for(Devices d : scanResult) {
			System.out.println(d.getDEviceName() + " " + d.getDeviceId() + " " + d.getCreationDate());
			System.out.println(d.getPayLoad().getHumidity() + " " + d.getPayLoad().getSpeed() + " " + d.getPayLoad().getTemperature());
		}
	}
	
	private static void query(DynamoDBMapper mapper) {
		Map<String, com.amazonaws.services.dynamodbv2.model.AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withN("1"));
		eav.put(":v2",new AttributeValue().withN(Long.toString(Instant.now().toEpochMilli())));
		eav.put(":v3",new AttributeValue().withN(Long.toString(Instant.now().toEpochMilli()-111118000)));

		
		/*DynamoDBQueryExpression<Devices> queryExpression = new DynamoDBQueryExpression<Devices>() 
			    .withKeyConditionExpression("Id = :v1 and ReplyDateTime > :v2")
			    .setExpressionAttributeValues(eav);*/
		DynamoDBQueryExpression<Devices> queryExpression = new DynamoDBQueryExpression<Devices>() 
			    .withKeyConditionExpression("DeviceId = :v1 and creationDate between :v3 and  :v2")
			    .withExpressionAttributeValues(eav);
		
		List<Devices> latestReplies = mapper.query(Devices.class, queryExpression);
		System.out.println(latestReplies.size());
		for(Devices d :latestReplies ) {
			System.out.println(d.getDEviceName() + " " + d.getCreationDate());
		}
	}

	private static void insertData(DynamoDBMapper mapper) {
		// TODO Auto-generated method stub
		Devices dev = new Devices();
		dev.setDeviceId(1);
		dev.setDEviceName("Poonam");
		dev.setCreationDate(Instant.now().toEpochMilli());
		Payload pp = new Payload();
		pp.setHumidity(1.0f);
		pp.setSpeed(22);
		dev.setPayLoad(pp);
		mapper.save(dev);
	}

	private static void createTableRequest(DynamoDBMapper mapper, AmazonDynamoDB dynamodb) {
		
		DynamoDB dynamoDB = new DynamoDB(dynamodb);
		CreateTableRequest req = mapper.generateCreateTableRequest(Devices.class);
		// Table provision throughput is still required since it cannot be specified in your POJO
		// Fire off the CreateTableRequest using the low-level client
		
		req.setProvisionedThroughput(new ProvisionedThroughput(5L, 5L));
		Table table = dynamoDB.createTable(req);
		
		try {
			table.waitForActive();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	private static AmazonDynamoDB establishNewConenction() throws Exception {
		final String[] localArgs = { "-inMemory" };
        DynamoDBProxyServer server = null;
        try {
            server = ServerRunner.createServerFromCommandLineArgs(localArgs);
            server.start();

            AmazonDynamoDB dynamodb = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
                // we can use any region here
                new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
                .build();
            
           return dynamodb;
          
           
        }  finally {
            // Stop the DynamoDB Local endpoint
            if(server != null) {
                server.stop();
            }
        }
       
	}

}
