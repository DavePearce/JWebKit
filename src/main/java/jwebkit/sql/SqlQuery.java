package jwebkit.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents the results of a given SELECT query. The results of this query
 * are obtained by iterating over its contents. Alternatively the query can
 * be further refined using WHERE clauses.
 *
 * @author David J. Pearce
 *
 * @param <S>
 */

public class SqlQuery<T extends SqlRow> implements Iterable<T> {
	/**
	 * The set of possible operations that can be used to refine a query via a
	 * WHERE clause.
	 *
	 * @author David J. Pearce
	 *
	 */
	public enum Operator {
		Equal,
		NotEqual,
		GreaterThan,
		LessThan,
		GreaterThanOrEqual,
		LessThanOrEqual,
		Between,
		Like,
		IN
	}

	/**
	 * The table that this query is operating over.
	 */
	private final SqlTable<T> table;

	public SqlQuery(SqlTable<T> table) {
		this.table = table;
	}

	@Override
	public Iterator<T> iterator() {
		try {
			ResultSet r = table.getDatabase().query("SELECT * FROM " + table.getName() + ";");
			return new Iterator<T>(r, table.getSchema());
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception", e);
		}
	}

	public SqlQuery<T> where(SqlSchema.Column column, Operator operator, SqlValue value) {
		return this;
	}

	private static class Where<S extends SqlRow> extends SqlQuery<S> {
		private final SqlQuery<S> source;
		private final SqlSchema.Column column;
		private final Operator operator;
		private final SqlValue value;

		public Where(SqlTable<S> table) {
			super(table);
			// TODO Auto-generated constructor stub
		}
	}

	/**
	 * The iterator acts as a go-between for the JDBC ResultSet and the SqlRow
	 * values returned by this query.
	 *
	 * @author David J. Pearce
	 *
	 * @param <S>
	 */
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
				return schema.construct(row);
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
