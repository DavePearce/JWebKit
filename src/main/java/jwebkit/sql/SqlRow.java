package jwebkit.sql;

/**
 * Represents a single row of data in an SqlTable. The intention is that
 * this class be extended in order to provide a nicer user interface. Note
 * that instances of this class and any subclasses should always be immutable.
 *
 * @author David J. Pearce
 */
public interface SqlRow {

	public SqlValue get(int i);

	public int size();
}
