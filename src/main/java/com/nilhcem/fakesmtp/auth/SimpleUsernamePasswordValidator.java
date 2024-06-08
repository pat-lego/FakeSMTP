package com.nilhcem.fakesmtp.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.auth.LoginFailedException;
import org.subethamail.smtp.auth.UsernamePasswordValidator;

public class SimpleUsernamePasswordValidator implements UsernamePasswordValidator {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String SMTP_AUTH_USERNAME = "SMTP_AUTH_USERNAME";
    private final String SMTP_AUTH_PASSWORD = "SMTP_AUTH_PASSWORD";

    private final String DEFAULT_USERNAME = "testuser";
    private final String DEFAULT_PASSWORD = "testuser";

    @Override
    public void login(String username, String password) throws LoginFailedException {
        if (username != null && password != null) {
            if (username.equals(this.getUsernameEnv()) && password.equals(this.getPasswordEnv())) {
                return;
            } else {
                throw new LoginFailedException("Username and Password do not match expected result");
            }
        }
        throw new LoginFailedException("Username and Password are empty please provide a valid set of crendentials");
    }

    public String getUsernameEnv() {
        if (System.getenv(SMTP_AUTH_USERNAME) != null && !System.getenv(SMTP_AUTH_USERNAME).isBlank()) {
            return System.getenv(SMTP_AUTH_USERNAME);
        }
        logger.info("{} env var is empty defaulting to default username", SMTP_AUTH_USERNAME);
        return DEFAULT_USERNAME;
    }

    public String getPasswordEnv() {
        if (System.getenv(SMTP_AUTH_PASSWORD) != null && !System.getenv(SMTP_AUTH_PASSWORD).isBlank()) {
            return System.getenv(SMTP_AUTH_PASSWORD);
        }
        logger.info("{} env var is empty defaulting to default password", SMTP_AUTH_PASSWORD);
        return DEFAULT_PASSWORD;
    }

}
