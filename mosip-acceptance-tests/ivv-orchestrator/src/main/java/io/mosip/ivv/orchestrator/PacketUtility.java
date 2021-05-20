package io.mosip.ivv.orchestrator;

import static io.restassured.RestAssured.given;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Reporter;
import io.mosip.admin.fw.util.AdminTestException;
import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.AuthPartnerProcessor;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.ivv.core.exceptions.RigInternalError;
import io.mosip.ivv.e2e.constant.E2EConstants;
import io.mosip.testscripts.BioAuth;
import io.restassured.response.Response;

public class PacketUtility extends BaseTestCaseUtil {
	Logger logger = Logger.getLogger(PacketUtility.class);
	public List<String> generateResidents(int n, Boolean bAdult, Boolean bSkipGuardian, String gender,
			String missFields, HashMap<String, String> contextKey) throws RigInternalError {
		
		String url = baseUrl + props.getProperty("getResidentUrl") + n;
		JSONObject jsonwrapper = new JSONObject();
		JSONObject jsonReq = new JSONObject();
		JSONObject residentAttrib = new JSONObject();
		if (bAdult) {
			residentAttrib.put("Age", "RA_Adult");
		} else {
			residentAttrib.put("Age", "RA_Minor");
			residentAttrib.put("SkipGaurdian", bSkipGuardian);
		}
		residentAttrib.put("Gender", gender);
		residentAttrib.put("PrimaryLanguage", "eng");
		residentAttrib.put("Iris", true);
		// added for face biometric related issue
		residentAttrib.put("Finger", true);
		residentAttrib.put("Face", true);
		//
		
		if (missFields != null)
			residentAttrib.put("Miss", missFields);
		jsonReq.put("PR_ResidentAttribute", residentAttrib);
		jsonwrapper.put("requests", jsonReq);

		// Response response = postReqest(url, jsonwrapper.toString(),
		// "GENERATE_RESIDENTS_DATA");
		Response response = postRequestWithQueryParamAndBody(url, jsonwrapper.toString(), contextKey,
				"GENERATE_RESIDENTS_DATA");
		// assertTrue(response.getBody().asString().contains("SUCCESS"),"Unable to get
		// residentData from packet utility");
		if (!response.getBody().asString().toLowerCase().contains("success"))
			throw new RigInternalError("Unable to get residentData from packet utility");
		// assertTrue(response.getBody().asString().contains("Failure"),"Unable to get
		// residentData from packet utility");
		JSONArray resp = new JSONObject(response.getBody().asString()).getJSONArray("response");
		List<String> residentPaths = new ArrayList<>();
		for (int i = 0; i < resp.length(); i++) {
			JSONObject obj = resp.getJSONObject(i);
			String resFilePath = obj.get("path").toString();
			residentPaths.add(resFilePath);
			// residentTemplatePaths.put(resFilePath, null);
		}
		return residentPaths;

	}
	
	
	public Response generateResident(int n, Boolean bAdult, Boolean bSkipGuardian, String gender,
			String missFields, HashMap<String, String> contextKey) throws RigInternalError {
		
		String url = baseUrl + props.getProperty("getResidentUrl") + n;
		JSONObject jsonwrapper = new JSONObject();
		JSONObject jsonReq = new JSONObject();
		JSONObject residentAttrib = new JSONObject();
		if (bAdult) {
			residentAttrib.put("Age", "RA_Adult");
		} else {
			residentAttrib.put("Age", "RA_Minor");
			residentAttrib.put("SkipGaurdian", bSkipGuardian);
		}
		residentAttrib.put("Gender", gender);
		residentAttrib.put("PrimaryLanguage", "eng");
		residentAttrib.put("Iris", true);
		// added for face biometric related issue
		residentAttrib.put("Finger", true);
		residentAttrib.put("Face", true);
		//
		
		if (missFields != null)
			residentAttrib.put("Miss", missFields);
		jsonReq.put("PR_ResidentAttribute", residentAttrib);
		jsonwrapper.put("requests", jsonReq);

		// Response response = postReqest(url, jsonwrapper.toString(),
		// "GENERATE_RESIDENTS_DATA");
		Response response = postRequestWithQueryParamAndBody(url, jsonwrapper.toString(), contextKey,
				"GENERATE_RESIDENTS_DATA");
		return response;

	}
	
	public JSONArray getTemplate(Set<String> resPath, String process, HashMap<String, String> contextKey)
			throws RigInternalError {
		JSONObject jsonReq = new JSONObject();
		JSONArray arr = new JSONArray();
		for (String residentPath : resPath) {

			arr.put(residentPath);
		}
		jsonReq.put("personaFilePath", arr);
		String url = baseUrl + props.getProperty("getTemplateUrl") + process + "/ /";
		// Response templateResponse = postReqest(url, jsonReq.toString(),
		// "GET-TEMPLATE");
		Response templateResponse = postRequestWithQueryParamAndBody(url, jsonReq.toString(), contextKey,
				"GET-TEMPLATE");
		JSONObject jsonResponse = new JSONObject(templateResponse.asString());
		JSONArray resp = jsonResponse.getJSONArray("packets");
		if ((resp.length() <= 0))
			throw new RigInternalError("Unable to get Template from packet utility");
		return resp;
	}

	public void requestOtp(String resFilePath, HashMap<String, String> contextKey, String emailOrPhone) {
		String url = baseUrl + props.getProperty("sendOtpUrl") + emailOrPhone;
		JSONObject jsonReq = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(resFilePath);
		jsonReq.put("personaFilePath", jsonArray);
		// postReqest(url,jsonReq.toString(),"Send Otp");
		postRequestWithQueryParamAndBody(url, jsonReq.toString(), contextKey, "Send Otp");

	}

	public void verifyOtp(String resFilePath, HashMap<String, String> contextKey, String emailOrPhone)
			throws RigInternalError {
		String url = baseUrl + props.getProperty("verifyOtpUrl") + emailOrPhone;
		JSONObject jsonReq = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(resFilePath);
		jsonReq.put("personaFilePath", jsonArray);
		// Response response =postReqest(url,jsonReq.toString(),"Verify Otp");
		Response response = postRequestWithQueryParamAndBody(url, jsonReq.toString(), contextKey, "Verify Otp");
		// assertTrue(response.getBody().asString().contains("VALIDATION_SUCCESSFUL"),"Unable
		// to Verify Otp from packet utility");
		if (!response.getBody().asString().toLowerCase().contains("validation_successful"))
			throw new RigInternalError("Unable to Verify Otp from packet utility");

	}

	public String preReg(String resFilePath, HashMap<String, String> contextKey) throws RigInternalError {
		String url = baseUrl + props.getProperty("preregisterUrl");
		JSONObject jsonReq = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(resFilePath);
		jsonReq.put("personaFilePath", jsonArray);
		// Response response =postReqest(url,jsonReq.toString(),"AddApplication");
		Response response = postRequestWithQueryParamAndBody(url, jsonReq.toString(), contextKey, "AddApplication");
		String prid = response.getBody().asString();
		// assertTrue((int)prid.charAt(0)>47 && (int)prid.charAt(0)<58 ,"Unable to
		// pre-register from packet utility");
		if (!((int) prid.charAt(0) > 47 && (int) prid.charAt(0) < 58))
			throw new RigInternalError("Unable to pre-register using packet utility");
		return prid;

	}

//	public String updateApplication(String resFilePath, HashMap<String, String> residentPathsPrid,
//			HashMap<String, String> contextKey) throws RigInternalError {
//		String url = baseUrl + props.getProperty("updateApplication") + residentPathsPrid.get(resFilePath);
//		JSONObject jsonReq = new JSONObject();
//		JSONArray jsonArray = new JSONArray();
//		jsonArray.put(resFilePath);
//		jsonReq.put("personaFilePath", jsonArray);
//		Response response = putRequestWithQueryParamAndBody(url, jsonReq.toString(), contextKey, "UpdateApplication");
//		String prid = response.getBody().asString();
//		if (!((int) prid.charAt(0) > 47 && (int) prid.charAt(0) < 58))
//			throw new RigInternalError("Unable to updateApplication using packet utility");
//		return prid;
//
//	}

	public void uploadDocuments(String resFilePath, String prid, HashMap<String, String> contextKey) {
		String url = baseUrl + "/prereg/documents/" + prid;
		JSONObject jsonReq = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(resFilePath);
		jsonReq.put("personaFilePath", jsonArray);
		// postReqest(url,jsonReq.toString(),"Upload Documents");
		postRequestWithQueryParamAndBody(url, jsonReq.toString(), contextKey, "Upload Documents");
	}

	public void bookAppointment(String prid, int nthSlot, HashMap<String, String> contextKey, boolean bookOnHolidays)
			throws RigInternalError {
		//String url = baseUrl + "/bookappointment/" + prid + "/" + nthSlot + "/" + bookOnHolidays;
		String url = baseUrl + "/prereg/appointment/" + prid + "/" + nthSlot + "/" + bookOnHolidays;
		JSONObject jsonReq = new JSONObject();
		Response response = postRequestWithQueryParamAndBody(url, jsonReq.toString(), contextKey, "BookAppointment");
		if (!response.getBody().asString().toLowerCase().contains("appointment booked successfully"))
			throw new RigInternalError("Unable to BookAppointment from packet utility");
	}

	public String generateAndUploadPacket(String prid, String packetPath, HashMap<String, String> contextKey,String responseStatus)
			throws RigInternalError {
		String rid =null;
		String url = baseUrl + "/packet/sync/" + prid;
		JSONObject jsonReq = new JSONObject();
		JSONArray arr = new JSONArray();
		arr.put(packetPath);
		jsonReq.put("personaFilePath", arr);
		// Response response =postReqest(url,jsonReq.toString(),"Generate And
		// UploadPacket");
		Response response = postRequestWithQueryParamAndBody(url, jsonReq.toString(), contextKey,
				"Generate And UploadPacket");
		if(!(response.getBody().asString().toLowerCase().contains("failed"))) {
			JSONObject jsonResp = new JSONObject(response.getBody().asString());
			rid = jsonResp.getJSONObject("response").getString("registrationId");
		}
		//JSONObject jsonResp = new JSONObject(response.getBody().asString());
		//String rid = jsonResp.getJSONObject("response").getString("registrationId");
		// assertTrue(response.getBody().asString().contains("SUCCESS") ,"Unable to
		// Generate And UploadPacket from packet utility");
		//if (!response.getBody().asString().toLowerCase().contains("success"))
		if (!response.getBody().asString().toLowerCase().contains(responseStatus))
			throw new RigInternalError("Unable to Generate And UploadPacket from packet utility");
		return rid;
	}

	public String updateResidentRid(String personaFilePath, String rid) throws RigInternalError {
		String url = baseUrl + props.getProperty("updateResidentUrl") + "?RID=" + rid;

		JSONObject jsonwrapper = new JSONObject();
		JSONObject jsonReq = new JSONObject();
		JSONObject residentAttrib = new JSONObject();

		residentAttrib.put("rid", personaFilePath);

		jsonReq.put("PR_ResidentList", residentAttrib);

		jsonwrapper.put("requests", jsonReq);

		Response response = postReqest(url, jsonwrapper.toString(), "link Resident data with RID");

		if (!response.getBody().asString().toLowerCase().contains("success"))
			throw new RigInternalError("Unable to add Resident RID in resident data");
		String ret = response.getBody().asString();
		return ret;

	}
	
	public String updateResidentUIN(String personaFilePath, String uin) throws RigInternalError {
		String url = baseUrl + props.getProperty("updateResidentUrl") + "?UIN=" + uin;

		JSONObject jsonwrapper = new JSONObject();
		JSONObject jsonReq = new JSONObject();
		JSONObject residentAttrib = new JSONObject();

		residentAttrib.put("uin", personaFilePath);

		jsonReq.put("PR_ResidentList", residentAttrib);

		jsonwrapper.put("requests", jsonReq);

		Response response = postReqest(url, jsonwrapper.toString(), "link Resident data with UIN");

		if (!response.getBody().asString().toLowerCase().contains("success"))
			throw new RigInternalError("Unable to add UIN in resident data");
		String ret = response.getBody().asString();
		return ret;

	}

	public String updateResidentGuardian(String residentFilePath, boolean withRid, String missingFields,
			String parentEmailOrPhone) throws RigInternalError {
		Reporter.log("<b><u>Execution Steps for Generating GuardianPacket And linking with Child Resident: </u></b>");
		/*
		 * String missingField=null; //boolean isGaurdianVal=false;
		 * if(isGaurdianValid!=null) { //missingFields=isGaurdianValid.split("@@");
		 * //isGaurdianVal=Boolean.parseBoolean(missingFields[0]);
		 * missingField=missingFields[0]; }
		 */
		// List<String> generatedResidentData = generateResidents(1,
		// true,true,"Any",null,contextKey);
		List<String> generatedResidentData = generateResidents(1, true, true, "Any", missingFields, contextInuse);
		JSONArray jsonArray = getTemplate(new HashSet<String>(generatedResidentData), "NEW", contextInuse);
		JSONObject obj = jsonArray.getJSONObject(0);
		String templatePath = obj.get("path").toString();
		requestOtp(generatedResidentData.get(0), contextInuse, parentEmailOrPhone);
		verifyOtp(generatedResidentData.get(0), contextInuse, parentEmailOrPhone);
		String prid = preReg(generatedResidentData.get(0), contextInuse);
		uploadDocuments(generatedResidentData.get(0), prid, contextInuse);
		bookAppointment(prid, 1, contextInuse, false);
		String rid = generateAndUploadPacket(prid, templatePath, contextInuse,"success");

		String url = baseUrl + props.getProperty("updateResidentUrl");

		if (withRid)
			updateResidentRid(generatedResidentData.get(0), rid);

		JSONObject jsonwrapper = new JSONObject();
		JSONObject jsonReq = new JSONObject();
		JSONObject residentAttrib = new JSONObject();
		residentAttrib.put("guardian", generatedResidentData.get(0));
		residentAttrib.put("child", residentFilePath);
		jsonReq.put("PR_ResidentList", residentAttrib);
		jsonwrapper.put("requests", jsonReq);
		Response response = postReqest(url, jsonwrapper.toString(), "Update Resident Guardian");
		// assertTrue(response.getBody().asString().contains("SUCCESS") ,"Unable to
		// update Resident Guardian from packet utility");
		Reporter.log("<b><u>Generated GuardianPacket with Rid: " + rid + " And linked to child </u></b>");
		if (!response.getBody().asString().toLowerCase().contains("success"))
			throw new RigInternalError("Unable to update Resident Guardian from packet utility");
		return rid;

	}

	public String updateResidentWithGuardianSkippingPreReg(String residentFilePath, HashMap<String, String> contextKey,
			boolean withRid, String missingFields) throws RigInternalError {
		Reporter.log("<b><u>Execution Steps for Generating GuardianPacket And linking with Child Resident: </u></b>");
		/*
		 * String missingField=null; boolean isGaurdianVal=false; String
		 * []missingFields=null; if(isGaurdianValid!=null&&!isGaurdianValid.isEmpty()) {
		 * missingFields=isGaurdianValid.split("@@");
		 * isGaurdianVal=Boolean.parseBoolean(missingFields[0]); if(isGaurdianVal)
		 * missingField=missingFields[1]; }
		 */
		// List<String> generatedResidentData = generateResidents(1,
		// true,true,"Any",null,contextKey);
		List<String> generatedResidentData = generateResidents(1, true, true, "Any", missingFields, contextKey);
		JSONArray jsonArray = getTemplate(new HashSet<String>(generatedResidentData), "NEW", contextKey);
		JSONObject obj = jsonArray.getJSONObject(0);
		String templatePath = obj.get("path").toString();
		String rid = generateAndUploadPacketSkippingPrereg(templatePath, generatedResidentData.get(0), contextKey,"success");

		String url = baseUrl + props.getProperty("updateResidentUrl");

		if (withRid)
			updateResidentRid(generatedResidentData.get(0), rid);

		JSONObject jsonwrapper = new JSONObject();
		JSONObject jsonReq = new JSONObject();
		JSONObject residentAttrib = new JSONObject();
		residentAttrib.put("guardian", generatedResidentData.get(0));
		residentAttrib.put("child", residentFilePath);
		jsonReq.put("PR_ResidentList", residentAttrib);
		jsonwrapper.put("requests", jsonReq);
		Response response = postReqest(url, jsonwrapper.toString(), "Update Resident Guardian");
		// assertTrue(response.getBody().asString().contains("SUCCESS") ,"Unable to
		// update Resident Guardian from packet utility");
		if (!response.getBody().asString().toLowerCase().contains("success"))
			throw new RigInternalError("Unable to update Resident Guardian from packet utility");
		Reporter.log("<b><u>Generated GuardianPacket with Rid: " + rid + " And linked to child </u></b>");
		return rid;

	}

	public String generateAndUploadPacketSkippingPrereg(String packetPath, String residentPath,
			HashMap<String, String> contextKey,String responseStatus) throws RigInternalError {
		String rid = null;
		String url = baseUrl + "/packet/sync/0";
		JSONObject jsonReq = new JSONObject();
		JSONArray arr = new JSONArray();
		arr.put(0, packetPath);
		arr.put(1, residentPath);
		jsonReq.put("personaFilePath", arr);
		Response response = postRequestWithQueryParamAndBody(url, jsonReq.toString(), contextKey,
				"Generate And UploadPacket");
		if(!(response.getBody().asString().toLowerCase().contains("failed"))) {
			JSONObject jsonResp = new JSONObject(response.getBody().asString());
			rid = jsonResp.getJSONObject("response").getString("registrationId");
		}
		//JSONObject jsonResp = new JSONObject(response.getBody().asString());
		//String rid = jsonResp.getJSONObject("response").getString("registrationId");
		// assertTrue(response.getBody().asString().contains("SUCCESS") ,"Unable to
		// Generate And UploadPacket from packet utility");
		//if (!response.getBody().asString().toLowerCase().contains("success"))
		if (!response.getBody().asString().toLowerCase().contains(responseStatus))
			throw new RigInternalError("Unable to Generate And UploadPacket from packet utility");
		return rid;
	}

	public String createContext(String key, String baseUrl) throws RigInternalError {
		String url = this.baseUrl + "/servercontext/" + key;
		
		JSONObject jsonReq = new JSONObject();
		jsonReq.put("urlBase", baseUrl);
		jsonReq.put("mosip.test.baseurl", baseUrl);
		jsonReq.put("mosip.test.regclient.machineid", E2EConstants.MACHINE_ID);
		jsonReq.put("mosip.test.regclient.centerid", E2EConstants.CENTER_ID);
		jsonReq.put("regclient.centerid", E2EConstants.CENTER_ID);
		jsonReq.put("mosip.test.regclient.userid", E2EConstants.USER_ID);
		jsonReq.put("prereg.operatorId", E2EConstants.USER_ID);
		jsonReq.put("mosip.test.regclient.password", E2EConstants.USER_PASSWD);
		jsonReq.put("prereg.password", E2EConstants.USER_PASSWD);
		jsonReq.put("mosip.test.regclient.supervisorid", E2EConstants.SUPERVISOR_ID);
		jsonReq.put("prereg.preconfiguredOtp", E2EConstants.PRECONFIGURED_OTP);
		Response response = postReqest(url, jsonReq.toString(), "SetContext");
		// Response response =
		// given().contentType(ContentType.JSON).body(jsonReq.toString()).post(url);
		if (!response.getBody().asString().toLowerCase().contains("true"))
			throw new RigInternalError("Unable to set context from packet utility");
		return response.getBody().asString();

	}
	
	
	public String createContexts(String key, String userAndMachineDetailParam, String baseUrl) throws RigInternalError {
		String url = this.baseUrl + "/servercontext/" + key;
		Map<String,String> map= new HashMap<String,String>();
		if(userAndMachineDetailParam!=null && !userAndMachineDetailParam.isEmpty()) {
			String[] details=userAndMachineDetailParam.split("@@");
			for (String detail : details) {
	            String detailData[] = detail.split("=");
	            String keys = detailData[0].trim();
	            String value = detailData[1].trim();
	            map.put(keys, value);
	        }
				
		}
	//  machineid=10082@@centerid=10002@@userid=110126@@password=Techno@123@@supervisorid=110126
		JSONObject jsonReq = new JSONObject();
		jsonReq.put("urlBase", baseUrl);
		jsonReq.put("mosip.test.baseurl", baseUrl);
		jsonReq.put("mosip.test.regclient.machineid", (map.get("machineid")!=null)?map.get("machineid"):E2EConstants.MACHINE_ID);
		jsonReq.put("mosip.test.regclient.centerid", (map.get("centerid")!=null)?map.get("centerid"):E2EConstants.CENTER_ID);
		jsonReq.put("regclient.centerid", (map.get("centerid")!=null)?map.get("centerid"):E2EConstants.CENTER_ID);
		jsonReq.put("mosip.test.regclient.userid", (map.get("userid")!=null)?map.get("userid"):E2EConstants.USER_ID);
		jsonReq.put("prereg.operatorId", (map.get("userid")!=null)?map.get("userid"):E2EConstants.USER_ID);
		jsonReq.put("mosip.test.regclient.password", (map.get("password")!=null)?map.get("password"):E2EConstants.USER_PASSWD);
		jsonReq.put("prereg.password", (map.get("password")!=null)?map.get("password"):E2EConstants.USER_PASSWD);
		jsonReq.put("mosip.test.regclient.supervisorid", (map.get("supervisorid")!=null)?map.get("supervisorid"):E2EConstants.SUPERVISOR_ID);
		jsonReq.put("prereg.preconfiguredOtp", E2EConstants.PRECONFIGURED_OTP);
		Response response = postReqest(url, jsonReq.toString(), "SetContext");
		// Response response =
		// given().contentType(ContentType.JSON).body(jsonReq.toString()).post(url);
		if (!response.getBody().asString().toLowerCase().contains("true"))
			throw new RigInternalError("Unable to set context from packet utility");
		return response.getBody().asString();

	}

	public String updateBiometric(String resFilePath, List<String> attributeList, List<String> missAttributeList)
			throws RigInternalError {
		String url = baseUrl + props.getProperty("updatePersonaData");
		JSONObject jsonReqInner = new JSONObject();
		if (missAttributeList != null && !(missAttributeList.isEmpty()))
			jsonReqInner.put("missAttributeList", missAttributeList);
		jsonReqInner.put("personaFilePath", resFilePath);
		if (attributeList != null && !(attributeList.isEmpty()))
			jsonReqInner.put("regenAttributeList", attributeList);
		JSONArray jsonReq = new JSONArray();
		jsonReq.put(0, jsonReqInner);
		//Response response = postReqest(url, jsonReq.toString(), "Update BiometricData");
		Response response = putReqestWithBody(url, jsonReq.toString(), "Update BiometricData");
		if (!response.getBody().asString().toLowerCase().contains("sucess"))
			throw new RigInternalError("Unable to update BiometricData " + attributeList + " from packet utility");
		return response.getBody().asString();

	}
	
	public String packetSync(String personaPath, HashMap<String,String> contextKey) throws RigInternalError {
		String url = baseUrl + props.getProperty("packetsyncUrl");
		JSONObject jsonReq = new JSONObject();
		JSONArray arr = new JSONArray();
		arr.put(personaPath);	
		jsonReq.put("personaFilePath",arr);
		Response response =postRequestWithQueryParamAndBody(url,jsonReq.toString(),contextKey,"Packet Sync:");
		if(!response.getBody().asString().toLowerCase().contains("packet has reached"))
			throw new RigInternalError("Unable to do sync packet from packet utility");
		return response.getBody().asString();
	}
	
	 public void bioAuth(String modility, String bioValue, String uin, Properties deviceProps, TestCaseDTO test, BioAuth bioAuth) throws RigInternalError {
		 
		 test.setEndPoint(test.getEndPoint().replace("$PartnerKey$", deviceProps.getProperty("partnerKey")));
		 String input = test.getInput();
		 input = JsonPrecondtion.parseAndReturnJsonContent(input,uin, "individualId");
		 input = JsonPrecondtion.parseAndReturnJsonContent(input,
					deviceProps.getProperty("bioSubType"), "identityRequest.bioSubType");
		 input = JsonPrecondtion.parseAndReturnJsonContent(input,
					deviceProps.getProperty("bioType"), "identityRequest.bioType");
		 input = JsonPrecondtion.parseAndReturnJsonContent(input,
					deviceProps.getProperty("deviceCode"), "identityRequest.deviceCode");
		 input = JsonPrecondtion.parseAndReturnJsonContent(input,
					deviceProps.getProperty("deviceProviderID"), "identityRequest.deviceProviderID");
		 input = JsonPrecondtion.parseAndReturnJsonContent(input,
					deviceProps.getProperty("deviceServiceID"), "identityRequest.deviceServiceID");
		 input = JsonPrecondtion.parseAndReturnJsonContent(input,
					deviceProps.getProperty("deviceServiceVersion"), "identityRequest.deviceServiceVersion");
		 input = JsonPrecondtion.parseAndReturnJsonContent(input,
					deviceProps.getProperty("deviceProvider"), "identityRequest.deviceProvider");
		 input = JsonPrecondtion.parseAndReturnJsonContent(input,
					deviceProps.getProperty("deviceSubType"), "identityRequest.deviceSubType");
		 input = JsonPrecondtion.parseAndReturnJsonContent(input,
					deviceProps.getProperty("make"), "identityRequest.make");
		 input = JsonPrecondtion.parseAndReturnJsonContent(input,
					deviceProps.getProperty("model"), "identityRequest.model");
		 input = JsonPrecondtion.parseAndReturnJsonContent(input,
					deviceProps.getProperty("serialNo"), "identityRequest.serialNo");
		 input = JsonPrecondtion.parseAndReturnJsonContent(input,
					deviceProps.getProperty("type"), "identityRequest.type");
		 input = JsonPrecondtion.parseAndReturnJsonContent(input,
					deviceProps.getProperty("individualIdType"), "individualIdType");
		 input = JsonPrecondtion.parseAndReturnJsonContent(input, bioValue, "identityRequest.bioValue");
		 test.setInput(input);
		 Reporter.log("<b><u>" + test.getTestCaseName()+"_"+ modility + "</u></b>");

			try {
				bioAuth.test(test);
			} catch (AuthenticationTestException | AdminTestException e) {
				throw new RigInternalError(e.getMessage());
			}finally {
				 AuthPartnerProcessor.authPartherProcessor.destroyForcibly();

			}
	 }
	 public String retrieveBiometric(String resFilePath, List<String> retriveAttributeList) throws RigInternalError {
			String url = baseUrl + props.getProperty("getPersonaData");
			JSONObject jsonReqInner = new JSONObject();
			if (retriveAttributeList != null && !(retriveAttributeList.isEmpty()))
				jsonReqInner.put("retriveAttributeList", retriveAttributeList);
			jsonReqInner.put("personaFilePath", resFilePath);
			JSONArray jsonReq = new JSONArray();
			jsonReq.put(0, jsonReqInner);
			Response response = getReqest(url, jsonReq.toString(), "Retrive BiometricData");
			if (response.getBody().asString().equals(""))
				throw new RigInternalError(
						"Unable to retrive BiometricData " + retriveAttributeList + " from packet utility");
			return response.getBody().asString();

		}

		private Response getReqest(String url, String body, String opsToLog) {
			Response apiResponse = getRequestWithbody(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
			return apiResponse;
		}

		private Response getRequestWithbody(String url, String body, String contentHeader, String acceptHeader) {
			logger.info("RESSURED: Sending a GET request to " + url);
			logger.info("REQUEST: Sending a GET request to " + url);
			Response getResponse = given().relaxedHTTPSValidation().accept("*/*").contentType("application/json").log()
					.all().when().body(body).get(url).then().extract().response();
			logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
			return getResponse;
		}
		
		public static Properties getParamsFromArg(String argVal, String pattern){
			Properties props = new Properties();
			
			String [] attr =  argVal.split(pattern);
			if(attr != null) {
				for(String s: attr) {
					String[] arr = s.split("=");
					if(arr.length > 1) {
						props.put(arr[0].trim(), arr[1].trim());
					}
				}
			}
			return props;
		}
		
		
	public void serverResourceStatusManager(String responsePattern,String status) throws RigInternalError {
		String respnseStatus = "";
		HashMap<String, String> getHMapQParam = createGetRequest();
		String url = baseUrl + props.getProperty("statusCheck");
		Response getResponse = getRequestWithQueryParam(url, getHMapQParam, "Get server status");
		if (getResponse == null) {
			throw new RigInternalError("Packet utility get method doesn't return any response");
		}
		respnseStatus = getResponse.getBody().asString();
		if (!respnseStatus.isEmpty()) {
			if (respnseStatus.toLowerCase().contains(responsePattern.toLowerCase())) {
				HashMap<String, String> putHMapQParam =createPutReqeust(status);
				putRequestWithQueryParam(url, putHMapQParam, "Update server key");
			} else {
				throw new RigInternalError("execution status alrady in use");
			}
		} else {
			throw new RigInternalError("got empty status");
		}
	}
		
		
	private HashMap<String, String> createGetRequest() {
		HashMap<String, String> getHMapQParam = new HashMap<>();
		getHMapQParam.put("key", "automation_key");
		return getHMapQParam;
	}

	private HashMap<String, String> createPutReqeust(String status) {
		HashMap<String, String> putHMapQParam = new HashMap<>();
		putHMapQParam.put("key", "automation_key");
		putHMapQParam.put("status", status);
		return putHMapQParam;
	}
	
	public void setMockabisExpectaion(boolean duplicate, JSONArray filePathArray, HashMap<String, String> contextKey)
			throws RigInternalError {
		String url = baseUrl + props.getProperty("mockAbis") + duplicate;
		Response response = postRequestWithQueryParamAndBody(url, filePathArray.toString(), contextKey,
				"Mockabis Expectaion");
		if (!response.getBody().asString().toLowerCase().contains("success"))
			throw new RigInternalError("Unable to set mockabis expectaion from packet utility");
	}
	
	////Activate/DeActivate machine--- start
	public Boolean activateDeActiveMachine(String jsonInput, String machineSpecId, String machineid, String zoneCode,
			String token, String status) throws RigInternalError {
		/*
		 * String regCenterId = JsonPrecondtion.getValueFromJson(jsonInput,
		 * "response.(machines)[0].regCenterId"); String url =
		 * System.getProperty("env.endpoint") +
		 * props.getProperty("getRegistrationCenter") + regCenterId+ "/eng"; Response
		 * getResponse = getRequestWithCookiesAndPathParam(url, token,
		 * "Get zoneCode by regCenterId"); if
		 * (getResponse.getBody().asString().toLowerCase().contains("errorcode")) {
		 * logger.error("zoneCode not found for  :[" + regCenterId + "]"); throw new
		 * RigInternalError("zoneCode not found for  :[" + regCenterId + "]"); }
		 * JSONObject jsonResp = new JSONObject(getResponse.getBody().asString());
		 * String zoneCode = JsonPrecondtion.getValueFromJson(jsonResp.toString(),
		 * "response.(registrationCenters)[0].zoneCode");
		 */
		
		JSONObject jsonPutReq = machineRequestBuilder(jsonInput, machineSpecId, machineid, zoneCode, status);
		Boolean isActive = updateMachineDetail(jsonPutReq, token,status);
		return isActive;
	}

	public Boolean updateMachineDetail(JSONObject jsonPutReq, String token,String status) throws RigInternalError {
		String url = System.getProperty("env.endpoint") + props.getProperty("getMachine");
		Response puttResponse = putReqestWithCookiesAndBody(url, jsonPutReq.toString(), token, "Update machine detail with status[isActive="+status+"]");
		if (puttResponse.getBody().asString().toLowerCase().contains("errorcode")) {
			logger.error("unable to update machine detail");
			throw new RigInternalError("unable to update machine detail");
		}
		JSONObject jsonResp = new JSONObject(puttResponse.getBody().asString());
		Boolean isActive = jsonResp.getJSONObject("response").getBoolean("isActive");
		return isActive;
	}

	public Response putReqestWithCookiesAndBody(String url, String body, String token, String opsToLog) {
		Reporter.log("<pre> <b>" + opsToLog + ": </b> <br/>" + body + "</pre>");
		Response puttResponse = given().relaxedHTTPSValidation().body(body).contentType(MediaType.APPLICATION_JSON)
				.accept("*/*").log().all().when().cookie("Authorization", token).put(url).then().log().all().extract()
				.response();
		Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + url + ") <pre>"
				+ puttResponse.getBody().asString() + "</pre>");
		return puttResponse;
	}
	
	public Response patchReqestWithCookiesAndBody(String url, String body, String token, String opsToLog) {
		Reporter.log("<pre> <b>" + opsToLog + ": </b> <br/>" + body + "</pre>");
		Response puttResponse = given().relaxedHTTPSValidation().body(body).contentType(MediaType.APPLICATION_JSON)
				.accept("*/*").log().all().when().cookie("Authorization", token).patch(url).then().log().all().extract()
				.response();
		Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + url + ") <pre>"
				+ puttResponse.getBody().asString() + "</pre>");
		return puttResponse;
	}
	
	public  Response patchRequestWithQueryParm(String url, HashMap<String, String> queryParam, String token, String opsToLog) {
		Reporter.log("<pre> <b>" + opsToLog + " </b></pre>");
		Response patchResponse = given().relaxedHTTPSValidation().queryParams(queryParam).contentType(MediaType.APPLICATION_JSON).cookie("Authorization", token)
				.accept("*/*").log().all().when().patch(url).then().log().all().extract().response();
		Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + url + ") <pre>"
				+ patchResponse.getBody().asString() + "</pre>");
		return patchResponse;
	}

	public  Response getRequestWithCookiesAndPathParam(String url, String token, String opsToLog) {
		Reporter.log("<pre> <b>" + opsToLog + ": </b> <br/></pre>");
		Response getResponse = given().relaxedHTTPSValidation().cookie("Authorization", token).log().all().when()
				.get(url).then().log().all().extract().response();
		Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + url + ") <pre>"
				+ getResponse.getBody().asString() + "</pre>");
		return getResponse;
	}

	public JSONObject machineRequestBuilder(String jsonInput, String machineSpecId, String machineid, String zoneCode,
			String status) {
		JSONObject jsonOutterReq = new JSONObject();
		jsonOutterReq.put("id", "string");
		jsonOutterReq.put("metadata", new JSONObject());
		JSONObject jsonInnerReq = new JSONObject();
		jsonInnerReq.put("id", machineid);
		jsonInnerReq.put("machineSpecId", machineSpecId);
		jsonInnerReq.put("zoneCode", zoneCode);
		jsonInnerReq.put("isActive", status);
		jsonInnerReq.put("name", (jsonInput == null) ? "xyz_".hashCode(): JsonPrecondtion.getValueFromJson(jsonInput, "response.(machines)[0].name"));
		jsonInnerReq.put("macAddress", (jsonInput == null) ? "8C-16-45-88-E1-1D": JsonPrecondtion.getValueFromJson(jsonInput, "response.(machines)[0].macAddress"));
		jsonInnerReq.put("ipAddress", (jsonInput == null) ? "193.168.0.122"	: JsonPrecondtion.getValueFromJson(jsonInput, "response.(machines)[0].ipAddress"));
		jsonInnerReq.put("langCode", "eng");
		jsonOutterReq.put("request", jsonInnerReq);
		jsonOutterReq.put("requesttime", getCurrentDateAndTimeForAPI());
		jsonOutterReq.put("version", "string");
		return jsonOutterReq;
	}
	//Activate/DeActivate machine--- end
	
	public  String getCurrentDateAndTimeForAPI() {
		return	javax.xml.bind.DatatypeConverter.printDateTime(
			    Calendar.getInstance(TimeZone.getTimeZone("UTC"))
			);
	}
	
	public JSONObject updatePartnerRequestBuilder(String status) throws RigInternalError {
		List<String> statusList = Arrays.asList("Active", " De-activate");
		if (!(statusList.contains(status))) {
			logger.error(status + " is not supported only allowed status[Active/De-Active]");
			throw new RigInternalError(status + " is not supported only allowed status[Active/De-Active]");
		}
		JSONObject jsonOutterReq = new JSONObject();
		jsonOutterReq.put("id", "string");
		jsonOutterReq.put("metadata", new JSONObject());
		JSONObject jsonInnerReq = new JSONObject();
		jsonInnerReq.put("status", status); // status can be Active and De-Active
		jsonOutterReq.put("request", jsonInnerReq);
		jsonOutterReq.put("requesttime",getCurrentDateAndTimeForAPI());
		jsonOutterReq.put("version", "string");
		return jsonOutterReq;
	}
	
	
	//Activate/DeActivate RegCenter--- start
		public Boolean activateDeActiveRegCenter(String jsonInput, String id,String locationCode,String zoneCode,String token, String status) throws RigInternalError {
			JSONObject jsonPutReq = regCenterPutrequestBuilder(jsonInput,id,locationCode,zoneCode,status);
			String url = System.getProperty("env.endpoint") + props.getProperty("getRegistrationCenter");
			Response puttResponse = putReqestWithCookiesAndBody(url, jsonPutReq.toString(), token, "Update RegCenter details with status[isActive=]"+status);
			if (puttResponse.getBody().asString().toLowerCase().contains("errorcode")) {
				logger.error("unable to update RegCenter detail");
				throw new RigInternalError("unable to update RegCenter detail");
			}
			JSONObject jsonResp = new JSONObject(puttResponse.getBody().asString());
			Boolean isActive = jsonResp.getJSONObject("response").getBoolean("isActive");
			return isActive;
		}
		
		
		public  JSONObject regCenterPutrequestBuilder(String jsonInput, String id,String locationCode,String zoneCode,String status) {
			JSONObject jsonOutterReq = new JSONObject();
			JSONObject jsonInnerReq = new JSONObject();
			jsonOutterReq.put("id", "string");jsonOutterReq.put("metadata", new JSONObject());
			jsonInnerReq.put("addressLine1", (jsonInput==null)?"addressLine1":JsonPrecondtion.getValueFromJson(jsonInput, "response.(registrationCenters)[0].addressLine1"));
			jsonInnerReq.put("centerEndTime", (jsonInput==null)?"17:00:00":JsonPrecondtion.getValueFromJson(jsonInput, "response.(registrationCenters)[0].centerEndTime"));
			jsonInnerReq.put("centerStartTime", (jsonInput==null)?"09:00:00":JsonPrecondtion.getValueFromJson(jsonInput, "response.(registrationCenters)[0].centerStartTime"));
			jsonInnerReq.put("centerTypeCode", (jsonInput==null)?"REG":JsonPrecondtion.getValueFromJson(jsonInput, "response.(registrationCenters)[0].centerTypeCode"));
			jsonInnerReq.put("holidayLocationCode", (jsonInput==null)?"KTA":JsonPrecondtion.getValueFromJson(jsonInput, "response.(registrationCenters)[0].holidayLocationCode"));
			jsonInnerReq.put("id", id);
			jsonInnerReq.put("isActive", status);jsonInnerReq.put("langCode", "eng");
			jsonInnerReq.put("latitude", (jsonInput==null)?"35.405692":JsonPrecondtion.getValueFromJson(jsonInput, "response.(registrationCenters)[0].latitude"));
			jsonInnerReq.put("locationCode", locationCode);
			jsonInnerReq.put("longitude", (jsonInput==null)?"-5.433368":JsonPrecondtion.getValueFromJson(jsonInput, "response.(registrationCenters)[0].longitude"));
			jsonInnerReq.put("name", (jsonInput==null)?"name1":JsonPrecondtion.getValueFromJson(jsonInput, "response.(registrationCenters)[0].name"));
			jsonInnerReq.put("perKioskProcessTime", (jsonInput==null)?"00:15:00":JsonPrecondtion.getValueFromJson(jsonInput, "response.(registrationCenters)[0].perKioskProcessTime"));
			jsonInnerReq.put("workingHours", (jsonInput==null)?"8:00:00":JsonPrecondtion.getValueFromJson(jsonInput, "response.(registrationCenters)[0].workingHours"));
			JSONObject jsonArrayPutPostDtoJsonReq = new JSONObject();
			JSONArray exceptionalHolidayPutPostDtoJsonReq = new JSONArray();
			jsonArrayPutPostDtoJsonReq.put("exceptionHolidayDate","2021-01-01");
			jsonArrayPutPostDtoJsonReq.put("exceptionHolidayName","New year");
			jsonArrayPutPostDtoJsonReq.put("exceptionHolidayReson","New year eve");
			exceptionalHolidayPutPostDtoJsonReq.put(jsonArrayPutPostDtoJsonReq);
			jsonInnerReq.put("exceptionalHolidayPutPostDto",exceptionalHolidayPutPostDtoJsonReq);
			jsonInnerReq.put("zoneCode", zoneCode);jsonOutterReq.put("request", jsonInnerReq);
			jsonOutterReq.put("requesttime", getCurrentDateAndTimeForAPI());jsonOutterReq.put("version", "string");
			return jsonOutterReq;
		}
		
		//Activate/DeActivate RegCenter--- end
	
}
