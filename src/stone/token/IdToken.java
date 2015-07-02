package stone.token;

/**
 * Token子类，定义标识符
 * @author zsp-pc
 *
 */
public class IdToken extends Token {
	private String text;
	
	public IdToken(int line, String id) {
		super(line);
		text = id;
	}
	
	public boolean isIdentifier() { return true; }
	public String getText() { return text; }
}
