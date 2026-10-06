package runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

import io.cucumber.core.options.Constants;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("steps")
@ConfigurationParameter(key = Constants.FEATURES_PROPERTY_NAME, value = "src/test/resources/features/")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "steps,hooks,mappers")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty,html:test-results/cucumber.html,json:test-results/cucumber.json,junit:test-results/cucumber.xml")
@ConfigurationParameter(key = Constants.SNIPPET_TYPE_PROPERTY_NAME, value = "camelcase")
@ConfigurationParameter(key = Constants.EXECUTION_DRY_RUN_PROPERTY_NAME, value = "false")
@ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "@Home")
@SuiteDisplayName("BDD Tests")
class TestRunner {

}
