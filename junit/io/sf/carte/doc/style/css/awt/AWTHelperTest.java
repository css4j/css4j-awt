/*

 Copyright (c) 2005-2019, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

package io.sf.carte.doc.style.css.awt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.Locale;

import org.junit.Test;

import io.sf.carte.doc.dom.HTMLDocument;
import io.sf.carte.doc.dom.TestDOMImplementation;
import io.sf.carte.doc.style.css.CSSComputedProperties;
import io.sf.carte.doc.style.css.CSSElement;
import io.sf.carte.doc.style.css.CSSMediaException;
import io.sf.carte.doc.style.css.CSSTypedValue;
import io.sf.carte.doc.style.css.CSSValue;
import io.sf.carte.doc.style.css.CSSValue.CssType;
import io.sf.carte.doc.style.css.ExtendedCSSStyleDeclaration;
import io.sf.carte.doc.style.css.om.BaseCSSStyleDeclaration;
import io.sf.carte.doc.style.css.property.CSSPropertyValueException;

public class AWTHelperTest {

	@Test
	public void testGetAWTColor() throws CSSPropertyValueException {
		ExtendedCSSStyleDeclaration style = new BaseCSSStyleDeclaration();
		style.setCssText("color: rgba(8,63,255,0.5); ");
		CSSValue cssColor = style.getPropertyCSSValue("color");
		assertNotNull(cssColor);
		assertEquals(CssType.TYPED, cssColor.getCssValueType());
		assertEquals(CSSValue.Type.COLOR, ((CSSTypedValue) cssColor).getPrimitiveType());
		Color color = AWTHelper.getAWTColor((CSSTypedValue) cssColor);
		assertNotNull(color);
		assertEquals(8, color.getRed());
		assertEquals(63, color.getGreen());
		assertEquals(255, color.getBlue());
		assertEquals(128, color.getAlpha());
		//
		style.setCssText("color: #f00; ");
		cssColor = style.getPropertyCSSValue("color");
		assertNotNull(cssColor);
		assertEquals(CssType.TYPED, cssColor.getCssValueType());
		assertEquals(CSSValue.Type.COLOR, ((CSSTypedValue) cssColor).getPrimitiveType());
		color = AWTHelper.getAWTColor((CSSTypedValue) cssColor);
		assertNotNull(color);
		assertEquals(255, color.getRed());
		assertEquals(0, color.getGreen());
		assertEquals(0, color.getBlue());
		assertEquals(255, color.getAlpha());
		//
		style.setCssText("color: transparent; ");
		cssColor = style.getPropertyCSSValue("color");
		assertNotNull(cssColor);
		assertEquals(CssType.TYPED, cssColor.getCssValueType());
		assertEquals(CSSValue.Type.IDENT, ((CSSTypedValue) cssColor).getPrimitiveType());
		color = AWTHelper.getAWTColor((CSSTypedValue) cssColor);
		assertNotNull(color);
		assertEquals(0, color.getAlpha());
	}

	@Test
	public void testCreateFont() throws CSSMediaException, IOException {
		HTMLDocument xhtmlDoc = TestDOMImplementation.sampleHTMLDocument();
		CSSElement elm = xhtmlDoc.getElementById("span1");
		xhtmlDoc.setTargetMedium("handheld");
		CSSComputedProperties styledecl = xhtmlDoc.getStyleSheet().getComputedStyle(elm, null);
		assertNotNull(styledecl);
		Font font = AWTHelper.createFont(styledecl);
		assertNotNull(font);
		assertNotNull(font.getFamily(Locale.ROOT));
		assertEquals("Helvetica", font.getName());
		assertEquals(12, font.getSize());
		xhtmlDoc.setTargetMedium("screen");
		styledecl = xhtmlDoc.getStyleSheet().getComputedStyle(elm, null);
		assertNotNull(styledecl);
		font = AWTHelper.createFont(styledecl);
		assertNotNull(font);
		assertNotNull(font.getFamily(Locale.ROOT));
		assertEquals("Helvetica", font.getName());
		assertEquals(20, font.getSize());
	}
}
