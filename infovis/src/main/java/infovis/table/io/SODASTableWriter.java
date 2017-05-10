/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.table.io;

import infovis.Column;
import infovis.Table;
import infovis.column.BooleanColumn;
import infovis.column.CategoricalColumn;
import infovis.column.ColumnFilter;
import infovis.column.DoubleColumn;
import infovis.column.IntColumn;
import infovis.column.NumberColumn;
import infovis.column.StringColumn;
import infovis.column.filter.ComposeOrFilter;
import infovis.column.format.CategoricalFormat;
import infovis.data.CategoricalDistribution;
import infovis.data.CategoricalDistributionColumn;
import infovis.data.DoubleInterval;
import infovis.data.IntervalColumn;
import infovis.io.AbstractWriter;
import infovis.metadata.VisualRole;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.text.MutableAttributeSet;

import org.apache.log4j.Logger;

/**
 * Write a SODAS (.sds) file from a table, provided the table contains the
 * expected meta-information. The SODASParser can read .sds files.
 * 
 * @version $Revision: 1.16 $
 * @author Elie Naulleau, Jean-Daniel Fekete, Nghi
 * @see SODASParser
 * 
 * at infovis.factory TableWriterFactory sds
 */

public class SODASTableWriter extends AbstractWriter 
    implements SODASConstants, ColumnFilter {
    protected Column nameColumn;
    protected Column libelColumn;
    
    private final Logger LOG = Logger.getLogger(SODASTableWriter.class);
    
    private StringBuffer catAppended = new StringBuffer("A");
    private int catAppendedIndex = 0;
	/**
	 * Creates a table writer.
	 * 
	 * @param out
	 *            the output stream
	 * @param name
	 *            the name
	 * @param table
	 *            the table
	 */
	public SODASTableWriter(OutputStream out, String name, Table table) {
		super(out, name, table);
		setColumnFilter(new ComposeOrFilter(this, getColumnFilter()));
		nameColumn = VisualRole.getColumn(table, VisualRole.VISUAL_ROLE_SHORT_LABEL);
		libelColumn = VisualRole.getLabelColumn(table);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean filter(Column column) {
	    return column == libelColumn || column == nameColumn;
	}
	
	/**
     * @return the nameColumn
     */
    public Column getNameColumn() {
        return nameColumn;
    }
    
    /**
     * @param nameColumn the nameColumn to set
     */
    public void setNameColumn(Column nameColumn) {
        this.nameColumn = nameColumn;
    }
    
    /**
     * @return the libelColumn
     */
    public Column getLibelColumn() {
        return libelColumn;
    }
    
    /**
     * @param libelColumn the libelColumn to set
     */
    public void setLibelColumn(Column libelColumn) {
        this.libelColumn = libelColumn;
    }
    
	/**
	 * {@inheritDoc}
	 */
	public boolean write() {
        ArrayList labels = getColumnLabels();
        if (labels == null) {
            addAllColumnLabels();
            labels = getColumnLabels();
        }

		// actual data columns, without name and label columns
		Column[] columns = new Column[labels.size()];
		for (int i = 0; i < columns.length; i++) {
		    String name = (String)labels.get(i);
		    columns[i] = table.getColumn(name);
		    if (columns[i] == null) {
		        LOG.error("Unknown table column named "+name);
		    }
		}

		try {
			write("SODAS = (\n\n");

			Object value = null;

			// CONTAINS
			write("CONTAINS = ( FILES, HEADER, INDIVIDUALS, VARIABLES, RECTANGLE_MATRIX");

			boolean a = true;

//			value = table.getMetadata().getAttribute(ATTR_FILES);
//			if (value != null) {
//				write("FILES");
//				a = true;
//			}
//			value = table.getMetadata().getAttribute(ATTR_HEADER);
//			if (value != null) {
//				if (a)
//					write(", ");
//				write("HEADER");
//				a = true;
//			}
//
//			value = table.getMetadata().getAttribute(ATTR_INDIVIDUALS);
//			if (value != null) {
//				if (a)
//					write(", ");
//				write("INDIVIDUALS");
//				a = true;
//			}
//
//			value = table.getMetadata().getAttribute(ATTR_HIERARCHY);
//			if (value != null) {
//				if (a)
//					write(", ");
//				write("HIERARCHIE");
//				a = true;
//			}
//
//			value = table.getMetadata().getAttribute(ATTR_RULES);
//			if (value != null) {
//				if (a)
//					write(", ");
//				write("RULES");
//				a = true;
//			}

			value = table.getMetadata().getAttribute(ATTR_MEMO);
			if (value != null) {
				if (a)
					write(", ");
				write("MEMO");
				a = true;
			}


//			value = table.getMetadata().getAttribute(ATTR_DIST_MATRIX);
//			if (value != null) {
//				if (a)
//					write(", ");
//				write("DIST_MATRIX");
//				a = true;
//			}
//
//			value = table.getMetadata().getAttribute(ATTTR_TRIANGLE_MATRIX);
//			if (value != null) {
//				if (a)
//					write(", ");
//				write("TRIANGLE_MATRIX");
//			}


			write("),\n\n");

			// FILE
			a = false;
			write("FILE = (\n");
			value = table.getMetadata().getAttribute(ATTR_PROCEDURE_NAME);
			if (value != null) {
				write("procedure_name=\t" + Q(value.toString()));
				a = true;
			}

			value = table.getMetadata().getAttribute(ATTR_VERSION);
			if (value != null) {
				if (a)
					write(",\n");
				write("version=\t\t" + Q(value.toString()));
				a = true;
			}

			value = table.getMetadata().getAttribute(ATTR_CREATE_DATE);
			if (value == null) {
			    value = DateFormat.getInstance().format(Calendar.getInstance().getTime());
			}
			if (a)
					write(",\n");
			write("create_date=\t" + Q(value.toString()));
			a = true;

			value = table.getMetadata().getAttribute(ATTR_FILIERE_NAME);
			if (value != null) {
				if (a)
					write(",\n");
				write("filiere_name=\t" + Q(value.toString()));
				a = true;
			}

			value = table.getMetadata().getAttribute(ATTR_FILIERE_PATH);
			if (value != null) {
				if (a)
					write(",\n");
				write("filiere_path=\t" + Q(value.toString()));
				a = true;
			}

			value = table.getMetadata().getAttribute(ATTR_BASE);
			if (value != null) {
				if (a)
					write(",\n");
				write("base=\t\t\t" + Q(value.toString()) + "\n");
			}

			write("),\n\n");
			a = false;

			// HEADER
			write("HEADER = (\n");
			// We assume for now that these values have corrected populated.
			// Some of them depend on column content and distributions. We say
			// for
			// now that is
			// the responsibility of the columns to update those values.
			value = table.getMetadata().getAttribute(ATTR_TITLE);
			if (value != null) {
				write("title=\t\t" + Q(value.toString()));
				a = true;
			}

			value = table.getMetadata().getAttribute(ATTR_SUBTITLE);
			if (value != null) {
				if (a)
					write(",\n");
				write("sub_title=\t" + Q(value.toString()));
				a = true;
			}

			if (a)
				write(",\n");
			write("indiv_nb=\t" + table.getRowCount());
			a = true;

			value = table.getMetadata().getAttribute("var_nb");
			if (value == null) {
			    int var_nb = columns.length;
			    value = new Integer(var_nb);
			}			    
			if (a)
			    write(",\n");
			write("var_nb=\t\t" + value.toString());

//			value = table.getMetadata().getAttribute("rules_nb");
//			if (value != null) {
//				if (a)
//					write(",\n");
//				write("rules_nb=\t" + value.toString());
//			}

//			value = table.getMetadata().getAttribute("nb_var_set");
//			if (value != null) {
//				if (a)
//					write(",\n");
//				write("nb_var_set=\t" + value.toString());
//			}
//
//			value = table.getMetadata().getAttribute("nb_indiv_set");
//			if (value != null) {
//				if (a)
//					write(",\n");
//				write("nb_indiv_set=\t" + value.toString());
//			}
//
//			value = table.getMetadata().getAttribute("nb_var_nom");
//			if (value != null) {
//				if (a)
//					write(",\n");
//				write("nb_var_nom=\t\t" + value.toString());
//			}
//
//			value = table.getMetadata().getAttribute("nb_var_cont");
//			if (value != null) {
//				if (a)
//					write(",\n");
//				write("nb_var_cont=\t" + value.toString());
//			}
//
//			value = table.getMetadata().getAttribute("nb_var_text");
//			if (value != null) {
//				if (a)
//					write(",\n");
//				write("nb_var_text=\t" + value.toString());
//			}
//
//			value = table.getMetadata().getAttribute("nb_var_cont_symb");
//			if (value != null) {
//				if (a)
//					write(",\n");
//				write("nb_var_cont_symb=\t" + value.toString());
//			}
//
//			value = table.getMetadata().getAttribute("nb_var_nom_symb");
//			if (value != null) {
//				if (a)
//					write(",\n");
//				write("nb_var_nom_symb=\t" + value.toString());
//			}
//
//			value = table.getMetadata().getAttribute("nb_var_nom_mod");
//			if (value != null) {
//				if (a)
//					write(",\n");
//				write("nb_var_nom_mod=\t" + value.toString());
//				a = true;
//			}

			value = table.getMetadata().getAttribute("nb_na");
			if (value != null) {
				if (a)
					write(",\n");
				write("nb_na=\t\t" + value.toString());
				a = true;
			}

			value = table.getMetadata().getAttribute("nb_nu");
			if (value == null) {
    		    int nb_nu = 0;
    		    nb_nu = countUndefined(columns);
                if (nb_nu != 0) {
                    value = new Integer(nb_nu);
                }
            }
			if (value != null) {
				if (a)
					write(",\n");
				write("nb_nu=\t\t" + value.toString());
				a = true;
			}

//			value = table.getMetadata().getAttribute("nb_hierarchies");
//			if (value != null) {
//				if (a)
//					write(",\n");
//				write("nb_hierarchies=\t" + value.toString() + "\n");
//				a = true;
//			}

			write("),\n\n");

			// INDIVIDUALS
//			if (table.getMetadata().getAttribute("INDIVIDUALS") != null) {
			write("INDIVIDUALS = (\n");
			
			int n = table.getRowCount();
			for (int r = 0; r < n; r++) {
			    if (r != 0) {
			        write(",\n");
			    }			        
			    write("(");
                write(String.valueOf(r));
                write(", \"");
                if (nameColumn == null || nameColumn.isValueUndefined(r)) {
                    write("ROW" + String.valueOf(r));
                }
                else {
                    write(nameColumn.getValueAt(r));
                }
                write("\", \"");
                if (libelColumn == null || libelColumn.isValueUndefined(r)) {
                    write(String.valueOf(r));
                }
                else {
                    write(libelColumn.getValueAt(r));
                }
                write("\" )");
			}
			write("),\n\n");


			// VARIABLES

			// nominal => nominal_desc : CategoricalColumn
			// continue => DoubleColumn
			// mult_nominal => CategoricalDistributionColumn
			// mult_nominal_Modif => CategoricalDistributionColumn
			// inter_cont => IntervalColumn

			write("VARIABLES = (\n");
			for (int vidx = 1; vidx <= columns.length; vidx++) {
				Column column = columns[vidx-1];
                String name = column.getName();
                MutableAttributeSet md = column.getMetadata();
				
				String modifier_poss = (String) md.getAttribute(ATTR_MOD_NAME_POSS);
				String sodasVariableType = (String) md.getAttribute(ATTR_SODAS_TYPE);
                String label = (String) md.getAttribute(ATTR_LABEL);
                if (label == null) {
                    label = name;
                }
                write("(" + String.valueOf(vidx) + ", "); 

                if (column instanceof StringColumn) {
                    CategoricalColumn cat = CategoricalColumn.create(column);
                    columns[vidx-1] = cat;
                    column = cat;
                }
                if (column instanceof CategoricalDistributionColumn) {
					CategoricalDistributionColumn cdc = (CategoricalDistributionColumn) column;
					CategoricalFormat cf = (CategoricalFormat) cdc.getCategoricalFormat();

					if (sodasVariableType == null)
						sodasVariableType = "mult_nominal_Modif"; // pure infovis table

					write(sodasVariableType + ", \"\", \"");
					write(name);
					write("\", \"");
					write(label);
					write("\", ");
					if (modifier_poss != null) {
						write(modifier_poss);
						write(", ");
					}
					write("0"); // nb_na
					write(", ");
					//write("0"); // nb_nu
					write(String.valueOf(countUndefined(column)));
					write(", ");
                    int cnt = cf.getCategories().size();
					write(String.valueOf(cnt));
					write(",\n");

					writeCategories(cf);

				}
				else if (column instanceof BooleanColumn) {
					sodasVariableType = "nominal"; // pure

					// (17 ,mult_nominal_Modif ,"" ,"AR00" ,"Image" ,0, 0 ,5, (
					// (1 ,"AR01" ,"Plutot OK" ,1),
					// )
					// )

					write(sodasVariableType + ", \"\", \"");
					write(name);
					write("\", \"");
					write(label);
					write("\", ");
					if (modifier_poss != null) {
						write(modifier_poss);
						write(", ");
					}
					write("0"); // nb_na
					write(", ");
					//write("0"); // nb_nu
                    write(String.valueOf(countUndefined(column)));
					write(", ");
					write("2"); // 2 categories : false, true
					write(",\n");
					write("(");
					write("(1 ,\"BOOL0\" ,\"false\" ,0),\n");
					write("(2 ,\"BOOL1\" ,\"true\" ,0))\n");
					write(")");
				} 
				else if (column instanceof CategoricalColumn) {
					//System.out.println("CategoricalColumn: PROCESSING : "+ name + "   cn=" + column.getName());
					CategoricalColumn ccol = (CategoricalColumn) column;
					CategoricalFormat cf = (CategoricalFormat)ccol.getFormat();
					
					if (sodasVariableType == null)
						sodasVariableType = "nominal";

					write(sodasVariableType + ", \"\", \"");
					write(name);
					write("\", \"");
					write(label);
					write("\", ");
					if (modifier_poss != null) {
						write(modifier_poss);
						write(", ");
					}
					write("0"); // nb_na
					write(", ");
					//write("0"); // nb_nu
                    write(String.valueOf(countUndefined(column)));
					write(", ");
                    int cnt = cf.getCategories().size();
					write(spl(cnt));
					write(",\n");


					writeCategories(cf);
				} else if (column instanceof IntervalColumn) {
//					System.out.println("IntervalColumn: PROCESSING : " + name
//							+ "   cn=" + column.getName());

					IntervalColumn icol = (IntervalColumn) column;

					// (3, inter_continue, "", "AD00", "recomp_chanson", 0, 0,
					// 0, 6),
					write("inter_continue, \"\", \"");
					write(name);
					write("\", \"");
					write(label);
					write("\", ");
					write("0"); // nb_na, should we compute it, what it is
					// exactly ?
					write(", ");
					//write("0"); // nb_nu
                    write(String.valueOf(countUndefined(column)));
					// exactly ?
					write(", ");
					write(spl(icol.getDoubleMin()));
					write(", ");
					write(spl(icol.getDoubleMax()));
					write(")");

				} else if (column instanceof DoubleColumn) {
//					System.out.println("DoubleColumn: PROCESSING : " + name
//							+ "   cn=" + column.getName());
					DoubleColumn dc = (DoubleColumn) column;

					write("continue, \"\", \"");
					write(name);
					write("\", \"");
					write(label);
					write("\", ");
					write("0"); // nb_na, should we compute it, what it is
					// exactly ?
					write(", ");
					//write("0"); // nb_nu
                    write(String.valueOf(countUndefined(column)));
					write(", ");
					write(spl(dc.getDoubleMin()));
					write(", ");
					write(spl(dc.getDoubleMax()));
					write(")");
				} else if (column instanceof IntColumn) {
//					System.out.println("IntColumn: PROCESSING : " + name
//							+ "   cn=" + column.getName());
					IntColumn dc = (IntColumn) column;

					write("continue, \"\", \"");
					write(name);
					write("\", \"");
					write(label);
					write("\", ");
					write("0"); // nb_na, should we compute it, what it is
					// exactly ?
					write(", ");
					//write("0"); // nb_nu
                    write(String.valueOf(countUndefined(column)));
					write(", ");
					write(spl(dc.getDoubleMin()));
					write(", ");
					write(spl(dc.getDoubleMax()));
					write(")");
				}
                if (vidx != columns.length) {
                    write(",\n");
                }
			}

			write("),\n\n");

			// HIERARCHY
			// not yet supported

			getWriter().flush();

			// MATRIX
			// Apparently, in the provided sample files, only RECTANGLE_MATRIX
			// occurs.

			// if(false) {
			write("RECTANGLE_MATRIX = (\n");
			double height;
			for (int r = 0; r < table.getRowCount(); r++) {
				
				write("(");
				for (int c = 0; c < columns.length; c++) {
					// System.out.println("r="+r + " c="+c +" : " + columns[c].getClass().getName());
					Column col = columns[c];
					if (col.isValueUndefined(r)) {
					    write("NU");
					}
					else if (col instanceof CategoricalColumn) {
					    CategoricalColumn cc = (CategoricalColumn)col;
                        write(String.valueOf(cc.getIntAt(r) + 1));
					}
					else if (col instanceof BooleanColumn) {
					    BooleanColumn bc = (BooleanColumn)col;
					    if (bc.get(r))
					        write("2");
					    else
					        write("1");
                    }
					else if (col instanceof IntervalColumn) {
					    IntervalColumn ic = (IntervalColumn)col;
						DoubleInterval di = (DoubleInterval)ic.get(r);
						write("(" + spl(di.getMin()) + ":" + spl(di.getMax()) + ")");
					}
//					else if (col instanceof StringColumn) { // should not h
//						CategoricalFormat cf = (CategoricalFormat) columns[c].getMetadata().getAttribute("CATEGORICAL_FORMAT");
//						if (o != null) {
//							write(String.valueOf(1+cf.getCategory((String)o)));
//						} else
//							write("0");
//					}
					else if (col instanceof CategoricalDistributionColumn) {
					    CategoricalDistributionColumn cc = (CategoricalDistributionColumn)col;
						a = false;
						
						CategoricalDistribution cd = (CategoricalDistribution) col.getObjectAt(r);
						write("(");
						for (int cat = 0; cat < cd.size(); cat++) {
							if ((height = cd.getHeight(cat)) != 0) {
								if (a)
									write(",");
								a = true;
								write(String.valueOf(cat + 1) + "("+ spl(height) + ")");
							}
						}
						write(")");
					}
                    else if (col instanceof NumberColumn) {
                        write(col.getValueAt(r));
                    }
					else {
					    LOG.error("Unhandled column type");
					    write(col.getValueAt(r));
					}
					
					if (c + 1 < columns.length)
						write(",\n");
				} // loop columns
				if (r + 1 >= table.getRowCount())
					write(")\n");
				else
					write("),\n");
			} // loop rows

			write(")\n\n");

			write(") END");
			getWriter().close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

    private void writeCategories(CategoricalFormat cf)
            throws IOException {
    	
        int cnt = cf.getCategories().size();
        write("(");
        for (int cat = 0; cat < cnt; cat++) {
        	String catName = cf.getCategoryName(cat);
        	String libelle = (String) cf.getCategoryAttribute(cat, "label");
        	write("(");
        	write(String.valueOf(cat + 1));
        	write(", ");
        	
        	if(libelle!=null) {
        		
        		
        	write("\"" + catName + "\"");
        	write(", ");
        	if (libelle != null)
        		write("\"" + libelle + "\"");
        	else
        		write("\"\"");
        	
        	
        	} else {
        		
        		write("\"CAT" +catAppended.toString()+ String.valueOf(cat + 1)+ "\"");
            	write(", ");
            	
            		write("\"" + catName + "\"");
            	
        	}
        	write(", 0"); // ???
        	write(" )");
        	if (cat + 1 < cnt)
        		write(",\n");
        	else
        		write(")\n");

        }
        write(")");
        char c = catAppended.charAt(catAppendedIndex);
        if(c=='Z') {
        	catAppended.setCharAt(catAppendedIndex, 'A');
        	catAppendedIndex++;
        } else {
        	c++;
        	catAppended.setCharAt(catAppendedIndex, 'A');        	
        }

    }

    protected int countUndefined(Column[] cols) {
        int nb_nu = 0;
        for (int c = 0; c < cols.length; c++) {
            Column col = cols[c];
            nb_nu += countUndefined(col);
        }
        return nb_nu;
    }
    
    protected int countUndefined(Column col) {
        int nb_nu = 0;
        for(int row = col.size(); --row>=0; ) {
            if (col.isValueUndefined(row)) {
                nb_nu++;
            }
        }
        return nb_nu;
        
    }

	String spl(double v) {

		int vi = (int)v;

		if (vi == v)
			return String.valueOf(vi);
		else
			return String.valueOf(v);

	}

	private String Q(String tok) {
        if (tok == null)
            return "\"\"";
        else
            return quoteString(tok);
    }

}
