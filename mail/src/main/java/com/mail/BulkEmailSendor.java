package com.mail;

import org.apache.log4j.Logger;
import java.sql.*;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

/**
 * BulkEmailSendor Author : Chetan Khatri
 */
public class BulkEmailSendor {

	final static String USER_ID = "noreply@kutchibrahmakshatriya.com";
	final static String USER_PW = "password"; // Replace with actual password
	final static Logger logger = Logger.getLogger(BulkEmailSendor.class);

	public static void main(String[] args) {
		BulkEmailSendor ap = new BulkEmailSendor();
		ap.sendEmail();
	}

	public void sendEmail() {

		Connection con;
		Statement st;
		ResultSet rs;
		Statement stNotify;
		try {
			Class.forName("java.sql.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/wwwkutch_vpdata", "root", "12345");

			Properties pros = new Properties();

			pros.put("mail.smtp.host", "mail.kutchibrahmakshatriya.com");
			pros.put("mail.smtp.auth", true);
			pros.put("mail.smtp.port", "587");

			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getDefaultInstance(pros, auth);
			Message msg = new MimeMessage(session);
			InternetAddress addressFrom = new InternetAddress(USER_ID, "Kachchi Brahmkshtriya Community");
			msg.setFrom(addressFrom);
			String subject = "Greetings from Shree Kutchhi Brahmkshtriya Community web portal";

			st = con.createStatement();
			rs = st.executeQuery("select email,username, passw from wwwkutch_vpdata.users");

			while (rs.next()) {

				String email = rs.getString("email").replaceAll("\\s+", "");
				String userName = rs.getString("username").replaceAll("\\s+", "");
				String password = rs.getString("passw").replaceAll("\\s+", "");

				InternetAddress addressTo = new InternetAddress(email);
				msg.setRecipient(Message.RecipientType.TO, addressTo);

				String content = "<html>" + "<body>" + "<b>Dear Kutchhi Brahmkshtriya Community User,</b> </br></br>"
						+ "Greetings from Shree Kutchhi Brahmkshtriya Community web portal !</br></br>"
						+ " Kindly Update your and your family Information at our digital portal www.kutchibrahmakshatriya.com</br>"
						+ " If you have forgotten your username and password,</br> here is your username and password.</br>"
						+ "<b> Username: </b>" + userName + " </br>" + "<b> Password: </b>" + password + " </br>"
						+ "</body></html>";

				msg.setSubject(subject);
				msg.setContent(content, "text/html");
				Transport.send(msg);
				System.out.println("Message has been sent");
				logger.info("Message has been sent to " + addressTo);

				stNotify = con.createStatement();
				stNotify.executeUpdate("update wwwkutch_vpdata.users set notify=1 where username='"
						+ rs.getString("username") + "'" + "and passw='" + rs.getString("passw") + "'");

			}

		}

		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			String username = USER_ID;
			String password = USER_PW;
			return new PasswordAuthentication(username, password);
		}
	}
}
