package infovis.table.io;

import java.util.Enumeration;

import infovis.Table;

import com.thoughtworks.xstream.XStream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.AttributeMapper;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * A definition class for defining XStream mappings
 * 
 * @version $Revision: 1.2 $
 * @author Elie Naulleau
 */
public class XMLTableXStreamMapping {

	private XMLTableXStreamMapping() {
	}

	public static void loadMap(XStream xstream) {

		// xstream.alias("person", Person.class);
		// xstream.addImplicitCollection(Blog.class, "entries");
		// xstream.useAttributeFor(Blog.class, "author");
		// xstream.registerConverter(new AuthorConverter());

		xstream.alias("DefaultTable", infovis.table.DefaultTable.class);
		xstream.alias("Table", infovis.Table.class);
		xstream.alias("DynamicTable", infovis.DynamicTable.class);
		xstream.alias("DefaultDynamicTable",
				infovis.table.DefaultDynamicTable.class);
		xstream.alias("Column", infovis.Column.class);
		xstream.alias("Metadata", infovis.Metadata.class);

		xstream.alias("DistributionFormat",
				infovis.column.format.DistributionFormat.class);
		xstream.alias("Distribution", infovis.data.Distribution.class);
		xstream.alias("DefaultDistribution",
				infovis.data.DefaultDistribution.class);
		xstream.alias("DefaultDistributionColumn",
				infovis.data.DefaultDistributionColumn.class);

		xstream.alias("DoubleDistribution",
				infovis.data.DoubleDistribution.class);
		xstream.alias("Logical", infovis.data.Logical.class);
		xstream.alias("PrimitiveSet", infovis.data.PrimitiveSet.class);
		xstream.alias("SymbolicValue", infovis.data.SymbolicValue.class);
		xstream.alias("DefaultValueSet", infovis.data.DefaultValueSet.class);
		xstream.alias("ValueSet", infovis.data.ValueSet.class);

		xstream.alias("CategoricalDistribution",
				infovis.data.CategoricalDistribution.class);
		xstream.alias("CategoricalDistributionColumn",
				infovis.data.CategoricalDistributionColumn.class);

		xstream.alias("StringFormat", infovis.column.format.StringFormat.class);
		xstream.alias("StringColumn", infovis.column.StringColumn.class);
		xstream.alias("Interval", infovis.data.Interval.class);
		xstream.alias("IntervalColumn", infovis.data.IntervalColumn.class);
		xstream.alias("IntervalFormat",
				infovis.column.format.IntervalFormat.class);
		xstream.alias("CategoricalFormat",
				infovis.column.format.CategoricalFormat.class);
		xstream.alias("BasicColumn", infovis.column.BasicColumn.class);
		xstream.alias("CategoricalColumn",
				infovis.column.CategoricalColumn.class);

		xstream.alias("DoubleInterval", infovis.data.DoubleInterval.class);

		xstream.alias("CaseInsensitiveComparator",
				infovis.utils.CaseInsensitiveComparator.class);

		
		
		// More mappings to come later
		// ...
		
		
		
		// This one requires a specific converter, otherwise, it'll be hard to
		// xpath meaningfully within it.
		// Quoique, en utilisant les opérateurs "siblings", on a la clef, et la
		// valeur est le frère immédiat,
		// reste qu'il y a risque de confusion entre clef et valeur.

		xstream.registerConverter(new SimpleAttributeSetConverter(xstream
				.getMapper()));
		xstream.alias("SimpleAttributeSet",
				javax.swing.text.SimpleAttributeSet.class);
	}

	static class SimpleAttributeSetConverter extends
			AbstractCollectionConverter {

		public SimpleAttributeSetConverter(Mapper m) {
			super(m);
		}

		public boolean canConvert(Class clazz) {
			return clazz.equals(javax.swing.text.SimpleAttributeSet.class);
		}

		public void marshal(Object value, HierarchicalStreamWriter writer,
				MarshallingContext context) {

			javax.swing.text.SimpleAttributeSet sas = (javax.swing.text.SimpleAttributeSet) value;

			Enumeration en = sas.getAttributeNames();
			while (en.hasMoreElements()) {
				Object key = en.nextElement();
				writer.startNode("entry");
				writer.startNode("key");
				writeItem(key, context, writer);
				writer.endNode();
				writer.startNode("value");
				writeItem(sas.getAttribute(key), context, writer);
				writer.endNode();
				writer.endNode();
			}
		}

		public Object unmarshal(HierarchicalStreamReader reader,
				UnmarshallingContext context) {
			
			javax.swing.text.SimpleAttributeSet sas = new javax.swing.text.SimpleAttributeSet();
			populateMap(reader, context, sas);
			//System.out.println("SimpleAttributeSet: "+sas.toString());
			return sas;
		}
		
		 protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, javax.swing.text.SimpleAttributeSet map) {
		        while (reader.hasMoreChildren()) {
		        	reader.moveDown();
		            reader.moveDown();
		            reader.moveDown();
		            Object key = readItem(reader, context, map);
		            reader.moveUp();
		            reader.moveUp();
		            reader.moveDown();
		            reader.moveDown();
		            Object value = readItem(reader, context, map);
		            map.addAttribute(key, value);
		            reader.moveUp();
		            reader.moveUp();
		            reader.moveUp();		            
		        }
		    }
	}
}
