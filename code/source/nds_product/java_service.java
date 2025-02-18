package nds_product;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.math.BigDecimal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
// --- <<IS-END-IMPORTS>> ---

public final class java_service

{
	// ---( internal utility methods )---

	final static java_service _instance = new java_service();

	static java_service _newInstance() { return new java_service(); }

	static java_service _cast(Object o) { return (java_service)o; }

	// ---( server methods )---




	public static final void callRepository (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(callRepository)>> ---
		// @sigtype java 3.5
		// [i] field:0:required serviceName
		// [i] field:0:required serviceFolder
		// [i] record:0:required data
		// [o] record:0:required result
		IDataCursor cursor = pipeline.getCursor();
		
		String serviceName = IDataUtil.getString(cursor, "serviceName");
		String serviceFolder = IDataUtil.getString(cursor, "serviceFolder");
		
		try {
			IData input = IDataUtil.getIData(cursor, "data");
			
			IData output = Service.doInvoke(serviceFolder, serviceName, input);
			
			IDataUtil.put(cursor, "result", output);
			cursor.destroy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// --- <<IS-END>> ---

                
	}



	public static final void callService (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(callService)>> ---
		// @sigtype java 3.5
		// [i] field:0:required requestBody
		// [i] field:0:required requestHeader
		// [i] record:0:required data
		// [i] field:0:required profile
		// [o] field:0:required requestBody
		// [o] record:0:required requestHeader
		IDataCursor cursor = pipeline.getCursor();
		
		String requestBody = IDataUtil.getString(cursor, "requestBody");
		String requestHeader = IDataUtil.getString(cursor, "requestHeader");
		String profile = IDataUtil.getString(cursor, "profile");
		IData iData = IDataUtil.getIData(cursor, "data");
		
		profileObj = JsonParser.parseString(profile).getAsJsonObject();
		data = readData(iData);
		
		JsonElement requestHeaderElement = JsonParser.parseString(requestHeader);
		JsonObject requestHeaderObj = requestHeaderElement.getAsJsonObject();
		
		JsonElement requestBodyElement = JsonParser.parseString(requestBody);
		JsonObject requestBodyObj = requestBodyElement.getAsJsonObject();
		JsonObject newRequestBodyObj = printObject(requestBodyObj);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		IData requestHeaderIData = buildHeader(requestHeaderObj);
		requestBody = gson.toJson(newRequestBodyObj);
		
		IDataUtil.put(cursor, "requestBody", requestBody);
		IDataUtil.put(cursor, "requestHeader", requestHeaderIData);
		// --- <<IS-END>> ---

                
	}



	public static final void readParameters (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(readParameters)>> ---
		// @sigtype java 3.5
		// [i] record:1:required parameters
		// [i] record:0:required data
		// [o] record:0:required input
		// [o] object:0:required success
		// [o] record:0:required error
		IDataCursor cursor = pipeline.getCursor();
		
		IData[] parameters = IDataUtil.getIDataArray(cursor, "parameters");
		IData values = IDataUtil.getIData(cursor, "values");
		IDataCursor valuesCursor = values.getCursor();
		
		IData input = IDataFactory.create();
		IDataCursor inputCursor = input.getCursor();
		
		IData error = IDataFactory.create();
		IDataCursor errorCursor = error.getCursor();
		
		Boolean success = true;
		
		for (IData parameter :  parameters) {
			IDataCursor parameterCursor = parameter.getCursor();
			String key = IDataUtil.getString(parameterCursor, "SERVICE_PARAMETER_NAME");
			String type = IDataUtil.getString(parameterCursor, "SERVICE_PARAMETER_TYPE");
			Boolean required = IDataUtil.getBoolean(parameterCursor, "REQUIRED");
			
			Object  value = IDataUtil.getString(valuesCursor, key);
			
			if (required && value == null) {
				success = false;
				IDataUtil.put(errorCursor, key, "Parameter " + key + " is required");
				continue;
			}
			
			switch (type) {
			case "string":
				value = IDataUtil.getString(valuesCursor, key);
				value = value == null;
				break;
			case "stringArray":
				value = IDataUtil.getStringArray(valuesCursor, key);
				break;
			case "number":
				value = IDataUtil.get(valuesCursor, key);
				break;
			case "numberArray":
				value = IDataUtil.getObjectArray(valuesCursor, key);
				break;
			case "object":
				value = IDataUtil.getIData(valuesCursor, key);
				break;
			case "objectArray":
				value = IDataUtil.getIDataArray(valuesCursor, key);
				break;
			}
			
			IDataUtil.put(inputCursor, key, value);
			parameterCursor.destroy();
		}
		
		inputCursor.destroy();
		errorCursor.destroy();
		valuesCursor.destroy();
		
		if (!success) input = null;
		IDataUtil.put(cursor, "success", success);
		IDataUtil.put(cursor, "input", input);
		IDataUtil.put(cursor, "error", error);
		cursor.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void wkwkland (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(wkwkland)>> ---
		// @sigtype java 3.5
		// [i] field:0:required json
		// [i] record:0:required data
		// [o] field:0:required requestBody
		// [o] record:0:required header
		IDataCursor cursor = pipeline.getCursor();
		
		//		IData data = IDataUtil.getIData(cursor, "data");
		//		IDataCursor dataCursor = data.getCursor();
		//		
		String json = IDataUtil.getString(cursor, "json");
		
		Gson gson = new Gson();
		JsonObject jsonObj = JsonParser.parseString(json).getAsJsonObject();
		JsonObject body = jsonObj.get("body").getAsJsonObject();
		
		HashMap<String, Object> request = new HashMap<String, Object>();
		readJsonObject(body, request);
		
		String requestBody = gson.toJson(request);
		IDataUtil.put(cursor, "requestBody", requestBody);
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	
	private static HashMap<String, Object> data;
	private static JsonObject profileObj;
	
	private static IData buildHeader(JsonObject data) {
		IData iData = IDataFactory.create();
		IDataCursor cursor = iData.getCursor();
		
		for (Map.Entry<String, JsonElement> d : data.entrySet()) {
			IDataUtil.put(cursor, d.getKey(), d.getValue().getAsString());
		}
		
		cursor.destroy();
		return iData;
	}
	
	private static HashMap<String, Object> readData(IData data) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		IDataCursor cursor = data.getCursor();
		
		while (cursor.next()) {
			String key = cursor.getKey();
			Object value = cursor.getValue();
			result.put(key, value);
		}
		return result;
	}
	
	private static JsonObject printObject(JsonObject obj) {
		JsonObject newObj = new JsonObject();
		for (Map.Entry<String, JsonElement> objElement: obj.entrySet()) {
			//System.out.println("\nkey>"+objElement.getKey());
			if (objElement.getValue().isJsonObject()) {
				JsonObject newChildObj = new JsonObject();
				newChildObj = printObject(objElement.getValue().getAsJsonObject());
				newObj.add(objElement.getKey(), newChildObj);
			} else if (objElement.getValue().isJsonArray()) {
				JsonArray newChildArr = new JsonArray();
				newChildArr = printArray(objElement.getValue().getAsJsonArray());
				newObj.add(objElement.getKey(), newChildArr);
			} else {
				//System.out.println("value>"+objElement.getValue().getAsString());
				newObj = addParameter(newObj, objElement.getKey(), objElement.getValue().getAsString());
			}
		}
		return newObj;
	}
	
	private static JsonArray printArray(JsonArray array) {
		JsonArray newArr = new JsonArray();
		JsonObject newObj = new JsonObject();
		JsonObject obj = array.get(0).getAsJsonObject();
		for (Map.Entry<String, JsonElement> objElement: obj.entrySet()) {
			//System.out.println("\nkey>"+objElement.getKey());
			if (objElement.getValue().isJsonObject()) {
				JsonObject newChildObj = new JsonObject();
				newChildObj = printObject(objElement.getValue().getAsJsonObject());
				newObj.add(objElement.getKey(), newChildObj);
			} else if (objElement.getValue().isJsonArray()) {
				JsonArray newChildArr = new JsonArray();
				newChildArr = printArray(objElement.getValue().getAsJsonArray());
				newObj.add(objElement.getKey(), newChildArr);
			} else {
				//System.out.println("value>"+objElement.getValue().getAsString());
				newObj = addParameter(newObj, objElement.getKey(), objElement.getValue().getAsString());
			}
		}
		newArr.add(newObj);
		return newArr;
	}
	
	private static JsonObject addParameter(JsonObject object, String key, String tag) {
		String format = tag.split("\\|")[0];
		String value = getValue(tag.split("\\|")[1]).toString();
		//System.out.println("test>"+format+">"+value);
		switch (format) {
			case "string":
				object.addProperty(key, value);
	    	    break;
			case "number":
				object.addProperty(key, Long.valueOf(value));
	    	    break;
			case "decimal":
				object.addProperty(key, BigDecimal.valueOf(Long.valueOf(value)));
	    	    break;
			case "boolean":
				object.addProperty(key, Boolean.valueOf(value));
	    	    break;
		}
		return object;
	}
	
	private static Object getValue(String id) {
		Object result = new Object();
		JsonElement profile = profileObj.get(id);
		if (profile != null) {
			String[] splitted = profile.getAsString().split("\\|");
			String key = splitted[1];
			result = data.getOrDefault(key, "0");
		} else {
			result = "0";
		}
		//String[] splitted = profile.split("\\|");
	//	
		//String key = splitted[1];
		//result = data.getOrDefault(key, "0");
		//result = data.getOrDefault(id, "0");
		//result = id;
		//get value from db, sementara hardcode dulu
	//		Random rand = new Random();
	//		
	//		switch (id) {
	//			case "id":
	//				result = "nilai id";
	//	    	    break;
	//			case "name":
	//				result = "nilai name";
	//	    	    break;
	//			case "total":
	//				result = rand.nextInt();
	//	    	    break;
	//			case "true-false":
	//				result = true;
	//	    	    break;
	//		}
		return result;
	}
	
	public static HashMap<String, Object> readJsonObject(JsonObject jsonObject, HashMap<String, Object> request) {
		
		for (Map.Entry<String, JsonElement> j : jsonObject.entrySet()) {
			Object value = null;
			if (j.getValue().isJsonObject()) {
				HashMap<String, Object> newRequest = new HashMap<String, Object>();
				readJsonObject(j.getValue().getAsJsonObject(), newRequest);
				value = newRequest;
			} else {
				String[] splitted = j.getValue().getAsString().split("\\|");
				if (splitted[0].equals("string")) {
					value = splitted[1];
				} else if (splitted[0].equals("number")) {
					value = Integer.parseInt(splitted[1]);
				}
			}
			request.put(j.getKey(), value);
		}
		return request;
	}
	// --- <<IS-END-SHARED>> ---
}

