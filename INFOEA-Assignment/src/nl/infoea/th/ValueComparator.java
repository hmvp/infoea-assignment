/**
 * 
 */
package nl.infoea.th;

import java.util.Comparator;
import java.util.Map;

/**
 * Comparator to sort on the best expected value from Q in adaptive pursuit by
 * comparing on value instead of key.
 * 
 * @author tim
 * 
 * @param <K>
 *            key
 * @param <V>
 *            value
 */
public class ValueComparator<K, V extends Comparable<V>> implements
		Comparator<K> {

	Map<K, V> base;

	public ValueComparator(Map<K, V> base) {
		this.base = base;
	}

	public int compare(K a, K b) {
		return base.get(a).compareTo(base.get(b));
	}
}