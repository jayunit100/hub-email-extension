package com.blackducksoftware.integration.email.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import com.blackducksoftware.integration.email.model.EmailData;
import com.blackducksoftware.integration.email.notifier.routers.AbstractEmailRouter;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;

public class MockRouter extends AbstractEmailRouter<String> {

	public final static String ROUTER_NAME = "Mock Router";
	private final String expectedData;

	public MockRouter(final EmailMessagingService emailMessagingService, final EmailTaskData taskData,
			final String expectedData) {
		super(emailMessagingService, taskData);
		this.expectedData = expectedData;
	}

	@Override
	public String getName() {
		return ROUTER_NAME;
	}

	@Override
	public EmailData transform(final List<String> data) {
		assertNotNull(data);
		assertEquals(1, data.size());
		final String item = data.get(0);
		assertEquals(expectedData, item);
		return null;
	}
}
