package stone.token;

/**
 * Token子类，定义字符串
 * @author zsp-pc
 *
 */
public class StrToken extends Token {
    private String literal;
    
    public StrToken(int line, String str) {
        super(line);
        literal = str;
    }
    
    public boolean isString() { return true; }
    public String getText() { return literal; }
}
