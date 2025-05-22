package com.dealermonkey.api.docs;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ApiDocsConstants {
    public static final String API_VERSION = "1";
    public static final String API_BASE_RESOURCE_PATH = "/v" + API_VERSION + "/";
    public static final String GPL3_URL =
            "https://www.gnu.org/licenses/gpl-3.0.en.html?ref=itsfoss.com#license-text";
    public static final String API_DESCRIPTION = """
            A RESTful API for managing a standard 52 card deck. 
            """;
}