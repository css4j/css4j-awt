/*

 Copyright (c) 2005-2019, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

package io.sf.carte.doc.style.css.awt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Font;
import java.io.IOException;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import io.sf.carte.doc.agent.HeadlessDeviceFactory;
import io.sf.carte.doc.style.css.CSSDocument;
import io.sf.carte.doc.style.css.CSSElement;
import io.sf.carte.doc.style.css.CSSMediaException;
import io.sf.carte.doc.style.css.CSSStyleSheet;
import io.sf.carte.doc.style.css.LinkStyle;
import io.sf.carte.doc.style.css.om.FontFaceRule;
import io.sf.carte.doc.style.css.om.TestCSSStyleSheetFactory;

public class AWTStyleDatabaseTest {

	private CSSDocument cssdoc;
	private Node styleText;
	private CSSStyleSheet<?> sheet;
	private AWTStyleDatabase styleDb;

	@Before
	public void setUp() throws DOMException, ParserConfigurationException, CSSMediaException {
		TestCSSStyleSheetFactory factory = new TestCSSStyleSheetFactory();
		HeadlessDeviceFactory deviceFactory = new HeadlessDeviceFactory();
		styleDb = new AWTStyleDatabase();
		deviceFactory.setStyleDatabase("screen", styleDb);
		factory.setDeviceFactory(deviceFactory);
		DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
		Document doc = dbFac.newDocumentBuilder().getDOMImplementation().createDocument(null, "html", null);
		Element head = doc.createElement("head");
		Element style = doc.createElement("style");
		style.setAttribute("id", "styleId");
		style.setIdAttribute("id", true);
		style.setAttribute("type", "text/css");
		style.setAttribute("media", "screen");
		style.setTextContent(" ");
		doc.getDocumentElement().appendChild(head);
		head.appendChild(style);
		Element body = doc.createElement("body");
		body.setAttribute("id", "bodyId");
		body.setIdAttribute("id", true);
		doc.getDocumentElement().appendChild(body);
		cssdoc = factory.createCSSDocument(doc);
		cssdoc.setTargetMedium("screen");
		CSSElement cssStyle = cssdoc.getElementById("styleId");
		sheet = ((LinkStyle<?>) cssStyle).getSheet();
		styleText = cssStyle.getChildNodes().item(0);
	}

	@Test
	public void testFontFaceRule() throws IOException {
		styleText.setNodeValue(
				"@font-face{font-family:'OpenSans Regular';src:url('http://www.example.com/fonts/OpenSans-Regular.ttf') format('truetype')}");
		FontFaceRule ffrule = (FontFaceRule) sheet.getCssRules().item(0);
		assertEquals(2, ffrule.getStyle().getLength());
		assertEquals(
				"@font-face {font-family: 'OpenSans Regular'; src: url('http://www.example.com/fonts/OpenSans-Regular.ttf') format('truetype'); }",
				ffrule.getCssText());
		assertEquals("url('http://www.example.com/fonts/OpenSans-Regular.ttf') format('truetype')",
				ffrule.getStyle().getPropertyValue("src"));
		//
		CSSElement body = cssdoc.getElementById("bodyId");
		body.getComputedStyle(null);
		assertTrue(styleDb.isFontFaceName("opensans regular"));
		Font font = styleDb.getFont("opensans regular");
		assertNotNull(font);
		assertEquals("Open Sans", font.getFamily(Locale.ROOT));
		assertEquals("Open Sans Regular", font.getFontName(Locale.ROOT));
	}

}
