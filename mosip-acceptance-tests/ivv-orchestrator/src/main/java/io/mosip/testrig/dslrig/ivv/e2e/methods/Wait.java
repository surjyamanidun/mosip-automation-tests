package io.mosip.testrig.dslrig.ivv.e2e.methods;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.Reporter;

import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.dslrig.ivv.core.base.StepInterface;
import io.mosip.testrig.dslrig.ivv.core.exceptions.RigInternalError;
import io.mosip.testrig.dslrig.ivv.orchestrator.BaseTestCaseUtil;
import io.mosip.testrig.dslrig.ivv.orchestrator.PacketUtility;

public class Wait extends BaseTestCaseUtil implements StepInterface {

	public static Logger logger = Logger.getLogger(Wait.class);
	
	static {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	@Override
	public void run() throws RigInternalError {
		int waitFromActuator = 0;
		Long waitTime = DEFAULT_WAIT_TIME;
		if (step.getParameters() == null || step.getParameters().isEmpty()) {
			logger.warn("Wait Time is Missing : Taking default Time as 30 Sec");
		}

	// Pass flag as true in the step to get the wait time from the regproc actuator for the reprocessor to kick in  
		else if (step.getParameters().size() == 1 && step.getParameters().get(0).contains("true")) {
			Boolean flag = Boolean.parseBoolean(step.getParameters().get(0));
			if (flag) {
				waitFromActuator = PacketUtility.getActuatorDelay();
				waitTime = TIME_IN_MILLISEC * waitFromActuator;
			}

		} else {
			waitTime = TIME_IN_MILLISEC * Integer.parseInt(step.getParameters().get(0));
		}

		try {
			Reporter.log("Total waiting for: " + waitTime / 1000 + " Sec");
			Reporter.log("Starting Waiting: " + getDateTime());
			Thread.sleep(waitTime);
			Reporter.log("Waiting Done: " + getDateTime());
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			Thread.currentThread().interrupt();
		}
	}

	

}
