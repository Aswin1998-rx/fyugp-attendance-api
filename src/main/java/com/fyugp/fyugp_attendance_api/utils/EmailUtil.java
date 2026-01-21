package com.fyugp.fyugp_attendance_api.utils;


import com.fyugp.fyugp_attendance_api.exceptions.AppException;

/**
 * Email utils- username extraction.
 */
public class EmailUtil {

    /**
     * Utility function to extract username.
     *
     * @param email email
     * @return extracted email
     */
    public static String extractUserName(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }

        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            throw new AppException("errors.invalidEmail");
        }

        var username = email.substring(0, atIndex);
        return username;
    }
}
