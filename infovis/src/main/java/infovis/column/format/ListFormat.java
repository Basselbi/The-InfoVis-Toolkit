package infovis.column.format;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * <b>ListFormat</b> is a formater for list columns.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class ListFormat extends Format {
    /**
     * Singleton
     */
    static public final ListFormat INSTANCE = new ListFormat();

    /**
     * {@inheritDoc}
     */
    public Object parseObject(String source, ParsePosition pos) {
        pos.setIndex(source.length());
        return source.split(";");
    }

    /**
     * {@inheritDoc}
     */
    public StringBuffer format(
            Object obj,
            StringBuffer toAppendTo,
            FieldPosition pos) {
        String[] s = (String[]) obj;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length; i++) {
            if (i != 0) {
                sb.append(';');
            }
            sb.append(s[i]);
        }

        toAppendTo.append(sb.toString());
        return toAppendTo;
    }
}
