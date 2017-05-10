header {
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
}

{


import infovis.Column;
import infovis.column.IntColumn;
import infovis.Graph;
import infovis.column.StringColumn;
import infovis.io.WrongFormatException;
import infovis.utils.RowIterator;
import cern.colt.map.OpenIntIntHashMap;
import java.util.ArrayList;
import java.awt.geom.Rectangle2D;

import org.apache.log4j.Logger;

/**
 * Parser for the GraphEd Graph format.
 *
 * @author Jean-Daniel Fekete
 */
}	
class GraphEdParser extends Parser;
options {
	k = 2;
}
{
	private static final Logger      LOG = Logger.getLogger(GraphEdParser.class);
	Graph graph;
	OpenIntIntHashMap nodeVertexMap;
	int node;
	int vertex;
	int edge;
	AbstractGraphReader graphReader;
    String attributePrefix = "#ge_";
	
	protected void setGraph(Graph graph) {
		this.graph = graph;
		this.nodeVertexMap = new OpenIntIntHashMap();
		// Initialize the map with vertex ids when there are vertices
		for (RowIterator iter = graph.getVertexTable().iterator();
			iter.hasNext(); ) {
			int v = iter.nextRow();
			nodeVertexMap.put(v+1, v); // node indices start at 1
		}
	}
	
	public AbstractGraphReader getGraphReader() {
		return graphReader;
	}
	
	public void setGraphReader(AbstractGraphReader reader) {
		graphReader = reader;
		setGraph(reader.getGraph());
	}
	
	int findNode(int node) {
		int vertex = -1;
		if (nodeVertexMap.containsKey(node)) {
			vertex = nodeVertexMap.get(node);
		}
		else {
			vertex = graph.addVertex();
			nodeVertexMap.put(node, vertex);
		}
		return vertex;
	}
	
	protected void startNode(int n) {
		node = n;
		vertex = findNode(node);
	}
	
	void setNodeLabel(String name) {
		if (name.length()==0) return;
		Column nameColumn = graph.getVertexTable().getColumn("name");
		if (nameColumn == null) {
			nameColumn = new StringColumn("name");
			graph.getVertexTable().addColumn(nameColumn);
		}
		nameColumn.setValueOrNullAt(vertex, name);
	}
	
	void addNodeAttribute(ArrayList attr) {
		String key = (String)attr.get(0);
		if (key.equals("NodeSize") || key.equals("NS")) {
			addNodeSize(attr);
		}
		else if (key.equals("NodePosition") || key.equals("NP")) {
			addNodePosition(attr);
		}
		else {
			Column c = graph.getVertexTable().getColumn(key);
			if (c == null) {
				c = new StringColumn(key);
				graph.getVertexTable().addColumn(c);
			}
			String value = null;
			if (attr.size()==2) {
				value = attr.get(1).toString();
			}
			else if (attr.size() > 2) {
				StringBuffer sb = new StringBuffer();
				sb.append(attr.get(1).toString());
				for (int i = 2; i < attr.size(); i++) {
					sb.append(' ');
					sb.append(attr.get(i).toString());
				}
				value = sb.toString();
			}
			c.setValueOrNullAt(vertex, value);
		}
	}
	
	void addNodePosition(ArrayList attr) {
		if (attr.size() != 3
			|| ! (attr.get(1) instanceof Number)
			|| ! (attr.get(2) instanceof Number)) {
			LOG.warn("Invalid value for node position: "
				+node);
			return;
		}
		Rectangle2D.Float s = (Rectangle2D.Float)graphReader.findNodeShape(vertex);
		s.x = ((Number)attr.get(1)).floatValue();
		s.y = ((Number)attr.get(2)).floatValue();
	}
	
	void addNodeSize(ArrayList attr) {
		if (attr.size() != 3
			|| ! (attr.get(1) instanceof Number)
			|| ! (attr.get(2) instanceof Number)) {
			LOG.warn("Invalid value for node size: "
				+node);
			return;
		}
		Rectangle2D.Float s = (Rectangle2D.Float)graphReader.findNodeShape(vertex);
		s.width = ((Number)attr.get(1)).floatValue();
		s.height = ((Number)attr.get(2)).floatValue();

	}
	
	void addEdge(int node) {
		int v2 = findNode(node);
		edge = graph.getEdge(vertex, v2);
		if (edge == Graph.NIL) {
			edge = graph.addEdge(vertex, v2);
		}
	}

	void setEdgeLabel(String name) {
		Column nameColumn = graph.getEdgeTable().getColumn("name");
		if (nameColumn == null) {
			nameColumn = new StringColumn("name");
			graph.getEdgeTable().addColumn(nameColumn);
		}
		nameColumn.setValueOrNullAt(edge, name);
	}

    /** Parser error-reporting function can be overridden in subclass */
    public void reportError(RecognitionException ex) {
		throw new RuntimeException(ex);
    }

    /** Parser error-reporting function can be overridden in subclass */
    public void reportError(String s) {
        if (getFilename() == null) {
        	throw new RuntimeException(s);
        }
        else {
        	throw new RuntimeException(getFilename()+s);
        }
    }
}

graph
{	String name = null;
}
	:	"GRAPH"
		(name=label	{ graph.setName(name);})? (EQUALS)? (directed)?
		(graphAttributes)*
		(node)*
		"END"
	;
	
node
{ int n = -1;
  String name = null; }
	:	n=number	{ startNode(n); }
		(nodeAttributes)*
		(name=label		{ setNodeLabel(name); })?
		(edge)*
		SEMI
	;

edge
{ int n = -1;
  String name = null; }
	:	n=number			{ addEdge(n); }
		(edgeAttributes)*
		(name=label			{setEdgeLabel(name); })?
	;

number returns [int n]
{	n = -1; }
	:	v:NUMBER	{ n=Integer.parseInt(v.getText()); }
	;

directed
	:	"DIRECTED"		//{ graph.setDirected(true); }
	|	"UNDIRECTED"	//{ graph.setDirected(false); }
	;

label returns [String ret]
{	ret = null; }
	:	s:STRING	{ ret=s.getText(); }
	;

attributes 
	:	START_DELIM (attr)* END_DELIM
	;

attr returns [ArrayList e]
{
	e = new ArrayList();
	Object o = null;
}
	:	k:ID	{ e.add(k.getText()); }
		(o=value	{ e.add(o); })*
	;

value returns [Object o]
{
	o = null;
}
	:	s:STRING	{ o = s.getText(); }
	|	n:NUMBER	{ o =  Float.valueOf(n.getText()); }
	|	LBRACK (o=attr)* RBRACK
	;

nodeAttributes
{	ArrayList a = null; }
	:	START_DELIM 
		(a=attr	{ addNodeAttribute(a);} )*
		END_DELIM
	;

edgeAttributes
	:	attributes
	;

graphAttributes
	:	attributes
	;

/**
 * Lexer for the GraphEd Graph format.
 *
 * @author Jean-Daniel Fekete
 */
class GraphEdLexer extends Lexer;
options {
	k=4;
	charVocabulary = '\3'..'\377';
	testLiterals = true;
}

WS	:	(' '
	|	'\t'
	|	'\n'	{newline();}
	|	'\r')
		{ _ttype = Token.SKIP; }
	;

STRING	:	'"'! (ESC|~'"')* '"'! ;

protected
ESC
    :   '\\'
		(	'n'
		|	'N'
		|	'r'
		|	't'
		|	'b'
		|	'f'
		|	'l'
		|	'"'
		|	'\n'	{newline();}
		|	'\r'
		|	'\''
		|	'\\'
		|	'$'
		)
    ;

ID
options {
	testLiterals = true;
}
 	:	('a'..'z'|'A'..'Z'|'_'|':')
 		('a'..'z'|'A'..'Z'|'_'|':'|'0'..'9')*
 	;

NUMBER
	:    ('+' | '-')? (DIGIT)+                  // base-10 
             (  '.' (DIGIT)*                      	{$setType(FLOAT);}
	         (('e' | 'E') ('+' | '-')? (DIGIT)+)? 
	     |   ('e' | 'E') ('+' | '-')? (DIGIT)+   	{$setType(FLOAT);}
             )?
	;

FLOAT
	:    '.' (DIGIT)+ (('e' | 'E') ('+' | '-')? (DIGIT)+)?
     	;

protected
DIGIT
	:	'0'..'9'
	;

SEMI	:	';'	;
LBRACK	:	'['	;
RBRACK	:	']'	;
EQUALS	:	'='	;
START_DELIM	:	"{$"	;
END_DELIM	:	"$}"	;

ML_COMMENT
	:	'{' (~'$')
		(ESC|~'}')*
		'}'
			{ $setType(Token.SKIP); }
	;
