package nl.infoea.th;

import java.util.Comparator;
import java.util.Map;

class ValueComparator<K> implements Comparator<K> {

	Map<K, Double> base;

	public ValueComparator(Map<K, Double> base) {
		this.base = base;
	}

	public int compare(K a, K b) {

		if (base.get(a) < base.get(b)) {
			return 1;
		} else if (base.get(a) == base.get(b)) {
			return 0;
		} else {
			return -1;
		}
	}
}
