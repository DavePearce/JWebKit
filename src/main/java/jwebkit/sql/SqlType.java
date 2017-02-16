package jwebkit.sql;

public abstract class SqlType {

	/**
	 * SQL TINYINT corresponds to an 8-bit integer value
	 */
	public static final SqlType TINYINT = new INT(-128,127);

	/**
	 * SQL SMALLINT corresponds to a 16-bit integer value
	 */
	public static final SqlType SMALLINT = new INT(-32768,32767);

	/**
	 * SQL INT corresponds to an 32-bit integer value
	 */
	public static final SqlType INT = new INT(Integer.MIN_VALUE,Integer.MAX_VALUE);

	/**
	 * SQL BIGINT corresponds to an 64-bit integer value
	 */
	public static final SqlType BIGINT = new INT(Long.MIN_VALUE,Long.MAX_VALUE);

	/**
	 * SQL VARCHAR stores a variable-length string of characters, where the
	 * definition of character is determined by the character encoding.
	 *
	 * @param width
	 * @return
	 */
	public static final SqlType.VARCHAR VARCHAR(int width) {
		if(width < 0 || width >= 65536) {
			throw new IllegalArgumentException("Invalid VARCHAR width");
		}
		return new VARCHAR(width);
	}

	/**
	 * SQL TINYTEXT datatype which has a maximum width of 255 bytes.
	 */
	public static final SqlType.TEXT TINYTEXT = new TEXT(255);

	/**
	 * SQL MEDIUMTEXT datatype which has a maximum width of 16777215 bytes.
	 */
	public static final SqlType.TEXT MEDIUMTEXT = new TEXT(16777215);

	/**
	 * SQL LONGTEXT datatype which has a maximum width of 4GB bytes.
	 */
	public static final SqlType.TEXT LONGTEXT = new TEXT(Integer.MAX_VALUE);


	/**
	 * SQL TEXT stores a variable-length string of characters, where the
	 * width limit is counted in bytes.
	 *
	 * @param width
	 * @return
	 */
	public static final SqlType.TEXT TEXT(int width) {
		if(width < 0 || width >= 65536) {
			throw new IllegalArgumentException("Invalid TEXT width");
		}
		return new TEXT(width);
	}

	/**
	 * Represents the int datatype which corresponds to 32bit signed integers.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class INT extends SqlType {
		private final long lower;
		private final long upper;

		private INT(long lower, long upper) {
			this.lower = lower;
			this.upper = upper;
		}

		@Override
		public boolean isInstance(SqlValue value) {
			if (value instanceof SqlValue.Int) {
				SqlValue.Int i = (SqlValue.Int) value;
				return i.asLong() >= lower && i.asLong() <= upper;
			}
			return false;
		}

		@Override
		public String toString() {
			return "INT";
		}
	}

	/**
	 * Represents a variable-length string datatype with a fixed maximum number
	 * of characters.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class VARCHAR extends SqlType {
		private final int width;

		private VARCHAR(int maxLength) {
			this.width = maxLength;
		}

		@Override
		public boolean isInstance(SqlValue value) {
			if (value instanceof SqlValue.Text) {
				SqlValue.Text t = (SqlValue.Text) value;
				// FIXME: this is completely broken for e.g. UTF32 strings
				if (t.asString().length() > width) {
					return false;
				}
			}
			return true;
		}

		@Override
		public String toString() {
			return "VARCHAR(" + width + ")";
		}
	}

	/**
	 * Represents a string datatype with a fixed maximum number of characters.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class TEXT extends SqlType {
		private final int width;

		private TEXT(int maxLength) {
			this.width = maxLength;
		}

		@Override
		public boolean isInstance(SqlValue value) {
			if (value instanceof SqlValue.Text) {
				SqlValue.Text t = (SqlValue.Text) value;
				// FIXME: this is completely broken for e.g. UTF32 strings
				if (t.asString().length() > width) {
					return false;
				}
			}
			return true;
		}

		@Override
		public String toString() {
			return "VARCHAR(" + width + ")";
		}
	}

	public static final SqlType.DATE DATE = new DATE();

	public static class DATE extends SqlType {

		private DATE() {}

		@Override
		public boolean isInstance(SqlValue value) {
			return value instanceof SqlValue.Date;
		}

		@Override
		public String toString() {
			return "DATE";
		}
	}

	public static class DATETIME extends SqlType {

		@Override
		public boolean isInstance(SqlValue value) {
			return value instanceof SqlValue.DateTime;
		}

		@Override
		public String toString() {
			return "DATETIME";
		}
	}

	public static class TIMESTAMP extends SqlType {

		@Override
		public boolean isInstance(SqlValue value) {
			return value instanceof SqlValue.TimeStamp;
		}

		@Override
		public String toString() {
			return "TIMESTAMP";
		}
	}

	/**
	 * Check whether a given value is an instance of this type or not.
	 */
	public abstract boolean isInstance(SqlValue value);
}
