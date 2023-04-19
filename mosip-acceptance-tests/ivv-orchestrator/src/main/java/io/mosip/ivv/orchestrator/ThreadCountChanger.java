package io.mosip.ivv.orchestrator;

import java.util.List;

import org.apache.log4j.Logger;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

import io.mosip.ivv.e2e.methods.ApproveRejectPacket;
import io.mosip.kernel.util.ConfigManager;

public class ThreadCountChanger implements IAlterSuiteListener  {
	Logger logger = Logger.getLogger(ThreadCountChanger.class);
	
	@Override
    public void alter(List<XmlSuite> suites) {
        System.err.println("**Alter is invoked**");

        //        int count = Integer.parseInt(System.getProperty("threadCount", "1"));
        
        int count = Integer.parseInt(ConfigManager.getThreadCount());
        
        logger.info("Running suite with thread count : "+count);

        for (XmlSuite suite : suites) {
            suite.setThreadCount(count);
        }
    }
}


