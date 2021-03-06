package jwebkit.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Represents a given SELECT oR DELETE query. The results of this query are
 * obtained by iterating over its contents. Alternatively the query can be
 * further refined using WHERE clauses.
 *
 * @author David J. Pearce
 *
 * @param <S>
 */
public abstract class SqlQuery<T extends SqlRow> implements Iterable<T> {
	/**
	 * The set of possible operations that can be used to refine a query via a
	 * WHERE clause.
	 *
	 * @author David J. Pearce
	 *
	 */
	public enum Operator {
		Equal {
			@Override
			public String toString() { return "="; }
		},
		NotEqual {
			@Override
			public String toString() { return "<>"; }
		},
		GreaterThan {
			@Override
			public String toString() { return ">"; }
		},
		LessThan {
			@Override
			public String toString() { return "<"; }
		},
		GreaterThanOrEqual {
			@Override
			public String toString() { return ">="; }
		},
		LessThanOrEqual {
			@Override
			public String toString() { return "<="; }
		},
		Between {
			@Override
			public String toString() { return "BETWEEN"; }
		},
		Like {
			@Override
			public String toString() { return "LIKE"; }
		},
		IN {
			@Override
			public String toString() { return "IN"; }
		}
	}

	protected abstract SqlTable<T> getTable();

	protected abstract String getQueryString();

	@Override
	public Iterator<T> iterator() {
		try {
			SqlTable<T> table = getTable();
			ResultSet r = table.getDatabase().query(getQueryString() + ";");
			return new Iterator<>(r, table);
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception", e);
		}
	}

	/**
	 * Apply the given query. This is normally used for queries where results
	 * are not expected.
	 */
	public int apply() {
		try {
			SqlTable<T> table = getTable();
			return table.getDatabase().update(getQueryString() + ";");
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception", e);
		}
	}

	/**
	 * Collect all results from this query into a given collection. This is
	 * essentially a convenience method for the case that we want to quickly get
	 * all results into a collection for some reason.
	 *
	 * @param collection
	 */
	public <S extends Collection<T>> S collect(S collection) {
		Iterator<T> iterator = iterator();
		while(iterator.hasNext()) {
			collection.add(iterator.next());
		}
		return collection;
	}

	/**
	 * Refine a query using a "WHERE column=value" expression. The right-hand
	 * side must be an appropriate value for the type of the given column, else
	 * an IllegalArgumentException is thrown.
	 *
	 * @param columnName
	 *            The name of the column being queried
	 * @param value
	 *            The value used to refine the query
	 * @return
	 */
	public SqlQuery<T> whereEqual(String columnName, SqlValue value) {
		return whereEqual(getTable().getColumn(columnName),value);
	}

	/**
	 * Refine a query using a "WHERE column=value" expression. The right-hand
	 * side must be an appropriate value for the type of the given column, else
	 * an IllegalArgumentException is thrown.
	 *
	 * @param column
	 *            The column being queried
	 * @param value
	 *            The value used to refine the query
	 * @return
	 */
	public SqlQuery<T> whereEqual(SqlTable.Column column, SqlValue value) {
		if(!column.getType().isInstance(value)) {
			throw new IllegalArgumentException("Invalid value for WHERE clause");
		}
		return new Where<>(this,column,Operator.Equal,value);
	}

	/**
	 * Refine a query using a "WHERE column<>value" expression. The right-hand
	 * side must be an appropriate value for the type of the given column, else
	 * an IllegalArgumentException is thrown.
	 *
	 * @param columnName
	 *            The name of the column being queried
	 * @param value
	 *            The value used to refine the query
	 * @return
	 */
	public SqlQuery<T> whereNotEqual(String columnName, SqlValue value) {
		return whereEqual(getTable().getColumn(columnName),value);
	}

	/**
	 * Refine a query using a "WHERE column<>value" expression. The right-hand
	 * side must be an appropriate value for the type of the given column, else
	 * an IllegalArgumentException is thrown.
	 *
	 * @param column
	 *            The column being queried
	 * @param value
	 *            The value used to refine the query
	 * @return
	 */
	public SqlQuery<T> whereNotEqual(SqlTable.Column column, SqlValue value) {
		if(!column.getType().isInstance(value)) {
			throw new IllegalArgumentException("Invalid value for WHERE clause");
		}
		return new Where<>(this,column,Operator.NotEqual,value);
	}

	/**
	 * Refine a query using a "WHERE column>value" expression. The right-hand
	 * side must be an appropriate value for the type of the given column, else
	 * an IllegalArgumentException is thrown.
	 *
	 * @param column
	 *            The name of the column being queried
	 * @param value
	 *            The value used to refine the query
	 * @return
	 */
	public SqlQuery<T> whereGreater(String columnName, SqlValue value) {
		return whereGreater(getTable().getColumn(columnName),value);
	}

	/**
	 * Refine a query using a "WHERE column>value" expression. The right-hand
	 * side must be an appropriate value for the type of the given column, else
	 * an IllegalArgumentException is thrown.
	 *
	 * @param column
	 *            The column being queried
	 * @param value
	 *            The value used to refine the query
	 * @return
	 */
	public SqlQuery<T> whereGreater(SqlTable.Column column, SqlValue value) {
		if(!column.getType().isInstance(value)) {
			throw new IllegalArgumentException("Invalid value for WHERE clause");
		} else if(!(value instanceof SqlValue.Int)) {
			throw new IllegalArgumentException("Invalid value for WHERE comparison");
		}
		return new Where<>(this,column,Operator.GreaterThan,value);
	}

	/**
	 * Refine a query using a "WHERE column<value" expression. The right-hand
	 * side must be an appropriate value for the type of the given column, else
	 * an IllegalArgumentException is thrown.
	 *
	 * @param column
	 *            The name of the column being queried
	 * @param value
	 *            The value used to refine the query
	 * @return
	 */
	public SqlQuery<T> whereLess(String columnName, SqlValue value) {
		return whereLess(getTable().getColumn(columnName),value);
	}

	/**
	 * Refine a query using a "WHERE column<value" expression. The right-hand
	 * side must be an appropriate value for the type of the given column, else
	 * an IllegalArgumentException is thrown.
	 *
	 * @param column
	 *            The column being queried
	 * @param value
	 *            The value used to refine the query
	 * @return
	 */
	public SqlQuery<T> whereLess(SqlTable.Column column, SqlValue value) {
		if(!column.getType().isInstance(value)) {
			throw new IllegalArgumentException("Invalid value for WHERE clause");
		} else if(!(value instanceof SqlValue.Int)) {
			throw new IllegalArgumentException("Invalid value for WHERE comparison");
		}
		return new Where<>(this,column,Operator.LessThan,value);
	}

	/**
	 * Refine a query using a "WHERE column>=value" expression. The right-hand
	 * side must be an appropriate value for the type of the given column, else
	 * an IllegalArgumentException is thrown.
	 *
	 * @param column
	 *            The name of the column being queried
	 * @param value
	 *            The value used to refine the query
	 * @return
	 */
	public SqlQuery<T> whereGreaterOrEqual(String columnName, SqlValue value) {
		return whereGreaterOrEqual(getTable().getColumn(columnName),value);
	}

	/**
	 * Refine a query using a "WHERE column<=value" expression. The right-hand
	 * side must be an appropriate value for the type of the given column, else
	 * an IllegalArgumentException is thrown.
	 *
	 * @param column
	 *            The column being queried
	 * @param value
	 *            The value used to refine the query
	 * @return
	 */
	public SqlQuery<T> whereGreaterOrEqual(SqlTable.Column column, SqlValue value) {
		if(!column.getType().isInstance(value)) {
			throw new IllegalArgumentException("Invalid value for WHERE clause");
		} else if(!(value instanceof SqlValue.Int)) {
			throw new IllegalArgumentException("Invalid value for WHERE comparison");
		}
		return new Where<>(this,column,Operator.GreaterThanOrEqual,value);
	}

	/**
	 * Refine a query using a "WHERE column>=value" expression. The right-hand
	 * side must be an appropriate value for the type of the given column, else
	 * an IllegalArgumentException is thrown.
	 *
	 * @param column
	 *            The name of the column being queried
	 * @param value
	 *            The value used to refine the query
	 * @return
	 */
	public SqlQuery<T> whereLessOrEqual(String columnName, SqlValue value) {
		return whereLessOrEqual(getTable().getColumn(columnName),value);
	}

	/**
	 * Refine a query using a "WHERE column<=value" expression. The right-hand
	 * side must be an appropriate value for the type of the given column, else
	 * an IllegalArgumentException is thrown.
	 *
	 * @param column
	 *            The column being queried
	 * @param value
	 *            The value used to refine the query
	 * @return
	 */
	public SqlQuery<T> whereLessOrEqual(SqlTable.Column column, SqlValue value) {
		if(!column.getType().isInstance(value)) {
			throw new IllegalArgumentException("Invalid value for WHERE clause");
		} else if(!(value instanceof SqlValue.Int)) {
			throw new IllegalArgumentException("Invalid value for WHERE comparison");
		}
		return new Where<>(this,column,Operator.LessThanOrEqual,value);
	}

	/**
	 * Refine a query by using an "ORDER BY column,...,column" clause. At least
	 * one column must be given, else an IllegalArgumentException is thrown.
	 *
	 * @param columns
	 *            The columns being ordered by
	 * @return
	 */
	public SqlQuery<T> orderBy(SqlTable.Column... columns) {
		return orderBy(OrderByMode.None,columns);
	}

	/**
	 * Refine a query by using an "ORDER BY column,...,column ASC" clause. At least
	 * one column must be given, else an IllegalArgumentException is thrown.
	 *
	 * @param columns
	 *            The columns being ordered by
	 * @return
	 */
	public SqlQuery<T> orderByAsc(SqlTable.Column... columns) {
		return orderBy(OrderByMode.ASC,columns);
	}

	/**
	 * Refine a query by using an "ORDER BY column,...,column DESC" clause. At
	 * least one column must be given, else an IllegalArgumentException is
	 * thrown.
	 *
	 * @param columns
	 *            The columns being ordered by
	 * @return
	 */
	public SqlQuery<T> orderByDesc(SqlTable.Column... columns) {
		return orderBy(OrderByMode.DESC,columns);
	}

	private SqlQuery<T> orderBy(OrderByMode mode, SqlTable.Column... columns) {
		if(columns.length == 0) {
			throw new IllegalArgumentException("Require at least one column for ORDER BY clause");
		}
		return new OrderBy<>(this,mode,columns);
	}

	/**
	 * Represents a SELECT query.
	 *
	 * @author David J. Pearce
	 *
	 * @param <S>
	 */
	public static class Select<S extends SqlRow> extends SqlQuery<S> {

		/**
		 * The table that this query is operating over.
		 */
		private final SqlTable<S> table;

		public Select(SqlTable<S> table) {
			this.table = table;
		}

		@Override
		protected String getQueryString() {
			return "SELECT * FROM " + table.getName();
		}

		@Override
		protected SqlTable<S> getTable() {
			return table;
		}
	}

	/**
	 * Represents the results of a given DELETE query.
	 *
	 * @author David J. Pearce
	 *
	 * @param <S>
	 */
	public static class Delete<S extends SqlRow> extends SqlQuery<S> {
		/**
		 * The table that this query is operating over.
		 */
		private final SqlTable<S> table;

		public Delete(SqlTable<S> table) {
			this.table = table;
		}

		@Override
		protected String getQueryString() {
			return "DELETE FROM " + table.getName();
		}

		@Override
		protected SqlTable<S> getTable() {
			return table;
		}
	}

	/**
	 * Represents the results of a query which are refined by a given WHERE
	 * clause.
	 *
	 * @author David J. Pearce
	 *
	 * @param <S>
	 */
	private static class Where<S extends SqlRow> extends SqlQuery<S> {
		private final SqlQuery<S> source;
		private final SqlTable.Column column;
		private final Operator operator;
		private final SqlValue value;

		public Where(SqlQuery<S> source, SqlTable.Column column, Operator operator, SqlValue value) {
			this.source = source;
			this.column = column;
			this.operator = operator;
			this.value = value;
		}

		@Override
		protected String getQueryString() {
			boolean needAND = source instanceof Where;
			String qs = source.getQueryString();
			if(needAND) {
				qs += " AND ";
			} else {
				qs += " WHERE ";
			}
			qs += column.getName();
			qs += operator.toString();
			qs += value.toString();
			return qs;
		}

		@Override
		protected SqlTable<S> getTable() {
			return source.getTable();
		}
	}

	private enum OrderByMode {
		None,
		ASC,
		DESC
	}

	private static class OrderBy<S extends SqlRow> extends SqlQuery<S> {
		private final SqlQuery<S> source;
		private final OrderByMode mode;
		private final SqlTable.Column[] columns;

		public OrderBy(SqlQuery<S> source, OrderByMode mode, SqlTable.Column... columns) {
			this.source = source;
			this.mode = mode;
			this.columns = columns;
		}

		@Override
		protected SqlTable<S> getTable() {
			return source.getTable();
		}

		@Override
		protected String getQueryString() {
			String qs = source.getQueryString();
			qs += " ORDER BY ";
			for(int i=0;i!=columns.length;++i) {
				SqlTable.Column col = columns[i];
				if(i != 0) {
					qs += ", ";
				}
				qs += col.getName();
			}
			if(mode == OrderByMode.ASC) {
				qs += " ASC";
			} else if(mode == OrderByMode.DESC) {
				qs += " DESC";
			}
			return qs;
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
		private final SqlTable<S> table;

		public Iterator(ResultSet data, SqlTable<S> table) {
			this.data = data;
			this.table = table;
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
				SqlValue[] row = new SqlValue[table.size()];
				for (int i = 0; i != row.length; ++i) {
					SqlTable.Column column = table.getColumn(i);
					Object rowObject = data.getObject(i + 1);
					row[i] = rowObject == null ? null : column.getType().fromObject(rowObject);
				}
				return table.newRowInstance(row);
			} catch (SQLException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub

		}
	}
}
