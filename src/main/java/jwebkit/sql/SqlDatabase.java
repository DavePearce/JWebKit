package jwebkit.sql;

import java.util.*;
import java.sql.*;

public class SqlDatabase {
	/**
	 * The JDBC connection which provides access to the physical database
	 * backing this object.
	 */
	private Connection connection;

	/**
	 * The set of bound tables in this database object.
	 */
	private Map<String, SqlTable> tables;

	public SqlDatabase(Connection connection) {
		this.tables = new HashMap<String, SqlTable>();
		this.connection = connection;
	}

	/**
	 * Bind a table schema to this database. The intention is that this table
	 * already exists within the database and this makes it visible from this
	 * object.
	 *
	 * @param tableName
	 * @param schema
	 * @return
	 */
	public <T extends SqlRow> SqlDatabase bindTable(String tableName, SqlSchema schema) {
		tables.put(tableName, new SqlTable(this, tableName, schema));
		return this;
	}

	/**
	 * Get the Table instance corresponding to a given table in the database.
	 * This must have been previously created or bound to its corresponding
	 * table in the underlying database.
	 */
	public SqlTable getTable(String tableName) {
		return tables.get(tableName);
	}

	// ================================================================
	// Private Helpers
	// ================================================================

	/**
	 * Execute a given SQL query. This is given package level visibility so that
	 * it may be called from other classes in this package.
	 *
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	ResultSet query(String sql) throws SQLException {
		Statement stmt = connection.createStatement();
		return stmt.executeQuery(sql);
	}

	/**
	 * Insert a given row into a given table
	 * @param table
	 * @param row
	 */
	void insertInto(SqlTable table, SqlRow row) throws SQLException {
		String sql = "INSERT INTO " + table.getName() + " VALUES(";
		for(int i=0;i!=row.size();++i) {
			if(i != 0) {
				sql += ",";
			}
			sql += toString(row.get(i));
		}
		sql += ");";
		Statement stmt = connection.createStatement();
		stmt.execute(sql);
		System.out.println("QUERY : " + sql);
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
