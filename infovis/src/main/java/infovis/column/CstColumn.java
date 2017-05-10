/*****************************************************************************
 * Copyright (C) 2008 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package infovis.column;

/**
 * Class CstColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class CstColumn extends ConstantColumn {
        protected double cst;
        
        public CstColumn(String name, double cst, int size) {
            super(name, size);
            this.cst = cst;
        }
        /**
         * {@inheritDoc}
         */
        public double getDoubleAt(int row) {
            return cst;
        }
}
