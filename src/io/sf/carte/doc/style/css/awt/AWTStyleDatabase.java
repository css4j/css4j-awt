/*

 Copyright (c) 2005-2020, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://carte.sourceforge.io/LICENSE.txt

 */

package io.sf.carte.doc.style.css.awt;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

import io.sf.carte.doc.style.css.ExtendedCSSFontFaceRule;
import io.sf.carte.doc.style.css.om.AbstractStyleDatabase;

/**
 * CSS style database for use with AWT objects.
 * <p>
 * 
 * @author Carlos Amengual
 * 
 */
public class AWTStyleDatabase extends AbstractStyleDatabase {

	private GraphicsConfiguration gConfiguration = null;

	/*
	 * A4 defaults
	 */
	private float defaultWidth = 595f;
	private float defaultHeight = 842f;

	private final Map<String,Font> fontfaceNames = new HashMap<String,Font>();

	/**
	 * Constructs a default style database with no graphics configuration.
	 */
	public AWTStyleDatabase() {
		this(null);
	}

	/**
	 * Constructs a style database for the given graphics configuration.
	 * 
	 * @param gConf
	 *            the graphics configuration.
	 */
	public AWTStyleDatabase(GraphicsConfiguration gConf) {
		super();
		gConfiguration = gConf;
	}

	/**
	 * Gets the name of the default font used when a generic font family (serif,
	 * sans-serif, monospace, cursive, fantasy) is specified.
	 * <p>
	 * This class attempts to map the generic name to a "logical font" name.
	 * </p>
	 * <p>
	 * As, in Java, logical font names are internally mapped to physical fonts
	 * by the Java runtime environment, the name of the corresponding "logical
	 * font" is returned, and no further mapping is attempted.
	 * </p>
	 * 
	 * @param genericFamily
	 *            the name of the logical font.
	 * @return the name of the associated logical font, or null if none.
	 */
	@Override
	public String getDefaultGenericFontFamily(String genericFamily) {
		String fontName = null;
		if (genericFamily.equals("serif")) {
			fontName = "Serif";
		} else if (genericFamily.equals("sans serif")) {
			fontName = "SansSerif";
		} else if (genericFamily.equals("monospace")) {
			fontName = "Monospaced";
		}
		return fontName;
	}

	@Override
	protected boolean isFontFamilyAvailable(String fontFamily) {
		String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (int i = 0; i < fontNames.length; i++) {
			if (fontNames[i].equalsIgnoreCase(fontFamily)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isFontFaceName(String requestedFamily) {
		return fontfaceNames.containsKey(requestedFamily);
	}

	@Override
	protected boolean loadFontFace(String familyName, FontFormat format, InputStream is, ExtendedCSSFontFaceRule rule)
			throws IOException {
		int fontFormat;
		if (format == null || (fontFormat = fontFormatFromEnum(format)) == -1) {
			return false;
		}
		Font font;
		try {
			font = Font.createFont(fontFormat, is);
		} catch (FontFormatException e) {
			rule.getParentStyleSheet().getErrorHandler().fontFormatError(rule, e);
			return false;
		}
		fontfaceNames.put(familyName, font);
		return true;
	}

	private int fontFormatFromEnum(FontFormat format) {
		int fontFormat;
		switch (format) {
		case TRUETYPE:
		case OPENTYPE:
			fontFormat = Font.TRUETYPE_FONT;
			break;
		default:
			fontFormat = -1;
		}
		return fontFormat;
	}

	/**
	 * Get a font that was loaded by a {@literal @}font-face rule.
	 * 
	 * @param lcFamilyName the family name in lowercase.
	 * @return the font, or <code>null</code> if no font with that family name (in
	 *         lowercase) has been loaded from a {@literal @}font-face rule.
	 */
	public Font getFont(String lcFamilyName) {
		return fontfaceNames.get(lcFamilyName);
	}

	/**
	 * Gets the GraphicsConfiguration for this style database.
	 * 
	 * @return the GraphicsConfiguration object previously set, the default one
	 *         if none was set, or null if the graphics environment is headless.
	 */
	protected GraphicsConfiguration getGraphicsConfiguration() {
		return gConfiguration;
	}

	public void setGraphicsConfiguration(GraphicsConfiguration configuration) {
		gConfiguration = configuration;
	}

	/**
	 * This method is used to normalize sizes to a 595pt width.
	 * 
	 * @return the factor by which screensize-dependent quantities can be
	 *         multiplied to be normalized.
	 */
	protected float deviceResolutionFactor() {
		return getDeviceWidth() / 595f;
	}

	@Override
	public float getFontSizeFromIdentifier(String familyName, String fontSizeIdentifier) throws DOMException {
		// Normalize to device resolution
		float factor = Math.max(0.9f, deviceResolutionFactor());
		float sz;
		if (fontSizeIdentifier.equals("xx-small")) {
			sz = 8f * factor;
		} else if (fontSizeIdentifier.equals("x-small")) {
			sz = 9f * factor;
		} else if (fontSizeIdentifier.equals("small")) {
			sz = 10f * factor;
		} else if (fontSizeIdentifier.equals("medium")) {
			sz = 12f * factor;
		} else if (fontSizeIdentifier.equals("large")) {
			sz = 14f * factor;
		} else if (fontSizeIdentifier.equals("x-large")) {
			sz = 18f * factor;
		} else if (fontSizeIdentifier.equals("xx-large")) {
			sz = 24f * factor;
		} else {
			throw new DOMException(DOMException.INVALID_ACCESS_ERR, "Unknown size identifier: " + fontSizeIdentifier);
		}
		return sz;
	}

	@Override
	public float getWidthSize(String widthIdentifier, float fontSize) throws DOMException {
		// Normalize to device resolution
		float factor = Math.max(0.62f, deviceResolutionFactor());
		if ("thin".equalsIgnoreCase(widthIdentifier)) {
			return 1f * factor;
		} else if ("thick".equalsIgnoreCase(widthIdentifier)) {
			return 4f * factor;
		} else if ("medium".equalsIgnoreCase(widthIdentifier)) {
			return 2f * factor;
		} else {
			throw new DOMException(DOMException.SYNTAX_ERR, "Unknown identifier " + widthIdentifier);
		}
	}

	@Override
	public short getNaturalUnit() {
		return CSSPrimitiveValue.CSS_PT;
	}

	@Override
	public float getDeviceHeight() {
		float height;
		if (gConfiguration != null) {
			height = (float) getGraphicsConfiguration().getBounds().getHeight();
		} else {
			height = defaultHeight;
		}
		return height;
	}

	@Override
	public float getDeviceWidth() {
		float width;
		if (gConfiguration != null) {
			width = (float) getGraphicsConfiguration().getBounds().getWidth();
		} else {
			width = defaultWidth;
		}
		return width;
	}

	public void setDefaultWidth(float defaultWidth) {
		this.defaultWidth = defaultWidth;
	}

	public void setDefaultHeight(float defaultHeight) {
		this.defaultHeight = defaultHeight;
	}

	@Override
	public int getColorDepth() {
		int bpc = 255;
		if (gConfiguration != null) {
			int[] comp = gConfiguration.getColorModel().getComponentSize();
			for (int i = 0; i < 3; i++) {
				if (bpc > comp[i]) {
					bpc = comp[i];
				}
			}
		}
		return bpc;
	}

}
