package com.fyugp.fyugp_attendance_api.utils;

import java.security.SecureRandom;
import java.util.*;

public class PasswordUtil {
    private static final char[] ALPHA_UPPER_CHARACTERS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    private static final char[] ALPHA_LOWER_CHARACTERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    private static final char[] NUMERIC_CHARACTERS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    private static final char[] SPECIAL_CHARACTERS = { '~', '`', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', '[', '{', ']', '}', '\\', '|', ';', ':', ',', '<', '.', '>','?' };
    private static final Random random = new SecureRandom();

    private enum PasswordCharacterSet {
        ALPHA_UPPER(ALPHA_UPPER_CHARACTERS, 1),
        ALPHA_LOWER(ALPHA_LOWER_CHARACTERS, 1),
        NUMERIC(NUMERIC_CHARACTERS, 1),
        SPECIAL(SPECIAL_CHARACTERS, 1);

        private final char[] chars;
        private final int minUsage;

        PasswordCharacterSet(char[] chars, int minUsage) {
            this.chars = chars;
            this.minUsage = minUsage;
        }

        public char[] getCharacters() {
            return chars;
        }

        public int getMinCharacters() {
            return minUsage;
        }
    }

    public static String generatePassword(int minLength, int maxLength) {
        Set<PasswordCharacterSet> characterSets = EnumSet.allOf(PasswordCharacterSet.class);
        List<PasswordCharacterSet> pwSets = new ArrayList<>(characterSets);

        int presetCharacterCount = 0;
        int totalCharacterCount = 0;
        for (PasswordCharacterSet pwSet : pwSets) {
            presetCharacterCount += pwSet.getMinCharacters();
            totalCharacterCount += pwSet.getCharacters().length;
        }

        if (minLength < presetCharacterCount) {
            throw new IllegalArgumentException("Combined minimum lengths "
                    + presetCharacterCount
                    + " are greater than the minLength of " + minLength);
        }

        char[] allCharacters = new char[totalCharacterCount];
        int currentIndex = 0;
        for (PasswordCharacterSet pwSet : pwSets) {
            char[] chars = pwSet.getCharacters();
            System.arraycopy(chars, 0, allCharacters, currentIndex, chars.length);
            currentIndex += chars.length;
        }

        SecureRandom rand = new SecureRandom();
        int pwLength = minLength + rand.nextInt(maxLength - minLength + 1);
        int randomCharacterCount = pwLength - presetCharacterCount;

        List<Integer> remainingIndexes = new ArrayList<>(pwLength);
        for (int i = 0; i < pwLength; ++i) {
            remainingIndexes.add(i);
        }

        char[] password = new char[pwLength];
        for (PasswordCharacterSet pwSet : pwSets) {
            addRandomCharacters(password, pwSet.getCharacters(), pwSet.getMinCharacters(), remainingIndexes, rand);
        }
        addRandomCharacters(password, allCharacters, randomCharacterCount, remainingIndexes, rand);

        return new String(password);
    }

    private static void addRandomCharacters(char[] password, char[] characterSet, int numCharacters, List<Integer> remainingIndexes, SecureRandom rand) {
        for (int i = 0; i < numCharacters; ++i) {
            int pwIndex = remainingIndexes.remove(rand.nextInt(remainingIndexes.size()));
            int randCharIndex = rand.nextInt(characterSet.length);
            password[pwIndex] = characterSet[randCharIndex];
        }
    }



    /**
     * This function generates random cryptographically secure random numbers.
     *
     * @param length the size of the string generated
     * @return random number string
     */
    public static String secureRandomNumber(int length) {
        return random.ints(length, '0', '9')
                .mapToObj(ch -> (char) ch)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
