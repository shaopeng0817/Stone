package stone.env;

public interface Environment {
	public void put(String name, Object value);
	public Object get(String name);
}
