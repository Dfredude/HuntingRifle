package org.example;

import junit.framework.TestCase;

public class DataTellerTest extends TestCase {
    public void testGetAnswer() {
        DataTeller dataTeller = new DataTeller();
        String answer = dataTeller.getAnswer("What is the answer to life, the universe, and everything?");
        assertNull(null, answer);
        answer = dataTeller.getAnswer("How many years of work experience do you have with Microsoft Outlook?");
        assertEquals("99", answer);
    }

    public void testGetUnwantedKeywords() {
        DataTeller dataTeller = new DataTeller();
        assertEquals(6, dataTeller.getUnwantedKeywords().size());
    }
}
