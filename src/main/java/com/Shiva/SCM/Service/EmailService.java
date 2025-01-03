package com.Shiva.SCM.Service;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	

	public boolean sendEmail(String subjet,String message,String to)
	{
		// rest of the code
		
		boolean f=false;
		
		String from="byas.paswan99@gmail.com";
		
		// variable for gmail
		
		String host="smtp.gmail.com";
		
		// get the system properties
		 Properties properties=System.getProperties();
		  System.out.println("Properties:"+properties);
		
		// setting important information to the properties
		  
		  // host
		  
		  properties.put("mail.smtp.host", host);
		  properties.put("mail.smtp.port", "465");
		  properties.put("mail.smtp.ssl.enable","true");
		  properties.put("mail.smtp.auth", "true");
		  
		  
		  // step 1: to get the session object..
		   Session session=Session.getInstance(properties,new Authenticator(){

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				
				return new PasswordAuthentication("byas.paswan99@gmail.com","Byas@123456");
			}
			   
			   
			   
		   });
				 
		   
		   
		   
		   session.setDebug(true);
		   
		   
		   // step:2  compose the message [text,multi media]
		   
		   MimeMessage m=new MimeMessage(session);
		   
		   try {
			   // from email
			   
			   m.setFrom(from);
			   
			   // adding recipent to message
			   
			   m.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
			   
			   // adding subject to message 
			   
			   m.setSubject(subjet);
			   
			   // adding text message
			   
			  // m.setText(message);
			   m.setContent(message, "text/html");
			   
			   // step 3: send message using transport class
			   
			   Transport.send(m);
			   
			   System.out.println("Send success.............");
			   f=true;
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
		   }
		   
		   return f;
	}
	
	
}
