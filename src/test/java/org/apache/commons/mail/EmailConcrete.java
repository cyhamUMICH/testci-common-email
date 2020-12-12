package org.apache.commons.mail;

// Concrete class that extends the abstract class Email
public class EmailConcrete extends Email{
	// Override for function not implemented in abstract class
	@Override
	public Email setMsg(String msg) throws EmailException {
		return null;
	}
}