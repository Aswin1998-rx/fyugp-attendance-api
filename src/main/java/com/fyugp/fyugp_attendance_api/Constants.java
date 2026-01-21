package com.fyugp.fyugp_attendance_api;

import java.util.regex.Pattern;

/**
 * Constants.
 */
public class Constants {
    public static final String COMMA = ",";
    public static final Pattern BASE64_REGEX = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    public static final String ANONYMOUS_USER_NAME = "ANONYMOUS";

    public static final String JWT_USER_EMAIL_CLAIM_KEY = "email";
    public static final String JWT_USER_PRIVILEGE_CLAIM_KEY = "privilegeCacheKey";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String USER_INVITE_SUBJECT = "You're Invited: AlphaStar Booking application";

    public static final String APPLICATION_JSON = "application/json";
    public static final int OTP_LENGTH = 6;

    public static final String OTP_MESSAGE = "message.otpMessage";
    public static final String OTP_SUBJECT = "message.otpSubject";
    public static final String BOOKING_CLIENT = "booking-client";

    public static final String PASSWORD_EXPIRY_SUBJECT = "message.resetThePassword";
    public static final String PASSWORD_EXPIRED = "message.passwordExpired";
    public static final String SMS_API_PATH = "/message";
    public static final String SMS_SIGNIN_API_PATH = "/signin";
    public static final String PASSWORD = "PASSWORD";
    public static final String ALPHA_STAR = "alpha star";
    public static final String OTP = "OTP";
    public static final String DELETED_USER = "Cannot add deleted users: ";
}
