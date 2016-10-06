package com.blackducksoftware.integration.email.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.email.EmailEngine;
import com.blackducksoftware.integration.email.mock.TestEmailEngine;
import com.blackducksoftware.integration.email.model.EmailTarget;
import com.blackducksoftware.integration.email.model.ProjectDigest;
import com.blackducksoftware.integration.email.model.ProjectsDigest;
import com.blackducksoftware.integration.email.notifier.AbstractDigestNotifier;
import com.blackducksoftware.integration.email.transformer.NotificationCountTransformer;
import com.blackducksoftware.integration.hub.api.notification.VulnerabilitySourceQualifiedId;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.dataservices.notification.items.ComponentAggregateData;
import com.blackducksoftware.integration.hub.dataservices.notification.items.ComponentVulnerabilitySummary;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.ProjectAggregateData;
import com.blackducksoftware.integration.hub.dataservices.notification.items.VulnerabilityContentItem;

import freemarker.template.TemplateException;

public class EmailMessagingServiceTest {

	private EmailEngine engine;

	@Before
	public void init() throws Exception {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final URL propFileUrl = classLoader.getResource("extension.properties");
		final File file = new File(propFileUrl.toURI());
		System.setProperty("ext.config.location", file.getCanonicalFile().getParent());
		engine = new TestEmailEngine();
		engine.start();
	}

	@Test
	public void testSendingEmail() throws IOException, MessagingException, TemplateException {
		final Map<String, Object> model = new HashMap<>();
		model.put("title", "A Glorious Day");
		model.put("message", "this should have html and plain text parts");
		model.put("items", Arrays.asList("apple", "orange", "pear", "banana"));
		final EmailTarget target = new EmailTarget("testUser@a.domain.com1", "sampleTemplate.ftl", model);
		engine.getEmailMessagingService().sendEmailMessage(target);
	}

	@Test
	public void testDigest() throws Exception {
		final ProjectVersion projectVersion = new ProjectVersion();
		projectVersion.setProjectName("Test Project");
		projectVersion.setProjectVersionName("0.1.0-TEST");
		projectVersion.setProjectVersionLink("versionLink");
		final String componentName = "componentName";
		final String componentVersion = "componentVersion";
		final String firstName = "firstName";
		final String lastName = "lastName";
		final UUID componentId = UUID.randomUUID();
		final UUID componentVersionId = UUID.randomUUID();
		final List<PolicyViolationContentItem> violationList = new ArrayList<>();
		final List<PolicyOverrideContentItem> overrideList = new ArrayList<>();
		final List<VulnerabilityContentItem> vulnerabilityList = new ArrayList<>();
		final PolicyRule rule = new PolicyRule(null, "aRule", "", true, true, null, "", "", "", "");
		final List<PolicyRule> ruleList = new ArrayList<>();
		ruleList.add(rule);
		final PolicyViolationContentItem violationContent = new PolicyViolationContentItem(new Date(), projectVersion,
				componentName, componentVersion, componentId, componentVersionId, ruleList);
		final PolicyOverrideContentItem overrideContent = new PolicyOverrideContentItem(new Date(), projectVersion,
				componentName, componentVersion, componentId, componentVersionId, ruleList, firstName, lastName);

		final List<VulnerabilitySourceQualifiedId> sourceIdList = new ArrayList<>();
		sourceIdList.add(new VulnerabilitySourceQualifiedId("source", "id"));
		final VulnerabilityContentItem vulnerabilityContent = new VulnerabilityContentItem(new Date(), projectVersion,
				componentName, componentVersion, componentId, componentVersionId, sourceIdList, sourceIdList,
				sourceIdList);
		violationList.add(violationContent);
		overrideList.add(overrideContent);
		vulnerabilityList.add(vulnerabilityContent);
		final int sourceIDSize = sourceIdList.size();
		final int total = violationList.size() + overrideList.size() + vulnerabilityList.size();
		final ComponentVulnerabilitySummary vulnSummary = new ComponentVulnerabilitySummary(componentName,
				componentVersion, 1, 2, 3, 6);
		final ComponentAggregateData componentData = new ComponentAggregateData(componentName, componentVersion,
				violationList, overrideList, vulnerabilityList, sourceIDSize, sourceIDSize, sourceIDSize, vulnSummary);
		final List<ComponentAggregateData> componentList = new ArrayList<>();
		componentList.add(componentData);
		final ProjectAggregateData countData = new ProjectAggregateData(new Date(), new Date(), projectVersion,
				violationList.size(), overrideList.size(), vulnerabilityList.size(), total, sourceIDSize, sourceIDSize,
				sourceIDSize, componentList);
		final List<ProjectDigest> projectData = new ArrayList<>();
		final NotificationCountTransformer transformer = new NotificationCountTransformer(true, true, true, true);
		final ProjectDigest digest = transformer.transform(countData);
		projectData.add(digest);

		final Map<String, String> totalsMap = new HashMap<>();
		totalsMap.put(AbstractDigestNotifier.KEY_TOTAL_NOTIFICATIONS, String.valueOf(countData.getTotal()));
		totalsMap.put(AbstractDigestNotifier.KEY_TOTAL_POLICY_VIOLATIONS,
				String.valueOf(countData.getPolicyViolationCount()));
		totalsMap.put(AbstractDigestNotifier.KEY_TOTAL_POLICY_OVERRIDES,
				String.valueOf(countData.getPolicyOverrideCount()));
		totalsMap.put(AbstractDigestNotifier.KEY_TOTAL_VULNERABILITIES,
				String.valueOf(countData.getVulnerabilityCount()));
		final ProjectsDigest projectsDigest = new ProjectsDigest(totalsMap, projectData);

		final Map<String, Object> model = new HashMap<>();
		model.put("startDate", String.valueOf(countData.getStartDate()));
		model.put("endDate", String.valueOf(countData.getEndDate()));
		model.put("hubUserName", "Mr./Ms. Hub User");
		model.put("notificationCounts", projectsDigest);
		model.put("hubServerUrl", "http://hub-a.domain.com1/");
		model.put(AbstractDigestNotifier.KEY_CATEGORY, "Daily");

		final EmailTarget target = new EmailTarget("testUser@a.domain.com1", "digest.ftl", model);
		engine.getEmailMessagingService().sendEmailMessage(target);
	}
}
