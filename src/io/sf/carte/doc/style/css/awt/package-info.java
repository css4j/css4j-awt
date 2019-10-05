/**
 * Helper classes for AWT rendering.
 * <h3><code>AWTHelper</code>: create AWT <code>Font</code> and
 * <code>Color</code> objects</h3>
 * <p>
 * Once you have a computed style, on systems where AWT is available you can
 * create an AWT font with the <code>createFont</code> static method in the
 * <code>AWTHelper</code> class:
 * </p>
 * 
 * <pre style="padding: 0.8em;background-color: #f8f5e1">
CSSComputedProperties style = ...
java.awt.Font font = AWTHelper.createFont(style);</pre>
 * <p>
 * And <code>AWTHelper.getAWTColor</code> method can create AWT colors from any
 * value, regardless of it coming from computed or declared styles.
 * <pre style="padding: 0.8em;background-color: #f8f5e1">
CSSPrimitiveValue cssColor = (CSSPrimitiveValue) style.getPropertyCSSValue("color");
java.awt.Color color = AWTHelper.getAWTColor(cssColor);</pre>
 * </p>
 * <h3><code>AWTStyleDatabase</code></h3>
 * <p><code>AWTStyleDatabase</code> is a sample style database for AWT.</p>
 */
package io.sf.carte.doc.style.css.awt;
