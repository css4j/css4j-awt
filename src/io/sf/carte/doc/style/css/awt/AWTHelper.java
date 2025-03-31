/*

 Copyright (c) 2005-2025, Carlos Amengual.

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

import org.w3c.dom.DOMException;

import io.sf.carte.doc.style.css.CSSComputedProperties;
import io.sf.carte.doc.style.css.CSSPrimitiveValue;
import io.sf.carte.doc.style.css.CSSTypedValue;
import io.sf.carte.doc.style.css.CSSUnit;
import io.sf.carte.doc.style.css.CSSValue.CssType;
import io.sf.carte.doc.style.css.CSSValue.Type;
import io.sf.carte.doc.style.css.RGBAColor;
import io.sf.carte.doc.style.css.property.CSSPropertyValueException;

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
		Map<TextAttribute, Object> textAttrs = new HashMap<>();
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
	 * @throws CSSPropertyValueException if a color cannot be derived from the CSS
	 *                                   value.
	 */
	public static Color getAWTColor(CSSTypedValue cssColor) throws CSSPropertyValueException {
		Color awtcolor = null;
		if (cssColor != null) {
			switch (cssColor.getPrimitiveType()) {
			case COLOR:
			case IDENT:
				RGBAColor color;
				try {
					color = cssColor.toRGBColor();
				} catch (RuntimeException e) {
					CSSPropertyValueException ex = new CSSPropertyValueException(
							"Cannot obtain a RGB color.", e);
					ex.setValueText(cssColor.getCssText());
					throw ex;
				}

				double[] rgb;
				try {
					rgb = color.toNumberArray();
				} catch (RuntimeException e) {
					CSSPropertyValueException ex = new CSSPropertyValueException(
							"Cannot obtain the color components.", e);
					ex.setValueText(cssColor.getCssText());
					throw ex;
				}

				CSSPrimitiveValue prialpha = color.getAlpha();

				if (prialpha.getCssValueType() != CssType.TYPED) {
					CSSPropertyValueException ex = new CSSPropertyValueException(
							"Unsupported alpha channel.");
					ex.setValueText(cssColor.getCssText());
					throw ex;
				}

				float alpha = normalizedAlphaComponent((CSSTypedValue) prialpha);

				try {
					awtcolor = new Color((float) rgb[0], (float) rgb[1], (float) rgb[2], alpha);
				} catch (IllegalArgumentException e) {
					CSSPropertyValueException ex = new CSSPropertyValueException("Unknown color.",
							e);
					ex.setValueText(cssColor.getCssText());
					throw ex;
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

	/**
	 * Normalize the alpha component to a [0,1] interval.
	 * 
	 * @param typed the component.
	 * @return the normalized component.
	 */
	private static float normalizedAlphaComponent(CSSTypedValue typed) {
		float comp;
		short unit = typed.getUnitType();
		if (unit == CSSUnit.CSS_PERCENTAGE) {
			comp = typed.getFloatValue(CSSUnit.CSS_PERCENTAGE) * 0.01f;
		} else if (unit == CSSUnit.CSS_NUMBER) {
			comp = typed.getFloatValue(CSSUnit.CSS_NUMBER);
		} else if (typed.getPrimitiveType() == Type.IDENT) {
			comp = 0f;
		} else {
			throw new DOMException(DOMException.TYPE_MISMATCH_ERR,
					"Wrong component: " + typed.getCssText());
		}
		return Math.max(Math.min(1f, comp), 0f);
	}

}
