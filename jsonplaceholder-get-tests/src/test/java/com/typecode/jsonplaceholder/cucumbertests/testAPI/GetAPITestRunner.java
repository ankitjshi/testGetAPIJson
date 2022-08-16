package com.typecode.jsonplaceholder.cucumbertests.testAPI;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * TestRunner to run all the tests based on the tags passed
 * It glues test features with implementation step-definitions
 * tags - Smoke, Sanity, Regression, Performance
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "json:target/cucumber-report.json", "html:target/cucumber-report"},
//        dryRun = true,
        features = "src/test/resources/features",
        glue = {"com.typecode.jsonplaceholder.cucumbertests.testAPI"},
        tags = "@Regression"
)
public class GetAPITestRunner {
}
