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
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
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
    public void testApp()
    {
        assertTrue( true );
    }

    public void testApplyToJob()
    {
        String jobID = "3772650869";
        LinkedInDriver.driver = LinkedInDriver.getDriver();
        LinkedInDriver.driver.get("https://www.linkedin.com/jobs");
        LinkedInDriver.js = LinkedInDriver.getJS();
        LinkedInDriver.logIn();
        LinkedInDriver.JobPage.applyToJob(jobID);
    }
}
