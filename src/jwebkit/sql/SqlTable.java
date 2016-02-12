package jwebkit.sql;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Respresents an SQL table as accessed via JDBC. The purpose of this class
 * is to simplify the process of communicating with the table.
 * 
 * @author David J. Pearce
 *
 */
public class SqlTable<T extends SqlRow> implements Iterable<T> {
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
	private SqlSchema schema;
	
	public SqlTable(SqlDatabase db, String name, SqlSchema schema) {
		this.database = db;
		this.name = name;
		this.schema = schema;		
	}

	public java.util.Iterator<T> iterator() {
		try {
			ResultSet r = database.query("SELECT * FROM " + name + ";");
			return new Iterator<T>(r, schema);
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception", e);
		}
	}
	
	private static class Iterator<S extends SqlRow> implements java.util.Iterator<S> {
		private final ResultSet data;
		private final SqlSchema<S> schema;
		
		public Iterator(ResultSet data, SqlSchema<S> schema) {
			this.data = data;
			this.schema = schema;
		}

		@Override
		public boolean hasNext() {
			try {
				return data.next();
			} catch (SQLException e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}

		@Override
		public S next() {
			try {
				Object[] row = new Object[schema.size()];
				for(int i=0;i!=row.length;++i) {
					row[i] = data.getObject(i+1);
				}
				return schema.constructRow(row);
			} catch (SQLException e) {
				throw new RuntimeException(e.getMessage(),e);
			} 
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub

		}
	}
	
	
}