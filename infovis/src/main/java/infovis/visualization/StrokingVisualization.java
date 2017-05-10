/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import java.awt.geom.Rectangle2D;
import java.util.Set;

import infovis.Table;
import infovis.table.Item;
import infovis.visualization.magicLens.StrokingExcentricItem;

/**
 * Visualization for shapes considered as strokes and not filled shapes.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.14 $
 */
public class StrokingVisualization extends DefaultVisualization {
    /** Property name for showing excentric labels. */
    public static final String PROPERTY_SHOW_EXCENTRIC = "showExcentric";
    
    private boolean showExcentric = true;
    
    /**
     * Constructor.
     * @param table the table
     * @param ir the item renderer
     */
	public StrokingVisualization(
        Table table,
        ItemRenderer ir) {
        super(table, ir);
    }

	/**
	 * Constructor with the item renderer from the ItemRendererFactory.
	 * @param table the table
	 */
    public StrokingVisualization(Table table) {
        super(table);
    }

    /**
     * {@inheritDoc}
     */
    public LabeledItem createLabelItem(Item row) {
        return new StrokingExcentricItem(this, row);
    }

    /**
     * @return true if showing excentric labels
     */
    public boolean isShowExcentric() {
        return showExcentric;
    }

    /**
     * Sets wether excentric labels are shown.
     * @param showExcentric value
     */
    public void setShowExcentric(boolean showExcentric) {
        if (this.showExcentric == showExcentric) return;
        this.showExcentric = showExcentric;
        repaint();
        firePropertyChange(PROPERTY_SHOW_EXCENTRIC, !showExcentric, showExcentric);
    }

    /**
     * {@inheritDoc}
     */
    public Set pickAll(Rectangle2D hitBox, Rectangle2D bounds, Set pick) {
        if (showExcentric)
            pick = super.pickAll(hitBox, bounds, pick);
        return pick;
    }
}
