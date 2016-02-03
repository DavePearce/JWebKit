package jwebkit.sql;

public abstract class SqlType {
	
	public static final SqlType Int = new Int();
			
	/**
	 * Represents the int datatype which corresponds to 32bit signed integers.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static class Int extends SqlType {
		
	}
	
	/**
	 * Represents the int datatype which corresponds to 32bit signed integers.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static class VARCHAR extends SqlType {
		private final int maxLength;
		public VARCHAR(int maxLength) {
			this.maxLength = maxLength;
		}
	}
}
