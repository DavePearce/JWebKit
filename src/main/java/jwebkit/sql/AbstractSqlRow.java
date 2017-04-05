package jwebkit.sql;

public abstract class AbstractSqlRow implements SqlRow {
	private final SqlValue[] items;

	public AbstractSqlRow(SqlValue... items) {
		this.items = items;
	}

	@Override
	public int size() {
		return items.length;
	}

	@Override
	public SqlValue get(int i) {
		return items[i];
	}

	@Override
	public boolean equals(Object r) {
		if(r.getClass() != getClass()) {
			return false;
		}
		SqlRow ro = (SqlRow) r;
		//
		if(items.length != ro.size()) {
			return false;
		}
		for (int i = 0; i != items.length; ++i) {
			if (!items[i].equals(ro.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		//
		int hc = items[0].hashCode();
		//
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
