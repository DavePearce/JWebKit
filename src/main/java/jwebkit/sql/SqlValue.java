package jwebkit.sql;

import java.util.Arrays;

public class SqlValue {

	/**
	 * Provides a generic class for representing SQL integer data types.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Int extends SqlValue {
		private final long value;

		public Int(long value) {
			this.value = value;
		}

		public int asInt() {
			if(value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
				return (int) value;
			} else {
				throw new IllegalArgumentException();
			}
		}

		public long asLong() {
			return value;
		}

		@Override
		public int hashCode() {
			return Long.hashCode(value);
		}

		@Override
		public boolean equals(Object other) {
			if(other instanceof Int) {
				Int i = (Int) other;
				return value == i.value;
			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			return Long.toString(value);
		}
	}

	/**
	 * Provides a generic class for representing the various SQL string types
	 * (e.g. VARCHAR, TEXT, etc). The internal representation is an array of
	 * bytes (rather than e.g. a java.lang.String). This is done for several
	 * reasons: firstly, to eliminate any dependence on a specific character set
	 * (as this is controlled by the type); secondly, to ensure proper
	 * conformance with the myriad of different encodings such as ASCII, UTF8,
	 * etc.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Text extends SqlValue {
		private byte[] value;

		public Text(String value) {
			this.value = value.getBytes();
		}

		public String asString() {
			return new String(value);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(value);
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof Text) {
				Text t = (Text) other;
				return Arrays.equals(value, t.value);
			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			// FIXME: this is broken
			return "\"" + new String(value) + "\"";
		}
	}
}
