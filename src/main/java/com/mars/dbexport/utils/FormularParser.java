package com.mars.dbexport.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Simple formular parser, support : + - × ÷ & | >> << and parentheses Just
 * support integers at present
 * 
 * @author Yao
 * 
 */
public class FormularParser {
	final static String add = "+";
	final static String sub = "-";
	final static String mul = "*";
	final static String div = "/";
	final static String and = "&";
	final static String or = "|";
	final static String left = "(";
	final static String right = ")";
	final static String shift_l = "<<";
	final static String shift_r = ">>";
	private final static List<String> operators = new ArrayList<String>();
	static {
		operators.add(shift_r);
		operators.add(shift_l);
		operators.add(or);
		operators.add(and);
		operators.add(div);
		operators.add(mul);
		operators.add(sub);
		operators.add(add);
	}

	private static boolean validFormular(String formular) {
		if (StringUtils.isEmpty(formular))
			return false;
		if (findCharNum(formular, ')') != findCharNum(formular, '('))
			return false;
		String regx = "((<<)|(>>)|[0-9+\\-*/&|()]|(\\{[0-9]+\\}))+";
		Pattern pat = Pattern.compile(regx);
		Matcher mat = pat.matcher(formular);
		if (!mat.matches())
			return false;
		return true;
	}

	private static int findCharNum(String src, char target) {
		int num = 0;
		for (char c : src.toCharArray()) {
			if (c == target)
				num++;
		}
		return num;
	}

	public static long calculateFormularValue(String formular) {
		if (!validFormular(formular))
			return 0;
		while (formular.contains(left)) {
			int sp = formular.indexOf(left);
			int ep = findNearPair(formular, sp);
			long subvalue = calculateFormularValue(formular.substring(sp + 1,
					ep));
			String subv = subvalue >= 0 ? subvalue + "" : ":" + (-subvalue);
			formular = replaceSubString(formular, subv, sp, ep+1);
		}
		return calculatePureFormular(formular);
	}

	private static long calculatePureFormular(String subformular) {
		for (String oper : operators) {
			while (subformular.contains(oper)) {
				String regx = "[:]?[0-9]+(\\" + oper + ")[:]?[0-9]+";
				Pattern pat = Pattern.compile(regx);
				Matcher mat = pat.matcher(subformular);
				if (mat.find()) {
					String group = mat.group();
					int pos = mat.start();
					long sbvalue = calculateCellFormular(group, oper);
					String subv = sbvalue >= 0 ? sbvalue + "" : ":" + (-sbvalue);
					subformular = replaceSubString(subformular, subv, pos, pos
							+ group.length());
				}
			}
		}

		return parseLong(subformular);
	}

	private static long calculateCellFormular(String cellformular, String oper) {
		int point = cellformular.indexOf(oper);
		long vv1 = parseLong(cellformular.substring(0, point));
		long vv2 = parseLong(cellformular.substring(point + oper.length()));
		if (add.equals(oper)) {
			return vv1 + vv2;
		} else if (sub.equals(oper)) {
			return vv1 - vv2;
		} else if (mul.equals(oper)) {
			return vv1 * vv2;
		} else if (div.equals(oper)) {
			return vv1 / vv2;
		} else if (and.equals(oper)) {
			return vv1 & vv2;
		} else if (or.equals(oper)) {
			return vv1 | vv2;
		} else if (shift_l.equals(oper)) {
			return vv1 << vv2;
		} else if (shift_r.equals(oper)) {
			return vv1 >> vv2;
		}
		return 0;
	}

	private static boolean isNumber(String src) {
		String regx = "[:\\-]?[0-9]+";
		Pattern pat = Pattern.compile(regx);
		Matcher mat = pat.matcher(src);
		return mat.matches();
	}

	private static long parseLong(String src) {
		if (isNumber(src)) {
			if (src.startsWith("-") || src.startsWith(":")) {
				return -Long.parseLong(src.substring(1));
			} else {
				return Long.parseLong(src);
			}
		}
		return 0;
	}

	private static String replaceSubString(String src, String target,
			int start, int end) {
		if (start < 0 || start >= end || end > src.length())
			return "";
		StringBuilder sb = new StringBuilder();
		if (start > 0) {
			sb.append(src.substring(0, start));
		}
		sb.append(target);
		if (end < src.length() - 1) {
			sb.append(src.substring(end));
		}
		return sb.toString();
	}

	public static int findNearPair(String src, int local) {
		if (src.length() <= local)
			return -1;
		char sc = src.charAt(local);
		char ec = 0;
		if (sc == '(') {
			ec = ')';
		} else if (sc == '[') {
			ec = ']';
		} else if (sc == '{') {
			ec = '}';
		} else {
			return -1;
		}

		int cs = 0;
		int ce = 0;
		int idx = 0;
		boolean find = false;
		for (idx = local; idx < src.length(); idx++) {
			char tc = src.charAt(idx);
			if (tc == sc) {
				cs++;
			} else if (tc == ec) {
				ce++;
			}

			if (cs == ce) {
				find = true;
				break;
			}
		}

		return find ? idx : -1;
	}

	public static String findNearPairString(String src, int local) {
		int pair = findNearPair(src, local);
		return pair > 0 ? src.substring(local + 1, pair) : "";
	}

	public static List<String> findPairs(String src) {
		List<String> pairs = new ArrayList<String>();
		int sc = 0;
		int fc = -1;
		while (true) {
			fc = findNearPair(src, sc);
			if (fc < 0)
				break;
			pairs.add(src.substring(sc + 1, fc));
			sc = fc + 1;
		}
		return pairs;
	}

	public static void main(String[] args) {
		String str = "1+2-110*(10+10*(3-5))+55";
		System.out.println(calculateFormularValue(str));
	}
}
