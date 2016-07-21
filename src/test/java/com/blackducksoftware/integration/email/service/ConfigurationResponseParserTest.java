package com.blackducksoftware.integration.email.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.email.Application;
import com.blackducksoftware.integration.email.model.EmailConfiguration;
import com.blackducksoftware.integration.email.model.EmailFrequencyEnum;
import com.blackducksoftware.integration.email.model.EmailTriggerEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class ConfigurationResponseParserTest {
	@Autowired
	private ConfigurationResponseParser configurationResponseParser;

	@Test
	public void testConfigurationResponseIsPopulated() throws IOException {
		final String resourcePath = "com/blackducksoftware/integration/email/service/configuration_response.json";
		final ClassPathResource configResponseResource = new ClassPathResource(resourcePath);
		final String json = IOUtils.toString(configResponseResource.getInputStream(), Charset.forName("UTF-8"));

		final EmailConfiguration emailConfiguration = configurationResponseParser.fromJson(json);
		assertTrue(emailConfiguration.isOptIn());
		assertEquals("borderOfDaises.vm", emailConfiguration.getTemplateName());
		assertTrue(EmailFrequencyEnum.DAILY == emailConfiguration.getEmailFrequency());
		assertEquals(Arrays.asList(EmailTriggerEnum.POLICY_VIOLATION, EmailTriggerEnum.OVERRIDE_POLICY_VIOLATION),
				emailConfiguration.getEmailTriggers());
	}

}
