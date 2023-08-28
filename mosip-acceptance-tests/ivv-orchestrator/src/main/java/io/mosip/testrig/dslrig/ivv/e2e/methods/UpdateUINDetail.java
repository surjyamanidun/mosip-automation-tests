package io.mosip.testrig.dslrig.ivv.e2e.methods;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Reporter;

import io.mosip.testrig.apirig.admin.fw.util.AdminTestException;
import io.mosip.testrig.apirig.admin.fw.util.TestCaseDTO;
import io.mosip.testrig.apirig.authentication.fw.util.AuthenticationTestException;
import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.apirig.testscripts.PostWithBodyWithOtpGenerate;
import io.mosip.testrig.dslrig.ivv.core.base.StepInterface;
import io.mosip.testrig.dslrig.ivv.core.exceptions.RigInternalError;
import io.mosip.testrig.dslrig.ivv.orchestrator.BaseTestCaseUtil;

public class UpdateUINDetail extends BaseTestCaseUtil implements StepInterface {
	private static final String UPDATE_DEMOPHRAPIC_DETAIL = "preReg/updateUINDetail/UpdateUIN.yml";
	static Logger logger = Logger.getLogger(UpdateUINDetail.class);
    
	static {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}
    @Override
    public void run() throws RigInternalError {
    	String fileName = UPDATE_DEMOPHRAPIC_DETAIL;
    	PostWithBodyWithOtpGenerate postWithBodyWithOtpGenerate= new PostWithBodyWithOtpGenerate();
    	Object[] casesList = postWithBodyWithOtpGenerate.getYmlTestData(fileName);
		Object[] testCaseList = filterTestCases(casesList);
		logger.info("No. of TestCases in Yml file : " + testCaseList.length);
		
				for (Object object : testCaseList) {
					for(String uin: step.getScenario().getUinReqIds().keySet()) {
						try {
						TestCaseDTO test = (TestCaseDTO) object;
						String input=test.getInput().replace("$UIN$", uin).replace("$UIN$", uin).replace("$UIN$", uin);
						JSONObject inputJson = new JSONObject(input);
						String idJsonValue=inputJson.get("identityJsonValue").toString();
						inputJson.remove("identityJsonValue");
						String encodedIdJson=encoder(idJsonValue);
						String actualInput=inputJson.toString().replace("$IDJSON$", encodedIdJson);
						test.setInput(actualInput);
						Reporter.log("<b><u>"+test.getTestCaseName()+ "</u></b>");
						long startTime = System.currentTimeMillis();
						logger.info(this.getClass().getSimpleName()+" starts at..."+startTime +" MilliSec");
						postWithBodyWithOtpGenerate.test(test);
						long stopTime = System.currentTimeMillis();
						long elapsedTime = stopTime - startTime;
						logger.info("Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec");
						Reporter.log("<b><u>"+"Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec"+ "</u></b>");
						JSONObject res = new JSONObject(postWithBodyWithOtpGenerate.response.asString());
					JSONObject responseJson = new JSONObject(res.get("response").toString());
					for (String prid : step.getScenario().getPridsAndRids().keySet()) {
						step.getScenario().getPridsAndRids().put(prid, responseJson.get("registrationId").toString());
					}

				} catch (AuthenticationTestException | AdminTestException e) {
					logger.error("Failed at downloading card: " + e.getMessage());
					this.hasError=true;
					//assertFalse(true, "Failed at downloading card");
					throw new RigInternalError("Failed at Update UIN api response validation");
				}
			}
		}

	}

}
