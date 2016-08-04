package com.blackducksoftware.integration.email.notifier.routers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.EmailData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.notification.NotificationService;
import com.blackducksoftware.integration.hub.notification.api.PolicyOverrideNotificationItem;

public class PolicyViolationOverrideCancelRouter extends AbstractEmailRouter<PolicyOverrideNotificationItem> {

	public PolicyViolationOverrideCancelRouter(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties, final NotificationService notificationService,
			final EmailTaskData taskData) {
		super(emailMessagingService, customerProperties, notificationService, taskData);
	}

	@Override
	public EmailData transform(final List<PolicyOverrideNotificationItem> data) {
		final List<String> addresses = new ArrayList<>();
		final Map<String, Object> emailDataMap = new HashMap<>();
		return new EmailData(addresses, emailDataMap);
	}
}
