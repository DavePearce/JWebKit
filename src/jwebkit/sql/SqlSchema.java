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

	public int size() {
		return columns.length;
	}
	
	/**
	 * Construct a new row from this schema based on one or more values.
	 * 
	 * @param values
	 * @return
	 */
	public abstract T constructRow(Object...values);
	
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
