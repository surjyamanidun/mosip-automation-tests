package io.mosip.testrig.dslrig.ivv.e2e.methods;

import static org.testng.Assert.assertTrue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Reporter;

import io.mosip.testrig.apirig.admin.fw.util.TestCaseDTO;
import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.apirig.testscripts.SimplePostForAutoGenId;
import io.mosip.testrig.dslrig.ivv.core.base.StepInterface;
import io.mosip.testrig.dslrig.ivv.core.exceptions.RigInternalError;
import io.mosip.testrig.dslrig.ivv.orchestrator.BaseTestCaseUtil;

public class ApproveRejectPacket extends BaseTestCaseUtil implements StepInterface {
	private static final String DECISSIONDATA_YML = "preReg/approveRejectPacket/decissionData.yml";
	public static Logger logger = Logger.getLogger(ApproveRejectPacket.class);
	
	static {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}
	
	@Override
	public void run() throws RigInternalError {
		String status_Code=null;
		if (step.getParameters() == null || step.getParameters().isEmpty()) {
			logger.error("Parameter is  missing from DSL step");
			assertTrue(false,"StatusCode paramter is  missing in step: "+step.getName());
		} else {
			status_Code =step.getParameters().get(0);
		}
		SimplePostForAutoGenId simplePostForAutoGenId= new SimplePostForAutoGenId();
		Object[] casesList = simplePostForAutoGenId.getYmlTestData(DECISSIONDATA_YML);
		Object[] testCaseList = filterTestCases(casesList);
		simplePostForAutoGenId.idKeyName="regId";
		logger.info("No. of TestCases in Yml file : " + testCaseList.length);
		try {
			for (Object object : testCaseList) {
				TestCaseDTO test = (TestCaseDTO) object;
				for(String keys:step.getScenario().getManualVerificationRid().keySet()) {
					test.setInput(test.getInput()
							.replace("$mvUsrId$", keys)
							.replace("$regId$", step.getScenario().getManualVerificationRid().get(keys))
							.replace("$statusCode$", status_Code));
					test.setOutput(test.getOutput()
							.replace("$mvUsrId$", keys)
							.replace("$statusCode$", status_Code)
							);
				}
				Reporter.log("<b><u>"+test.getTestCaseName()+ "</u></b>");
				long startTime = System.currentTimeMillis();
				logger.info(this.getClass().getSimpleName()+" starts at..."+startTime +" MilliSec");
				simplePostForAutoGenId.test(test);
				long stopTime = System.currentTimeMillis();
				long elapsedTime = stopTime - startTime;
				logger.info("Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec");
				JSONObject response = new JSONObject(simplePostForAutoGenId.response.asString());
				if(!response.get("response").toString().equals("null"))
				{
					JSONObject responseJson = new JSONObject(response.get("response").toString());
					step.getScenario().setStatusCode( responseJson.get("statusCode").toString());
					step.getScenario().getPridsAndRids().clear();
					step.getScenario().getPridsAndRids().put(null, responseJson.get("regId").toString());
				}

			}
		} catch (Exception e) {
			 this.hasError=true;
			logger.error(e.getMessage());
			throw new RigInternalError("Failed at decission data(approved or reject) Response validation");
		}
	}

}
