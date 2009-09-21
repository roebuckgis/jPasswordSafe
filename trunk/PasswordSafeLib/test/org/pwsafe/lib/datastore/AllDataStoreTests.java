/*
 * $Id$
 * Copyright (c) 2008-2009 David Muller <roxon@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License 2.0 terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pwsafe.lib.datastore;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllDataStoreTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for org.pwsafe.lib.datastore");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestSparseRecords.class);
		//$JUnit-END$
		return suite;
	}

}
