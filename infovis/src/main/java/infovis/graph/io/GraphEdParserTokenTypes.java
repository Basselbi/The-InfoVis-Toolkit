// $ANTLR : "GraphEd.g" -> "GraphEdLexer.java"$

/*****************************************************************************
 * Copyright (C) 2003-2006 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/

package infovis.graph.io;
/**
 * Parser and Lexer for the GraphEd Graph format.
 *
 * <p>see <a href="http://citeseer.ist.psu.edu/140899.html">An Interchange File Format for Graphs
 * by  Michael Himsolt</a>.</p>
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */

public interface GraphEdParserTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int LITERAL_GRAPH = 4;
	int EQUALS = 5;
	int LITERAL_END = 6;
	int SEMI = 7;
	int NUMBER = 8;
	int LITERAL_DIRECTED = 9;
	int LITERAL_UNDIRECTED = 10;
	int STRING = 11;
	int START_DELIM = 12;
	int END_DELIM = 13;
	int ID = 14;
	int LBRACK = 15;
	int RBRACK = 16;
	int WS = 17;
	int ESC = 18;
	int FLOAT = 19;
	int DIGIT = 20;
	int ML_COMMENT = 21;
}
