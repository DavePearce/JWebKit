package jwebkit.sql;

import java.sql.ResultSet;
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
	private String name;

	/**
	 * The type of row object held within this table.
	 */
	private SqlSchema<T> schema;

	public SqlTable(SqlDatabase db, String name, SqlSchema<T> schema) {
		this.database = db;
		this.name = name;
		this.schema = schema;
	}

	public SqlDatabase getDatabase() {
		return database;
	}

	public String getName() {
		return name;
	}

	public SqlSchema<T> getSchema() {
		return schema;
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
		return new SqlQuery<T>(this);
	}
}