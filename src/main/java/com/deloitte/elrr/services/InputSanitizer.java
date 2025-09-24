package com.deloitte.elrr.services;

import org.apache.commons.validator.GenericValidator;

public class InputSanitizer {

    protected InputSanitizer() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("checkstyle:linelength")
    private static final String CHAR_WHITE_LIST_REGEX = "^[\\x09\\x0A\\x0D\\x20-\\x7E | \\xC2-\\xDF | \\xE0\\xA0-\\xBF | [\\xE1-\\xEC\\xEE\\xEF]{2} | \\xED\\x80-\\x9F | [\\xF0\\\\x90-\\xBF]{2} | [\\xF1-\\xF3]{3} | [\\xF4\\x80-\\x8F]{2}]*$";

    /**
     * Filter execution method.
     *
     * @param input Input string to check
     * @return Boolean whether or not input is valid
     */
    public static boolean isValidInput(String input) {
        return GenericValidator.matchRegexp(input, CHAR_WHITE_LIST_REGEX);
    }
}
