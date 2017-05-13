package jwebkit.sql;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

	/**
	 * The table that this query is operating over.
	 */
	private final SqlTable<T> table;

	public SqlQuery(SqlTable<T> table) {
		this.table = table;
	}

	protected String getQueryString() {
		return "SELECT * FROM " + table.getName();
	}

	@Override
	public Iterator<T> iterator() {
		try {
			ResultSet r = table.getDatabase().query(getQueryString() + ";");
			return new Iterator<>(r, table);
		} catch (SQLException e) {
			throw new RuntimeException("SQL Exception", e);
		}
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
		return whereEqual(table.getColumn(columnName),value);
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
		return new Where<>(table,this,column,Operator.Equal,value);
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
		return whereEqual(table.getColumn(columnName),value);
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
		return new Where<>(table,this,column,Operator.NotEqual,value);
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
		return whereGreater(table.getColumn(columnName),value);
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
		return new Where<>(table,this,column,Operator.GreaterThan,value);
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
		return whereLess(table.getColumn(columnName),value);
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
		return new Where<>(table,this,column,Operator.LessThan,value);
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
		return whereGreaterOrEqual(table.getColumn(columnName),value);
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
		return new Where<>(table,this,column,Operator.GreaterThanOrEqual,value);
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
		return whereLessOrEqual(table.getColumn(columnName),value);
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
		return new Where<>(table,this,column,Operator.LessThanOrEqual,value);
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

		public Where(SqlTable<S> table, SqlQuery<S> source, SqlTable.Column column, Operator operator, SqlValue value) {
			super(table);
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
