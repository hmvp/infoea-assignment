/**
 * 
 */
package nl.infoea.th;

import java.util.Comparator;
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

class ValueComparator<K, V extends Comparable<V>> implements Comparator<K> {

	Map<K, V> base;

	public ValueComparator(Map<K, V> base) {
		this.base = base;
	}

	public int compare(K a, K b) {
		return base.get(a).compareTo(base.get(b));
	}
}
