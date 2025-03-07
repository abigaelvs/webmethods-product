package nds_product;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.math.BigDecimal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import id.co.nds.race.formatter.xslt.profile.ProfileField;
import id.co.nds.race.formatter.xslt.profile.ProfileSection;
import id.co.nds.race.formatter.html.ProfileHTML;
import id.co.nds.race.formatter.xslt.profile.ProfileException;
import id.co.nds.race.formatter.models.ExportImportSettings;
import id.co.nds.race.formatter.JDBCExportToCSV;
import id.co.nds.race.formatter.JDBCExportToFLV;
import id.co.nds.race.formatter.JDBCExportToJson;
import id.co.nds.race.formatter.JDBCImportFromCSV;
import id.co.nds.race.formatter.JDBCImportFromFLV;
import id.co.nds.race.formatter.constants.ConfigurationChecker;
import id.co.nds.race.formatter.text_formatter.formatter.html.csv.xport.CSVExportHTML;
import id.co.nds.race.formatter.xslt.metadata.MetadataField;
import id.co.nds.race.formatter.text_formatter.formatter.html.IFormatterHTML;
import id.co.nds.race.formatter.xslt.metadata.csv.CSVMetadataException;
import id.co.nds.race.formatter.xslt.metadata.flv.FLVMetadataException;
import id.co.nds.race.formatter.text_formatter.formatter.html.flv.xport.FLVExportHTML;
// --- <<IS-END-IMPORTS>> ---

public final class java_service

{
	// ---( internal utility methods )---

	final static java_service _instance = new java_service();

	static java_service _newInstance() { return new java_service(); }

	static java_service _cast(Object o) { return (java_service)o; }

	// ---( server methods )---




	public static final void JDBCExport (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(JDBCExport)>> ---
		// @sigtype java 3.5
		// [i] recref:0:required MsProduct nds_product.rest_service.entities:MsProduct
		// [i] recref:1:required MsParamProducts nds_product.rest_service.entities:MsParamProduct
		// [i] recref:0:required MsBill nds_product.rest_service.entities:MsBill
		// [i] recref:0:required MsProductFile nds_product.rest_service.entities:MsProductFile
		// [o] field:0:required data
		// [o] object:0:required success
		// [o] field:0:required error
		IDataCursor cursor = pipeline.getCursor();
		
		IData MsProduct = IDataUtil.getIData(cursor, "MsProduct");
		IDataCursor MsProductCursor = MsProduct.getCursor();
		
		String productId = IDataUtil.getString(MsProductCursor, "product_id");
		String productType = IDataUtil.getString(MsProductCursor, "product_type");
		String productProfile = IDataUtil.getString(MsProductCursor, "product_profile");
		String productDirection = IDataUtil.getString(MsProductCursor, "product_profile");
		
		IData[] MsParamProducts = IDataUtil.getIDataArray(cursor, "MsParamProducts");
		IData MsBill = IDataUtil.getIData(cursor, "MsBill");
		IDataCursor MsBillCursor = MsBill.getCursor();
		
		
		String fileType = "";
		if (productType.equals("file")) {
			IData MsProductFile = IDataUtil.getIData(cursor, "MsProductFile");
			IDataCursor MsProductFileCursor = MsProductFile.getCursor();
			fileType = IDataUtil.getString(MsProductFileCursor, "file_type");
		}
		
		String profileFileName = null;
		String metadataFileName = IDataUtil.getString(MsProductCursor, "product_metadata");
		String xmlLibFileName = null;
		String xslLibFileName = null;
		String xslFileName = IDataUtil.getString(MsProductCursor, "product_xsl");
		
		for (IData MsParamProduct : MsParamProducts) {
			IDataCursor MsParamProductCursor = MsParamProduct.getCursor();
				
			String keyParam = IDataUtil.getString(MsParamProductCursor, "key_param");
			String valueParam = IDataUtil.getString(MsParamProductCursor, "value_param");
		
			switch (keyParam) {
				case "xml-lib":
					xmlLibFileName = valueParam;
					break;
				case "xsl-lib":
					xslLibFileName = valueParam;
					break;
				default:
					profileFileName = valueParam;
					break;
			}
			
			MsParamProductCursor.destroy();
		}
		
		ExportImportSettings settings = new ExportImportSettings();
		settings.setProfileFileName(profileFileName);
		settings.setMetadataFileName(metadataFileName);
		settings.setXmlLibFileName(xmlLibFileName);
		settings.setXslLibFileName(xslLibFileName);
		settings.setXslFileName(xslFileName);
		
		ArrayList<ProfileField> profileHeaderFields = null;
		ArrayList<ProfileField> profileBodyFields = null;
		
		ArrayList<MetadataField> metadataHeaderFields = null;
		ArrayList<MetadataField> metadataBodyFields = null;
		ArrayList<MetadataField> metadataFooterFields = null;
		
		String error = "";
		
		try {
			ProfileHTML profile = new ProfileHTML(productProfile, productDirection, profileFileName, xmlLibFileName, "header");
			profileHeaderFields = profile.getHeader().getFields();
			profileBodyFields = profile.getBody().getFields();
			
			IFormatterHTML html = getExportHtml(productType, productProfile, fileType, productId, "header", settings);		
			metadataHeaderFields = html.getMetadataSection().getRow().getFields();
			
			html = getExportHtml(productType, productProfile, fileType, productId, "body", settings);		
			metadataBodyFields = html.getMetadataSection().getRow().getFields();
			
			html = getExportHtml(productType, productProfile, fileType, productId, "footer", settings);		
			metadataFooterFields = html.getMetadataSection().getRow().getFields();
		} catch (Exception ex) {
			error = ex.toString();
		}
		
		Map<String, Object> datas = new HashMap<String, Object>();
		
		for (MetadataField header : metadataHeaderFields) {
			if (!header.getTagName().isEmpty() && !header.getSource().equals("var") && !datas.containsKey(header.getTagName())) {
				ProfileField profileField = profileHeaderFields.stream().filter(f -> f.getName().equals(header.getTagName())).findFirst().orElse(null);
				Object value = IDataUtil.get(MsBillCursor, profileField.getFieldName());
				datas.put(header.getTagName(), value);
			}
		}
		
		for (MetadataField body : metadataBodyFields) {
			if (!body.getTagName().isEmpty() && !body.getSource().equals("var") && !datas.containsKey(body.getTagName())) {
				ProfileField profileField = profileBodyFields.stream().filter(f -> f.getName().equals(body.getTagName())).findFirst().orElse(null);
				Object value = IDataUtil.get(MsBillCursor, profileField.getFieldName());
				datas.put(body.getTagName(), value);
			}
		}
		
		for (MetadataField footer : metadataFooterFields) {
			if (!footer.getTagName().isEmpty() && !footer.getSource().equals("var") && !datas.containsKey(footer.getTagName())) {
				ProfileField profileField = profileBodyFields.stream().filter(f -> f.getName().equals(footer.getTagName())).findFirst().orElse(null);
				Object value = IDataUtil.get(MsBillCursor, profileField.getFieldName());
				datas.put(footer.getTagName(), value);
			}
		}
		
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		data.add(datas);
		
		List<Map<String, Object>> headerData = new ArrayList<Map<String, Object>>();
		for (Map.Entry<String, Object> d : datas.entrySet()) {
			Map<String, Object> headerD = new HashMap<String, Object>();
			headerD.put("keyId", d.getKey());
			headerD.put("value", d.getValue());
			headerData.add(headerD);
		}
				
		String text = "";
			
		try {
			JDBCExportToJson exportData = new JDBCExportToJson(productProfile, productId, settings);
			exportData.setHeader(headerData);
			exportData.setBody(data);
			exportData.setFooter(headerData);
			text = exportData.export();
		} catch (Exception ex) {
			error = ex.toString();
		}
		
		Boolean success = error.equals("") ? true : false;
		IDataUtil.put(cursor, "success", success);
		IDataUtil.put(cursor, "error", error);
		IDataUtil.put(cursor, "data", text);
		
		MsProductCursor.destroy();
		MsBillCursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void JDBCExportToCSV (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(JDBCExportToCSV)>> ---
		// @sigtype java 3.5
		// [i] recref:0:required MsProductFile nds_product.rest_service.entities:MsProductFile
		// [i] recref:0:required MsBill nds_product.rest_service.entities:MsBill
		// [i] field:0:required product_id
		// [o] field:0:required data
		// [o] object:0:required success
		// [o] field:0:required error
		IDataCursor cursor = pipeline.getCursor();
		
		String productId = IDataUtil.getString(cursor, "product_id");
		
		IData MsProductFile = IDataUtil.getIData(cursor, "MsProductFile");
		IDataCursor MsProductFileCursor = MsProductFile.getCursor();
		
		String productProfile = IDataUtil.getString(MsProductFileCursor, "product_profile");
		
		IData[] MsParamProducts = IDataUtil.getIDataArray(cursor, "MsParamProducts");
		IData MsBill = IDataUtil.getIData(cursor, "MsBill");
		IDataCursor MsBillCursor = MsBill.getCursor();
		
		String profileFileName = null;
		String metadataFileName = IDataUtil.getString(MsProductFileCursor, "file_xml");
		String xmlLibFileName = null;
		String xslLibFileName = null;
		String xslFileName = IDataUtil.getString(MsProductFileCursor, "file_xsl");
		
		for (IData MsParamProduct : MsParamProducts) {
			IDataCursor MsParamProductCursor = MsParamProduct.getCursor();
				
			String keyParam = IDataUtil.getString(MsParamProductCursor, "key_param");
			String valueParam = IDataUtil.getString(MsParamProductCursor, "value_param");
		
			switch (keyParam) {
				case "xml-lib":
					xmlLibFileName = valueParam;
					break;
				case "xsl-lib":
					xslLibFileName = valueParam;
					break;
				default:
					profileFileName = valueParam;
					break;
			}
			
			MsParamProductCursor.destroy();
		}
		
		ExportImportSettings settings = new ExportImportSettings();
		settings.setProfileFileName(profileFileName);
		settings.setMetadataFileName(metadataFileName);
		settings.setXmlLibFileName(xmlLibFileName);
		settings.setXslLibFileName(xslLibFileName);
		settings.setXslFileName(xslFileName);
		
		ArrayList<ProfileField> profileHeaderFields = null;
		ArrayList<ProfileField> profileBodyFields = null;
		
		ArrayList<MetadataField> metadataHeaderFields = null;
		ArrayList<MetadataField> metadataBodyFields = null;
		ArrayList<MetadataField> metadataFooterFields = null;
		
		String error = "";
		
		try {
			ProfileHTML profile = new ProfileHTML(productProfile, "export", profileFileName, xmlLibFileName, "header");
			profileHeaderFields = profile.getHeader().getFields();
			profileBodyFields = profile.getBody().getFields();
			
			CSVExportHTML html = new CSVExportHTML(productProfile, productId, settings.getProfileFileName(), settings.getMetadataFileName(), settings.getXslLibFileName(), "header");
			metadataHeaderFields = html.getMetadataSection().getRow().getFields();
			
			html = new CSVExportHTML(productProfile, productId, settings.getProfileFileName(), settings.getMetadataFileName(), settings.getXslLibFileName(), "body");
			metadataBodyFields = html.getMetadataSection().getRow().getFields();
			
			html = new CSVExportHTML(productProfile, productId, settings.getProfileFileName(), settings.getMetadataFileName(), settings.getXslLibFileName(), "footer");
			metadataFooterFields = html.getMetadataSection().getRow().getFields();
		} catch (Exception ex) {
			error = ex.toString();
		}
		
		Map<String, Object> datas = new HashMap<String, Object>();
		
		for (MetadataField header : metadataHeaderFields) {
			if (!header.getTagName().isEmpty() && !header.getSource().equals("var") && !datas.containsKey(header.getTagName())) {
				ProfileField profileField = profileHeaderFields.stream().filter(f -> f.getName().equals(header.getTagName())).findFirst().orElse(null);
				Object value = IDataUtil.get(MsBillCursor, profileField.getFieldName());
				datas.put(header.getTagName(), value);
			}
		}
		
		for (MetadataField body : metadataBodyFields) {
			if (!body.getTagName().isEmpty() && !body.getSource().equals("var") && !datas.containsKey(body.getTagName())) {
				ProfileField profileField = profileBodyFields.stream().filter(f -> f.getName().equals(body.getTagName())).findFirst().orElse(null);
				Object value = IDataUtil.get(MsBillCursor, profileField.getFieldName());
				datas.put(body.getTagName(), value);
			}
		}
		
		for (MetadataField footer : metadataFooterFields) {
			if (!footer.getTagName().isEmpty() && !footer.getSource().equals("var") && !datas.containsKey(footer.getTagName())) {
				ProfileField profileField = profileBodyFields.stream().filter(f -> f.getName().equals(footer.getTagName())).findFirst().orElse(null);
				Object value = IDataUtil.get(MsBillCursor, profileField.getFieldName());
				datas.put(footer.getTagName(), value);
			}
		}
		
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		data.add(datas);
		
		List<Map<String, Object>> headerData = new ArrayList<Map<String, Object>>();
		for (Map.Entry<String, Object> d : datas.entrySet()) {
			Map<String, Object> headerD = new HashMap<String, Object>();
			headerD.put("keyId", d.getKey());
			headerD.put("value", d.getValue());
			headerData.add(headerD);
		}
		
		
		String text = "";
		
		try {
			JDBCExportToCSV exportData = new JDBCExportToCSV(productProfile, productId, settings);
			exportData.setHeader(headerData);
			exportData.setBody(data);
			exportData.setFooter(headerData);
			text = exportData.export();
		} catch (Exception ex) {
			error = ex.toString();
		}
		
		Boolean success = error.equals("") ? true : false;
		IDataUtil.put(cursor, "success", success);
		IDataUtil.put(cursor, "error", error);
		IDataUtil.put(cursor, "data", text);
		// --- <<IS-END>> ---

                
	}



	public static final void JDBCExportToFLV (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(JDBCExportToFLV)>> ---
		// @sigtype java 3.5
		// [i] recref:0:required MsProductFile nds_product.rest_service.entities:MsProductFile
		// [i] recref:0:required MsBill nds_product.rest_service.entities:MsBill
		// [i] field:0:required product_id
		// [o] field:0:required data
		// [o] object:0:required success
		// [o] field:0:required error
		IDataCursor cursor = pipeline.getCursor();
		
		String productId = IDataUtil.getString(cursor, "product_id");
		
		IData MsProductFile = IDataUtil.getIData(cursor, "MsProductFile");
		IDataCursor MsProductFileCursor = MsProductFile.getCursor();
		
		String productProfile = IDataUtil.getString(MsProductFileCursor, "product_profile");
		
		IData[] MsParamProducts = IDataUtil.getIDataArray(cursor, "MsParamProducts");
		IData MsBill = IDataUtil.getIData(cursor, "MsBill");
		IDataCursor MsBillCursor = MsBill.getCursor();
		
		String profileFileName = null;
		String metadataFileName = IDataUtil.getString(MsProductFileCursor, "file_xml");
		String xmlLibFileName = null;
		String xslLibFileName = null;
		String xslFileName = IDataUtil.getString(MsProductFileCursor, "file_xsl");
		
		for (IData MsParamProduct : MsParamProducts) {
			IDataCursor MsParamProductCursor = MsParamProduct.getCursor();
				
			String keyParam = IDataUtil.getString(MsParamProductCursor, "key_param");
			String valueParam = IDataUtil.getString(MsParamProductCursor, "value_param");
		
			switch (keyParam) {
				case "xml-lib":
					xmlLibFileName = valueParam;
					break;
				case "xsl-lib":
					xslLibFileName = valueParam;
					break;
				default:
					profileFileName = valueParam;
					break;
			}
			
			MsParamProductCursor.destroy();
		}
		
		ExportImportSettings settings = new ExportImportSettings();
		settings.setProfileFileName(profileFileName);
		settings.setMetadataFileName(metadataFileName);
		settings.setXmlLibFileName(xmlLibFileName);
		settings.setXslLibFileName(xslLibFileName);
		settings.setXslFileName(xslFileName);
		
		ArrayList<ProfileField> profileHeaderFields = null;
		ArrayList<ProfileField> profileBodyFields = null;
		
		ArrayList<MetadataField> metadataHeaderFields = null;
		ArrayList<MetadataField> metadataBodyFields = null;
		ArrayList<MetadataField> metadataFooterFields = null;
		
		String error = "";
		
		try {
			ProfileHTML profile = new ProfileHTML(productProfile, "export", profileFileName, xmlLibFileName, "header");
			profileHeaderFields = profile.getHeader().getFields();
			profileBodyFields = profile.getBody().getFields();
			
			FLVExportHTML html = new FLVExportHTML(productProfile, productId, settings.getProfileFileName(), settings.getMetadataFileName(), settings.getXslLibFileName(), "header");
			metadataHeaderFields = html.getMetadataSection().getRow().getFields();
			
			html = new FLVExportHTML(productProfile, productId, settings.getProfileFileName(), settings.getMetadataFileName(), settings.getXslLibFileName(), "body");
			metadataBodyFields = html.getMetadataSection().getRow().getFields();
			
			html = new FLVExportHTML(productProfile, productId, settings.getProfileFileName(), settings.getMetadataFileName(), settings.getXslLibFileName(), "footer");
			metadataFooterFields = html.getMetadataSection().getRow().getFields();
		} catch (Exception ex) {
			error = ex.toString();
		}
		
		Map<String, Object> datas = new HashMap<String, Object>();
		
		for (MetadataField header : metadataHeaderFields) {
			if (!header.getTagName().isEmpty() && !header.getSource().equals("var") && !datas.containsKey(header.getTagName())) {
				ProfileField profileField = profileHeaderFields.stream().filter(f -> f.getName().equals(header.getTagName())).findFirst().orElse(null);
				Object value = IDataUtil.get(MsBillCursor, profileField.getFieldName());
				datas.put(header.getTagName(), value);
			}
		}
		
		for (MetadataField body : metadataBodyFields) {
			if (!body.getTagName().isEmpty() && !body.getSource().equals("var") && !datas.containsKey(body.getTagName())) {
				ProfileField profileField = profileBodyFields.stream().filter(f -> f.getName().equals(body.getTagName())).findFirst().orElse(null);
				Object value = IDataUtil.get(MsBillCursor, profileField.getFieldName());
				datas.put(body.getTagName(), value);
			}
		}
		
		for (MetadataField footer : metadataFooterFields) {
			if (!footer.getTagName().isEmpty() && !footer.getSource().equals("var") && !datas.containsKey(footer.getTagName())) {
				ProfileField profileField = profileBodyFields.stream().filter(f -> f.getName().equals(footer.getTagName())).findFirst().orElse(null);
				Object value = IDataUtil.get(MsBillCursor, profileField.getFieldName());
				datas.put(footer.getTagName(), value);
			}
		}
		
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		data.add(datas);
		
		List<Map<String, Object>> headerData = new ArrayList<Map<String, Object>>();
		for (Map.Entry<String, Object> d : datas.entrySet()) {
			Map<String, Object> headerD = new HashMap<String, Object>();
			headerD.put("keyId", d.getKey());
			headerD.put("value", d.getValue());
			headerData.add(headerD);
		}
		
		
		String text = "";
		
		try {
			JDBCExportToFLV exportData = new JDBCExportToFLV(productProfile, productId, settings);
			exportData.setHeader(headerData);
			exportData.setBody(data);
			exportData.setFooter(headerData);
			text = exportData.export();
		} catch (Exception ex) {
			error = ex.toString();
		}
		
		Boolean success = error.equals("") ? true : false;
		IDataUtil.put(cursor, "success", success);
		IDataUtil.put(cursor, "error", error);
		IDataUtil.put(cursor, "data", text);
		// --- <<IS-END>> ---

                
	}



	public static final void JDBCExportToJson (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(JDBCExportToJson)>> ---
		// @sigtype java 3.5
		// [i] recref:1:required MsParamProducts nds_product.rest_service.entities:MsParamProduct
		// [i] recref:0:required MsBill nds_product.rest_service.entities:MsBill
		// [i] field:0:required product_id
		// [o] field:0:required data
		// [o] object:0:required success
		// [o] field:0:required error
		IDataCursor cursor = pipeline.getCursor();
		
		String productId = IDataUtil.getString(cursor, "product_id");
		
		IData MsProductApi = IDataUtil.getIData(cursor, "MsProductApi");
		IDataCursor MsProductApiCursor = MsProductApi.getCursor();
		
		String productProfile = IDataUtil.getString(MsProductApiCursor, "product_profile");
		
		IData[] MsParamProducts = IDataUtil.getIDataArray(cursor, "MsParamProducts");
		IData MsBill = IDataUtil.getIData(cursor, "MsBill");
		IDataCursor MsBillCursor = MsBill.getCursor();
		
		String profileFileName = null;
		String metadataFileName = IDataUtil.getString(MsProductApiCursor, "request_body_schema");
		String xmlLibFileName = null;
		String xslLibFileName = null;
		String xslFileName = IDataUtil.getString(MsProductApiCursor, "request_body_xsl");
		
		for (IData MsParamProduct : MsParamProducts) {
			IDataCursor MsParamProductCursor = MsParamProduct.getCursor();
				
			String keyParam = IDataUtil.getString(MsParamProductCursor, "key_param");
			String valueParam = IDataUtil.getString(MsParamProductCursor, "value_param");
		
			switch (keyParam) {
				case "xml-lib":
					xmlLibFileName = valueParam;
					break;
				case "xsl-lib":
					xslLibFileName = valueParam;
					break;
				default:
					profileFileName = valueParam;
					break;
			}
			
			MsParamProductCursor.destroy();
		}
		
		ExportImportSettings settings = new ExportImportSettings();
		settings.setProfileFileName(profileFileName);
		settings.setMetadataFileName(metadataFileName);
		settings.setXmlLibFileName(xmlLibFileName);
		settings.setXslLibFileName(xslLibFileName);
		settings.setXslFileName(xslFileName);
		
		ArrayList<ProfileField> profileHeaderFields = null;
		ArrayList<ProfileField> profileBodyFields = null;
		
		ArrayList<MetadataField> metadataHeaderFields = null;
		ArrayList<MetadataField> metadataBodyFields = null;
		ArrayList<MetadataField> metadataFooterFields = null;
		
		String error = "";
		
		try {
			ProfileHTML profile = new ProfileHTML(productProfile, "export", profileFileName, xmlLibFileName, "header");
			profileHeaderFields = profile.getHeader().getFields();
			profileBodyFields = profile.getBody().getFields();
			
			CSVExportHTML html = new CSVExportHTML(productProfile, productId, settings.getProfileFileName(), settings.getMetadataFileName(), settings.getXslLibFileName(), "header");
			metadataHeaderFields = html.getMetadataSection().getRow().getFields();
			
			html = new CSVExportHTML(productProfile, productId, settings.getProfileFileName(), settings.getMetadataFileName(), settings.getXslLibFileName(), "body");
			metadataBodyFields = html.getMetadataSection().getRow().getFields();
			
			html = new CSVExportHTML(productProfile, productId, settings.getProfileFileName(), settings.getMetadataFileName(), settings.getXslLibFileName(), "footer");
			metadataFooterFields = html.getMetadataSection().getRow().getFields();
		} catch (Exception ex) {
			error = ex.toString();
		}
		
		Map<String, Object> datas = new HashMap<String, Object>();
		
		for (MetadataField header : metadataHeaderFields) {
			if (!header.getTagName().isEmpty() && !header.getSource().equals("var") && !datas.containsKey(header.getTagName())) {
				ProfileField profileField = profileHeaderFields.stream().filter(f -> f.getName().equals(header.getTagName())).findFirst().orElse(null);
				Object value = IDataUtil.get(MsBillCursor, profileField.getFieldName());
				datas.put(header.getTagName(), value);
			}
		}
		
		for (MetadataField body : metadataBodyFields) {
			if (!body.getTagName().isEmpty() && !body.getSource().equals("var") && !datas.containsKey(body.getTagName())) {
				ProfileField profileField = profileBodyFields.stream().filter(f -> f.getName().equals(body.getTagName())).findFirst().orElse(null);
				Object value = IDataUtil.get(MsBillCursor, profileField.getFieldName());
				datas.put(body.getTagName(), value);
			}
		}
		
		for (MetadataField footer : metadataFooterFields) {
			if (!footer.getTagName().isEmpty() && !footer.getSource().equals("var") && !datas.containsKey(footer.getTagName())) {
				ProfileField profileField = profileBodyFields.stream().filter(f -> f.getName().equals(footer.getTagName())).findFirst().orElse(null);
				Object value = IDataUtil.get(MsBillCursor, profileField.getFieldName());
				datas.put(footer.getTagName(), value);
			}
		}
		
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		data.add(datas);
		
		List<Map<String, Object>> headerData = new ArrayList<Map<String, Object>>();
		for (Map.Entry<String, Object> d : datas.entrySet()) {
			Map<String, Object> headerD = new HashMap<String, Object>();
			headerD.put("keyId", d.getKey());
			headerD.put("value", d.getValue());
			headerData.add(headerD);
		}
		
		
		String text = "";
		
		try {
			JDBCExportToJson exportData = new JDBCExportToJson(productProfile, productId, settings);
			exportData.setHeader(headerData);
			exportData.setBody(data);
			exportData.setFooter(headerData);
			text = exportData.export();
		} catch (Exception ex) {
			error = ex.toString();
		}
		
		Boolean success = error.equals("") ? true : false;
		IDataUtil.put(cursor, "success", success);
		IDataUtil.put(cursor, "error", error);
		IDataUtil.put(cursor, "data", text);
		// --- <<IS-END>> ---

                
	}



	public static final void JDBCImportFromCSV (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(JDBCImportFromCSV)>> ---
		// @sigtype java 3.5
		// [i] recref:0:required MsProductFile nds_product.rest_service.entities:MsProductFile
		// [i] field:0:required product_id
		// [i] field:0:required text
		// [i] recref:1:required MsParamProducts nds_product.rest_service.entities:MsParamProduct
		// [o] record:1:required header
		// [o] record:1:required body
		// [o] record:1:required footer
		IDataCursor cursor = pipeline.getCursor();
		
		String productId = IDataUtil.getString(cursor, "product_id");
		String text = IDataUtil.getString(cursor, "text");
		
		IData MsProductFile = IDataUtil.getIData(cursor, "MsProductFile");
		IDataCursor MsProductFileCursor = MsProductFile.getCursor();
		
		String productProfile = IDataUtil.getString(MsProductFileCursor, "product_profile");
		
		IData[] MsParamProducts = IDataUtil.getIDataArray(cursor, "MsParamProducts");
		
		String profileFileName = null;
		String metadataFileName = IDataUtil.getString(MsProductFileCursor, "file_xml");
		String xmlLibFileName = null;
		String xslLibFileName = null;
		String xslFileName = IDataUtil.getString(MsProductFileCursor, "file_xsl");
		
		for (IData MsParamProduct : MsParamProducts) {
			IDataCursor MsParamProductCursor = MsParamProduct.getCursor();
				
			String keyParam = IDataUtil.getString(MsParamProductCursor, "key_param");
			String valueParam = IDataUtil.getString(MsParamProductCursor, "value_param");
		
			switch (keyParam) {
				case "xml-lib":
					xmlLibFileName = valueParam;
					break;
				case "xsl-lib":
					xslLibFileName = valueParam;
					break;
				default:
					profileFileName = valueParam;
					break;
			}
			
			MsParamProductCursor.destroy();
		}
		
		ExportImportSettings settings = new ExportImportSettings();
		settings.setProfileFileName(profileFileName);
		settings.setMetadataFileName(metadataFileName);
		settings.setXmlLibFileName(xmlLibFileName);
		settings.setXslLibFileName(xslLibFileName);
		settings.setXslFileName(xslFileName);
		
		Map<String, Object> result = null;
		
		String error = "";
		
		try {
			JDBCImportFromCSV csvImport = new JDBCImportFromCSV(productProfile, productId, settings);
			result = csvImport.importFrom(text, null, true);
		} catch (Exception ex) {
			error = ex.toString();
		}
		
		List<Map<String, Object>> header = (List<Map<String, Object>>) result.get("header");
		List<Map<String, Object>> body = (List<Map<String, Object>>) result.get("body");
		List<Map<String, Object>> footer = (List<Map<String, Object>>) result.get("footer");
		
		
		IData[] headerIData = new IData[header.size()];
		IData[] bodyIData = new IData[body.size()];
		IData[] footerIData = new IData[footer.size()];
		
		for (Integer i = 0; i < header.size(); i++) {
			headerIData[i] = mapToIData(header.get(i));
		}
		
		
		for (Integer i = 0; i < body.size(); i++) {
			bodyIData[i] = mapToIData(header.get(i));
		}
		
		for (Integer i = 0; i < footer.size(); i++) {
			footerIData[i] = mapToIData(header.get(i));
		}
		
		Boolean success = error.equals("") ? true : false;
		IDataUtil.put(cursor, "success", success);
		IDataUtil.put(cursor, "header", headerIData);
		IDataUtil.put(cursor, "body", bodyIData);
		IDataUtil.put(cursor, "footer", footerIData);
		// --- <<IS-END>> ---

                
	}



	public static final void JDBCImportFromFLV (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(JDBCImportFromFLV)>> ---
		// @sigtype java 3.5
		// [i] recref:0:required MsProductFile nds_product.rest_service.entities:MsProductFile
		// [i] field:0:required product_id
		// [i] field:0:required text
		// [i] recref:1:required MsParamProducts nds_product.rest_service.entities:MsParamProduct
		// [o] record:1:required header
		// [o] record:1:required body
		// [o] record:1:required footer
		// [o] field:0:required result
		// [o] field:0:required profileFileName
		// [o] field:0:required metadataFileName
		// [o] field:0:required xmlLibFileName
		// [o] field:0:required xslLibFileName
		// [o] field:0:required xslFileName
		IDataCursor cursor = pipeline.getCursor();
		
		String productId = IDataUtil.getString(cursor, "product_id");
		String text = IDataUtil.getString(cursor, "text");
		
		IData MsProductFile = IDataUtil.getIData(cursor, "MsProductFile");
		IDataCursor MsProductFileCursor = MsProductFile.getCursor();
		
		String productProfile = IDataUtil.getString(MsProductFileCursor, "product_profile");
		
		IData[] MsParamProducts = IDataUtil.getIDataArray(cursor, "MsParamProducts");
		
		String profileFileName = null;
		String metadataFileName = IDataUtil.getString(MsProductFileCursor, "file_xml");
		String xmlLibFileName = null;
		String xslLibFileName = null;
		String xslFileName = IDataUtil.getString(MsProductFileCursor, "file_xsl");
		
		for (IData MsParamProduct : MsParamProducts) {
			IDataCursor MsParamProductCursor = MsParamProduct.getCursor();
				
			String keyParam = IDataUtil.getString(MsParamProductCursor, "key_param");
			String valueParam = IDataUtil.getString(MsParamProductCursor, "value_param");
		
			switch (keyParam) {
				case "xml-lib":
					xmlLibFileName = valueParam;
					break;
				case "xsl-lib":
					xslLibFileName = valueParam;
					break;
				default:
					profileFileName = valueParam;
					break;
			}
			
			MsParamProductCursor.destroy();
		}
		
		ExportImportSettings settings = new ExportImportSettings();
		settings.setProfileFileName(profileFileName);
		settings.setMetadataFileName(metadataFileName);
		settings.setXmlLibFileName(xmlLibFileName);
		settings.setXslLibFileName(xslLibFileName);
		settings.setXslFileName(xslFileName);
		
		Map<String, Object> result = null;
		
		String error = "";
		
		try {
			JDBCImportFromFLV csvImport = new JDBCImportFromFLV(productProfile, productId, settings);
			result = csvImport.importFrom(text, null, false);
		} catch (Exception ex) {
			error = ex.toString();
		}
				
		
		
		
		List<Map<String, Object>> header = (List<Map<String, Object>>) result.get("header");
		List<Map<String, Object>> body = (List<Map<String, Object>>) result.get("body");
		List<Map<String, Object>> footer = (List<Map<String, Object>>) result.get("footer");
		
		
		IData[] headerIData = new IData[header.size()];
		IData[] bodyIData = new IData[body.size()];
		IData[] footerIData = new IData[footer.size()];
		
		for (Integer i = 0; i < header.size(); i++) {
			headerIData[i] = mapToIData(header.get(i));
		}
		
		
		for (Integer i = 0; i < body.size(); i++) {
			bodyIData[i] = mapToIData(header.get(i));
		}
		
		for (Integer i = 0; i < footer.size(); i++) {
			footerIData[i] = mapToIData(header.get(i));
		}
		
		Boolean success = error.equals("") ? true : false;
		IDataUtil.put(cursor, "success", success);
		IDataUtil.put(cursor, "error", error);
		IDataUtil.put(cursor, "header", headerIData);
		IDataUtil.put(cursor, "body", bodyIData);
		IDataUtil.put(cursor, "footer", footerIData);
		// --- <<IS-END>> ---

                
	}



	public static final void getExportHtml (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getExportHtml)>> ---
		// @sigtype java 3.5
	
		// --- <<IS-END>> ---

                
	}



	public static final void prepareConsumerApi (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(prepareConsumerApi)>> ---
		// @sigtype java 3.5
		// [i] field:0:required data
		// [o] record:0:required requestHeader
		// [o] field:0:required requestBody
		// [o] field:0:required queryParam
		IDataCursor cursor = pipeline.getCursor();
		
		String data = IDataUtil.getString(cursor, "data");
		
		JsonObject obj = JsonParser.parseString(data).getAsJsonObject();
		
		JsonObject header = obj.get("header").getAsJsonObject();
		JsonElement body = obj.get("body");
		JsonObject footer = obj.get("footer").getAsJsonObject();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		IData requestHeaders = jsonObjectToIData(header);
		String requestBody = gson.toJson(body);
		String queryParam = jsonObjectToQueryParam(footer);
		
		
		IDataUtil.put(cursor, "requestHeaders", requestHeaders);
		IDataUtil.put(cursor, "requestBody", requestBody);
		IDataUtil.put(cursor, "queryParam", queryParam);
		// --- <<IS-END>> ---

                
	}



	public static final void validateConsumerResponse (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(validateConsumerResponse)>> ---
		// @sigtype java 3.5
		// [i] field:0:required param_name_success
		// [i] field:0:required param_value_success
		// [i] field:0:required http_status_success
		// [i] field:0:required status_code
		// [i] record:0:required responseBody
		// [o] object:0:required success
		// [o] field:0:required error
		IDataCursor cursor = pipeline.getCursor();
		
		String paramNameSuccess = IDataUtil.getString(cursor, "param_name_success");
		List<String> paramValueSuccess = Arrays.asList(
				IDataUtil.getString(cursor, "param_value_success")
				.split("\\|"));
		List<String> httpStatusSuccess = Arrays.asList(
				IDataUtil.getString(cursor, "http_status_success")
				.split("\\|"));
		IData responseBody = IDataUtil.getIData(cursor, "responseBody");
		IDataCursor responseBodyCursor = responseBody.getCursor();
		
		
		Boolean success = true;
		String error = "";
		
		String name = IDataUtil.getString(responseBodyCursor, paramNameSuccess);
		String statusCode = IDataUtil.getString(cursor, "status_code");
		
		if (!httpStatusSuccess.contains(statusCode)) {
			success = false;
			error = "Http Status Code response doesn't match success criteria";
		}
		
		if (name == null) {
			success = false;
			error = "Field " + name + " doesn't exist in response";
		} else {
			if (!paramValueSuccess.contains(name)) {
				success = false;
				error = "Value of field " + name + " doesn't match success criteria";
			}
		}
		
		IDataUtil.put(cursor, "success", success);
		IDataUtil.put(cursor, "error", error);
		
		responseBodyCursor.destroy();
		cursor.destroy();
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	private static IData mapToIData(Map<String, Object> data) {
		IData result = IDataFactory.create();
		IDataCursor cursor = result.getCursor();
		
		for (Map.Entry<String, Object> d : data.entrySet()) {
			IDataUtil.put(cursor, d.getKey(), d.getValue());
		}
		
		cursor.destroy();
		return result;
	}
	
	private static IFormatterHTML getExportHtml(String productType, String productProfile, 
		String fileType, String productId, String sectionName, ExportImportSettings settings) throws ProfileException, CSVMetadataException, FLVMetadataException {
	
		IFormatterHTML html = null;
		if (productType.equals("api")) {
			html = new CSVExportHTML(productProfile, productId, settings.getProfileFileName(), settings.getMetadataFileName(), settings.getXslLibFileName(), sectionName);
		} else {
			if (fileType.equals("csv")) {
				html = new CSVExportHTML(productProfile, productId, settings.getProfileFileName(), settings.getMetadataFileName(), settings.getXslLibFileName(), sectionName);
			} else {
				html = new FLVExportHTML(productProfile, productId, settings.getProfileFileName(), settings.getMetadataFileName(), settings.getXslLibFileName(), sectionName);
			}
		}
	
		return html;
	}
	
	private static String jsonObjectToQueryParam(JsonObject obj) {
		String queryParam = "?";
	
		Integer count = 0;
		for (Map.Entry<String, JsonElement> o : obj.entrySet()) {
			if (count != 0) {
				queryParam += "&";
			}
			String key = o.getKey() + "=";
			if (o.getValue().isJsonArray()) {
				JsonArray arr = o.getValue().getAsJsonArray();
				
				for (Integer i = 0; i < arr.size(); i++) {
					if (i != 0) {
						queryParam += "&";
					}
					queryParam += key + arr.get(i).getAsString();
				}
			} else {
				queryParam += key + o.getValue().getAsString();
			}
			count++;
		}
		return queryParam;
	}
	
	private static IData jsonObjectToIData(JsonObject data) {
		IData iData = IDataFactory.create();
		IDataCursor cursor = iData.getCursor();
	
		for (Map.Entry<String, JsonElement> d : data.entrySet()) {
			IDataUtil.put(cursor, d.getKey(), d.getValue().getAsString());
		}
	
		cursor.destroy();
		return iData;
	}
	private static List<Map<String, Object>> IDataArrayToMap(IData[] dataArray) {
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		for (IData data : dataArray) {
			
			IDataCursor dataCursor = data.getCursor();
			
			Map<String, Object> result = new HashMap<String, Object>();
			while (dataCursor.next()) {
				String key = dataCursor.getKey();
				Object value = dataCursor.getValue();
				
				result.put(key, value);
			}
			results.add(result);
		}
		return results;
	}
	
		
	// --- <<IS-END-SHARED>> ---
}

