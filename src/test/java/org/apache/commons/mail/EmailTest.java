package org.apache.commons.mail;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Session;
import javax.mail.internet.MimeMultipart;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EmailTest {
	// Test Emails
	private static final String[] TEST_EMAILS = { "ab@cd.com", "a.b@c.org", 
			"abc@abc.com.uk", "test123@test.com", "anotherTest@test.edu"};
	
	// Concrete Email object to test with
	private EmailConcrete email;
	
	@Before
	public void setUpEmailTest() throws Exception {
		email = new EmailConcrete();
	} // END of the setup function
	
	@After
	public void tearDownEmailTest() throws Exception {
		// Do nothing on tear down
	} // END of the tear down function
	
	// Test for addBcc(String... emails) function
	@Test
	public void testAddBcc() throws Exception {
		// Call addBcc with TEST_EMAILS
		email.addBcc(TEST_EMAILS);
		// Check if the number of BCC'd emails is 5
		assertEquals(5, email.getBccAddresses().size());
	} // END of Test for addBcc(String... emails)
	
	// Test for addCc(String email)
	@Test
	public void testAddCc() throws Exception {
		// Call addCc with first email in TEST_EMAILS
		email.addCc(TEST_EMAILS[0]);
		// Check if the CC'd email is ab@cd.com
		assertEquals("ab@cd.com", email.getCcAddresses().get(0).toString());
	} // END of Test for addCc(String email)
	
	// Test 1 for addHeader(String name, String value)
	@Test
	public void testHeader() throws Exception {
		// Call addHeader with key "KeyStr" and value "ValueStr"
		email.addHeader("KeyStr", "ValueStr");
		// Check if the value for key "KeyStr" is "ValueStr"
		assertEquals("ValueStr", email.headers.get("KeyStr"));
	} // END of Test 1 for addHeader(String name, String value)
	
	// Test 2 for addHeader(String name, String value)
	@Test (expected = IllegalArgumentException.class)
	public void test2Header() throws Exception {
		// Should throw an exception for having an empty key
		email.addHeader("", "ValueStr");
	} // END of Test 2 for addHeader(String name, String value)
	
	// Test for addReplyTo(String email, String name)
	@Test
	public void testAddReplyTo() throws Exception {
		// Call addReplyTo with first email in TEST_EMAILS and "John Doe"
		email.addReplyTo(TEST_EMAILS[0], "John Doe");
		// Check if the Reply To email address is ab@cd.com
		assertEquals("ab@cd.com", email.getReplyToAddresses().get(0).getAddress());
	} // END of Test for addReplyTo(String email, String name)
	
	// Test for setFrom(String email)
	@Test
	public void testSetFrom() throws Exception {
		// Call setFrom with the first email in TEST_EMAILS
		email.setFrom(TEST_EMAILS[0]);
		// Check if the From email address is ab@cd.com
		assertEquals("ab@cd.com", email.getFromAddress().toString());
	} // END of Test for setFrom(String email)
	
	// Test for getMailSession()
	@Test
	public void testGetMailSession() throws Exception {
		// Create properties object and use it to create a session
		Properties prop = new Properties();
		prop.put(EmailConstants.MAIL_HOST, "test.com");
		Session expectedSession = Session.getInstance(prop);
		// Set the mail session
		email.setMailSession(expectedSession);
		
		// Call getMailSession()
		Session actualSession = email.getMailSession();
		// Check that the session returned from getMailSession() is the
		// 		same as the one that was created
		assertEquals(expectedSession, actualSession);
	} // END of Test for getMailSession()
	
	// Test 2 for getMailSession()
	@Test (expected = EmailException.class)
	public void test2GetMailSession() throws Exception {
		// Should throw an exception for not having a hostname
		// 		Don't need to save the result since it should throw an exception
		email.getMailSession();
	} // END of Test 2 for getMailSession()
	
	// Test 1 for buildMimeMessage()
	@Test
	public void testBuildMimeMessage() throws Exception {
		// Note: setFrom(String email), addCc(String email), addReplyTo(String email, String name),
		// 		and addHeader(String name, String value) have test(s) of their own and should not
		//		be the cause of failure for this test if their test(s) have passed.
		
		// Set subject, hostname, and From address
		email.setSubject("Test Subject");
		email.setHostName("test.com");
		email.setFrom(TEST_EMAILS[0]);
		// Add To address, BCC address, CC address, Reply address, and header
		email.addTo(TEST_EMAILS[1]);
		email.addBcc(TEST_EMAILS[2]);
		email.addCc(TEST_EMAILS[3]);
		email.addReplyTo(TEST_EMAILS[4], "John Doe");
		email.addHeader("KeyStr", "ValueStr");
		
		// Call buildMimeMessage
		email.buildMimeMessage();
		
		// Recipients are the To, BCC, and CC addresses
		String[] expectedRecipients = {TEST_EMAILS[1], TEST_EMAILS[2], TEST_EMAILS[3]};
		ArrayList<String> actualArrayList = new ArrayList<String>();
		
		// Convert to a String each Address element in the Address[] returned from 
		// 		email.getMimeMessage().getAllRecipients() and add the String to the ArrayList
		for (Address address : email.getMimeMessage().getAllRecipients()) {
			actualArrayList.add(address.toString());
		} // END of for loop that goes through all Address elements in Address[]
		
		// Convert the ArrayList<String> to an Object[] and then to a String[]
		Object[] actualObjectArray = actualArrayList.toArray();
		String[] actualRecipients = Arrays.copyOf(actualObjectArray, actualObjectArray.length, String[].class);
		
		// Sort both the expected and actual recipients arrays, so they will both be in the same order
		Arrays.sort(expectedRecipients);
		Arrays.sort(actualRecipients);
		
		// Use assertArrayEquals to check if every corresponding element in the two String[] match
		assertArrayEquals(expectedRecipients, actualRecipients);
	} // END of Test 1 for buildMimeMessage()
	
	// Test 2 for buildMimeMessage()
	@Test
	public void test2BuildMimeMessage() throws Exception {
		// Note: setFrom(String email) has a test of its own and should not
		//		be the cause of failure for this test if its test has passed.
		
		// Set hostname and From address
		email.setHostName("test.com");
		email.setFrom(TEST_EMAILS[0]);
		// Add a To address
		email.addTo(TEST_EMAILS[1]);
		
		// Create and set the content for the email
		MimeMultipart content = new MimeMultipart();
		content.setPreamble("Preamble Test");
		email.setContent(content);
		
		// Call buildMimeMessage()
		email.buildMimeMessage();
		// Check if the created content matches email.getMimeMessage().getContent()
		assertEquals(content, email.getMimeMessage().getContent());
	} // END of Test 2 for buildMimeMessage()
	
	// Test 3 for buildMimeMessage()
	@Test (expected = EmailException.class)
	public void test3BuildMimeMessage() throws Exception {
		// Should throw an exception for not including a host
		email.buildMimeMessage();
	} // END of Test 3 for buildMimeMessage()
	
	// Test 4 for buildMimeMessage()
	@Test (expected = EmailException.class)
	public void test4BuildMimeMessage() throws Exception {
		// Set the hostname
		email.setHostName("test.com");
		// Should throw an exception for not including a From address
		email.buildMimeMessage();
	} // END of Test 4 for buildMimeMessage()
	
	// Test 5 for buildMimeMessage()
	@Test (expected = EmailException.class)
	public void test5BuildMimeMessage() throws Exception {
		// Note: setFrom(String email) has a test of its own and should not
		//		be the cause of failure for this test if its test has passed.
		
		// Set the hostname and From address
		email.setHostName("test.com");
		email.setFrom(TEST_EMAILS[0]);
		// Should throw an exception for not including any To addresses
		email.buildMimeMessage();
	} // END of Test 5 for buildMimeMessage()
	
	// Test 1 for getHostName()
	@Test
	public void testGetHostName() throws Exception {
		String expectedHostName = "test.com";
		// Set the hostname
		email.setHostName(expectedHostName);
		
		// Call getHostName() and check if the expected and actual match
		String actualHostName = email.getHostName();
		assertEquals(expectedHostName, actualHostName);
	} // END of Test 1 for getHostName()
	
	// Test 2 for getHostName()
	@Test
	public void test2GetHostName() throws Exception {
		String expectedHostName = "test.com";
		// Create properties object and use it to create a session
		Properties prop = new Properties();
		prop.put(EmailConstants.MAIL_HOST, expectedHostName);
		Session expectedSession = Session.getInstance(prop);
		// Set the session
		email.setMailSession(expectedSession);
		
		// Call getHostName() and check if the expected and actual match
		String actualHostName = email.getHostName();
		assertEquals(expectedHostName, actualHostName);
	} // END of Test 2 for getHostName()
	
	// Test for getSentDate()
	@Test
	public void testGetSentDate() throws Exception {
		// Set the sent date
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		Date expectedDate = df.parse("01/01/2020 12:00 PM");
		email.setSentDate(expectedDate);
		
		// Call getSentDate() and see if the expected and actual match
		Date actualDate = email.getSentDate();
		assertEquals(expectedDate, actualDate);
	} // END of Test for getSentDate()
	
	// Test for getSocketConnectionTimeout()
	@Test
	public void testGetSocketConnectionTimeout() throws Exception {
		// Set the socket connection timeout
		int expectedConnectionTimeout = 100;
		email.setSocketConnectionTimeout(expectedConnectionTimeout);
		
		// Call getSocketConnectionTimeout() and see if the expected and actual match
		int actualConnectionTimeout = email.getSocketConnectionTimeout();
		assertEquals(expectedConnectionTimeout, actualConnectionTimeout);
	} // END of Test for getSocketConnectionTimeout()
}
