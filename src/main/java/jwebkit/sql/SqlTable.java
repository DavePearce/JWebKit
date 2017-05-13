package jwebkit.sql;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * Respresents an SQL table as accessed via JDBC. The purpose of this class
 * is to simplify the process of communicating with the table.
 *
 * @author David J. Pearce
 *
 */
public class SqlTable<T extends SqlRow> {
	/**
	 * Parent reference
	 */
	private SqlDatabase database;

	/**
	 * The table name
	 */
	private final String name;

	/**
	 * The kind of row which will be instantiated by this table
	 */
	private final Class<T> rowClass;

	/**
	 * The schema for this table
	 */
	private final Column[] schema;

	public SqlTable(SqlDatabase db, String name, Class<T> rowClass, Column... schema) {
		this.database = db;
		this.name = name;
		this.rowClass = rowClass;
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

	public boolean exists() {
		try {
			return database.exists(this);
		} catch(SQLException e) {
			throw new RuntimeException("SQL Exception", e);
		}
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
	public T newRowInstance(SqlValue[] row) {
		// Check that each value is an instance of its corresponding column
		// type.
		for (int i = 0; i != schema.length; ++i) {
			SqlValue value = row[i];
			SqlType type = schema[i].type;
			if (!type.isInstance(value)) {
				throw new IllegalArgumentException("invalid value " + value + " for column " + i + " with type " + type);
			}
		}
		// Construct a new row instance
		try {
			Constructor<T> constructor = rowClass.getConstructor(SqlValue[].class);
			return constructor.newInstance(new Object[] { row });
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		} catch (SecurityException e) {
			throw new IllegalArgumentException(e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Create a table of the given name and schema in the database. This assumes
	 * that the table does not already exist.
	 */
	public void create() {
		try {
			database.create(this);
		} catch(SQLException e) {
			throw new RuntimeException("SQL Exception", e);
		}
	}

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
		return new SqlQuery<>(this);
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