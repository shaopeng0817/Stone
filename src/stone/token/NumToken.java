package stone.token;

/**
 * Token子类，定义整数
 * @author zsp-pc
 *
 */
public class NumToken extends Token {
	private int value;
	
	public NumToken(int line, int v) {
		super(line);
		value = v;
	}
	
	public boolean isNumber() { return true; }
	public String getText() { return Integer.toString(value); }
	public int getNumber() { return value; }
}
