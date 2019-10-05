/*

 Copyright (c) 2005-2019, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

package io.sf.carte.doc.style.css.awt;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.css.CSSPrimitiveValue;

import io.sf.carte.doc.style.css.CSSComputedProperties;
import io.sf.carte.doc.style.css.CSSPrimitiveValue2;
import io.sf.carte.doc.style.css.RGBAColor;
import io.sf.carte.doc.style.css.property.CSSPropertyValueException;
import io.sf.carte.doc.style.css.property.ColorIdentifiers;

/**
 * AWT helper methods.
 * 
 * @author Carlos Amengual
 *
 */
public class AWTHelper {

	/**
	 * Create an AWT Font object from a computed style.
	 * 
	 * @param computedStyle
	 *            the computed style.
	 * @return the font.
	 */
	public static Font createFont(CSSComputedProperties computedStyle) {
		String fontfamily = computedStyle.getUsedFontFamily();
		float sz = computedStyle.getComputedFontSize();
		// Font style
		String stylename = computedStyle.getPropertyValue("font-style");
		int style = Font.PLAIN;
		if (stylename.length() > 0) {
			stylename = stylename.toLowerCase();
			if (stylename.equals("italic")) {
				style = Font.ITALIC;
			}
		}
		String fontweight = computedStyle.getFontWeight();
		if (fontweight != null) {
			fontweight = fontweight.toLowerCase();
			if (fontweight.equals("bold")) {
				if (style != Font.ITALIC) {
					style = Font.BOLD;
				} else {
					style = Font.ITALIC & Font.BOLD;
				}
			}
		}
		Map<TextAttribute, Object> textAttrs = new HashMap<TextAttribute, Object>();
		String decoration = computedStyle.getPropertyValue("text-decoration");
		if (decoration.length() > 0) {
			decoration = decoration.toLowerCase();
			if (decoration.equals("underline")) {
				textAttrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			} else if (decoration.equals("line-through")) {
				textAttrs.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
			}
		}
		//
		Font font = new Font(fontfamily, style, Math.round(sz));
		return font.deriveFont(textAttrs);
	}

	/**
	 * Gets the AWT color as obtained from the given CSS primitive value.
	 * 
	 * @param cssColor
	 *            the primitive color value, which can contain an RGB color, a number or an
	 *            identifier.
	 * @return the AWT color object, or null if the color was specified as an unknown
	 *         identifier.
	 * @throws CSSPropertyValueException
	 *             if the color declaration is malformed or a color identifier is unknown.
	 */
	public static Color getAWTColor(CSSPrimitiveValue2 cssColor) throws CSSPropertyValueException {
		Color awtcolor = null;
		if (cssColor != null) {
			switch (cssColor.getPrimitiveType()) {
			case CSSPrimitiveValue.CSS_RGBCOLOR:
				RGBAColor color = cssColor.getRGBColorValue();
				CSSPrimitiveValue red = color.getRed();
				CSSPrimitiveValue green = color.getGreen();
				CSSPrimitiveValue blue = color.getBlue();
				float alpha = color.getAlpha().getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
				switch (red.getPrimitiveType()) {
				case CSSPrimitiveValue.CSS_PERCENTAGE:
					awtcolor = new Color(clipColorValue(red.getFloatValue(CSSPrimitiveValue.CSS_PERCENTAGE) / 100f),
							clipColorValue(green.getFloatValue(CSSPrimitiveValue.CSS_PERCENTAGE) / 100f),
							clipColorValue(blue.getFloatValue(CSSPrimitiveValue.CSS_PERCENTAGE) / 100f), alpha);
					break;
				case CSSPrimitiveValue.CSS_NUMBER:
					try {
						awtcolor = new Color(clipColorValue((int) red.getFloatValue(CSSPrimitiveValue.CSS_NUMBER)),
								clipColorValue((int) green.getFloatValue(CSSPrimitiveValue.CSS_NUMBER)),
								clipColorValue((int) blue.getFloatValue(CSSPrimitiveValue.CSS_NUMBER)),
								Math.round(alpha * 255f));
					} catch (IllegalArgumentException e) {
						CSSPropertyValueException ex = new CSSPropertyValueException("Unknown color", e);
						ex.setValueText(cssColor.getCssText());
						throw ex;
					}
				}
				break;
			case CSSPrimitiveValue.CSS_IDENT:
				String sv = cssColor.getStringValue();
				String s = ColorIdentifiers.getInstance().getColor(sv);
				if (s != null) {
					try {
						awtcolor = Color.decode(s);
					} catch (NumberFormatException e) {
						CSSPropertyValueException ex = new CSSPropertyValueException("Unknown color", e);
						ex.setValueText(sv);
						throw ex;
					}
				} else if ("transparent".equals(sv)) {
					return new Color(0, 0, 0, 0);
				} else {
					return Color.getColor(sv);
				}
				break;
			case CSSPrimitiveValue.CSS_STRING:
				String encoded = cssColor.getStringValue();
				if (encoded.charAt(0) == '#') {
					try {
						awtcolor = Color.decode(encoded);
					} catch (NumberFormatException e) {
						CSSPropertyValueException ex = new CSSPropertyValueException("Unknown color", e);
						ex.setValueText(encoded);
						throw ex;
					}
				} else {
					CSSPropertyValueException ex = new CSSPropertyValueException("Unknown color");
					ex.setValueText(encoded);
					throw ex;
				}
				break;
			case CSSPrimitiveValue.CSS_NUMBER:
				awtcolor = new Color((int) cssColor.getFloatValue(CSSPrimitiveValue.CSS_NUMBER));
				break;
			}
		}
		return awtcolor;
	}

	static int clipColorValue(int color) {
		return Math.max(Math.min(255, color), 0);
	}

	static float clipColorValue(float color) {
		return Math.max(Math.min(1f, color), 0f);
	}

}
