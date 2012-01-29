/**
 * 
 */
package nl.infoea.th;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author hiram
 *
 */
public class ValueSortedTreeMap<T, V extends Comparable<V>> extends TreeMap<T, V> {
	
	private static final long serialVersionUID = 4460933686921087483L;

	public ValueSortedTreeMap(Map<T, V> map)
	{
		super(new ValueComparator<T, V>(map));
		putAll(map);
	}
}
