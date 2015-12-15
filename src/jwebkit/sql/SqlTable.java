package jwebkit.sql;

import java.util.Iterator;

/**
 * Respresents an SQL table as accessed via JDBC. The purpose of this class is
 * to simplify the process of communicating with the table.
 * 
 * @author djp
 *
 */
public class SqlTable {
	/**
	 * The table name
	 */
	private String name;
	
	/**
	 * The schema for the table
	 */
	private SqlColumn[] schema;
	
	public SqlTable(String name, SqlColumn... schema) {
		this.name = name;
		this.schema = schema;
	}

	public SqlColumn getColumn(int index) {
		return schema[index];
	}
	
	public SqlColumn getColumn(String name) {
		for(int i=0;i!=schema.length;++i) {
			SqlColumn c = schema[i];
			if(c.getName().equals(name)) {
				return c;
			}
		}
		throw new IllegalArgumentException("Invalid column - " + name);
	}
	
	public int nColumns() {
		return schema.length;
	}
	
	public Iterator<SqlRow> iterator() {
		return new SqlTableIterator();
	}
		
	private class SqlTableIterator implements Iterator<SqlRow> {
		
		public SqlTableIterator() {
			
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public SqlRow next() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
	}
}
