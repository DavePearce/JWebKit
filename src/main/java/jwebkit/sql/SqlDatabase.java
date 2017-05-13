package jwebkit.sql;

import java.util.*;
import java.sql.*;
import java.time.LocalDateTime;

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
		this.tables = new HashMap<>();
		this.connection = connection;
	}

	/**
	 * Get the Table instance corresponding to a given table in the database.
	 * This must have been previously created or bound to its corresponding
	 * table in the underlying database.
	 */
	public SqlTable<?> getTable(String tableName) {
		return tables.get(tableName);
	}

	// ================================================================
	// Private Helpers
	// ================================================================

	/**
	 * Bind a table schema to this database. The intention is that this table
	 * already exists within the database and this makes it visible from this
	 * object.
	 *
	 * @param tableName
	 * @param schema
	 * @return
	 */
	public <T extends SqlRow> void bind(SqlTable<T> table) {
		tables.put(table.getName(), table);
	}

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
	 * Execute a given SQL update. This is given package level visibility so that
	 * it may be called from other classes in this package.
	 *
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	int update(String sql) throws SQLException {
		Statement stmt = connection.createStatement();
		return stmt.executeUpdate(sql);
	}

	<T extends SqlRow> void create(SqlTable<T> table) throws SQLException {
		String sql = "CREATE TABLE " + table.getName() + "(";
		for(int i=0;i!=table.size();++i) {
			if(i != 0) {
				sql += ",";
			}
			SqlTable.Column col = table.getColumn(i);
			sql += col.getName() + " " + col.getType();
		}
		sql += ");";
		System.out.println("QUERY : " + sql);
		Statement stmt = connection.createStatement();
		stmt.execute(sql);
	}

	/**
	 * Check whether a given table already exists in the database or not.
	 *
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	<T extends SqlRow> boolean exists(SqlTable<T> table) throws SQLException {
		DatabaseMetaData meta = connection.getMetaData();
		ResultSet tables = meta.getTables(null, null, table.getName(), null);
		boolean r = tables.next();
		tables.close();
		return r;
	}

	/**
	 * INSERT a given row INTO a given table. The row must be a valid instance
	 * of the schema associated with the corresponding table, otherwise an error
	 * will be thrown.
	 *
	 * @param table
	 * @param row
	 */
	<T extends SqlRow> void insert(SqlTable<T> table, T row) throws SQLException {
		// Sanity check the row is a valid instance.
		if(!table.isInstance(row)) {
			throw new IllegalArgumentException("invalid row for table");
		}
		// Perform the insert query.
		String sql = "INSERT INTO " + table.getName() + " VALUES(";
		for (int i = 0; i != row.size(); ++i) {
			if (i != 0) {
				sql += ",";
			}
			sql += "?"; // row.get(i);
		}
		sql += ");";
		System.out.println("QUERY : " + sql);
		//
		PreparedStatement stmt = connection.prepareStatement(sql);
		for (int i = 0; i != row.size(); ++i) {
			SqlValue value = row.get(i);
			if(value instanceof SqlValue.Int) {
				SqlValue.Int v = (SqlValue.Int) value;
				// FIXME: what to do if value doesn't fit into int?
				stmt.setInt(i+1, v.asInt());
			} else if(value instanceof SqlValue.Text) {
				SqlValue.Text t = (SqlValue.Text) value;
				stmt.setString(i+1, t.asString());
			} else if(value instanceof SqlValue.Date) {
				SqlValue.Date d = (SqlValue.Date) value;
				stmt.setDate(i+1, java.sql.Date.valueOf(d.asLocalDate()));
			} else if(value instanceof SqlValue.DateTime) {
				SqlValue.DateTime d = (SqlValue.DateTime) value;
				LocalDateTime dt = d.asLocalDateTime();
				java.sql.Date date = java.sql.Date.valueOf(dt.toLocalDate());
				stmt.setDate(i+1,date);
			} else {
				throw new IllegalArgumentException("unknown value encountered: " + value);
			}
		}
		stmt.executeUpdate();
	}

	/**
	 * Delete a given row from the database
	 *
	 * @param table
	 * @param row
	 * @throws SQLException
	 */
	<T extends SqlRow> void delete(SqlTable<T> table, T row) throws SQLException {
		// Sanity check the row is a valid instance.
		if(!table.isInstance(row)) {
			throw new IllegalArgumentException("invalid row for table");
		}
		String sql = "DELETE FROM " + table.getName() + " WHERE ";

		// FIXME: only really need to look at key values
		for (int i = 0; i != row.size(); ++i) {
			if (i != 0) {
				sql += " AND ";
			}
			String name = table.getColumn(i).getName();
			sql += name + "=" + row.get(i);
		}
		sql += ";";
		Statement stmt = connection.createStatement();
		stmt.execute(sql);
		System.out.println("QUERY : " + sql);
	}

}
