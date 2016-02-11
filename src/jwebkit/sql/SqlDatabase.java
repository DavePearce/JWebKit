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
	public SqlDatabase bindTable(String tableName, SqlTable.Column... schema) {
		tables.put(tableName, new SqlTable(tableName, schema));
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
	 * Execute a given SQL query
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	private ResultSet query(String sql) throws SQLException {
		Statement stmt = connection.createStatement();
		return stmt.executeQuery(sql);
	}
}
