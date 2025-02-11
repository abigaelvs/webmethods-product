package nds_product;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
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
}

