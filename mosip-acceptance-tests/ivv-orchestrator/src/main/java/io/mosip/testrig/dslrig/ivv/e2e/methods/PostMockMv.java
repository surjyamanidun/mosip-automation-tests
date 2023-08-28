package io.mosip.testrig.dslrig.ivv.e2e.methods;

import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.apirig.service.BaseTestCase;
import io.mosip.testrig.dslrig.ivv.core.base.StepInterface;
import io.mosip.testrig.dslrig.ivv.core.exceptions.RigInternalError;
import io.mosip.testrig.dslrig.ivv.orchestrator.BaseTestCaseUtil;
import io.restassured.response.Response;

public class PostMockMv extends BaseTestCaseUtil implements StepInterface {
	static Logger logger = Logger.getLogger(PostMockMv.class);
	
	static {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	@Override
	public void run() throws RigInternalError {

		String rid = "",uri=null,decision=null;
		HashMap<String, String> context=null;
		if (step.getParameters() == null || step.getParameters().isEmpty() || step.getParameters().size() < 1) {
			logger.error("Parameter is  missing from DSL step");
			this.hasError=true;throw new RigInternalError("PostMockMv paramter is  missing in step: " + step.getName());
		} else {
			rid = step.getParameters().get(0);
			rid = step.getScenario().getVariables().get(rid);
			decision=step.getParameters().get(1);
		}
		
		 uri=BaseTestCase.ApplnURI+ props.getProperty("setMockMVExpectation");
		JSONObject jo=new JSONObject();
		
		jo.put("rid", rid);
		jo.put("mockMvDecision", decision);
		Response response = postRequest(uri, jo.toString(), "MockMv",step);
	//	JSONObject res = new JSONObject(response.asString());
//		logger.info(response.toString());
//		
//		if (response.toString().contains("Successfully inserted expectation")) {
//			logger.info("RESPONSE=" + response.toString());
//		} else {
//			logger.error("RESPONSE=" + response.toString());
//			throw new RuntimeException("Mock mv" + response.toString());
//		}

	}
}