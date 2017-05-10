header {
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

}

class SODASParser extends Parser;
options {
	exportVocab=SODAS;
	k = 2;
}

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
}

sodas
	:	"SODAS" EQ OPAREN sodas_desc CPAREN "END"
	;

sodas_desc
	:	contain COMMA 
		( memoposs COMMA )?
		file COMMA
		header_ COMMA 
		(	(indiv COMMA variable)
		|	(variable COMMA indiv)
		) COMMA
		(hierarchies_poss COMMA)?
        (relations_poss COMMA)? 
        mat_rect
        (COMMA dist_mat_poss)?
	;

contain
	: 	"CONTAINS" EQ OPAREN contain_list CPAREN
	;

contain_list
	:	contain_elem (COMMA contain_elem)*
	;

contain_elem
{ String key = LT(1).getText();}
	:   file_kw            { tmd.addAttribute(key,TRUE); } 
	|	"HEADER"           { tmd.addAttribute(key,TRUE); } 
	|	"INDIVIDUALS"      { tmd.addAttribute(key,TRUE); } 
	|	"VARIABLES"        { tmd.addAttribute(key,TRUE); } 
	|	"HIERARCHIE"       { tmd.addAttribute(key,TRUE); } 
	|	"RULES"            { tmd.addAttribute(key,TRUE); } 
	|	"MEMO"             { tmd.addAttribute(key,TRUE); } 
	|	"DIST_MATRIX"      { tmd.addAttribute(key,TRUE); } 
	|   "TRIANGLE_MATRIX"  { tmd.addAttribute(key,TRUE); } 
	|	"RECTANGLE_MATRIX" { tmd.addAttribute(key,TRUE); } 
	;

memoposs
	:	"MEMO" EQ OPAREN str:C_STRING CPAREN { tmd.addAttribute("MEMO",str.getText()); } 
	;

cstring returns [String ret = null]
	: EQ s:C_STRING { ret = s.getText(); } 
	;
	
cint returns [ int ret = 0]
	: EQ v:C_INT { ret = Integer.parseInt(v.getText()); }
	;

file_kw
    :   "FILES"
    |   "FILE"
    ;

file
	:	file_kw EQ OPAREN file_desc (COMMA)? CPAREN 
	;

file_desc
{ String key = null, value = null; }
	:	key=file_elem value=cstring { tmd.addAttribute(key,value); }  
		(COMMA key=file_elem value=cstring { tmd.addAttribute(key,value); }  )*
    ;

file_elem returns [String ret = LT(1).getText()]
	:	"procedure_name"
	|	"version"
	|	"create_date"
	|	"filiere_name"
	|	"base"
	|	"filiere_path"
	;


header_
	: "HEADER" EQ OPAREN header_desc CPAREN { createStructure(); }
	;

header_desc
	:	header_elem (COMMA header_elem)*
	;

header_elem
{ String key = null, value = null; int v; }
	:	key=header_string_elem value=cstring	{ tmd.addAttribute(key, value); } 
    |	key=header_int_elem v=cint { reportHeader(key, v); }
    ;

header_string_elem returns [String ret = LT(1).getText()]
	:	"title"
	|	"sub_title"
	;

header_int_elem returns [ String ret = LT(1).getText()]
	:	"indiv_nb"
	|	"var_nb"
	|	"nb_var_cont"
	|	"nb_var_nom"
	|	"rules_nb"
	|	"nb_var_set"
	|	"nb_indiv_set"
	|	"nb_var_cont_symb"
    |	"nb_var_text"
    |	"nb_var_nom_symb"
    |	"nb_var_nom_mod"
    |	"nb_hierarchies"
    |   "nb_na"
    |   "nb_null" | "nb_nu"
    ;

indiv
    :   "INDIVIDUALS" EQ OPAREN indiv_desc_list CPAREN
    ;

indiv_desc_list
    :   indiv_desc (COMMA indiv_desc )*
    ;

indiv_desc
{
	int n;
	String name, libel;
}
    :   OPAREN n=indiv_num COMMA name=indiv_name COMMA libel=indiv_libel CPAREN
    	{ addIndividual(n, name, libel); }
    ;

indiv_num returns [int v = 0]
    :   i:C_INT { v = Integer.parseInt(i.getText()); }
    ;

indiv_name returns [String ret=null]
    :   s:C_STRING { ret = s.getText(); }
    |   i:IDENT { ret = i.getText(); }
    ;

indiv_libel returns [String ret=null]
    :   s:C_STRING { ret = s.getText(); }
    ;

variable
    :   "VARIABLES" EQ OPAREN variable_desc_list CPAREN
    ;

variable_desc_list
    :  variable_desc (COMMA  variable_desc) *
    ;

variable_desc
{
	int vnum;
}
    :   OPAREN vnum=var_num COMMA var_desc[vnum] (COMMA  "ordered")? CPAREN
    ;

var_desc [ int vnum ]
{
	String st = null;
	String vn = null;
	String vl = null;
}
    :   "nominal" 
    	COMMA st=sub_type COMMA vn=var_name COMMA vl=var_libel COMMA  
    	nominal_desc[vnum,"nominal",st,vn,vl,null]
    |   "continue" 
    	COMMA st=sub_type COMMA vn=var_name COMMA vl=var_libel COMMA  
    	continue_desc[vnum,"continue",st,vn,vl,null]
    |	"mult_nominal" 
    	COMMA st=sub_type COMMA vn=var_name COMMA vl=var_libel COMMA  
    	nominal_desc[vnum,"mult_nominal",st,vn,vl,null]
    |	"mult_nominal_Modif"
    	COMMA st=sub_type COMMA vn=var_name COMMA vl=var_libel COMMA  
    	nominal_desc1[vnum,"mult_nominal_Modif",st,vn,vl]
    |	("inter_cont" | "inter_continue")
    	COMMA st=sub_type COMMA vn=var_name COMMA vl=var_libel COMMA  
    	continue_desc[vnum,"inter_cont",st,vn,vl,null]
    ;


sub_type returns [String ret=null] : s:C_STRING { ret = s.getText(); }; 
var_num returns [int ret = -1] : ret = integer;
var_name returns [String ret=null] : s:C_STRING { ret = s.getText(); };
var_libel returns [String ret=null] : s:C_STRING { ret = s.getText(); };


nominal_desc1 [ int vnum, String type, String st, String vn, String ln]
{ String mod = null; }
	:	(mod=modifier_name_poss COMMA)?
		nominal_desc[vnum,type,st,vn,ln,mod]
	;
	
nominal_desc [ int vnum, String type, String st, String vn, String ln, String mod]
{	CategoricalFormat cf = null;
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
}
	:	nb_na COMMA nb_nu COMMA nb_categ COMMA 
		OPAREN liste_nom[cf] CPAREN
	;

modifier_name_poss returns [ String mod = LT(1).getText()]
    :   "proba"
    |   "cardinal"
    ;

integer returns [int ret = -1] : i:C_INT { ret = Integer.parseInt(i.getText()); }; 
nb_na returns [int ret = -1] : ret=integer;
nb_nu : num ;
nb_categ returns [int ret = -1] : ret=integer;

liste_nom [CategoricalFormat cf]
	:	elem_nom[cf]
		(COMMA elem_nom[cf])*
	;
	
elem_nom [CategoricalFormat cf]
    :   OPAREN categ_desc[cf] COMMA  categ_frequency CPAREN
    ;
    
categ_desc [CategoricalFormat cf]
{	int cn = -1; String cname = null, cl=null; }
	:	cn=categ_num COMMA cname=categ_name COMMA cl=libel_categ
		{ cf.putCategory(cname, cn-1); cf.setCategoryAttribute(cn-1, "label", cl); }
	;
	
categ_num returns [int ret = -1] : ret = integer;
categ_name returns [String ret = null]
    :   s:C_STRING	{ ret = s.getText(); }
    |   i:IDENT		{ ret = s.getText(); }
    ;

categ_frequency: num;
libel_categ returns [String ret=null] : s:C_STRING { ret = s.getText(); }; 

continue_desc [ int vnum, String type, String st, String vn, String ln, String mod]
{
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
}
	:	nb_na COMMA nb_nu COMMA pmin COMMA pmax
	;

pmin  : num;
pmax  : num;

hierarchies_poss
    :   "HIERARCHIE" EQ OPAREN hierarchies_desc CPAREN
    ;

hierarchies_desc
    :   hierarchie_desc (COMMA hierarchie_desc)*
    ;
hierarchie_desc  : OPAREN var_hier_name COMMA OPAREN hval_ref_list CPAREN CPAREN;

var_hier_name
    :   "VAR" EQ var_num_ref COMMA nb_hier_val OPAREN hier_val_desc CPAREN 
    ;

hier_val_desc
    :   hier_val (COMMA hier_val)*
    |   OPAREN CPAREN
    ;

hier_val
    :   OPAREN  categ_num COMMA categ_name COMMA libel_categ COMMA num  CPAREN
    ;

nb_hier_val returns [int ret = -1] : ret=integer;

var_num_ref returns [int ret = -1] : ret=integer;

hval_ref_list
    :   hval_ref_dec (COMMA hval_ref_dec)*
    ;

hval_ref_dec
    :   categ_num EQ OPAREN hval_ref_list2 CPAREN
    ;

hval_ref_list2
    :   hval_ref (COMMA hval_ref)*
    ;

hval_ref
    :   categ_ref
    |   OPAREN categ_ref_list CPAREN
    | OBRAC num COLON num  CBRAC
    ;

categ_ref_list
	:	categ_ref (COMMA categ_ref)* 
	;

categ_ref returns [int ret = -1] : ret=integer;

num returns [double ret = Double.NaN]
    :   i:C_INT { ret = Integer.parseInt(i.getText()); }
    |   f:C_FLOAT  { ret = Double.parseDouble(f.getText()); } 
    ;

relations_poss
    :   "RULES" EQ OPAREN rel_desc_list CPAREN
    ;

rel_desc_list
    :   rel_desc (COMMA rel_desc)*
    ;

rel_desc
    :   OPAREN prem_desc  conclusion CPAREN
    ;

prem_desc
    :   OPAREN var_num EQ val CPAREN
    ;

conclusion
    :   OPAREN var_num EQ val CPAREN
    ;

mat_rect
    :   "RECTANGLE_MATRIX" EQ OPAREN ligne_desc_list CPAREN
    ;

ligne_desc_list
{
	int row = 0;
}
    :   ligne_desc[row++] (COMMA ligne_desc[row++])*
    ;

ligne_desc [int row]
    :   OPAREN val_desc_list[row] CPAREN
    ;

val_desc_list [int row]
{
	int col = 0;
}
    :   val_desc[row,col++] (COMMA  val_desc[row,col++])*
    ;

val_desc [int row, int col]
{
	currentColumn = columns[col];
	Format f = currentColumn.getFormat();
	if (f != null && f instanceof CategoricalFormat) {
		currentCategories = (CategoricalFormat)f;
	}
	else {
		currentCategories = null;
	}
	Object v = null;
}
	:	v=val	{ setObjectAt(row, v); }
	;

val returns [Object o = null]
{	double n;
}
    :   n=num	{ o = new Double(n); }
    |   OPAREN (o=val_list | o=interval ) CPAREN
    |   o=interval
    |   "NA"
    |   "NU"
    ;

val_list returns [ 
	CategoricalDistribution cd = new CategoricalDistribution(currentCategories)
]
{
	assert(currentCategories != null);
}
    :   valsimple[cd] (COMMA valsimple[cd])*
    ;

interval returns [ Interval i = null ]
{
	double min, max;
}
    :   min=num COLON max=num { i = new DoubleInterval(min,max); }
    ;

valsimple [ CategoricalDistribution cd ]
{
	int cat = -1;
	double v = 1;
}
    :  cat=integer (  OPAREN v=num CPAREN )? { cd.add(cat-1, v); }
    ;

dist_mat_poss : dist_mat ;

dist_matrix
    :   "TRIANGLE_MATRIX"
    |   "DIST_MATRIX"
    ;

dist_mat
    :   dist_matrix EQ OPAREN line_dist_set CPAREN
    ;

line_dist_set
    :   OPAREN line_dist CPAREN 
    	( OPAREN  line_dist CPAREN )*
    ;

line_dist
    :   num ( COMMA num)*
    ;

	
class SODASLexer extends Lexer;
options {
	exportVocab=SODAS;
	k=4;
	charVocabulary = '\3'..'\377';
	testLiterals = false;
}

IDENT
options {
	paraphrase = "a string value";
	testLiterals = true;
}
    :   ( 'a'..'z' | 'A'..'Z' | '_' ) ( 'a'..'z' | 'A'..'Z' | '_' | '.' | '0'..'9' )*
    ;

C_STRING
    :   '"'! (ESC|~'"')* '"'!
	|   '\''! (ESC|~'\'')* '\''!
    ;

protected
DIGIT
options {
  paraphrase = "a digit";
}
	:	'0'..'9'
	;

C_INT
options {
  paraphrase = "an integer value";
}
	:    ('+' | '-')? (DIGIT)+                  // base-10 
             (  '.' (DIGIT)*                      	{$setType(C_FLOAT);}
	         (('e' | 'E') ('+' | '-')? (DIGIT)+)? 
	     |   ('e' | 'E') ('+' | '-')? (DIGIT)+   	{$setType(C_FLOAT);}
             )?
	;

C_FLOAT
options {
  paraphrase = "an floating point value";
}

	:    '.' (DIGIT)+ (('e' | 'E') ('+' | '-')? (DIGIT)+)?
     	;

WS	:	(' '
	|	'\t'
	|	'\n'	{newline();}
	|	'\r')
		{ _ttype = Token.SKIP; }
	;

SL_COMMENT
	: 	"//"	(~'\n')* '\n'
		{ _ttype = Token.SKIP; newline(); }
	;

ML_COMMENT
	:	"/*"
		(	{ LA(2)!='/' }? '*'
		|	'\n' { newline(); }
		|	~('*'|'\n')
		)*
		"*/"
			{ $setType(Token.SKIP); }
	;



protected
ESC
    :   '\\' .
		(	'n'
		|	'N'
		|	'r'
		|	't'
		|	'b'
		|	'f'
		|	'"'
		|	'\n'
		|	'\r'
		|	'\''
		|	'\\'
		)
    ;

OPAREN : '(' ;
CPAREN : ')' ;
EQ : '=' ;
COMMA : ',' ;
OBRAC : '[' ;
CBRAC : ']' ;
COLON : ':' ;
