package jwebkit.sql;

public abstract class SqlType {
	
	public static final SqlType INT = new INT();
			
	public static final SqlType.VARCHAR VARCHAR(int maxLength) {
		return new VARCHAR(maxLength);
	}
	
	/**
	 * Represents the int datatype which corresponds to 32bit signed integers.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static class INT extends SqlType {
		private INT() {
			
		}
	}
	
	/**
	 * Represents the int datatype which corresponds to 32bit signed integers.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static class VARCHAR extends SqlType {
		private final int maxLength;
		private VARCHAR(int maxLength) {
			this.maxLength = maxLength;
		}
	}
}
