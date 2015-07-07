package stone.env;

import java.util.List;

import stone.StoneException;
import stone.ast.ASTLeaf;
import stone.ast.ASTList;
import stone.ast.ASTree;
import stone.ast.BinaryExpr;
import stone.ast.BlockStmnt;
import stone.ast.IfStmnt;
import stone.ast.Name;
import stone.ast.NegativeExpr;
import stone.ast.NullStmnt;
import stone.ast.NumberLiteral;
import stone.ast.StringLiteral;
import stone.ast.WhileStmnt;
import stone.token.Token;

public class BasicEvaluator {
	
	public static final int TRUE = 1;
	public static final int FALSE = 2;
	
	public static abstract class ASTreeEx extends ASTree {
		public abstract Object eval(Environment env);
	}
	
	public static class ASTListEx extends ASTList {
		public ASTListEx(List<ASTree> list) {
			super(list);
		}
		public Object eval(Environment env) {
			throw new StoneException("cannot eval: " + toString(), this);
		}
	}
	
	public static class ASTLeafEx extends ASTLeaf {
		public ASTLeafEx(Token t) {
			super(t);
		}
		public Object eval(Environment env) {
			throw new StoneException("cannot eval: " + toString(), this);
		}
	}
	
	//NumberEx
	public static class NumberEx extends NumberLiteral {
		public NumberEx(Token t) {
			super(t);
		}
		public Object eval(Environment env) {
			return value();
		}
	}
	
	//StringEx
	public static class StringEx extends StringLiteral {
		public StringEx(Token t) {
			super(t);
		}
		public Object eval(Environment env) {
			return value();
		}
	}
	
	//NameEx
	public static class NameEx extends Name {
		public NameEx(Token t) {
			super(t);
		}
		public Object eval(Environment env) {
			Object value = env.get(name());
			if(value == null) 
				throw new StoneException("undefined name: " + name(), this);
			else
				return value;
		}
	}
	
	//����
	public static class NegativeEx extends NegativeExpr {
		public NegativeEx(List<ASTree> c) {
			super(c);
		}
		public Object eval(Environment env) {
			Object value = ((ASTreeEx)operand()).eval(env);
			if(value instanceof Integer)
				return new Integer(-((Integer)value).intValue());
			else
				throw new StoneException("bad type for -", this);
		}
	}
	
	public static class BinaryEx extends BinaryExpr {
		public BinaryEx(List<ASTree> c) {
			super(c);
		}
		public Object eval(Environment env) {
			String op = operator();
			if("=".equals(op)) {
				Object right = ((ASTreeEx)right()).eval(env);
				return computeAssign(env, right);
			} else {
				Object right = ((ASTreeEx)right()).eval(env);
				Object left = ((ASTreeEx)left()).eval(env);
				return computeOp(left, op, right);
			}
		}
		
		protected Object computeAssign(Environment env, Object rvalue) {
			ASTree l = left();
			if(l instanceof Name) {
				env.put(((Name)l).name(), rvalue);
				return rvalue;
			} else {
				throw new StoneException("bad assignment", this);
			}
		}
		
		protected Object computeOp(Object left, String op, Object right) {
			if(left instanceof Integer && right instanceof Integer) {
				return computeNumber((Integer)left, op, (Integer)right);
			} else {
				if(op.equals("+")) {
					return String.valueOf(left) + String.valueOf(right);
				} else if(op.equals("==" )) {
					if(left == null)
						return right == null? TRUE : FALSE;
					else
						return left.equals(right)? TRUE : FALSE;
				} else {
					throw new StoneException("bad type", this);
				}
			}
		}
		
		protected Object computeNumber(Integer left, String op, Integer right) {
			int a = left.intValue();
            int b = right.intValue();
            if (op.equals("+"))
                return a + b;
            else if (op.equals("-"))
                return a - b;
            else if (op.equals("*"))
                return a * b;
            else if (op.equals("/"))
                return a / b;
            else if (op.equals("%"))
                return a % b;
            else if (op.equals("=="))
                return a == b ? TRUE : FALSE;
            else if (op.equals(">"))
                return a > b ? TRUE : FALSE;
            else if (op.equals("<"))
                return a < b ? TRUE : FALSE;
            else
                throw new StoneException("bad operator", this);
		}
	}
	
	public static class BlockEx extends BlockStmnt {
		public BlockEx(List<ASTree> c) {
			super(c);
		}
		public Object eval(Environment env) {
			Object result = 0;
			for(ASTree t : this) {
				if(!(t instanceof NullStmnt))
					result = ((ASTreeEx)t).eval(env);
			}
			return result;
		}
	}
	
	public static class IfEx extends IfStmnt {
		public IfEx(List<ASTree> c) {
			super(c);
		}
		public Object eval(Environment env) {
            Object c = ((ASTreeEx)condition()).eval(env);
            if (c instanceof Integer && ((Integer)c).intValue() != FALSE)
                return ((ASTreeEx)thenBlock()).eval(env);
            else {
                ASTree b = elseBlock();
                if (b == null)
                    return 0;
            else
                return ((ASTreeEx)b).eval(env);
            }
		}
	}
	
	public static class WhileEx extends WhileStmnt {
        public WhileEx(List<ASTree> c) { super(c); }
        public Object eval(Environment env) {
            Object result = 0;
            for (;;) {
                Object c = ((ASTreeEx)condition()).eval(env);
                if (c instanceof Integer && ((Integer)c).intValue() == FALSE)
                    return result;
                else
                    result = ((ASTreeEx)body()).eval(env);
            }
        }
    }
	
}