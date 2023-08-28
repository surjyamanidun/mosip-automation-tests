package io.mosip.testrig.dslrig.ivv.orchestrator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import io.mosip.testrig.apirig.admin.fw.util.TestCaseDTO;
import io.mosip.testrig.apirig.authentication.fw.precon.JsonPrecondtion;
import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.apirig.service.BaseTestCase;
import io.mosip.testrig.apirig.testscripts.GetWithParam;
import io.mosip.testrig.apirig.testscripts.GetWithQueryParam;
import io.mosip.testrig.apirig.testscripts.PatchWithPathParam;
import io.mosip.testrig.apirig.testscripts.PutWithPathParam;
import io.mosip.testrig.apirig.testscripts.SimplePost;
import io.mosip.testrig.apirig.testscripts.SimplePut;
import io.mosip.testrig.dslrig.ivv.core.exceptions.RigInternalError;
import io.restassured.response.Response;

public class CenterHelper extends BaseTestCaseUtil {
	public static Logger logger = Logger.getLogger(CenterHelper.class);
	

	private static final String CreateRegistrationCenter = "ivv_masterdata/RegistrationCenter/CreateRegistrationCenter.yml";
	private static final String UpdateRegCentStatus = "ivv_masterdata/UpdateRegCentStatus/UpdateRegCentStatus.yml";
	private static final String UpdateRegistrationCenterNonLanguage = "ivv_masterdata/UpdateRegistrationCenterNonLanguage/UpdateRegistrationCenterNonLanguage.yml";
	private static final String UpdateRegistrationCenterLang = "ivv_masterdata/UpdateRegistrationCenterLang/UpdateRegistrationCenterLang.yml";
	private static final String DecommissionRegCenter = "ivv_masterdata/DecommissionRegCenter/DecommissionRegCenter.yml";
	private static final String GetLocationCodeHoliday = "ivv_masterdata/GetLocationCodeHoliday/GetLocationCodeHoliday.yml";
	
	private static final String GetPostalCode = "ivv_masterdata/GetPostalCode/GetPostalCode.yml";
	
	SimplePost simplepost=new SimplePost() ;
	PatchWithPathParam patchwithpathparam=new PatchWithPathParam();
	SimplePut simpleput=new SimplePut();
	PutWithPathParam putwithpathparam=new PutWithPathParam();
	GetWithParam getWithParam=new GetWithParam();
	GetWithQueryParam getWithQueryParam=new GetWithQueryParam();
	
	static {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}
	
	public CenterHelper() {
		  super();
	}

	public void centerUpdate(String centerId,String zone) throws RigInternalError {
		try {
			Object[] testObjPut=simpleput.getYmlTestData(UpdateRegistrationCenterNonLanguage);

			TestCaseDTO testPut=(TestCaseDTO)testObjPut[0];
			
			String input=testPut.getInput();
			input = JsonPrecondtion.parseAndReturnJsonContent(input,
					centerId, "id");
			input = JsonPrecondtion.parseAndReturnJsonContent(input,
					zone,"zoneCode");
			input = JsonPrecondtion.parseAndReturnJsonContent(input,
					BaseTestCase.languageCode,"langCode");
			
			testPut.setInput(input);

			
			String output=testPut.getOutput();
			output = JsonPrecondtion.parseAndReturnJsonContent(output,
					zone,"zoneCode");
			output = JsonPrecondtion.parseAndReturnJsonContent(output,
					BaseTestCase.languageCode,"langCode");
			testPut.setOutput(output);
			simpleput.test(testPut);
			Response response= simpleput.response;

			if (response!= null)
			{
				JSONObject jsonResp = new JSONObject(response.getBody().asString());
				logger.info( jsonResp.getJSONObject("response"));}

		} catch (Exception e) {
			this.hasError=true;
			throw new RigInternalError(e.getMessage());

		}

	}



	public void centerDcom(String id) throws RigInternalError {
		try {
			Object[] testObjPutDcom=simpleput.getYmlTestData(DecommissionRegCenter);

			TestCaseDTO testPutDcom=(TestCaseDTO)testObjPutDcom[0];
			String input=testPutDcom.getInput();
			input = JsonPrecondtion.parseAndReturnJsonContent(input,
					id, "regCenterID");

			testPutDcom.setInput(input);

			putwithpathparam.test(testPutDcom);
			Response response= putwithpathparam.response;

			if (response!= null)
			{
				JSONObject jsonResp = new JSONObject(response.getBody().asString());
				logger.info( jsonResp.getJSONObject("response"));}

		} catch (Exception e) {
			this.hasError=true;
			throw new RigInternalError(e.getMessage());

		}

	}

	public void centerStatusUpdate(String id,Boolean activeFlag) throws RigInternalError {
		try {
			Object[] testObjPatch=patchwithpathparam.getYmlTestData(UpdateRegCentStatus);

			TestCaseDTO testPatch=(TestCaseDTO)testObjPatch[0];
			
			String input=testPatch.getInput();
			input = JsonPrecondtion.parseAndReturnJsonContent(input,
					id, "id");
			input = JsonPrecondtion.parseAndReturnJsonContent(input,
					activeFlag.toString(), "isActive");
			testPatch.setInput(input);

			patchwithpathparam.test(testPatch);
			Response response= patchwithpathparam.response;

			if (response!= null)
			{
				JSONObject jsonResp = new JSONObject(response.getBody().asString());
				logger.info( jsonResp.getJSONObject("response"));}

		} catch (Exception e) {
			this.hasError=true;
			throw new RigInternalError(e.getMessage());

		}

	}

	public String centerCreate(String zone,String holidayLocationCode,String locationCode) throws RigInternalError {
		try {
			String id =null;
			Object[] testObjPost=simplepost.getYmlTestData(CreateRegistrationCenter);

			TestCaseDTO testPost=(TestCaseDTO)testObjPost[0];
			String input=testPost.getInput();
			input = JsonPrecondtion.parseAndReturnJsonContent(input,
					"DSL"+BaseTestCase.generateRandomAlphaNumericString(7), "name");
			
			input = JsonPrecondtion.parseAndReturnJsonContent(input,
					"DSL"+BaseTestCase.generateRandomAlphaNumericString(7), "addressLine1");
			
			input = JsonPrecondtion.parseAndReturnJsonContent(input,
					zone,"zoneCode");
			
			input = JsonPrecondtion.parseAndReturnJsonContent(input,
					BaseTestCase.languageCode,"langCode");
			
			input = JsonPrecondtion.parseAndReturnJsonContent(input,
					holidayLocationCode,"holidayLocationCode");
			
			input = JsonPrecondtion.parseAndReturnJsonContent(input,
					locationCode,"locationCode");
//			
//			   "holidayLocationCode":"RSK",
//		
//			   "locationCode":"10114",
//			
			
			testPost.setInput(input);
			
			String output=testPost.getOutput();
			output = JsonPrecondtion.parseAndReturnJsonContent(output,
					zone,"zoneCode");
			output = JsonPrecondtion.parseAndReturnJsonContent(output,
					BaseTestCase.languageCode,"langCode");
			testPost.setOutput(output);
			
			simplepost.test(testPost);
			Response response= simplepost.response;

			if (response!= null)
			{
				JSONObject jsonResp = new JSONObject(response.getBody().asString());
				logger.info( jsonResp.getJSONObject("response"));
				String name = jsonResp.getJSONObject("response").getString("name"); 
				 id = jsonResp.getJSONObject("response").getString("id"); 
			
			}
			logger.info("id="+id);
			return id;
		} catch (Exception e) {
			this.hasError=true;
			throw new RigInternalError(e.getMessage());

		}

	}



	public String getLocationCodeHoliday() throws RigInternalError {

		try {	String lastSyncTime =null;
		Object[] testObjPost=getWithQueryParam.getYmlTestData(GetLocationCodeHoliday);

		TestCaseDTO testPost=(TestCaseDTO)testObjPost[0];
		getWithParam.test(testPost);
		Response response= getWithParam.response;
		String locationCode=null;
		if(response!=null) 
		{
			JSONObject jsonObject = new JSONObject(response.getBody().asString());
			JSONObject responseObj = jsonObject.getJSONObject("response");
			JSONArray responseArray = responseObj.getJSONArray("holidays");
			if (responseArray.length() > 0) {
				JSONObject locationObject = responseArray.getJSONObject(0);
				locationCode = locationObject.getString("locationCode");

				// Traverse on the "code" field
				logger.info("Location Code: " + locationCode);
				return locationCode;
			} else {
				logger.error("No location data found in the response.");
			}


		}
		return locationCode;
		}catch (Exception e)
		{
			this.hasError=true;
			throw new RigInternalError(e.getMessage());

		}


	}



	public String getPostalCode() throws RigInternalError {

		try {	String lastSyncTime =null;
		Object[] testObjPost=getWithParam.getYmlTestData(GetPostalCode);

		TestCaseDTO testPost=(TestCaseDTO)testObjPost[0];
		//String langCode=BaseTestCase.languageCode;
		//testPost.setEndPoint(testPost.getEndPoint().replace("langcode", langCode));
		getWithParam.test(testPost);
		Response response= getWithParam.response;
		String locationCode=null;
		if(response!=null) 
		{
			JSONObject jsonObject = new JSONObject(response.getBody().asString());
			JSONObject responseObj = jsonObject.getJSONObject("response");
			JSONArray responseArray = responseObj.getJSONArray("locations");
			if (responseArray.length() > 0) {
				JSONObject locationObject = responseArray.getJSONObject(0);
				locationCode = locationObject.getString("code");

				// Traverse on the "code" field
				logger.info("Location Code: " + locationCode);
				return locationCode;
			} else {
				logger.error("No location data found in the response.");
			}


		}
		return locationCode;
		}catch (Exception e)
		{
			this.hasError=true;
			throw new RigInternalError(e.getMessage());

		}


	}



	
	
}
