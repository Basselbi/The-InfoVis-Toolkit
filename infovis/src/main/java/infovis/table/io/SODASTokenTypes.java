// $ANTLR : "sodas.g" -> "SODASParser.java"$

/*****************************************************************************
 * Copyright (C) 2003-2007 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.table.io;
/**
 * Parser and Lexer for the SODAS Table format.
 *
 * @author Nghi, Elie and Jean-Daniel 
 * @version $Revision: 1.12 $
 */

import infovis.Column;
import infovis.Table;
import infovis.column.DoubleColumn;
import infovis.column.StringColumn;
import infovis.column.CategoricalColumn;
import infovis.column.format.CategoricalFormat;
import infovis.data.CategoricalDistributionColumn;
import infovis.data.CategoricalDistribution;
import infovis.data.DefaultValueSet;
import infovis.data.IntervalColumn;
import infovis.data.Interval;
import infovis.data.DoubleInterval;
import infovis.data.ValueSet;
import infovis.metadata.VisualRole;

import java.text.Format;
import javax.swing.text.MutableAttributeSet;


public interface SODASTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int LITERAL_SODAS = 4;
	int EQ = 5;
	int OPAREN = 6;
	int CPAREN = 7;
	int LITERAL_END = 8;
	int COMMA = 9;
	int LITERAL_CONTAINS = 10;
	int LITERAL_HEADER = 11;
	int LITERAL_INDIVIDUALS = 12;
	int LITERAL_VARIABLES = 13;
	int LITERAL_HIERARCHIE = 14;
	int LITERAL_RULES = 15;
	int LITERAL_MEMO = 16;
	int LITERAL_DIST_MATRIX = 17;
	int LITERAL_TRIANGLE_MATRIX = 18;
	int LITERAL_RECTANGLE_MATRIX = 19;
	int C_STRING = 20;
	int C_INT = 21;
	int LITERAL_FILES = 22;
	int LITERAL_FILE = 23;
	int LITERAL_procedure_name = 24;
	int LITERAL_version = 25;
	int LITERAL_create_date = 26;
	int LITERAL_filiere_name = 27;
	int LITERAL_base = 28;
	int LITERAL_filiere_path = 29;
	int LITERAL_title = 30;
	int LITERAL_sub_title = 31;
	int LITERAL_indiv_nb = 32;
	int LITERAL_var_nb = 33;
	int LITERAL_nb_var_cont = 34;
	int LITERAL_nb_var_nom = 35;
	int LITERAL_rules_nb = 36;
	int LITERAL_nb_var_set = 37;
	int LITERAL_nb_indiv_set = 38;
	int LITERAL_nb_var_cont_symb = 39;
	int LITERAL_nb_var_text = 40;
	int LITERAL_nb_var_nom_symb = 41;
	int LITERAL_nb_var_nom_mod = 42;
	int LITERAL_nb_hierarchies = 43;
	int LITERAL_nb_na = 44;
	int LITERAL_nb_null = 45;
	int LITERAL_nb_nu = 46;
	int IDENT = 47;
	int LITERAL_ordered = 48;
	int LITERAL_nominal = 49;
	int LITERAL_continue = 50;
	int LITERAL_mult_nominal = 51;
	int LITERAL_mult_nominal_Modif = 52;
	int LITERAL_inter_cont = 53;
	int LITERAL_inter_continue = 54;
	int LITERAL_proba = 55;
	int LITERAL_cardinal = 56;
	int LITERAL_VAR = 57;
	int OBRAC = 58;
	int COLON = 59;
	int CBRAC = 60;
	int C_FLOAT = 61;
	int LITERAL_NA = 62;
	int LITERAL_NU = 63;
	int DIGIT = 64;
	int WS = 65;
	int SL_COMMENT = 66;
	int ML_COMMENT = 67;
	int ESC = 68;
}
