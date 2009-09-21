/*
 * $Id$
 * Copyright (c) 2008-2009 David Muller <roxon@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License 2.0 terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pwsafe.lib.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import junit.framework.TestCase;

import org.pwsafe.lib.Util;

import com.amazonaws.crypto.Base64;

public class StreamTests extends TestCase {
	public void testCryptoStreams() throws Exception {
		byte[] shortArray = new byte[33];
		byte[] longArray = new byte[1027];
		byte[] giantArray = new byte[88372];
		
		for(int i=0;i<10;i++) {
			System.out.println("Test round "+i);
			Util.newRandBytes(shortArray);
//			System.out.println("Short Array:");
//			System.out.println(Base64.encode(shortArray));
			runSingleTest(shortArray);
			Util.newRandBytes(longArray);
//			System.out.println("Long Array:");
//			System.out.println(Base64.encode(longArray));
			runSingleTest(longArray);
			Util.newRandBytes(giantArray);
//			System.out.println("Giant Array:");
//			System.out.println(Base64.encode(giantArray));
			runSingleTest(giantArray);
		}
	}
	public void runSingleTest(byte[] testdata) throws Exception {
		FileOutputStream rawout = new FileOutputStream("crypto_sample.dat");
		CryptoOutputStream co = new CryptoOutputStream("TEST", rawout);
		co.write(testdata);
		co.close();
		FileInputStream readback = new FileInputStream("crypto_sample.dat");
		CryptoInputStream ci = new CryptoInputStream("TEST", readback);
		byte[] readdata = new byte[testdata.length];
		ci.read(readdata);
		for(int j=0;j<testdata.length;j++) {
			assertEquals(testdata[j], readdata[j]);
		}
		int padding = 16 - (testdata.length % 16);
		for(int i=0;i<padding;i++) {
			assertTrue(ci.read()==0);
		}
		assertTrue(ci.read()==-1);
		ci.close();
	}
	public void testSignedBytes() throws Exception {
		String test = "Y8QcrYP/OGZT/8tdcobZRoGB";
		byte [] data = Base64.decodeData(test);
		runSingleTest(data);
	}
}
