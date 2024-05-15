package org.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    LinkedInDriver linkedInDriver = new LinkedInDriver();
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */

    public void testApplyToJob()
    {
        String jobID = "3919859060";
//        linkedInDriver.driver = LinkedInDriver.getDriver();
//        linkedInDriver.driver.get("https://www.linkedin.com/jobs");
//        linkedInDriver.js = LinkedInDriver.getJS();
        linkedInDriver.logIn();
        linkedInDriver.applyToJob(jobID);
        JobsContext jobsContext = new JobsContext();
        assertTrue(jobsContext.hasBeenAppliedTo(jobID));
    }
}
