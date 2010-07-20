/*
 * $Id$
 * 
 * Copyright (c) 2008-2009 David Muller <roxon@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License 2.0 terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pwsafe.lib.file;

import java.util.Arrays;
import java.util.Date;

import junit.framework.TestCase;

import org.pwsafe.lib.UUID;
import org.pwsafe.lib.Util;


/**
 * Tests basic Object Methods for all PwsFields.
 * 
 * @author roxon
 *
 */
public class PwsFieldTest extends TestCase {

	private void basicTwinPwsFieldAsserts (PwsField orig, PwsField twin) {
		assertNotSame(orig, twin);
		assertEquals(orig, twin);
		assertEquals(orig.getValue(), twin.getValue());
		assertTrue(Arrays.equals(orig.getBytes(), twin.getBytes()));
	}
	
	private void basicOtherPwsFieldAsserts (PwsField orig, PwsField other) {
		assertFalse(orig.equals(other));
		assertFalse(orig.getValue().equals(other.getValue()));
		assertFalse(Arrays.equals(orig.getBytes(),other.getBytes()));
	}

	public void testPwsStringField () {
		String value = "title";
		PwsStringField orig = new PwsStringField(PwsRecordV1.TITLE, value);
		byte[] payload = orig.getBytes();
		assertEquals(orig, value);
		
		PwsStringField twin = new PwsStringField(PwsFieldTypeV1.TITLE, value);
		basicTwinPwsFieldAsserts (orig, twin);
		
		PwsStringField other = new PwsStringField(PwsFieldTypeV1.TITLE, value + "1");
		basicOtherPwsFieldAsserts(orig, other);
		
		assertEquals(0, orig.compareTo(twin));
		assertTrue(orig.compareTo(other) < 0);
		
	}
	
	public void testPwsStringUnicodeField () throws Exception {
		String value = "title";
		PwsStringUnicodeField orig = new PwsStringUnicodeField(PwsRecordV1.TITLE, value);
		byte[] payload = orig.getBytes();
		assertEquals(orig, value);

		PwsStringUnicodeField twin = new PwsStringUnicodeField(PwsFieldTypeV1.TITLE, value);
		basicTwinPwsFieldAsserts (orig, twin);
		
		twin = new PwsStringUnicodeField(PwsRecordV1.TITLE, payload);
		basicTwinPwsFieldAsserts (orig, twin);

		PwsStringUnicodeField twin2 = new PwsStringUnicodeField(PwsRecordV1.TITLE, new StringBuilder(value));
		basicTwinPwsFieldAsserts (orig, twin2);
	
		PwsStringUnicodeField other = new PwsStringUnicodeField(PwsFieldTypeV1.TITLE, "title1");
		basicOtherPwsFieldAsserts(orig, other);
	
		assertEquals(0, orig.compareTo(twin));
		assertTrue(orig.compareTo(other) < 0);
		
		PwsStringUnicodeField nullField = new PwsStringUnicodeField(PwsRecordV3.GROUP, (StringBuilder) null);
		assertEquals("null", nullField.toString());
	}
	
	public void testPwsUUIDField () {
		UUID value = new UUID();
		PwsUUIDField orig = new PwsUUIDField(PwsRecordV1.TITLE, value);
		byte[] payload = orig.getBytes();
		assertEquals(orig, value);
		
		PwsUUIDField twin = new PwsUUIDField(PwsFieldTypeV1.TITLE, value);
		basicTwinPwsFieldAsserts (orig, twin);

		twin = new PwsUUIDField(PwsFieldTypeV1.TITLE, value.getBytes());
		basicTwinPwsFieldAsserts (orig, twin);
		
		PwsUUIDField other = new PwsUUIDField(PwsFieldTypeV1.TITLE, new UUID());
		basicOtherPwsFieldAsserts(orig, other);
		
		assertEquals(0, orig.compareTo(twin));
		assertTrue(orig.compareTo(other) != 0);
	}

	public void testPwsTimeField () {
		// PwsTimeField saves seconds, not milliseconds
		Date value = new Date(System.currentTimeMillis() / 1000 * 1000);
		PwsTimeField orig = new PwsTimeField(PwsRecordV1.TITLE, value);
		byte[] payload = orig.getBytes();
		assertEquals(orig, value);
		
		PwsTimeField twin = new PwsTimeField(PwsFieldTypeV1.TITLE, value);
		basicTwinPwsFieldAsserts (orig, twin);

		twin = new PwsTimeField(PwsFieldTypeV1.TITLE, orig.getBytes());
		basicTwinPwsFieldAsserts (orig, twin);
		
		PwsTimeField other = new PwsTimeField(PwsFieldTypeV1.TITLE, new Date((System.currentTimeMillis() + 1000) / 1000 * 1000));
		basicOtherPwsFieldAsserts(orig, other);
		
		assertEquals(0, orig.compareTo(twin));
		assertTrue(orig.compareTo(other) != 0);
	}
	
	public void testPwsIntegerField () {
		Integer value = Integer.valueOf(1234567);
		byte[] byteValue = new byte[4];
		Util.putIntToByteArray(byteValue, value, 0);
//		assertTrue(Arrays.equals(byteValue, Integer.)
		PwsIntegerField orig = new PwsIntegerField(PwsRecordV1.TITLE, byteValue);
		byte[] payload = orig.getBytes();
		assertEquals(orig, value);
		
		PwsIntegerField twin = new PwsIntegerField(PwsFieldTypeV1.TITLE, byteValue);
		basicTwinPwsFieldAsserts (orig, twin);

		twin = new PwsIntegerField(PwsFieldTypeV1.TITLE, orig.getBytes());
		basicTwinPwsFieldAsserts (orig, twin);
				
		Integer otherValue = Integer.valueOf(1234568);
		byte[] otherBytes = new byte[4];
		Util.putIntToByteArray(otherBytes, otherValue, 0);

		PwsIntegerField other = new PwsIntegerField(PwsFieldTypeV1.TITLE, otherBytes);
		basicOtherPwsFieldAsserts(orig, other);
		
		assertEquals(0, orig.compareTo(twin));
		assertTrue(orig.compareTo(other) != 0);
		
	}



}
