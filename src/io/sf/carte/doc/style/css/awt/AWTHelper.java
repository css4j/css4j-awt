/*

 Copyright (c) 2005-2023, Carlos Amengual.

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

import io.sf.carte.doc.style.css.CSSComputedProperties;
import io.sf.carte.doc.style.css.CSSPrimitiveValue;
import io.sf.carte.doc.style.css.CSSTypedValue;
import io.sf.carte.doc.style.css.CSSUnit;
import io.sf.carte.doc.style.css.CSSValue.CssType;
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
	 * @param cssColor the primitive color value, which can contain an RGB color, a
	 *                 number or an identifier.
	 * @return the AWT color object, or null if the color was specified as an
	 *         unknown identifier.
	 * @throws CSSPropertyValueException if the color declaration is malformed or a
	 *                                   color identifier is unknown.
	 */
	public static Color getAWTColor(CSSTypedValue cssColor) throws CSSPropertyValueException {
		Color awtcolor = null;
		if (cssColor != null) {
			switch (cssColor.getPrimitiveType()) {
			case COLOR:
				RGBAColor color = cssColor.toRGBColor();
				CSSPrimitiveValue red = color.getRed();
				CSSPrimitiveValue green = color.getGreen();
				CSSPrimitiveValue blue = color.getBlue();
				CSSPrimitiveValue prialpha = color.getAlpha();
				//
				if (red.getCssValueType() != CssType.TYPED || green.getCssValueType() != CssType.TYPED
						|| blue.getCssValueType() != CssType.TYPED || prialpha.getCssValueType() != CssType.TYPED) {
					CSSPropertyValueException ex = new CSSPropertyValueException("Unknown color.");
					ex.setValueText(cssColor.getCssText());
					throw ex;
				}
				float alpha = ((CSSTypedValue) prialpha).getFloatValue(CSSUnit.CSS_NUMBER);
				switch (red.getUnitType()) {
				case CSSUnit.CSS_PERCENTAGE:
					awtcolor = new Color(clipColorValue(((CSSTypedValue) red).getFloatValue(CSSUnit.CSS_PERCENTAGE) / 100f),
							clipColorValue(((CSSTypedValue) green).getFloatValue(CSSUnit.CSS_PERCENTAGE) / 100f),
							clipColorValue(((CSSTypedValue) blue).getFloatValue(CSSUnit.CSS_PERCENTAGE) / 100f), alpha);
					break;
				case CSSUnit.CSS_NUMBER:
					try {
						awtcolor = new Color(clipColorValue((int) ((CSSTypedValue) red).getFloatValue(CSSUnit.CSS_NUMBER)),
								clipColorValue((int) ((CSSTypedValue) green).getFloatValue(CSSUnit.CSS_NUMBER)),
								clipColorValue((int) ((CSSTypedValue) blue).getFloatValue(CSSUnit.CSS_NUMBER)),
								Math.round(alpha * 255f));
					} catch (IllegalArgumentException e) {
						CSSPropertyValueException ex = new CSSPropertyValueException("Unknown color.", e);
						ex.setValueText(cssColor.getCssText());
						throw ex;
					}
				}
				break;
			case IDENT:
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
			case NUMERIC:
				if (cssColor.getUnitType() == CSSUnit.CSS_NUMBER) {
					return new Color((int) cssColor.getFloatValue(CSSUnit.CSS_NUMBER));
				}
			default:
				CSSPropertyValueException ex = new CSSPropertyValueException("Unknown color");
				ex.setValueText(cssColor.getCssText());
				throw ex;
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
