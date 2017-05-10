package infovis.column.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import infovis.Column;
import infovis.column.ColumnFilter;

/**
 * This class maintains a set of column names to be filtered out. It allows 
 * for undoable column name filtering operations, which cannot be achieved 
 * through OR based filter composition.
 * 
 * @author Mohammad Ghoniem
 *
 */
public class NamesCollection implements ColumnFilter {

	protected Set<String> excluded = new HashSet<String>();
	
	public NamesCollection(){
	}	

	public NamesCollection(Collection<String> names){
		excluded.addAll(names);
	}	
	
	public void append(String name){
		excluded.add(name);
	}
	
	public void append(Column col){
		this.append(col.getName());
	}
	
	public void remove(String name){
		excluded.remove(name);
	}

	public void remove(Column col){
		this.remove(col.getName());
	}

	public boolean filter(Column column) {
		return excluded.contains(column.getName());
	}

}
