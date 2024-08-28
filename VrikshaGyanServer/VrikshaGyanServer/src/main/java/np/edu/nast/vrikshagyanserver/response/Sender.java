package np.edu.nast.vrikshagyanserver.response;

import java.util.Properties;

import jakarta.mail.*;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class Sender {
	final static String EMAIL = "vrikshagyan@gmail.com";
	final static String PASSWORD = "hfqh nszf jtbw pjnb";
	private static int RESULT = 0;
	public static int send(String to, String subject, String body){
		try {
			Properties props = System.getProperties();
			props.setProperty("mail.transport.protocol", "smtp");
			props.setProperty("mail.host", "smtp.gmail.com");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "25");
			props.put("mail.debug", "true");
			props.put("mail.smtp.socketFactory.port", "25");
			props.put("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");

			Session emailSession = Session.getInstance(props,
					new jakarta.mail.Authenticator() {
				protected PasswordAuthentication 
				getPasswordAuthentication() {
					return new 
							PasswordAuthentication(EMAIL,PASSWORD);
				}
			});
			emailSession.setDebug(true);
			Message message = new MimeMessage(emailSession);
			message.setFrom(new InternetAddress("VrikshaGyan <" + EMAIL + ">"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(body);
			Transport transport = emailSession.getTransport("smtps");
			transport.connect("smtp.gmail.com", EMAIL, PASSWORD);
			transport.sendMessage(message, message.getAllRecipients());
			RESULT = 1;
		} catch (MessagingException e) {
			RESULT = 0;
		}
		return RESULT;
	}
}