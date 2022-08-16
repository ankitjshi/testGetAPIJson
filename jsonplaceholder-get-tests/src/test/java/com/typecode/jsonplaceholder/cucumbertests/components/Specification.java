package com.typecode.jsonplaceholder.cucumbertests.components;

import com.typecode.jsonplaceholder.cucumbertests.utils.Logger;
import com.typecode.jsonplaceholder.cucumbertests.utils.ConfigProvider;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

/**
 * RestAssured used specifications to hit the URL
 * The method builds Request and Response Specification required for the hits
 */
public class Specification {
    /**
     * Initialize Loggers - Commonly used to log information
     * static variables Request and Response Specification for easier access via class
     */
    public static RequestSpecification requestSpecification;
    public static ResponseSpecification responseSpecification;
    public static org.apache.logging.log4j.Logger log = Logger.log;

    /**
     * Initializes the builder with help of constructor
     * Response Specification validates the URI is valid with response 200
     */
    public Specification() {
        log.info("Initializing request and response specifications");
        Specification.requestSpecification = new RequestSpecBuilder().setBaseUri(ConfigProvider.BASE_URL)
                .addHeader("Content-Type", "application/json")
                .build();
        Specification.responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(anyOf(is(200), is(201)))
                .build();
        log.info("Specification Verified.. URI to Ready");
    }
}
