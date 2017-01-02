package jwebkit.sql;

import java.sql.SQLException;

/**
 * Respresents an SQL table as accessed via JDBC. The purpose of this class
 * is to simplify the process of communicating with the table.
 *
 * @author David J. Pearce
 *
 */
public abstract class SqlTable<T extends SqlRow> {
	/**
	 * Parent reference
	 */
	private SqlDatabase database;

	/**
	 * The table name
	 */
	private final String name;

	/**
	 * The schema for this table
	 */
	private final Column[] schema;

	public SqlTable(SqlDatabase db, String name, Column... schema) {
		this.database = db;
		this.name = name;
		this.schema = schema;
		// Bind this table to the given database
		db.bind(this);
	}

	public SqlDatabase getDatabase() {
		return database;
	}

	public String getName() {
		return name;
	}

	/**
	 * Get the column schema for a given numbered column.
	 *
	 * @param index
	 * @return
	 */
	public Column getColumn(int index) {
		return schema[index];
	}

	/**
	 * Get the column schema for a given named column.
	 *
	 * @param name
	 *            --- Name of column schema to locate
	 * @return
	 */
	public Column getColumn(String name) {
		for (int i = 0; i != schema.length; ++i) {
			Column c = schema[i];
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
		return schema.length;
	}

	/**
	 * Check whether a given row object is an instance of the schema for this
	 * table. This checks that the required number of fields are present, and
	 * that each is an appropriate instance of the corresponding field type.
	 *
	 * @param row
	 * @return
	 */
	public boolean isInstance(SqlRow row) {
		if (row.size() != schema.length) {
			return false;
		} else {
			// Check that each value is an instance of its corresponding column
			// type.
			for (int i = 0; i != schema.length; ++i) {
				SqlValue value = row.get(i);
				SqlType type = schema[i].type;
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
	 * Add a new row to this table. If the row is not of the appropriate
	 * structure or if another row with matching primary key(s) already exists,
	 * then an error is thrown.
	 *
	 * @param row
	 */
	public void insert(T row) {
		try {
			database.insert(this, row);
		} catch(SQLException e) {
			throw new RuntimeException("SQL Exception", e);
		}
	}

	/**
	 * Delete a given row from the database. If this row is not of the
	 * appropriate structure, then an error is thrown.
	 *
	 * @param row
	 */
	public void delete(T row) {
		try {
			database.delete(this, row);
		} catch(SQLException e) {
			throw new RuntimeException("SQL Exception", e);
		}
	}

	/**
	 * Get an iterator over all rows of the table
	 */
	public SqlQuery<T> select() {
		return new SqlQuery<T>(this);
	}

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