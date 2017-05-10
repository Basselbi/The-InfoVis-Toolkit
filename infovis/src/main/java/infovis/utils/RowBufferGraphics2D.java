/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.utils;


import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * <b>RowBufferGraphics2D</b> store rows (ints) for each object drawn.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class RowBufferGraphics2D extends Graphics2D implements Cloneable {
    protected Graphics2D graphics;
    protected BufferedImage image;
    protected Paint paint;
    protected Color background;
    protected Composite composite;
    protected int row;
    protected Color rowColor;
    
    /**
     * Color of OFF pixels.
     */
    public final static Color OFF = new Color(-1, true);
    
    protected Rectangle lens;
    

//    GBufferInfo info = new GBufferInfo();
    
    /**
     * Creates a GBufferGraphics with a specified width and
     * height for its backing buffer.
     * @param width the width 
     * @param height the height
     */
    public RowBufferGraphics2D(int width, int height) {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.graphics = image.createGraphics();
        this.lens = new Rectangle(0, 0, width, height);
    }
    
    /**
     * Creates a GBufferGraphics from a specified lens.
     * @param lens the lens
     */
    public RowBufferGraphics2D(Rectangle lens) {
        this.image = new BufferedImage(lens.width, lens.height, BufferedImage.TYPE_INT_ARGB);
        this.graphics = image.createGraphics();
        setLens(lens);
    }
    
    /**
     * Clears the current lens.
     */
    public void clear() {
        getGraphics().setColor(OFF);
        getGraphics().fill(lens);
    }

    
    /**
     * @return the lens
     */
    public Rectangle getLens() {
        return new Rectangle(lens);
    }
    
    /**
     * @param lens the lens to set
     */
    public void setLens(Rectangle lens) {
        if (lens == null || lens.isEmpty()) {
            lens = new Rectangle(image.getWidth(), image.getHeight());
        }
        else if (this.lens == lens) 
            return;
        else if (image.getWidth() <= lens.width || image.getHeight() <= lens.height) {
            this.lens = lens;
            graphics.setTransform(new AffineTransform(1, 0, 0, 1, lens.x, lens.y));
            graphics.setClip(0, 0, lens.width, lens.height);
        }
        else {
            this.lens = lens;
            graphics.dispose();
            image.flush();
            image = new BufferedImage(lens.width, lens.height, BufferedImage.TYPE_INT_ARGB);
            this.graphics = image.createGraphics(); 
            graphics.setTransform(new AffineTransform(1, 0, 0, 1, lens.x, lens.y));
        }
    }
    
    /**
     * @return the proxy graphics
     */
    public Graphics2D getGraphics() {
        return graphics;
    }
    
    /**
     * @return the item
     */
    public int getItem() {
        return row;
    }
    
    /**
     * @param item the item to set
     */
    public void setItem(int item) {
        if (this.row != item) {
            this.row = item;
            this.rowColor = new Color(item, true);
            getGraphics().setColor(this.rowColor);
        }
    }
    
    /**
     * @return the itemColor
     */
    public Color getItemColor() {
        return rowColor;
    }
    
    /**
     * {@inheritDoc}
     */
    public Graphics create() {
        RowBufferGraphics2D other;
        try {
            other = (RowBufferGraphics2D) clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
        other.graphics = (Graphics2D)graphics.create();
        return other;
    }
    
    

    /**
     * {@inheritDoc}
     */
    public Graphics create(int x, int y, int width, int height) {
        RowBufferGraphics2D other;
        try {
            other = (RowBufferGraphics2D) clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
        other.graphics = (Graphics2D)graphics.create(x, y, width, height);
        return other;
    }

    /**
     * {@inheritDoc}
     */
    public Color getColor() {
        return (Color)paint;
    }
    
    /**
     * {@inheritDoc}
     */
    public Color getBackground() {
        return background;
    }

    /**
     * {@inheritDoc}
     */
    public Composite getComposite() {
        return composite;
    }

    /**
     * {@inheritDoc}
     */
    public Paint getPaint() {
        return paint;
    }

    /**
     * {@inheritDoc}
     */
    public void setBackground(Color color) {
        background = color;
    }

    /**
     * {@inheritDoc}
     */
    public void setComposite(Composite comp) {
        this.composite = comp;
    }

    /**
     * {@inheritDoc}
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    /**
     * {@inheritDoc}
     */
    public void setColor(Color c) {
        this.paint = c;
    }

    /**
     * {@inheritDoc}
     */
    public void setPaintMode() {
    }

    /**
     * {@inheritDoc}
     */
    public void setXORMode(Color c1) {
    }

    /**
     * {@inheritDoc}
     */
    public void addRenderingHints(Map<?, ?> hints) {
        getGraphics().addRenderingHints(hints);
    }

    /**
     * {@inheritDoc}
     */
    public void clearRect(int x, int y, int width, int height) {
        getGraphics().clearRect(x, y, width, height);
    }

    /**
     * {@inheritDoc}
     */
    public void clip(Shape s) {
        getGraphics().clip(s);
    }

    /**
     * {@inheritDoc}
     */
    public void clipRect(int x, int y, int width, int height) {
        getGraphics().clipRect(x, y, width, height);
    }

    /**
     * {@inheritDoc}
     */
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        getGraphics().copyArea(x, y, width, height, dx, dy);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        if (graphics != null) {
            getGraphics().dispose();
            graphics = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void draw(Shape s) {
        getGraphics().draw(s);
    }

    /**
     * {@inheritDoc}
     */
    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
        getGraphics().draw3DRect(x, y, width, height, raised);
    }

    /**
     * {@inheritDoc}
     */
    public void drawArc(
            int x,
            int y,
            int width,
            int height,
            int startAngle,
            int arcAngle) {
        getGraphics().drawArc(x, y, width, height, startAngle, arcAngle);
    }

    /**
     * {@inheritDoc}
     */
    public void drawBytes(byte[] data, int offset, int length, int x, int y) {
        getGraphics().drawBytes(data, offset, length, x, y);
    }

    /**
     * {@inheritDoc}
     */
    public void drawChars(char[] data, int offset, int length, int x, int y) {
        getGraphics().drawChars(data, offset, length, x, y);
    }

    /**
     * {@inheritDoc}
     */
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        getGraphics().drawGlyphVector(g, x, y);
    }

    /**
     * {@inheritDoc}
     */
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        getGraphics().drawImage(img, op, x, y);
    }

    /**
     * @param img
     * @param xform
     * @param obs
     * @return
     * @see java.awt.Graphics2D#drawImage(java.awt.Image, java.awt.geom.AffineTransform, java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        return getGraphics().drawImage(img, xform, obs);
    }

    /**
     * @param img
     * @param x
     * @param y
     * @param bgcolor
     * @param observer
     * @return
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, java.awt.Color, java.awt.image.ImageObserver)
     */
    public boolean drawImage(
            Image img,
            int x,
            int y,
            Color bgcolor,
            ImageObserver observer) {
        return getGraphics().drawImage(img, x, y, bgcolor, observer);
    }

    /**
     * @param img
     * @param x
     * @param y
     * @param observer
     * @return
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return getGraphics().drawImage(img, x, y, observer);
    }

    /**
     * @param img
     * @param x
     * @param y
     * @param width
     * @param height
     * @param bgcolor
     * @param observer
     * @return
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
     */
    public boolean drawImage(
            Image img,
            int x,
            int y,
            int width,
            int height,
            Color bgcolor,
            ImageObserver observer) {
        return getGraphics().drawImage(img, x, y, width, height, bgcolor, observer);
    }

    /**
     * @param img
     * @param x
     * @param y
     * @param width
     * @param height
     * @param observer
     * @return
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, java.awt.image.ImageObserver)
     */
    public boolean drawImage(
            Image img,
            int x,
            int y,
            int width,
            int height,
            ImageObserver observer) {
        return getGraphics().drawImage(img, x, y, width, height, observer);
    }

    /**
     * @param img
     * @param dx1
     * @param dy1
     * @param dx2
     * @param dy2
     * @param sx1
     * @param sy1
     * @param sx2
     * @param sy2
     * @param bgcolor
     * @param observer
     * @return
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
     */
    public boolean drawImage(
            Image img,
            int dx1,
            int dy1,
            int dx2,
            int dy2,
            int sx1,
            int sy1,
            int sx2,
            int sy2,
            Color bgcolor,
            ImageObserver observer) {
        return getGraphics().drawImage(
                img,
                dx1,
                dy1,
                dx2,
                dy2,
                sx1,
                sy1,
                sx2,
                sy2,
                bgcolor,
                observer);
    }

    /**
     * @param img
     * @param dx1
     * @param dy1
     * @param dx2
     * @param dy2
     * @param sx1
     * @param sy1
     * @param sx2
     * @param sy2
     * @param observer
     * @return
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.image.ImageObserver)
     */
    public boolean drawImage(
            Image img,
            int dx1,
            int dy1,
            int dx2,
            int dy2,
            int sx1,
            int sy1,
            int sx2,
            int sy2,
            ImageObserver observer) {
        return getGraphics().drawImage(
                img,
                dx1,
                dy1,
                dx2,
                dy2,
                sx1,
                sy1,
                sx2,
                sy2,
                observer);
    }

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @see java.awt.Graphics#drawLine(int, int, int, int)
     */
    public void drawLine(int x1, int y1, int x2, int y2) {
        getGraphics().drawLine(x1, y1, x2, y2);
    }

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @see java.awt.Graphics#drawOval(int, int, int, int)
     */
    public void drawOval(int x, int y, int width, int height) {
        getGraphics().drawOval(x, y, width, height);
    }

    /**
     * @param points
     * @param points2
     * @param points3
     * @see java.awt.Graphics#drawPolygon(int[], int[], int)
     */
    public void drawPolygon(int[] points, int[] points2, int points3) {
        getGraphics().drawPolygon(points, points2, points3);
    }

    /**
     * @param p
     * @see java.awt.Graphics#drawPolygon(java.awt.Polygon)
     */
    public void drawPolygon(Polygon p) {
        getGraphics().drawPolygon(p);
    }

    /**
     * @param points
     * @param points2
     * @param points3
     * @see java.awt.Graphics#drawPolyline(int[], int[], int)
     */
    public void drawPolyline(int[] points, int[] points2, int points3) {
        getGraphics().drawPolyline(points, points2, points3);
    }

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @see java.awt.Graphics#drawRect(int, int, int, int)
     */
    public void drawRect(int x, int y, int width, int height) {
        getGraphics().drawRect(x, y, width, height);
    }

    /**
     * @param img
     * @param xform
     * @see java.awt.Graphics2D#drawRenderableImage(java.awt.image.renderable.RenderableImage, java.awt.geom.AffineTransform)
     */
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        getGraphics().drawRenderableImage(img, xform);
    }

    /**
     * @param img
     * @param xform
     * @see java.awt.Graphics2D#drawRenderedImage(java.awt.image.RenderedImage, java.awt.geom.AffineTransform)
     */
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        getGraphics().drawRenderedImage(img, xform);
    }

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @param arcWidth
     * @param arcHeight
     * @see java.awt.Graphics#drawRoundRect(int, int, int, int, int, int)
     */
    public void drawRoundRect(
            int x,
            int y,
            int width,
            int height,
            int arcWidth,
            int arcHeight) {
        getGraphics().drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    /**
     * @param iterator
     * @param x
     * @param y
     * @see java.awt.Graphics2D#drawString(java.text.AttributedCharacterIterator, float, float)
     */
    public void drawString(
            AttributedCharacterIterator iterator,
            float x,
            float y) {
        getGraphics().drawString(iterator, x, y);
    }

    /**
     * @param iterator
     * @param x
     * @param y
     * @see java.awt.Graphics2D#drawString(java.text.AttributedCharacterIterator, int, int)
     */
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        getGraphics().drawString(iterator, x, y);
    }

    /**
     * @param s
     * @param x
     * @param y
     * @see java.awt.Graphics2D#drawString(java.lang.String, float, float)
     */
    public void drawString(String s, float x, float y) {
        getGraphics().drawString(s, x, y);
    }

    /**
     * @param str
     * @param x
     * @param y
     * @see java.awt.Graphics2D#drawString(java.lang.String, int, int)
     */
    public void drawString(String str, int x, int y) {
        getGraphics().drawString(str, x, y);
    }

    /**
     * @param s
     * @see java.awt.Graphics2D#fill(java.awt.Shape)
     */
    public void fill(Shape s) {
        getGraphics().fill(s);
    }

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @param raised
     * @see java.awt.Graphics2D#fill3DRect(int, int, int, int, boolean)
     */
    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
        getGraphics().fill3DRect(x, y, width, height, raised);
    }

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @param startAngle
     * @param arcAngle
     * @see java.awt.Graphics#fillArc(int, int, int, int, int, int)
     */
    public void fillArc(
            int x,
            int y,
            int width,
            int height,
            int startAngle,
            int arcAngle) {
        getGraphics().fillArc(x, y, width, height, startAngle, arcAngle);
    }

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @see java.awt.Graphics#fillOval(int, int, int, int)
     */
    public void fillOval(int x, int y, int width, int height) {
        getGraphics().fillOval(x, y, width, height);
    }

    /**
     * @param points
     * @param points2
     * @param points3
     * @see java.awt.Graphics#fillPolygon(int[], int[], int)
     */
    public void fillPolygon(int[] points, int[] points2, int points3) {
        getGraphics().fillPolygon(points, points2, points3);
    }

    /**
     * @param p
     * @see java.awt.Graphics#fillPolygon(java.awt.Polygon)
     */
    public void fillPolygon(Polygon p) {
        getGraphics().fillPolygon(p);
    }

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @see java.awt.Graphics#fillRect(int, int, int, int)
     */
    public void fillRect(int x, int y, int width, int height) {
        getGraphics().fillRect(x, y, width, height);
    }

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @param arcWidth
     * @param arcHeight
     * @see java.awt.Graphics#fillRoundRect(int, int, int, int, int, int)
     */
    public void fillRoundRect(
            int x,
            int y,
            int width,
            int height,
            int arcWidth,
            int arcHeight) {
        getGraphics().fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    /**
     * @return
     * @see java.awt.Graphics#getClip()
     */
    public Shape getClip() {
        return getGraphics().getClip();
    }

    /**
     * @return
     * @see java.awt.Graphics#getClipBounds()
     */
    public Rectangle getClipBounds() {
        return getGraphics().getClipBounds();
    }

    /**
     * @param r
     * @return
     * @see java.awt.Graphics#getClipBounds(java.awt.Rectangle)
     */
    public Rectangle getClipBounds(Rectangle r) {
        return getGraphics().getClipBounds(r);
    }

    /**
     * @return
     * @deprecated
     * @see java.awt.Graphics#getClipRect()
     */
    public Rectangle getClipRect() {
        return getGraphics().getClipRect();
    }

    /**
     * @return
     * @see java.awt.Graphics2D#getDeviceConfiguration()
     */
    public GraphicsConfiguration getDeviceConfiguration() {
        return getGraphics().getDeviceConfiguration();
    }

    /**
     * @return
     * @see java.awt.Graphics#getFont()
     */
    public Font getFont() {
        return getGraphics().getFont();
    }

    /**
     * @return
     * @see java.awt.Graphics#getFontMetrics()
     */
    public FontMetrics getFontMetrics() {
        return getGraphics().getFontMetrics();
    }

    /**
     * @param f
     * @return
     * @see java.awt.Graphics#getFontMetrics(java.awt.Font)
     */
    public FontMetrics getFontMetrics(Font f) {
        return getGraphics().getFontMetrics(f);
    }

    /**
     * @return
     * @see java.awt.Graphics2D#getFontRenderContext()
     */
    public FontRenderContext getFontRenderContext() {
        return getGraphics().getFontRenderContext();
    }

    /**
     * @param hintKey
     * @return
     * @see java.awt.Graphics2D#getRenderingHint(java.awt.RenderingHints.Key)
     */
    public Object getRenderingHint(Key hintKey) {
        return getGraphics().getRenderingHint(hintKey);
    }

    /**
     * @return
     * @see java.awt.Graphics2D#getRenderingHints()
     */
    public RenderingHints getRenderingHints() {
        return getGraphics().getRenderingHints();
    }

    /**
     * @return
     * @see java.awt.Graphics2D#getStroke()
     */
    public Stroke getStroke() {
        return getGraphics().getStroke();
    }

    /**
     * @return
     * @see java.awt.Graphics2D#getTransform()
     */
    public AffineTransform getTransform() {
        return getGraphics().getTransform();
    }

    /**
     * @param rect
     * @param s
     * @param onStroke
     * @return
     * @see java.awt.Graphics2D#hit(java.awt.Rectangle, java.awt.Shape, boolean)
     */
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return getGraphics().hit(rect, s, onStroke);
    }

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     * @see java.awt.Graphics#hitClip(int, int, int, int)
     */
    public boolean hitClip(int x, int y, int width, int height) {
        return getGraphics().hitClip(x, y, width, height);
    }

    /**
     * @param theta
     * @param x
     * @param y
     * @see java.awt.Graphics2D#rotate(double, double, double)
     */
    public void rotate(double theta, double x, double y) {
        getGraphics().rotate(theta, x, y);
    }

    /**
     * @param theta
     * @see java.awt.Graphics2D#rotate(double)
     */
    public void rotate(double theta) {
        getGraphics().rotate(theta);
    }

    /**
     * @param sx
     * @param sy
     * @see java.awt.Graphics2D#scale(double, double)
     */
    public void scale(double sx, double sy) {
        getGraphics().scale(sx, sy);
    }

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @see java.awt.Graphics#setClip(int, int, int, int)
     */
    public void setClip(int x, int y, int width, int height) {
        getGraphics().setClip(x, y, width, height);
    }

    /**
     * @param clip
     * @see java.awt.Graphics#setClip(java.awt.Shape)
     */
    public void setClip(Shape clip) {
        getGraphics().setClip(clip);
    }

    /**
     * @param font
     * @see java.awt.Graphics#setFont(java.awt.Font)
     */
    public void setFont(Font font) {
        getGraphics().setFont(font);
    }

    /**
     * @param hintKey
     * @param hintValue
     * @see java.awt.Graphics2D#setRenderingHint(java.awt.RenderingHints.Key, java.lang.Object)
     */
    public void setRenderingHint(Key hintKey, Object hintValue) {
        getGraphics().setRenderingHint(hintKey, hintValue);
    }

    /**
     * @param hints
     * @see java.awt.Graphics2D#setRenderingHints(java.util.Map)
     */
    public void setRenderingHints(Map<?, ?> hints) {
        getGraphics().setRenderingHints(hints);
    }

    /**
     * @param s
     * @see java.awt.Graphics2D#setStroke(java.awt.Stroke)
     */
    public void setStroke(Stroke s) {
        getGraphics().setStroke(s);
    }

    /**
     * @param Tx
     * @see java.awt.Graphics2D#setTransform(java.awt.geom.AffineTransform)
     */
    public void setTransform(AffineTransform Tx) {
        getGraphics().setTransform(Tx);
    }

    /**
     * @param shx
     * @param shy
     * @see java.awt.Graphics2D#shear(double, double)
     */
    public void shear(double shx, double shy) {
        getGraphics().shear(shx, shy);
    }

    /**
     * @param Tx
     * @see java.awt.Graphics2D#transform(java.awt.geom.AffineTransform)
     */
    public void transform(AffineTransform Tx) {
        getGraphics().transform(Tx);
    }

    /**
     * @param tx
     * @param ty
     * @see java.awt.Graphics2D#translate(double, double)
     */
    public void translate(double tx, double ty) {
        getGraphics().translate(tx, ty);
    }

    /**
     * @param x
     * @param y
     * @see java.awt.Graphics2D#translate(int, int)
     */
    public void translate(int x, int y) {
        getGraphics().translate(x, y);
    }
    

}
