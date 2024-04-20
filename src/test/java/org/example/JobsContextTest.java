package org.example;

import junit.framework.TestCase;

public class JobsContextTest extends TestCase {
    public void testIncreaseCounter() {
        String jobID = "3772650869";
        JobsContext jc = new JobsContext();
        jc.increaseCounter(jobID);
    }
}
