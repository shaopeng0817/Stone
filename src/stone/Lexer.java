package stone;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import stone.token.IdToken;
import stone.token.NumToken;
import stone.token.StrToken;
import stone.token.Token;

/**
 * 词法分析程序
 * @author zsp-pc
 *
 */
public class Lexer {
	// 要匹配的正则表达式
	// 整型：[0-9]+
	// 字符串："(\\"|\\\\|\\n|[^"])*"
	// 标识符：[A-Za-z][A-Za-z0-9]*|==|<=|>=|&&|\|\||\p{Punct}
	public static String regexPat = "\\s*((//.*)|([0-9]+)|(\"(\\\\\"|\\\\\\\\|\\\\n|[^\"])*\")|[A-Za-z][A-Za-z0-9]*|==|<=|>=|&&|\\|\\||\\p{Punct})?";
	private Pattern pattern = Pattern.compile(regexPat);
	private ArrayList<Token> queue = new ArrayList<>();
	private boolean hasMore;
	private LineNumberReader reader;

	public Lexer(Reader r) {
		hasMore = true;
		reader = new LineNumberReader(r);
	}

	public Token read() throws ParseException {
		if (fillQueue(0))
			return queue.remove(0);
		else
			return Token.EOF;
	}

	public Token peek(int i) throws ParseException {
		if (fillQueue(i))
			return queue.remove(i);
		else
			return Token.EOF;
	}

	/**
	 * 填充队列
	 * @param i
	 * @return
	 * @throws ParseException
	 */
	private boolean fillQueue(int i) throws ParseException {
		while (i >= queue.size()) 
			if(hasMore)
				readLine();
			else
				return false;
		return true;
	}
	
	/**
	 * 
	 * @throws ParseException
	 */
	protected void readLine() throws ParseException {
		String line;
		
		//读取一行
		try {
			line = reader.readLine();
		} catch (IOException e) {
			throw new ParseException(e);
		}
		
		//若为空，则hasMore=false，退出
		if(line == null) {
			hasMore = false;
			return;
		}
		
		int lineNo = reader.getLineNumber();
		Matcher matcher = pattern.matcher(line);
		matcher.useTransparentBounds(true).useAnchoringBounds(false);	//？？
		int pos = 0;
		int endPos = line.length();
		while(pos < endPos) {
			matcher.region(pos, endPos);	//限定该对象的匹配范围
			if(matcher.lookingAt()) {	//检查范围内进行正则表达式匹配
				addToken(lineNo, matcher);
				pos = matcher.end();	//取得匹配部分的结束位置
			} else {
				throw new ParseException("bad token at line " + lineNo);
			}
		}
		queue.add(new IdToken(lineNo, Token.EOL));
	}
	
	protected void addToken(int lineNo, Matcher matcher) {
		String m = matcher.group(1);
		if(m != null) {	// 如果不是空格
			if(matcher.group(2) == null) { // 如果不是注释
				Token token;
				if(matcher.group(3) != null)	//符合整数的正则
					token = new NumToken(lineNo, Integer.parseInt(m));
				else if(matcher.group(4) != null)	//符合字符串的正则
					token = new StrToken(lineNo, toStringLiteral(m));
				else
					token = new IdToken(lineNo, m);
				queue.add(token);
			}
		}
	}
	
	protected String toStringLiteral(String s) {
		StringBuilder sb = new StringBuilder();
		int len = s.length() - 1;
		for (int i = 1; i < len; i++) {
			char c = s.charAt(i);
			if(c == '\\' && i + 1 < len) {
				int c2 = s.charAt(i + 1);
				if(c2 == '"' || c2 == '\\')
					c = s.charAt(++i);
				else if(c2 == 'n') {
					++i;
					c = '\n';
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}
}
