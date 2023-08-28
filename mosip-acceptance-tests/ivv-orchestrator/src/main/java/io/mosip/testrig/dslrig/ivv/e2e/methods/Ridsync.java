package io.mosip.testrig.dslrig.ivv.e2e.methods;

import static org.testng.Assert.assertTrue;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.dslrig.ivv.core.base.StepInterface;
import io.mosip.testrig.dslrig.ivv.core.exceptions.RigInternalError;
import io.mosip.testrig.dslrig.ivv.e2e.constant.E2EConstants;
import io.mosip.testrig.dslrig.ivv.orchestrator.BaseTestCaseUtil;
import io.mosip.testrig.dslrig.ivv.orchestrator.TestRunner;
import io.restassured.response.Response;

public class Ridsync extends BaseTestCaseUtil implements StepInterface {
	static Logger logger = Logger.getLogger(Ridsync.class);
	public  String _additionalInfo=null;
	
	static {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}
	@Override
	public void run() throws RigInternalError {
		String process=null;
		
		if (step.getParameters() == null || step.getParameters().isEmpty()) {
			logger.error("Parameter is  missing from DSL step");
			assertTrue(false,"process paramter is  missing in step: "+step.getName());
		} else if(step.getParameters().size() == 1){
			process =step.getParameters().get(0);
			step.getScenario().getPridsAndRids().clear();
			String registrationId=null;
			for (String packetPath : step.getScenario().getTemplatePacketPath().values()) {
				registrationId=ridsync(packetPath, E2EConstants.APPROVED_SUPERVISOR_STATUS,process);
				step.getScenario().getPridsAndRids().put(packetPath, registrationId);
				step.getScenario().getRidPersonaPath().put(registrationId, step.getScenario().getRidPersonaPath().get(packetPath));
				step.getScenario().getRidPersonaPath().remove(packetPath);
			}
			storeProp(step.getScenario().getPridsAndRids());
		}else
			if(step.getParameters().size()>1) { // "$$rid=e2e_ridsync(NEW,$$zipPacketPath)"
				process =step.getParameters().get(0);
				String _zipPacketPath=step.getParameters().get(1);
				if(step.getParameters().size()==3) {
					_additionalInfo=step.getParameters().get(2);
					if(_additionalInfo.startsWith("$$")) {
						_additionalInfo=step.getScenario().getVariables().get(_additionalInfo);}
					}
				else
				{
					_additionalInfo=null;
				}
				
					if(_zipPacketPath.startsWith("$$")) {
						_zipPacketPath=step.getScenario().getVariables().get(_zipPacketPath);
						String _rid=ridsync(_zipPacketPath, E2EConstants.APPROVED_SUPERVISOR_STATUS,process);
						if(step.getOutVarName()!=null)
							step.getScenario().getVariables().put(step.getOutVarName(), _rid);
					}
				}

	}

	private String ridsync(String containerPath, String supervisorStatus,String process) throws RigInternalError {
		String url = baseUrl + props.getProperty("ridsyncUrl");
		JSONObject jsonReq = buildRequest(containerPath, supervisorStatus,process);
		Response response = postRequestWithQueryParamAndBody(url, jsonReq.toString(),step.getScenario().getCurrentStep(), "Ridsync",step);

		JSONArray jsonArray = new JSONArray(response.asString());
		JSONObject responseJson = new JSONObject(jsonArray.get(0).toString());
		if(!response.getBody().asString().toLowerCase().contains("success"))
			throw new RigInternalError("Unable to do RID sync from packet utility");
		return responseJson.get("registrationId").toString();

	}

	private JSONObject buildRequest(String containerPath, String supervisorStatus,String process) {
		JSONObject jsonReq = new JSONObject();
		jsonReq.put("containerPath", containerPath);
		jsonReq.put("email", "email");
		jsonReq.put("name", "name");
		jsonReq.put("phone", "phone");
		jsonReq.put("process", process);
		jsonReq.put("supervisorComment", "supervisorComment");
		jsonReq.put("supervisorStatus", supervisorStatus);
		jsonReq.put("supervisorStatus", supervisorStatus);
		jsonReq.put("additionalInfoReqId", _additionalInfo);

		return jsonReq;
	}

	private static void storeProp(HashMap<String,String> map) {
		Properties prop= new Properties();
		for(String key: map.keySet())
			prop.put(key, map.get(key));
		String filePath=TestRunner.getExternalResourcePath()
				+ props.getProperty("ivv.path.deviceinfo.folder") +"ridPersonaPathProp.properties";
		try(OutputStream output = new FileOutputStream(filePath,true);) {
			
			prop.store(output, null);
			output.close();
			output.flush();
		} catch (Exception e) {
		}
	}

}
