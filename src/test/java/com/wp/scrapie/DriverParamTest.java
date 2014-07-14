package com.wp.scrapie;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;

public class DriverParamTest {

	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	@Rule
	public final StandardOutputStreamLog log = new StandardOutputStreamLog();

	@Before
	public void before() {
		log.clear();
	}

	@Test
	public void testNoFile() throws Exception {
		test(new String[] { "-o", "myoutput" }, "Missing required option: f");
	}

	@Test
	public void testNoOutput() throws Exception {
		test(new String[] { "-f", "myInput" }, "Missing required option: o");
	}

	private void test(String[] test, String assertion) {
		exit.expectSystemExit();
		runWithArgs(test);
		assertTrue("looking for: " + assertion + ", found: " + log.getLog(),
				log.getLog().contains(assertion));
	}

	private void runWithArgs(String[] test) {
		try {
			Driver.main(test);
			fail("should exit");
		} catch (Exception e) {
		}
	}
}
