/******************************************************************************
 * Copyright (c) 2006, 2010 VMware Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html and the Apache License v2.0
 * is available at http://www.opensource.org/licenses/apache2.0.php.
 * You may elect to redistribute this code under either of these licenses.
 *
 * Contributors:
 *   VMware Inc.
 *****************************************************************************/

package org.eclipse.gemini.blueprint.iandt.testingFramework;

import static org.junit.Assert.fail;

import org.eclipse.gemini.blueprint.test.junit4.ConditionalTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Verifies that the custom test runner honors environment-based test skips.
 *
 * @author Costin Leau
 */
@RunWith(SkipTestRunner.class)
public class SkipTestsTst extends ConditionalTestCase {

	static final String TEST_SKIPPED_1 = "testFirstSkipped";
	static final String TEST_SKIPPED_2 = "testSecondSkipped";
	static final String TEST_RAN = "testActuallyRan";

	@Test
	public void testFirstSkipped() {
		fail("test should be skipped");
	}

	@Test
	public void testActuallyRan() {
	}

	@Test
	public void testSecondSkipped() {
		fail("test should be skipped");
	}

	@Override
	public boolean isDisabledInThisEnvironment(String testMethodName) {
		return testMethodName.endsWith("Skipped");
	}
}
