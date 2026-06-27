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

package org.eclipse.gemini.blueprint.test.internal.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.gemini.blueprint.test.internal.holder.HolderLoader;
import org.eclipse.gemini.blueprint.test.internal.holder.OsgiTestInfoHolder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.osgi.framework.BundleContext;
import org.springframework.util.ReflectionUtils;

/**
 * OSGi service for executing JUnit tests.
 *
 * @author Costin Leau
 * @author Michelle Cross
 */
public class OsgiJUnitService extends Runner {

	private BundleContext bc;

	@Override
	public Description getDescription() {
		OsgiTestInfoHolder holder = HolderLoader.INSTANCE.getHolder();
		String testClassName = holder.getTestClassName();
		String testMethodName = holder.getTestMethodName();
		return Description.createTestDescription(testClassName == null ? OsgiJUnitService.class.getName() : testClassName,
				testMethodName == null ? "OsgiJUnitService" : testMethodName);
	}

	@Override
	public void run(RunNotifier notifier) {
		OsgiTestInfoHolder holder = HolderLoader.INSTANCE.getHolder();
		String testClassName = holder.getTestClassName();
		String testMethodName = holder.getTestMethodName();
		Description description = Description.createTestDescription(testClassName, testMethodName);
		notifier.fireTestStarted(description);
		try {
			Class<?> testClass = bc.getBundle().loadClass(testClassName);
			Object testObject = testClass.getDeclaredConstructor().newInstance();
			Method testMethod = ReflectionUtils.findMethod(testClass, testMethodName);
			if (testMethod == null) {
				throw new IllegalArgumentException("no test method [" + testMethodName + "] found on " + testClass);
			}
			if (testMethod.isAnnotationPresent(Ignore.class)) {
				notifier.fireTestIgnored(description);
				return;
			}
			invoke(testClass, testObject, "injectBundleContext", new Class<?>[] { BundleContext.class }, bc);
			invoke(testClass, testObject, "osgiSetUp", new Class<?>[0]);
			try {
				if (testMethod.isAnnotationPresent(Test.class)) {
					ReflectionUtils.makeAccessible(testMethod);
					testMethod.invoke(testObject);
				}
				else {
					invoke(testClass, testObject, "osgiRunTest", new Class<?>[0]);
				}
			}
			finally {
				invoke(testClass, testObject, "osgiTearDown", new Class<?>[0]);
			}
		}
		catch (Throwable ex) {
			notifier.fireTestFailure(new Failure(description, ex));
		}
		finally {
			notifier.fireTestFinished(description);
		}
	}

	private void invoke(Class<?> testClass, Object testObject, String methodName, Class<?>[] parameterTypes,
			Object... arguments) throws Throwable {
		Method method = ReflectionUtils.findMethod(testClass, methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("no method [" + methodName + "] found on " + testClass);
		}
		ReflectionUtils.makeAccessible(method);
		try {
			method.invoke(testObject, arguments);
		}
		catch (InvocationTargetException ex) {
			throw ex.getTargetException();
		}
	}

	public void setBundleContext(BundleContext bc) {
		this.bc = bc;
	}
}
