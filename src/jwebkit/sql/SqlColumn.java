package jwebkit.sql;

/**
 * Represents a column in the table schema
 * @author David J. Pearce
 *
 */
public class SqlColumn {
	private final String name;
	private final SqlType type;
	
	public SqlColumn(String name, SqlType type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public SqlType getType() {
		return type;
	}
}	
