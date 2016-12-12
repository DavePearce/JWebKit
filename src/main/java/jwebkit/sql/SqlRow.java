package jwebkit.sql;

/**
 * Represents a single row of data in an SqlTable. The intention is that
 * this class be extended in order to provide a nicer user interface. Note
 * that instances of this class and any subclasses should always be immutable.
 *
 * @author David J. Pearce
 */
public class SqlRow {
	private Object[] items;

	public SqlRow(Object[] items) {
		this.items = items;
		// TODO: sanity check kinds of permitted items
	}

	public Object get(int i) {
		return items[i];
	}

	public int size() {
		return items.length;
	}

	@Override
	public boolean equals(Object r) {
		if(r.getClass() != getClass()) {
			return false;
		}
		SqlRow ro = (SqlRow) r;
		if(items.length != ro.items.length) {
			return false;
		}
		for(int i=0;i!=items.length;++i) {
			// FIXME: need to properly compare elements here I think. For
			// example, arrays need to be compared with Arrays.equals().
			if(!items[i].equals(ro.items.length)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hc = items[0].hashCode();

		for (int i = 1; i < items.length; ++i) {
			hc ^= items[i].hashCode();
		}
		return hc;
	}

	@Override
	public String toString() {
		String r = "";
		for (int i = 0; i != items.length; ++i) {
			if (i != 0) {
				r = r + ",";
			}
			r = r + toString(items[i]);
		}
		return "(" + r + ")";
	}

	/**
	 * Convert an object from a ResultSet into a human-readable string.
	 *
	 * @param o
	 * @return
	 */
	private static String toString(Object o) {
		if (o instanceof Integer) {
			return o.toString();
		} else if (o instanceof String) {
			return "\"" + o.toString() + "\"";
		} else {
			return o.toString();
		}
	}
}
