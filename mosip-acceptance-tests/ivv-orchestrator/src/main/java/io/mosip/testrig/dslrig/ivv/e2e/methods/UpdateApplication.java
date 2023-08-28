package io.mosip.testrig.dslrig.ivv.e2e.methods;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.Reporter;

import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.dslrig.ivv.core.base.StepInterface;
import io.mosip.testrig.dslrig.ivv.core.exceptions.RigInternalError;
import io.mosip.testrig.dslrig.ivv.orchestrator.BaseTestCaseUtil;

public class UpdateApplication extends BaseTestCaseUtil implements StepInterface {
	static Logger logger = Logger.getLogger(UpdateApplication.class);
	
	static {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	@Override
	public void run() throws RigInternalError {
		for (String resDataPath : step.getScenario().getResidentTemplatePaths().keySet()) {
			Reporter.log("<b><u>" + "UpdateApplication testCase </u></b>");
			//packetUtility.updateApplication(resDataPath, residentPathsPrid, contextKey);
		}
	}
}
