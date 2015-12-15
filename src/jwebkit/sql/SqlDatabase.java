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
	private Map<String, Table> tables;
		
	public SqlDatabase(Connection connection) {
		this.tables = new HashMap<String, Table>();
		this.connection = connection;
	}

	/**
	 * Get the Table instance corresponding to a given table in the database.
	 */
	public Table getTable(String tableName) {
		return tables.get(tableName);
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
	public SqlDatabase bindTable(String tableName, Column... schema) {
		tables.put(tableName, new Table(tableName, schema));
		return this;
	}

	/**
	 * Respresents an SQL table as accessed via JDBC. The purpose of this class
	 * is to simplify the process of communicating with the table.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public class Table {
		/**
		 * The table name
		 */
		private String name;

		/**
		 * The schema for the table
		 */
		private Column[] schema;

		public Table(String name, Column... schema) {
			this.name = name;
			this.schema = schema;
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

		public int nColumns() {
			return schema.length;
		}

		public Iterator<Object[]> iterator() {
			return new SqlTableIterator();
		}
	}

	/**
	 * Represents a column in the table schema
	 * @author David J. Pearce
	 *
	 */
	public static class Column {
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
	
	private static class SqlTableIterator implements Iterator<Object[]> {

		public SqlTableIterator() {

		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Object[] next() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub

		}
	}
}
