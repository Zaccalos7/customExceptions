package com.orbis.exception.info;

/**
 * Class for store current project verion and
 * annotations package
 */
public enum PROJECT {
    VERSION("1.0.2"),
    PACKAGE("com.orbis.exception.annotations");

    private final String value;

    PROJECT(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
