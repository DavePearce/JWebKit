package jwebkit.sql;

/**
 * Respresents an SQL table as accessed via JDBC. The purpose of this class is
 * to simplify the process of communicating with the table.
 * 
 * @author djp
 *
 */
public class SqlTable {
	
	/**
	 * Represents a column in the table schema
	 * @author David J. Pearce
	 *
	 */
	public static class Column {
		private String name;
		private SqlType type;
	}	
	
	/**
	 * Represents a 32bit twos-complement integer.
	 * 
	 * @author djp
	 *
	 */
	private static class Integer extends Column {
		
	}
}
