package io.mosip.testrig.dslrig.ivv.e2e.methods;

import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.dslrig.ivv.core.base.StepInterface;
import io.mosip.testrig.dslrig.ivv.core.exceptions.RigInternalError;
import io.mosip.testrig.dslrig.ivv.orchestrator.BaseTestCaseUtil;
import io.mosip.testrig.dslrig.ivv.orchestrator.SyncDataHelper;

public class SyncData extends BaseTestCaseUtil implements StepInterface {
	static Logger logger = Logger.getLogger(SyncData.class);

	SyncDataHelper syncDataHelper = new SyncDataHelper();
	
	static {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() throws RigInternalError {
		String id = null;
		HashMap<String, String> machineDetailsmap = new HashMap<>();
		String keycase = "";
		String calltype = null;
		int centerCount = 1;

		if (step.getParameters() == null || step.getParameters().isEmpty() || step.getParameters().size() < 1) {
			logger.error("Method Type[POST/GET/PUT/PATCH] parameter is  missing from DSL step");
			this.hasError = true;
			throw new RigInternalError(
					"Method Type[POST/GET/PUT/PATCH] parameter is  missing from DSL step: " + step.getName());
		} else {
			calltype = step.getParameters().get(0);

		}
		if (step.getParameters().size() >= 2 && step.getParameters().get(1).startsWith("$$")) {
			id = step.getParameters().get(1);
			if (id.startsWith("$$")) {
				machineDetailsmap = step.getScenario().getVariables();

			}
		}
		if (step.getParameters().size() >= 3) {
			keycase = step.getParameters().get(2);
		}
		switch (calltype) {
		case "TPM_VERIFY":
			String keyindex = syncDataHelper.verifyPublicKey(machineDetailsmap);
			if (keycase.equalsIgnoreCase("upper"))
				keyindex = keyindex.toUpperCase();
			else if (keycase.equalsIgnoreCase("lower"))
				keyindex = keyindex.toLowerCase();

			if (step.getOutVarName() != null) {
				machineDetailsmap.put("keyindex", keyindex);
				// step.getScenario().getVariables().put(step.getOutVarName(), keyIndex);
				step.getScenario().getVariables().putAll(machineDetailsmap);
			}
			break;
		case "TPM_VERIFY_INVALID":
			syncDataHelper.verifyPublicKeyInvalid(machineDetailsmap);

			break;

		case "CLIENT_SETTINGS_VALID":
			centerCount = Integer.parseInt(keycase);
			syncDataHelper.getClientsettingsValid(machineDetailsmap, centerCount);
			break;
		case "CLIENT_SETTINGS_INVALID":
			centerCount = Integer.parseInt(keycase);
			syncDataHelper.getClientsettingsInvalid(machineDetailsmap, centerCount);
			break;
		case "LATEST_ID_SCHEMA":
			syncDataHelper.getlatestidschema();
			break;
		case "CONFIGS_KEYINDEX":
			syncDataHelper.getConfigsKeyindex(machineDetailsmap);
			break;
		case "USER_DETAILS":
			syncDataHelper.getUserdetails(machineDetailsmap);
			break;
		case "USER_DETAILS_INVALID":
			syncDataHelper.getUserdetailsInvalid(machineDetailsmap);
			break;
		default:
			break;
		}

	}

}
