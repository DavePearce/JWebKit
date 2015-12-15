package jwebkit.sql;

public class SqlRow {
	private SqlTable table;
	
	public String getString(String column) {
		SqlColumn col = table.getColumn(column);
		if (col.getType() instanceof VarChar) {
			return null;
		} else {
			throw new IllegalArgumentException("Attempt to access non-String column as String");
		}
	}
	
	public String getString(int index) {
		SqlColumn col = table.getColumn(index);
		if (col.getType() instanceof VarChar) {
			return null;
		} else {
			throw new IllegalArgumentException("Attempt to access non-String column as String");
		}
	}
}
