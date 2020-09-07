package net.karanteeni.utilika.calculator;

import java.math.BigDecimal;

public class Calculator {
	public static BigDecimal eval(final String str) {
	    return new Object() {
	        int pos = -1, ch;

	        void nextChar() {
	            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }

	        boolean eat(int charToEat) {
	            while (ch == ' ') nextChar();
	            if (ch == charToEat) {
	                nextChar();
	                return true;
	            }
	            return false;
	        }

	        BigDecimal parse() {
	            nextChar();
	            BigDecimal x = parseExpression();
	            if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
	            return x;
	        }

	        // Grammar:
	        // expression = term | expression `+` term | expression `-` term
	        // term = factor | term `*` factor | term `/` factor
	        // factor = `+` factor | `-` factor | `(` expression `)`
	        //        | number | functionName factor | factor `^` factor

	        BigDecimal parseExpression() {
	        	BigDecimal x = parseTerm();
	            for (;;) {
	                if      (eat('+')) x = x.add(parseTerm()); // addition
	                else if (eat('-')) x = x.subtract(parseTerm()); // subtraction
	                else return x;
	            }
	        }

	        BigDecimal parseTerm() {
	        	BigDecimal x = parseFactor();
	            for (;;) {
	                if      (eat('*')) x = x.multiply(parseFactor()); // multiplication
	                else if (eat('/')) x = x.divide(parseFactor()); // division
	                else return x;
	            }
	        }

	        BigDecimal parseFactor() {
	            if (eat('+')) return parseFactor(); // unary plus
	            if (eat('-')) return parseFactor().negate(); // unary minus

	            BigDecimal x;
	            int startPos = this.pos;
	            if (eat('(')) { // parentheses
	                x = parseExpression();
	                eat(')');
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	                x = new BigDecimal(str.substring(startPos, this.pos));
	            } else if (ch >= 'a' && ch <= 'z') { // functions
	                while (ch >= 'a' && ch <= 'z') nextChar();
	                String func = str.substring(startPos, this.pos);
	                x = parseFactor();
	                //if (func.equals("sqrt")) x = Math.sqrt(x);
	                //else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
	                //else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
	                //else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
	                //else
	                	throw new RuntimeException("Unknown function: " + func);
	            } else {
	                throw new RuntimeException("Unexpected: " + (char)ch);
	            }

	            //if (eat('^')) x = x.pow(parseFactor().intValue()); // exponentiation

	            return x;
	        }
	    }.parse();
	}
}
