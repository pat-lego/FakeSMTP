package com.nilhcem.fakesmtp.server;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.auth.LoginAuthenticationHandlerFactory;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import com.nilhcem.fakesmtp.auth.SimpleUsernamePasswordValidator;
import com.nilhcem.fakesmtp.core.exception.BindPortException;
import com.nilhcem.fakesmtp.core.exception.OutOfRangePortException;

/**
 * Starts and stops the SMTP server.
 *
 * @author Nilhcem
 * @since 1.0
 */
public enum SMTPServerHandler {
	INSTANCE;

	private final String SMTP_ENABLE_AUTH = "SMTP_ENABLE_AUTH";
	private static final Logger LOGGER = LoggerFactory.getLogger(SMTPServerHandler.class);
	private final MailSaver mailSaver = new MailSaver();
	private final MailListener myListener = new MailListener(mailSaver);
	private SMTPServer smtpServer = null;

	SMTPServerHandler() {
	}

	/**
	 * Starts the server on the port and address specified in parameters.
	 *
	 * @param port the SMTP port to be opened.
	 * @param bindAddress the address to bind to. null means bind to all.
	 * @throws BindPortException when the port can't be opened.
	 * @throws OutOfRangePortException when port is out of range.
	 * @throws IllegalArgumentException when port is out of range.
	 */
	public void startServer(int port, InetAddress bindAddress) throws BindPortException, OutOfRangePortException {
		LOGGER.debug("Starting server on port {}", port);
		try {
			getSmtpServer().setBindAddress(bindAddress);
			getSmtpServer().setPort(port);
			getSmtpServer().start();
		} catch (RuntimeException exception) {
			if (exception.getMessage().contains("BindException")) { // Can't open port
				LOGGER.error("{}. Port {}", exception.getMessage(), port);
				throw new BindPortException(exception, port);
			} else if (exception.getMessage().contains("out of range")) { // Port out of range
				LOGGER.error("Port {} out of range.", port);
				throw new OutOfRangePortException(exception, port);
			} else { // Unknown error
				LOGGER.error("", exception);
				throw exception;
			}
		}
	}

	/**
	 * Stops the server.
	 * <p>
	 * If the server is not started, does nothing special.
	 * </p>
	 */
	public void stopServer() {
		if (getSmtpServer().isRunning()) {
			LOGGER.debug("Stopping server");
			getSmtpServer().stop();
		}
	}

	/**
	 * Returns the {@code MailSaver} object.
	 *
	 * @return the {@code MailSaver} object.
	 */
	public MailSaver getMailSaver() {
		return mailSaver;
	}

	/**
	 * Returns the {@code SMTPServer} object.
     *
     * @return the {@code SMTPServer} object.
	 */
	public SMTPServer getSmtpServer() {
		if (this.smtpServer != null) {
			return this.smtpServer;
		}

		if (System.getenv(SMTP_ENABLE_AUTH) != null && System.getenv(SMTP_ENABLE_AUTH).equals("true")) {
			this.smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(myListener), new LoginAuthenticationHandlerFactory(new SimpleUsernamePasswordValidator()));
		} else {
			this.smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(myListener), new SMTPAuthHandlerFactory());
		}
		return smtpServer;
	}
}
