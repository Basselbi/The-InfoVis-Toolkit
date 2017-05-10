/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.io;

import infovis.Table;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.znerd.xmlenc.InvalidXMLException;
import org.znerd.xmlenc.LineBreak;
import org.znerd.xmlenc.XMLEncoder;
import org.znerd.xmlenc.XMLEventListenerState;
import org.znerd.xmlenc.XMLOutputter;
import org.znerd.xmlenc.sax.SAXEventReceiver;

/**
 * Abstract writer for XML format.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.16 $
 */
public abstract class AbstractXMLWriter extends AbstractWriter {
//    static char[]                      LF             = {
//            '\n',
//            ' ',
//            ' ',
//            ' ',
//            ' ',
//            ' ',
//            ' ',
//            ' ',
//            ' ',
//            ' '                                      };
//    protected int                      depth;
    private XMLOutputter               outputter;
    private ContentHandler             writer;
    // protected XMLWriter writer;
    protected boolean                  indenting      = true;
    /** Empty attribute. */
    public static final AttributesImpl NULL_ATTRIBUTE = new AttributesImpl();

    /**
     * Constructor for an AbstractXMLWriter
     * 
     * @param out
     *            the Writer
     * @param table
     *            the Table.
     * @param encoding
     *            the stream encoding
     */
    protected AbstractXMLWriter(
            OutputStream out,
            String name,
            Table table,
            String encoding) throws UnsupportedEncodingException {
        super(out, name, table);
        setEncoding(encoding);
        outputter = new XMLOutputter(getWriter(), getEncoding());
        //outputter.setEscaping(true);
        writer = new SAXEventReceiver(outputter);
        if (isIdenting()) {
            outputter.setLineBreak(LineBreak.UNIX);
            outputter.setIndentation(" ");
//            indent();
        }
        // writer = new XMLWriter(getWriter());
    }

    protected AbstractXMLWriter(OutputStream out, String name, Table table) {
        super(out, name, table);
        setEncoding("UTF-8");
        try {
            outputter = new XMLOutputter(getWriter(), getEncoding());
            writer = new SAXEventReceiver(outputter);
            if (isIdenting()) {
                outputter.setLineBreak(LineBreak.UNIX);
                outputter.setIndentation(" ");
//                indent();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unexpected UTF-8 enconding error", e);
        }
    }

    protected AbstractXMLWriter(OutputStream out, Table table) {
        this(out, table.getName(), table);
    }

    /**
     * Produce an indentation.
     */
//    public void indent() {
//        if (!isIdenting())
//            return;
//        int len = Math.min(depth + 1, LF.length);
//        try {
//            writer.ignorableWhitespace(LF, 0, len);
//        } catch (SAXException e) {
//            ; // ignore
//        }
//    }

    /**
     * Returns the identing.
     * 
     * @return boolean
     */
    public boolean isIdenting() {
        return indenting;
    }

    /**
     * Sets the identing.
     * 
     * @param identing
     *            The identing to set
     */
    public void setIdenting(boolean identing) {
        if (indenting == this.indenting)
            return;
        this.indenting = identing;
        if (indenting) {
            outputter.setLineBreak(LineBreak.UNIX);
            outputter.setIndentation(" ");
            //indent();
        }
        else {
            outputter.setLineBreak(LineBreak.NONE);
        }
    }

    /**
     * @param ch
     * @param start
     * @param length
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        writer.characters(ch, start, length);
    }

    /**
     * Send a character string.
     * 
     * @param s
     *            the string
     * @throws SAXException
     */
    public void characters(String s) throws SAXException {
        characters(s.toCharArray(), 0, s.length());
    }

    /**
     * @param uri
     * @param localName
     * @param name
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        writer.endElement(uri, localName, name);
    }

    /**
     * Ends the specified element.
     * @param localName the local name
     * @throws SAXException in case of exception
     */
    public void endElement(String localName) throws SAXException {
        writer.endElement("", localName, localName);
    }

    /**
     * @param prefix
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    public void endPrefixMapping(String prefix) throws SAXException {
        writer.endPrefixMapping(prefix);
    }

    /**
     * @param ch
     * @param start
     * @param length
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        writer.ignorableWhitespace(ch, start, length);
    }

    /**
     * Output writespace.
     * 
     * @param s
     *            the whitespece string
     * @throws SAXException
     */
    public void ignorableWhitespace(String s) throws SAXException {
        ignorableWhitespace(s.toCharArray(), 0, s.length());
    }

    /**
     * @param target
     * @param data
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String,
     *      java.lang.String)
     */
    public void processingInstruction(String target, String data)
            throws SAXException {
        writer.processingInstruction(target, data);
    }

    /**
     * @param locator
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    public void setDocumentLocator(Locator locator) {
        writer.setDocumentLocator(locator);
    }

    /**
     * @param name
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     */
    public void skippedEntity(String name) throws SAXException {
        writer.skippedEntity(name);
    }

    /**
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        writer.startDocument();
    }

    /**
     * @param uri
     * @param localName
     * @param name
     * @param atts
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void emptyElement(
            String uri,
            String localName,
            String name,
            Attributes atts) throws SAXException {
        writer.startElement(uri, localName, name, atts);
        writer.endElement(uri, localName, name);
    }

    /**
     * Writes an empty element.
     * @param localName its name
     * @param atts it attributes
     * @throws SAXException 
     */
    public void emptyElement(String localName, Attributes atts)
            throws SAXException {
        emptyElement("", localName, localName, atts);
    }

    /**
     * Writes an empty element.
     * @param localName its name
     * @throws SAXException 
     */
    public void emptyElement(String localName) throws SAXException {
        emptyElement("", localName, localName, null);
    }

    /**
     * @param uri
     * @param localName
     * @param name
     * @param atts
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(
            String uri,
            String localName,
            String name,
            Attributes atts) throws SAXException {
        if (atts == null) {
            atts = NULL_ATTRIBUTE;
        }
        writer.startElement(uri, localName, name, atts);
    }

    /**
     * Starts a new element
     * @param localName its name
     * @param atts its attributes
     * @throws SAXException
     */
    public void startElement(String localName, Attributes atts)
            throws SAXException {
        startElement("", localName, localName, atts);
    }

    /**
     * Starts a new element
     * @param localName its name
     * @throws SAXException
     */
    public void startElement(String localName) throws SAXException {
        startElement("", localName, localName, null);
    }

    /**
     * @param prefix
     * @param uri
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
     *      java.lang.String)
     */
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        writer.startPrefixMapping(prefix, uri);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     * @throws IOException
     * @see org.znerd.xmlenc.XMLOutputter#attribute(java.lang.String,
     *      java.lang.String)
     */
    public void attribute(String arg0, String arg1)
            throws IllegalStateException, IllegalArgumentException, IOException {
        outputter.attribute(arg0, arg1);
    }

    /**
     * @param arg0
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     * @throws IOException
     * @see org.znerd.xmlenc.XMLOutputter#cdata(java.lang.String)
     */
    public void cdata(String arg0) throws IllegalStateException,
            IllegalArgumentException, IOException {
        outputter.cdata(arg0);
    }

    /**
     * @throws IllegalStateException
     * @throws IOException
     * @see org.znerd.xmlenc.XMLOutputter#close()
     */
    public void close() throws IllegalStateException, IOException {
        outputter.close();
    }

    /**
     * @param arg0
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     * @throws InvalidXMLException
     * @throws IOException
     * @see org.znerd.xmlenc.XMLOutputter#comment(java.lang.String)
     */
    public void comment(String arg0) throws IllegalStateException,
            IllegalArgumentException, InvalidXMLException, IOException {
        outputter.comment(arg0);
    }

    /**
     * @throws IllegalStateException
     * @throws IOException
     * @see org.znerd.xmlenc.XMLOutputter#declaration()
     */
    public void declaration() throws IllegalStateException, IOException {
        outputter.declaration();
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     * @throws InvalidXMLException
     * @throws IOException
     * @see org.znerd.xmlenc.XMLOutputter#dtd(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void dtd(String arg0, String arg1, String arg2)
            throws IllegalStateException, IllegalArgumentException,
            IOException {
        outputter.dtd(arg0, arg1, arg2);
    }

    /**
     * @throws IllegalStateException
     * @throws IOException
     * @see org.znerd.xmlenc.XMLOutputter#endDocument()
     */
    public void endDocument() throws IllegalStateException, IOException {
        outputter.endDocument();
    }

    /**
     * @throws IllegalStateException
     * @throws IOException
     * @see org.znerd.xmlenc.XMLOutputter#endTag()
     */
    public void endTag() throws IllegalStateException, IOException {
        outputter.endTag();
    }

    /**
     * @return the element stack
     * @see org.znerd.xmlenc.XMLOutputter#getElementStack()
     */
    public String[] getElementStack() {
        return outputter.getElementStack();
    }

    /**
     * @return the element stack capacity
     * @see org.znerd.xmlenc.XMLOutputter#getElementStackCapacity()
     */
    public int getElementStackCapacity() {
        return outputter.getElementStackCapacity();
    }

    /**
     * @return the element stack size
     * @see org.znerd.xmlenc.XMLOutputter#getElementStackSize()
     */
    public int getElementStackSize() {
        return outputter.getElementStackSize();
    }

    /**
     * @return the indentation
     * @see org.znerd.xmlenc.XMLOutputter#getIndentation()
     */
    public String getIndentation() {
        return outputter.getIndentation();
    }

    /**
     * @return the line break
     * @see org.znerd.xmlenc.XMLOutputter#getLineBreak()
     */
    public LineBreak getLineBreak() {
        return outputter.getLineBreak();
    }

    /**
     * @return the quotation mark
     * @see org.znerd.xmlenc.XMLOutputter#getQuotationMark()
     */
    public char getQuotationMark() {
        return outputter.getQuotationMark();
    }

    /**
     * @return the state
     * @see org.znerd.xmlenc.XMLOutputter#getState()
     */
    public XMLEventListenerState getState() {
        return outputter.getState();
    }

    /**
     * @return true if the outputter is escaping
     * @see org.znerd.xmlenc.XMLOutputter#isEscaping()
     */
    public boolean isEscaping() {
        return outputter.isEscaping();
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     * @throws IndexOutOfBoundsException
     * @throws InvalidXMLException
     * @throws IOException
     * @see org.znerd.xmlenc.XMLOutputter#pcdata(char[], int, int)
     */
    public void pcdata(char[] arg0, int arg1, int arg2)
            throws IllegalStateException, IllegalArgumentException,
            IndexOutOfBoundsException, InvalidXMLException, IOException {
        outputter.pcdata(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     * @throws InvalidXMLException
     * @throws IOException
     * @see org.znerd.xmlenc.XMLOutputter#pcdata(java.lang.String)
     */
    public void pcdata(String arg0) throws IllegalStateException,
            IllegalArgumentException, InvalidXMLException, IOException {
        outputter.pcdata(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     * @throws IOException
     * @see org.znerd.xmlenc.XMLOutputter#pi(java.lang.String, java.lang.String)
     */
    public void pi(String arg0, String arg1) throws IllegalStateException,
            IllegalArgumentException, IOException {
        outputter.pi(arg0, arg1);
    }

    /**
     * 
     * @see org.znerd.xmlenc.XMLOutputter#reset()
     */
    public void reset() {
        outputter.reset();
    }

    /**
     * @param arg0
     * @param arg1
     * @throws IllegalArgumentException
     * @throws UnsupportedEncodingException
     * @see org.znerd.xmlenc.XMLOutputter#reset(java.io.Writer,
     *      java.lang.String)
     */
    public void reset(Writer arg0, String arg1)
            throws IllegalArgumentException, UnsupportedEncodingException {
        outputter.reset(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws IllegalArgumentException
     * @throws UnsupportedEncodingException
     * @see org.znerd.xmlenc.XMLOutputter#reset(java.io.Writer,
     *      org.znerd.xmlenc.XMLEncoder)
     */
    public void reset(Writer arg0, XMLEncoder arg1)
            throws IllegalArgumentException, UnsupportedEncodingException {
        outputter.reset(arg0, arg1);
    }

    /**
     * @param arg0
     * @throws IllegalArgumentException
     * @throws OutOfMemoryError
     * @see org.znerd.xmlenc.XMLOutputter#setElementStackCapacity(int)
     */
    public void setElementStackCapacity(int arg0)
            throws IllegalArgumentException, OutOfMemoryError {
        outputter.setElementStackCapacity(arg0);
    }

    /**
     * @param arg0
     * @see org.znerd.xmlenc.XMLOutputter#setEscaping(boolean)
     */
    public void setEscaping(boolean arg0) {
        outputter.setEscaping(arg0);
    }

    /**
     * @param arg0
     * @throws IllegalStateException
     * @see org.znerd.xmlenc.XMLOutputter#setIndentation(java.lang.String)
     */
    public void setIndentation(String arg0) throws IllegalStateException {
        outputter.setIndentation(arg0);
    }

    /**
     * @param arg0
     * @see org.znerd.xmlenc.XMLOutputter#setLineBreak(org.znerd.xmlenc.LineBreak)
     */
    public void setLineBreak(LineBreak arg0) {
        outputter.setLineBreak(arg0);
    }

    /**
     * @param arg0
     * @throws IllegalArgumentException
     * @see org.znerd.xmlenc.XMLOutputter#setQuotationMark(char)
     */
    public void setQuotationMark(char arg0) throws IllegalArgumentException {
        outputter.setQuotationMark(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws IllegalArgumentException
     * @see org.znerd.xmlenc.XMLOutputter#setState(org.znerd.xmlenc.XMLEventListenerState,
     *      java.lang.String[])
     */
    public void setState(XMLEventListenerState arg0, String[] arg1)
            throws IllegalArgumentException {
        outputter.setState(arg0, arg1);
    }

    /**
     * @param arg0
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     * @throws IOException
     * @see org.znerd.xmlenc.XMLOutputter#startTag(java.lang.String)
     */
    public void startTag(String arg0) throws IllegalStateException,
            IllegalArgumentException, IOException {
        outputter.startTag(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     * @throws IndexOutOfBoundsException
     * @throws InvalidXMLException
     * @throws IOException
     * @see org.znerd.xmlenc.XMLOutputter#whitespace(char[], int, int)
     */
    public void whitespace(char[] arg0, int arg1, int arg2)
            throws IllegalStateException, IllegalArgumentException,
            IndexOutOfBoundsException, InvalidXMLException, IOException {
        outputter.whitespace(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     * @throws InvalidXMLException
     * @throws IOException
     * @see org.znerd.xmlenc.XMLOutputter#whitespace(java.lang.String)
     */
    public void whitespace(String arg0) throws IllegalStateException,
            IllegalArgumentException, InvalidXMLException, IOException {
        outputter.whitespace(arg0);
    }

}
