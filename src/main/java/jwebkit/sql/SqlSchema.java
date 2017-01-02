package jwebkit.sql;

public abstract class SqlSchema<T extends SqlRow> {
	/**
	 * Identifiers columns defined in the schema
	 */
	private final Column[] columns;

	public SqlSchema(Column...columns) {
		this.columns = columns;
	}

	/**
	 * Get the column schema for a given numbered column.
	 *
	 * @param index
	 * @return
	 */
	public Column getColumn(int index) {
		return columns[index];
	}

	/**
	 * Get the column schema for a given named column.
	 *
	 * @param name
	 *            --- Name of column schema to locate
	 * @return
	 */
	public Column getColumn(String name) {
		for (int i = 0; i != columns.length; ++i) {
			Column c = columns[i];
			if (c.getName().equals(name)) {
				return c;
			}
		}
		throw new IllegalArgumentException("Invalid column - " + name);
	}

	/**
	 * Get the number of columns defined by this schema.
	 *
	 * @return
	 */
	public int size() {
		return columns.length;
	}

	/**
	 * Check whether a given row object is an instance of this schema. This
	 * checks that the required number of fields are present, and that each is
	 * an appropriate instance of the corresponding field type.
	 *
	 * @param row
	 * @return
	 */
	public boolean isInstance(SqlRow row) {
		if (row.size() != columns.length) {
			return false;
		} else {
			// Check that each value is an instance of its corresponding column
			// type.
			for (int i = 0; i != columns.length; ++i) {
				SqlValue value = row.get(i);
				SqlType type = columns[i].type;
				if (!type.isInstance(value)) {
					return false;
				}
			}
			//
			return true;
		}
	}

	/**
	 * Construct a new row from this schema based on one or more values.
	 *
	 * @param values
	 * @return
	 */
	public abstract T construct(Object...values);

	/**
	 * Represents a column in the table. This determines the column's name and
	 * type, and essentially defines part of the table's schema.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Column {
		private final String name;
		private final SqlType type;

		public Column(String name, SqlType type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public SqlType getType() {
			return type;
		}
	}
}
