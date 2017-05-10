// $ANTLR : "GraphEd.g" -> "GraphEdParser.java"$

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

public class GraphEdParser extends antlr.LLkParser       implements GraphEdParserTokenTypes
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

protected GraphEdParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public GraphEdParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected GraphEdParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public GraphEdParser(TokenStream lexer) {
  this(lexer,2);
}

public GraphEdParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final void graph() throws RecognitionException, TokenStreamException {
		
			String name = null;
		
		
		try {      // for error handling
			match(LITERAL_GRAPH);
			{
			switch ( LA(1)) {
			case STRING:
			{
				name=label();
				graph.setName(name);
				break;
			}
			case EQUALS:
			case LITERAL_END:
			case NUMBER:
			case LITERAL_DIRECTED:
			case LITERAL_UNDIRECTED:
			case START_DELIM:
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
			case EQUALS:
			{
				match(EQUALS);
				break;
			}
			case LITERAL_END:
			case NUMBER:
			case LITERAL_DIRECTED:
			case LITERAL_UNDIRECTED:
			case START_DELIM:
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
			case LITERAL_DIRECTED:
			case LITERAL_UNDIRECTED:
			{
				directed();
				break;
			}
			case LITERAL_END:
			case NUMBER:
			case START_DELIM:
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
			_loop301:
			do {
				if ((LA(1)==START_DELIM)) {
					graphAttributes();
				}
				else {
					break _loop301;
				}
				
			} while (true);
			}
			{
			_loop303:
			do {
				if ((LA(1)==NUMBER)) {
					node();
				}
				else {
					break _loop303;
				}
				
			} while (true);
			}
			match(LITERAL_END);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
	}
	
	public final String  label() throws RecognitionException, TokenStreamException {
		String ret;
		
		Token  s = null;
			ret = null;
		
		try {      // for error handling
			s = LT(1);
			match(STRING);
			ret=s.getText();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		return ret;
	}
	
	public final void directed() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_DIRECTED:
			{
				match(LITERAL_DIRECTED);
				break;
			}
			case LITERAL_UNDIRECTED:
			{
				match(LITERAL_UNDIRECTED);
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
	}
	
	public final void graphAttributes() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			attributes();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
	}
	
	public final void node() throws RecognitionException, TokenStreamException {
		
		int n = -1;
		String name = null;
		
		try {      // for error handling
			n=number();
			startNode(n);
			{
			_loop306:
			do {
				if ((LA(1)==START_DELIM)) {
					nodeAttributes();
				}
				else {
					break _loop306;
				}
				
			} while (true);
			}
			{
			switch ( LA(1)) {
			case STRING:
			{
				name=label();
				setNodeLabel(name);
				break;
			}
			case SEMI:
			case NUMBER:
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
			_loop309:
			do {
				if ((LA(1)==NUMBER)) {
					edge();
				}
				else {
					break _loop309;
				}
				
			} while (true);
			}
			match(SEMI);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final int  number() throws RecognitionException, TokenStreamException {
		int n;
		
		Token  v = null;
			n = -1;
		
		try {      // for error handling
			v = LT(1);
			match(NUMBER);
			n=Integer.parseInt(v.getText());
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
		return n;
	}
	
	public final void nodeAttributes() throws RecognitionException, TokenStreamException {
		
			ArrayList a = null;
		
		try {      // for error handling
			match(START_DELIM);
			{
			_loop328:
			do {
				if ((LA(1)==ID)) {
					a=attr();
					addNodeAttribute(a);
				}
				else {
					break _loop328;
				}
				
			} while (true);
			}
			match(END_DELIM);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
	}
	
	public final void edge() throws RecognitionException, TokenStreamException {
		
		int n = -1;
		String name = null;
		
		try {      // for error handling
			n=number();
			addEdge(n);
			{
			_loop312:
			do {
				if ((LA(1)==START_DELIM)) {
					edgeAttributes();
				}
				else {
					break _loop312;
				}
				
			} while (true);
			}
			{
			switch ( LA(1)) {
			case STRING:
			{
				name=label();
				setEdgeLabel(name);
				break;
			}
			case SEMI:
			case NUMBER:
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
			recover(ex,_tokenSet_5);
		}
	}
	
	public final void edgeAttributes() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			attributes();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
	}
	
	public final void attributes() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(START_DELIM);
			{
			_loop319:
			do {
				if ((LA(1)==ID)) {
					attr();
				}
				else {
					break _loop319;
				}
				
			} while (true);
			}
			match(END_DELIM);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
	}
	
	public final ArrayList  attr() throws RecognitionException, TokenStreamException {
		ArrayList e;
		
		Token  k = null;
		
			e = new ArrayList();
			Object o = null;
		
		
		try {      // for error handling
			k = LT(1);
			match(ID);
			e.add(k.getText());
			{
			_loop322:
			do {
				if ((LA(1)==NUMBER||LA(1)==STRING||LA(1)==LBRACK)) {
					o=value();
					e.add(o);
				}
				else {
					break _loop322;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		return e;
	}
	
	public final Object  value() throws RecognitionException, TokenStreamException {
		Object o;
		
		Token  s = null;
		Token  n = null;
		
			o = null;
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case STRING:
			{
				s = LT(1);
				match(STRING);
				o = s.getText();
				break;
			}
			case NUMBER:
			{
				n = LT(1);
				match(NUMBER);
				o =  Float.valueOf(n.getText());
				break;
			}
			case LBRACK:
			{
				match(LBRACK);
				{
				_loop325:
				do {
					if ((LA(1)==ID)) {
						o=attr();
					}
					else {
						break _loop325;
					}
					
				} while (true);
				}
				match(RBRACK);
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
		return o;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"GRAPH\"",
		"EQUALS",
		"\"END\"",
		"SEMI",
		"NUMBER",
		"\"DIRECTED\"",
		"\"UNDIRECTED\"",
		"STRING",
		"START_DELIM",
		"END_DELIM",
		"ID",
		"LBRACK",
		"RBRACK",
		"WS",
		"ESC",
		"FLOAT",
		"DIGIT",
		"ML_COMMENT"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 6112L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 4416L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 320L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 6528L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 384L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 6592L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 90112L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 125184L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	
	}
