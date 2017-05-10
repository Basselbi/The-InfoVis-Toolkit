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


import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

public class SODASParser extends antlr.LLkParser       implements SODASTokenTypes
 {

	Table table;
	Column[] columns;
	StringColumn nameColumn;
	StringColumn libelColumn;
	int indiv_nb;
	Column currentColumn;
	CategoricalFormat currentCategories;
	final Boolean TRUE = Boolean.TRUE;
	MutableAttributeSet tmd;

	
	public void setTable(Table table) {
		this.table = table;
		if (table != null) {
		 	this.tmd = table.getMetadata();
		}
		else {
			this.tmd = null;
		}
	}
	
	public Table getTable() {
		return table;
	}
	
	void reportHeader(String key, int v) {
		tmd.addAttribute(key, new Integer(v));
		if ("var_nb".equals(key)) {
			columns = new Column[v];
		}
		else if ("indiv_nb".equals(key)) {
			indiv_nb = v;
		}
	}
	
	void createStructure() {
		nameColumn = StringColumn.findColumn(table, SODASConstants.ATTR_NAME);
		VisualRole.setVisualRole(nameColumn, VisualRole.VISUAL_ROLE_SHORT_LABEL);
		libelColumn = StringColumn.findColumn(table, SODASConstants.ATTR_LABEL);
		VisualRole.setLabel(libelColumn);
		nameColumn.clear();
		libelColumn.clear();
		nameColumn.ensureCapacity(indiv_nb);
		libelColumn.ensureCapacity(indiv_nb);
	}
	
	void addIndividual(int n, String name, String libel) {
		//assert(nameColumn.size()==n);
		//assert(libelColumn.size()==n);
		nameColumn.add(name);
		libelColumn.add(libel);
	}
	
	void setObjectAt(int row, Object v) {
		if (row >= currentColumn.size()) {
			currentColumn.setSize(row+1);
		}
		if (currentCategories != null && v instanceof Double) {
			int cat = ((Double)v).intValue();
			currentColumn.setObjectAt(row, new Integer(cat-1));
		}
		else {
			currentColumn.setObjectAt(row,v);
		}
	}

protected SODASParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public SODASParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected SODASParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public SODASParser(TokenStream lexer) {
  this(lexer,2);
}

public SODASParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final void sodas() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LITERAL_SODAS);
			match(EQ);
			match(OPAREN);
			sodas_desc();
			match(CPAREN);
			match(LITERAL_END);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
	}
	
	public final void sodas_desc() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			contain();
			match(COMMA);
			{
			switch ( LA(1)) {
			case LITERAL_MEMO:
			{
				memoposs();
				match(COMMA);
				break;
			}
			case LITERAL_FILES:
			case LITERAL_FILE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			file();
			match(COMMA);
			header_();
			match(COMMA);
			{
			switch ( LA(1)) {
			case LITERAL_INDIVIDUALS:
			{
				{
				indiv();
				match(COMMA);
				variable();
				}
				break;
			}
			case LITERAL_VARIABLES:
			{
				{
				variable();
				match(COMMA);
				indiv();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(COMMA);
			{
			switch ( LA(1)) {
			case LITERAL_HIERARCHIE:
			{
				hierarchies_poss();
				match(COMMA);
				break;
			}
			case LITERAL_RULES:
			case LITERAL_RECTANGLE_MATRIX:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case LITERAL_RULES:
			{
				relations_poss();
				match(COMMA);
				break;
			}
			case LITERAL_RECTANGLE_MATRIX:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			mat_rect();
			{
			switch ( LA(1)) {
			case COMMA:
			{
				match(COMMA);
				dist_mat_poss();
				break;
			}
			case CPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void contain() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LITERAL_CONTAINS);
			match(EQ);
			match(OPAREN);
			contain_list();
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
	}
	
	public final void memoposs() throws RecognitionException, TokenStreamException {
		
		Token  str = null;
		
		try {      // for error handling
			match(LITERAL_MEMO);
			match(EQ);
			match(OPAREN);
			str = LT(1);
			match(C_STRING);
			match(CPAREN);
			tmd.addAttribute("MEMO",str.getText());
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
	}
	
	public final void file() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			file_kw();
			match(EQ);
			match(OPAREN);
			file_desc();
			{
			switch ( LA(1)) {
			case COMMA:
			{
				match(COMMA);
				break;
			}
			case CPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
	}
	
	public final void header_() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LITERAL_HEADER);
			match(EQ);
			match(OPAREN);
			header_desc();
			match(CPAREN);
			createStructure();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
	}
	
	public final void indiv() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LITERAL_INDIVIDUALS);
			match(EQ);
			match(OPAREN);
			indiv_desc_list();
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
	}
	
	public final void variable() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LITERAL_VARIABLES);
			match(EQ);
			match(OPAREN);
			variable_desc_list();
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
	}
	
	public final void hierarchies_poss() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LITERAL_HIERARCHIE);
			match(EQ);
			match(OPAREN);
			hierarchies_desc();
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
	}
	
	public final void relations_poss() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LITERAL_RULES);
			match(EQ);
			match(OPAREN);
			rel_desc_list();
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
	}
	
	public final void mat_rect() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LITERAL_RECTANGLE_MATRIX);
			match(EQ);
			match(OPAREN);
			ligne_desc_list();
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final void dist_mat_poss() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			dist_mat();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void contain_list() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			contain_elem();
			{
			_loop52:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					contain_elem();
				}
				else {
					break _loop52;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void contain_elem() throws RecognitionException, TokenStreamException {
		
		String key = LT(1).getText();
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_FILES:
			case LITERAL_FILE:
			{
				file_kw();
				tmd.addAttribute(key,TRUE);
				break;
			}
			case LITERAL_HEADER:
			{
				match(LITERAL_HEADER);
				tmd.addAttribute(key,TRUE);
				break;
			}
			case LITERAL_INDIVIDUALS:
			{
				match(LITERAL_INDIVIDUALS);
				tmd.addAttribute(key,TRUE);
				break;
			}
			case LITERAL_VARIABLES:
			{
				match(LITERAL_VARIABLES);
				tmd.addAttribute(key,TRUE);
				break;
			}
			case LITERAL_HIERARCHIE:
			{
				match(LITERAL_HIERARCHIE);
				tmd.addAttribute(key,TRUE);
				break;
			}
			case LITERAL_RULES:
			{
				match(LITERAL_RULES);
				tmd.addAttribute(key,TRUE);
				break;
			}
			case LITERAL_MEMO:
			{
				match(LITERAL_MEMO);
				tmd.addAttribute(key,TRUE);
				break;
			}
			case LITERAL_DIST_MATRIX:
			{
				match(LITERAL_DIST_MATRIX);
				tmd.addAttribute(key,TRUE);
				break;
			}
			case LITERAL_TRIANGLE_MATRIX:
			{
				match(LITERAL_TRIANGLE_MATRIX);
				tmd.addAttribute(key,TRUE);
				break;
			}
			case LITERAL_RECTANGLE_MATRIX:
			{
				match(LITERAL_RECTANGLE_MATRIX);
				tmd.addAttribute(key,TRUE);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final void file_kw() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_FILES:
			{
				match(LITERAL_FILES);
				break;
			}
			case LITERAL_FILE:
			{
				match(LITERAL_FILE);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
	}
	
	public final String  cstring() throws RecognitionException, TokenStreamException {
		String ret = null;
		
		Token  s = null;
		
		try {      // for error handling
			match(EQ);
			s = LT(1);
			match(C_STRING);
			ret = s.getText();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		return ret;
	}
	
	public final int  cint() throws RecognitionException, TokenStreamException {
		 int ret = 0;
		
		Token  v = null;
		
		try {      // for error handling
			match(EQ);
			v = LT(1);
			match(C_INT);
			ret = Integer.parseInt(v.getText());
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		return ret;
	}
	
	public final void file_desc() throws RecognitionException, TokenStreamException {
		
		String key = null, value = null;
		
		try {      // for error handling
			key=file_elem();
			value=cstring();
			tmd.addAttribute(key,value);
			{
			_loop62:
			do {
				if ((LA(1)==COMMA) && ((LA(2) >= LITERAL_procedure_name && LA(2) <= LITERAL_filiere_path))) {
					match(COMMA);
					key=file_elem();
					value=cstring();
					tmd.addAttribute(key,value);
				}
				else {
					break _loop62;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final String  file_elem() throws RecognitionException, TokenStreamException {
		String ret = LT(1).getText();
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_procedure_name:
			{
				match(LITERAL_procedure_name);
				break;
			}
			case LITERAL_version:
			{
				match(LITERAL_version);
				break;
			}
			case LITERAL_create_date:
			{
				match(LITERAL_create_date);
				break;
			}
			case LITERAL_filiere_name:
			{
				match(LITERAL_filiere_name);
				break;
			}
			case LITERAL_base:
			{
				match(LITERAL_base);
				break;
			}
			case LITERAL_filiere_path:
			{
				match(LITERAL_filiere_path);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
		return ret;
	}
	
	public final void header_desc() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			header_elem();
			{
			_loop67:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					header_elem();
				}
				else {
					break _loop67;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void header_elem() throws RecognitionException, TokenStreamException {
		
		String key = null, value = null; int v;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_title:
			case LITERAL_sub_title:
			{
				key=header_string_elem();
				value=cstring();
				tmd.addAttribute(key, value);
				break;
			}
			case LITERAL_indiv_nb:
			case LITERAL_var_nb:
			case LITERAL_nb_var_cont:
			case LITERAL_nb_var_nom:
			case LITERAL_rules_nb:
			case LITERAL_nb_var_set:
			case LITERAL_nb_indiv_set:
			case LITERAL_nb_var_cont_symb:
			case LITERAL_nb_var_text:
			case LITERAL_nb_var_nom_symb:
			case LITERAL_nb_var_nom_mod:
			case LITERAL_nb_hierarchies:
			case LITERAL_nb_na:
			case LITERAL_nb_null:
			case LITERAL_nb_nu:
			{
				key=header_int_elem();
				v=cint();
				reportHeader(key, v);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final String  header_string_elem() throws RecognitionException, TokenStreamException {
		String ret = LT(1).getText();
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_title:
			{
				match(LITERAL_title);
				break;
			}
			case LITERAL_sub_title:
			{
				match(LITERAL_sub_title);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
		return ret;
	}
	
	public final String  header_int_elem() throws RecognitionException, TokenStreamException {
		 String ret = LT(1).getText();
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_indiv_nb:
			{
				match(LITERAL_indiv_nb);
				break;
			}
			case LITERAL_var_nb:
			{
				match(LITERAL_var_nb);
				break;
			}
			case LITERAL_nb_var_cont:
			{
				match(LITERAL_nb_var_cont);
				break;
			}
			case LITERAL_nb_var_nom:
			{
				match(LITERAL_nb_var_nom);
				break;
			}
			case LITERAL_rules_nb:
			{
				match(LITERAL_rules_nb);
				break;
			}
			case LITERAL_nb_var_set:
			{
				match(LITERAL_nb_var_set);
				break;
			}
			case LITERAL_nb_indiv_set:
			{
				match(LITERAL_nb_indiv_set);
				break;
			}
			case LITERAL_nb_var_cont_symb:
			{
				match(LITERAL_nb_var_cont_symb);
				break;
			}
			case LITERAL_nb_var_text:
			{
				match(LITERAL_nb_var_text);
				break;
			}
			case LITERAL_nb_var_nom_symb:
			{
				match(LITERAL_nb_var_nom_symb);
				break;
			}
			case LITERAL_nb_var_nom_mod:
			{
				match(LITERAL_nb_var_nom_mod);
				break;
			}
			case LITERAL_nb_hierarchies:
			{
				match(LITERAL_nb_hierarchies);
				break;
			}
			case LITERAL_nb_na:
			{
				match(LITERAL_nb_na);
				break;
			}
			case LITERAL_nb_null:
			{
				match(LITERAL_nb_null);
				break;
			}
			case LITERAL_nb_nu:
			{
				match(LITERAL_nb_nu);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
		return ret;
	}
	
	public final void indiv_desc_list() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			indiv_desc();
			{
			_loop74:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					indiv_desc();
				}
				else {
					break _loop74;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void indiv_desc() throws RecognitionException, TokenStreamException {
		
		
			int n;
			String name, libel;
		
		
		try {      // for error handling
			match(OPAREN);
			n=indiv_num();
			match(COMMA);
			name=indiv_name();
			match(COMMA);
			libel=indiv_libel();
			match(CPAREN);
			addIndividual(n, name, libel);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final int  indiv_num() throws RecognitionException, TokenStreamException {
		int v = 0;
		
		Token  i = null;
		
		try {      // for error handling
			i = LT(1);
			match(C_INT);
			v = Integer.parseInt(i.getText());
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return v;
	}
	
	public final String  indiv_name() throws RecognitionException, TokenStreamException {
		String ret=null;
		
		Token  s = null;
		Token  i = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case C_STRING:
			{
				s = LT(1);
				match(C_STRING);
				ret = s.getText();
				break;
			}
			case IDENT:
			{
				i = LT(1);
				match(IDENT);
				ret = i.getText();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return ret;
	}
	
	public final String  indiv_libel() throws RecognitionException, TokenStreamException {
		String ret=null;
		
		Token  s = null;
		
		try {      // for error handling
			s = LT(1);
			match(C_STRING);
			ret = s.getText();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		return ret;
	}
	
	public final void variable_desc_list() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			variable_desc();
			{
			_loop82:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					variable_desc();
				}
				else {
					break _loop82;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void variable_desc() throws RecognitionException, TokenStreamException {
		
		
			int vnum;
		
		
		try {      // for error handling
			match(OPAREN);
			vnum=var_num();
			match(COMMA);
			var_desc(vnum);
			{
			switch ( LA(1)) {
			case COMMA:
			{
				match(COMMA);
				match(LITERAL_ordered);
				break;
			}
			case CPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final int  var_num() throws RecognitionException, TokenStreamException {
		int ret = -1;
		
		
		try {      // for error handling
			ret=integer();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
		return ret;
	}
	
	public final void var_desc(
		 int vnum 
	) throws RecognitionException, TokenStreamException {
		
		
			String st = null;
			String vn = null;
			String vl = null;
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_nominal:
			{
				match(LITERAL_nominal);
				match(COMMA);
				st=sub_type();
				match(COMMA);
				vn=var_name();
				match(COMMA);
				vl=var_libel();
				match(COMMA);
				nominal_desc(vnum,"nominal",st,vn,vl,null);
				break;
			}
			case LITERAL_continue:
			{
				match(LITERAL_continue);
				match(COMMA);
				st=sub_type();
				match(COMMA);
				vn=var_name();
				match(COMMA);
				vl=var_libel();
				match(COMMA);
				continue_desc(vnum,"continue",st,vn,vl,null);
				break;
			}
			case LITERAL_mult_nominal:
			{
				match(LITERAL_mult_nominal);
				match(COMMA);
				st=sub_type();
				match(COMMA);
				vn=var_name();
				match(COMMA);
				vl=var_libel();
				match(COMMA);
				nominal_desc(vnum,"mult_nominal",st,vn,vl,null);
				break;
			}
			case LITERAL_mult_nominal_Modif:
			{
				match(LITERAL_mult_nominal_Modif);
				match(COMMA);
				st=sub_type();
				match(COMMA);
				vn=var_name();
				match(COMMA);
				vl=var_libel();
				match(COMMA);
				nominal_desc1(vnum,"mult_nominal_Modif",st,vn,vl);
				break;
			}
			case LITERAL_inter_cont:
			case LITERAL_inter_continue:
			{
				{
				switch ( LA(1)) {
				case LITERAL_inter_cont:
				{
					match(LITERAL_inter_cont);
					break;
				}
				case LITERAL_inter_continue:
				{
					match(LITERAL_inter_continue);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(COMMA);
				st=sub_type();
				match(COMMA);
				vn=var_name();
				match(COMMA);
				vl=var_libel();
				match(COMMA);
				continue_desc(vnum,"inter_cont",st,vn,vl,null);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final String  sub_type() throws RecognitionException, TokenStreamException {
		String ret=null;
		
		Token  s = null;
		
		try {      // for error handling
			s = LT(1);
			match(C_STRING);
			ret = s.getText();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return ret;
	}
	
	public final String  var_name() throws RecognitionException, TokenStreamException {
		String ret=null;
		
		Token  s = null;
		
		try {      // for error handling
			s = LT(1);
			match(C_STRING);
			ret = s.getText();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return ret;
	}
	
	public final String  var_libel() throws RecognitionException, TokenStreamException {
		String ret=null;
		
		Token  s = null;
		
		try {      // for error handling
			s = LT(1);
			match(C_STRING);
			ret = s.getText();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return ret;
	}
	
	public final void nominal_desc(
		 int vnum, String type, String st, String vn, String ln, String mod
	) throws RecognitionException, TokenStreamException {
		
			CategoricalFormat cf = null;
			Column column = null;
			String name = vn.length() == 0 ? ln : vn;
			if (type.equals("nominal")) {
				column = new CategoricalColumn(name);
				cf = (CategoricalFormat)column.getFormat();
			}
			else if (type.equals("mult_nominal") || type.equals("mult_nominal_Modif")) {
				column = new CategoricalDistributionColumn(name);
				cf = (CategoricalFormat)column.getFormat();
			}
			else {
				throw new RuntimeException("Invalid nominal description "+type);
			}
			cf.addCategoryAttribute("label", "string");
			column.getMetadata().addAttribute(SODASConstants.ATTR_SODAS_TYPE, type);
			column.getMetadata().addAttribute(SODASConstants.ATTR_LABEL, ln);
			if(mod!=null)
				column.getMetadata().addAttribute(SODASConstants.ATTR_MOD_NAME_POSS, mod);
			columns[vnum-1] = column;
			table.addColumn(column);
		
		
		try {      // for error handling
			nb_na();
			match(COMMA);
			nb_nu();
			match(COMMA);
			nb_categ();
			match(COMMA);
			match(OPAREN);
			liste_nom(cf);
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final void continue_desc(
		 int vnum, String type, String st, String vn, String ln, String mod
	) throws RecognitionException, TokenStreamException {
		
		
			Column column = null;
			String name = vn.length() == 0 ? ln : vn;
			if (type.equals("continue")) {
				column = new DoubleColumn(name);
			}
			else if (type.equals("inter_cont")) {
				column = new IntervalColumn(name);
			}
			column.getMetadata().addAttribute(SODASConstants.ATTR_SODAS_TYPE, type);
			column.getMetadata().addAttribute(SODASConstants.ATTR_LABEL, name);
			columns[vnum-1] = column;
			table.addColumn(column);
		
		
		try {      // for error handling
			nb_na();
			match(COMMA);
			nb_nu();
			match(COMMA);
			pmin();
			match(COMMA);
			pmax();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final void nominal_desc1(
		 int vnum, String type, String st, String vn, String ln
	) throws RecognitionException, TokenStreamException {
		
		String mod = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_proba:
			case LITERAL_cardinal:
			{
				mod=modifier_name_poss();
				match(COMMA);
				break;
			}
			case C_INT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			nominal_desc(vnum,type,st,vn,ln,mod);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final int  integer() throws RecognitionException, TokenStreamException {
		int ret = -1;
		
		Token  i = null;
		
		try {      // for error handling
			i = LT(1);
			match(C_INT);
			ret = Integer.parseInt(i.getText());
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		return ret;
	}
	
	public final String  modifier_name_poss() throws RecognitionException, TokenStreamException {
		 String mod = LT(1).getText();
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_proba:
			{
				match(LITERAL_proba);
				break;
			}
			case LITERAL_cardinal:
			{
				match(LITERAL_cardinal);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return mod;
	}
	
	public final int  nb_na() throws RecognitionException, TokenStreamException {
		int ret = -1;
		
		
		try {      // for error handling
			ret=integer();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return ret;
	}
	
	public final void nb_nu() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			num();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
	}
	
	public final int  nb_categ() throws RecognitionException, TokenStreamException {
		int ret = -1;
		
		
		try {      // for error handling
			ret=integer();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return ret;
	}
	
	public final void liste_nom(
		CategoricalFormat cf
	) throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			elem_nom(cf);
			{
			_loop101:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					elem_nom(cf);
				}
				else {
					break _loop101;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final double  num() throws RecognitionException, TokenStreamException {
		double ret = Double.NaN;
		
		Token  i = null;
		Token  f = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case C_INT:
			{
				i = LT(1);
				match(C_INT);
				ret = Integer.parseInt(i.getText());
				break;
			}
			case C_FLOAT:
			{
				f = LT(1);
				match(C_FLOAT);
				ret = Double.parseDouble(f.getText());
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		return ret;
	}
	
	public final void elem_nom(
		CategoricalFormat cf
	) throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(OPAREN);
			categ_desc(cf);
			match(COMMA);
			categ_frequency();
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final void categ_desc(
		CategoricalFormat cf
	) throws RecognitionException, TokenStreamException {
		
			int cn = -1; String cname = null, cl=null;
		
		try {      // for error handling
			cn=categ_num();
			match(COMMA);
			cname=categ_name();
			match(COMMA);
			cl=libel_categ();
			cf.putCategory(cname, cn-1); cf.setCategoryAttribute(cn-1, "label", cl);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
	}
	
	public final void categ_frequency() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			num();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final int  categ_num() throws RecognitionException, TokenStreamException {
		int ret = -1;
		
		
		try {      // for error handling
			ret=integer();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
		return ret;
	}
	
	public final String  categ_name() throws RecognitionException, TokenStreamException {
		String ret = null;
		
		Token  s = null;
		Token  i = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case C_STRING:
			{
				s = LT(1);
				match(C_STRING);
				ret = s.getText();
				break;
			}
			case IDENT:
			{
				i = LT(1);
				match(IDENT);
				ret = s.getText();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return ret;
	}
	
	public final String  libel_categ() throws RecognitionException, TokenStreamException {
		String ret=null;
		
		Token  s = null;
		
		try {      // for error handling
			s = LT(1);
			match(C_STRING);
			ret = s.getText();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return ret;
	}
	
	public final void pmin() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			num();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
	}
	
	public final void pmax() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			num();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final void hierarchies_desc() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			hierarchie_desc();
			{
			_loop114:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					hierarchie_desc();
				}
				else {
					break _loop114;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void hierarchie_desc() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(OPAREN);
			var_hier_name();
			match(COMMA);
			match(OPAREN);
			hval_ref_list();
			match(CPAREN);
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final void var_hier_name() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LITERAL_VAR);
			match(EQ);
			var_num_ref();
			match(COMMA);
			nb_hier_val();
			match(OPAREN);
			hier_val_desc();
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
	}
	
	public final void hval_ref_list() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			hval_ref_dec();
			{
			_loop125:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					hval_ref_dec();
				}
				else {
					break _loop125;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final int  var_num_ref() throws RecognitionException, TokenStreamException {
		int ret = -1;
		
		
		try {      // for error handling
			ret=integer();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return ret;
	}
	
	public final int  nb_hier_val() throws RecognitionException, TokenStreamException {
		int ret = -1;
		
		
		try {      // for error handling
			ret=integer();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		return ret;
	}
	
	public final void hier_val_desc() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			if ((LA(1)==OPAREN) && (LA(2)==C_INT)) {
				hier_val();
				{
				_loop119:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						hier_val();
					}
					else {
						break _loop119;
					}
					
				} while (true);
				}
			}
			else if ((LA(1)==OPAREN) && (LA(2)==CPAREN)) {
				match(OPAREN);
				match(CPAREN);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void hier_val() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(OPAREN);
			categ_num();
			match(COMMA);
			categ_name();
			match(COMMA);
			libel_categ();
			match(COMMA);
			num();
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final void hval_ref_dec() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			categ_num();
			match(EQ);
			match(OPAREN);
			hval_ref_list2();
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final void hval_ref_list2() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			hval_ref();
			{
			_loop129:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					hval_ref();
				}
				else {
					break _loop129;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void hval_ref() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case C_INT:
			{
				categ_ref();
				break;
			}
			case OPAREN:
			{
				match(OPAREN);
				categ_ref_list();
				match(CPAREN);
				break;
			}
			case OBRAC:
			{
				match(OBRAC);
				num();
				match(COLON);
				num();
				match(CBRAC);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final int  categ_ref() throws RecognitionException, TokenStreamException {
		int ret = -1;
		
		
		try {      // for error handling
			ret=integer();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		return ret;
	}
	
	public final void categ_ref_list() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			categ_ref();
			{
			_loop133:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					categ_ref();
				}
				else {
					break _loop133;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void rel_desc_list() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			rel_desc();
			{
			_loop139:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					rel_desc();
				}
				else {
					break _loop139;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void rel_desc() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(OPAREN);
			prem_desc();
			conclusion();
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final void prem_desc() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(OPAREN);
			var_num();
			match(EQ);
			val();
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
	}
	
	public final void conclusion() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(OPAREN);
			var_num();
			match(EQ);
			val();
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final Object  val() throws RecognitionException, TokenStreamException {
		Object o = null;
		
			double n;
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case OPAREN:
			{
				match(OPAREN);
				{
				if ((LA(1)==C_INT) && (LA(2)==OPAREN||LA(2)==CPAREN||LA(2)==COMMA)) {
					o=val_list();
				}
				else if ((LA(1)==C_INT||LA(1)==C_FLOAT) && (LA(2)==COLON)) {
					o=interval();
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				match(CPAREN);
				break;
			}
			case LITERAL_NA:
			{
				match(LITERAL_NA);
				break;
			}
			case LITERAL_NU:
			{
				match(LITERAL_NU);
				break;
			}
			default:
				if ((LA(1)==C_INT||LA(1)==C_FLOAT) && (LA(2)==CPAREN||LA(2)==COMMA)) {
					n=num();
					o = new Double(n);
				}
				else if ((LA(1)==C_INT||LA(1)==C_FLOAT) && (LA(2)==COLON)) {
					o=interval();
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		return o;
	}
	
	public final void ligne_desc_list() throws RecognitionException, TokenStreamException {
		
		
			int row = 0;
		
		
		try {      // for error handling
			ligne_desc(row++);
			{
			_loop146:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					ligne_desc(row++);
				}
				else {
					break _loop146;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void ligne_desc(
		int row
	) throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(OPAREN);
			val_desc_list(row);
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final void val_desc_list(
		int row
	) throws RecognitionException, TokenStreamException {
		
		
			int col = 0;
		
		
		try {      // for error handling
			val_desc(row,col++);
			{
			_loop150:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					val_desc(row,col++);
				}
				else {
					break _loop150;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void val_desc(
		int row, int col
	) throws RecognitionException, TokenStreamException {
		
		
			currentColumn = columns[col];
			Format f = currentColumn.getFormat();
			if (f != null && f instanceof CategoricalFormat) {
				currentCategories = (CategoricalFormat)f;
			}
			else {
				currentCategories = null;
			}
			Object v = null;
		
		
		try {      // for error handling
			v=val();
			setObjectAt(row, v);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final CategoricalDistribution  val_list() throws RecognitionException, TokenStreamException {
		 
	CategoricalDistribution cd = new CategoricalDistribution(currentCategories)
;
		
		
			assert(currentCategories != null);
		
		
		try {      // for error handling
			valsimple(cd);
			{
			_loop156:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					valsimple(cd);
				}
				else {
					break _loop156;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		return cd;
	}
	
	public final Interval  interval() throws RecognitionException, TokenStreamException {
		 Interval i = null ;
		
		
			double min, max;
		
		
		try {      // for error handling
			min=num();
			match(COLON);
			max=num();
			i = new DoubleInterval(min,max);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		return i;
	}
	
	public final void valsimple(
		 CategoricalDistribution cd 
	) throws RecognitionException, TokenStreamException {
		
		
			int cat = -1;
			double v = 1;
		
		
		try {      // for error handling
			cat=integer();
			{
			switch ( LA(1)) {
			case OPAREN:
			{
				match(OPAREN);
				v=num();
				match(CPAREN);
				break;
			}
			case CPAREN:
			case COMMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			cd.add(cat-1, v);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final void dist_mat() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			dist_matrix();
			match(EQ);
			match(OPAREN);
			line_dist_set();
			match(CPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void dist_matrix() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_TRIANGLE_MATRIX:
			{
				match(LITERAL_TRIANGLE_MATRIX);
				break;
			}
			case LITERAL_DIST_MATRIX:
			{
				match(LITERAL_DIST_MATRIX);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
	}
	
	public final void line_dist_set() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(OPAREN);
			line_dist();
			match(CPAREN);
			{
			_loop165:
			do {
				if ((LA(1)==OPAREN)) {
					match(OPAREN);
					line_dist();
					match(CPAREN);
				}
				else {
					break _loop165;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void line_dist() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			num();
			{
			_loop168:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					num();
				}
				else {
					break _loop168;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"SODAS\"",
		"EQ",
		"OPAREN",
		"CPAREN",
		"\"END\"",
		"COMMA",
		"\"CONTAINS\"",
		"\"HEADER\"",
		"\"INDIVIDUALS\"",
		"\"VARIABLES\"",
		"\"HIERARCHIE\"",
		"\"RULES\"",
		"\"MEMO\"",
		"\"DIST_MATRIX\"",
		"\"TRIANGLE_MATRIX\"",
		"\"RECTANGLE_MATRIX\"",
		"C_STRING",
		"an integer value",
		"\"FILES\"",
		"\"FILE\"",
		"\"procedure_name\"",
		"\"version\"",
		"\"create_date\"",
		"\"filiere_name\"",
		"\"base\"",
		"\"filiere_path\"",
		"\"title\"",
		"\"sub_title\"",
		"\"indiv_nb\"",
		"\"var_nb\"",
		"\"nb_var_cont\"",
		"\"nb_var_nom\"",
		"\"rules_nb\"",
		"\"nb_var_set\"",
		"\"nb_indiv_set\"",
		"\"nb_var_cont_symb\"",
		"\"nb_var_text\"",
		"\"nb_var_nom_symb\"",
		"\"nb_var_nom_mod\"",
		"\"nb_hierarchies\"",
		"\"nb_na\"",
		"\"nb_null\"",
		"\"nb_nu\"",
		"a string value",
		"\"ordered\"",
		"\"nominal\"",
		"\"continue\"",
		"\"mult_nominal\"",
		"\"mult_nominal_Modif\"",
		"\"inter_cont\"",
		"\"inter_continue\"",
		"\"proba\"",
		"\"cardinal\"",
		"\"VAR\"",
		"OBRAC",
		"COLON",
		"CBRAC",
		"an floating point value",
		"\"NA\"",
		"\"NU\"",
		"a digit",
		"WS",
		"SL_COMMENT",
		"ML_COMMENT",
		"ESC"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 128L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 512L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 640L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 672L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 32L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 544L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 736L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 1729382256910271104L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 64L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	
	}
