
//----------------------------------------------------
// The following code was generated by CUP v0.11a beta 20060608
// Fri Jun 13 13:53:03 GMT+01:00 2008
//----------------------------------------------------

package org.netbeans.modules.php.editor.parser;

/** CUP generated interface containing symbol constants. */
public interface ASTPHP5Symbols {
  /* terminals */
  public static final int T_BOOLEAN_AND = 93;
  public static final int T_INLINE_HTML = 10;
  public static final int T_EMPTY = 44;
  public static final int T_PROTECTED = 135;
  public static final int T_CLOSE_RECT = 126;
  public static final int T_IS_NOT_EQUAL = 98;
  public static final int T_INCLUDE = 68;
  public static final int T_QUATE = 141;
  public static final int T_GLOBAL = 40;
  public static final int T_PRINT = 77;
  public static final int T_OR_EQUAL = 86;
  public static final int T_LOGICAL_XOR = 75;
  public static final int T_FUNCTION = 33;
  public static final int T_STATIC = 131;
  public static final int T_NEKUDA = 114;
  public static final int T_THROW = 38;
  public static final int T_CLASS = 46;
  public static final int T_ABSTRACT = 132;
  public static final int T_ENCAPSED_AND_WHITESPACE = 11;
  public static final int T_MOD_EQUAL = 84;
  public static final int T_BREAK = 31;
  public static final int T_WHILE = 16;
  public static final int T_DO = 15;
  public static final int T_CONST = 34;
  public static final int T_CONTINUE = 32;
  public static final int T_FUNC_C = 56;
  public static final int T_DIV = 110;
  public static final int T_LOGICAL_OR = 74;
  public static final int T_OPEN_PARENTHESE = 137;
  public static final int T_REFERENCE = 96;
  public static final int T_COMMA = 73;
  public static final int T_ELSE = 130;
  public static final int T_IS_EQUAL = 97;
  public static final int T_LIST = 52;
  public static final int T_OR = 94;
  public static final int T_IS_IDENTICAL = 99;
  public static final int T_INC = 115;
  public static final int T_ELSEIF = 129;
  public static final int T_TRY = 36;
  public static final int T_START_NOWDOC = 143;
  public static final int T_PRIVATE = 134;
  public static final int T_UNSET_CAST = 123;
  public static final int T_INCLUDE_ONCE = 69;
  public static final int T_ENDIF = 128;
  public static final int T_SR_EQUAL = 89;
  public static final int EOF = 0;
  public static final int T_PUBLIC = 136;
  public static final int T_OBJECT_OPERATOR = 50;
  public static final int T_TILDA = 113;
  public static final int T_PAAMAYIM_NEKUDOTAYIM = 65;
  public static final int T_IS_SMALLER_OR_EQUAL = 101;
  public static final int T_XOR_EQUAL = 87;
  public static final int T_ENDFOREACH = 21;
  public static final int T_CONSTANT_ENCAPSED_STRING = 12;
  public static final int T_BACKQUATE = 142;
  public static final int T_AT = 124;
  public static final int T_AS = 26;
  public static final int T_CURLY_CLOSE = 64;
  public static final int T_ENDDECLARE = 23;
  public static final int T_CATCH = 37;
  public static final int T_CASE = 29;
  public static final int T_VARIABLE = 8;
  public static final int T_NEW = 127;
  public static final int T_MINUS_EQUAL = 80;
  public static final int T_PLUS = 107;
  public static final int T_SL_EQUAL = 88;
  public static final int T_ENDWHILE = 17;
  public static final int T_ENDFOR = 19;
  public static final int T_CLONE = 25;
  public static final int T_BOOLEAN_OR = 92;
  public static final int T_UNSET = 42;
  public static final int T_OPEN_TAG = 14;
  public static final int T_INTERFACE = 47;
  public static final int T_SWITCH = 27;
  public static final int T_IS_GREATER_OR_EQUAL = 102;
  public static final int T_OPEN_RECT = 125;
  public static final int T_CURLY_OPEN_WITH_DOLAR = 62;
  public static final int T_FINAL = 133;
  public static final int T_REQUIRE = 71;
  public static final int T_FILE = 58;
  public static final int T_DEC = 116;
  public static final int T_CLOSE_PARENTHESE = 138;
  public static final int T_CLASS_C = 54;
  public static final int T_EVAL = 70;
  public static final int T_RGREATER = 103;
  public static final int T_IS_NOT_IDENTICAL = 100;
  public static final int T_NOT = 112;
  public static final int T_REQUIRE_ONCE = 72;
  public static final int T_DOLLAR_OPEN_CURLY_BRACES = 61;
  public static final int T_VAR = 41;
  public static final int T_START_HEREDOC = 59;
  public static final int T_ENDSWITCH = 28;
  public static final int T_OBJECT_CAST = 121;
  public static final int T_ECHO = 13;
  public static final int T_LINE = 57;
  public static final int T_FOR = 18;
  public static final int T_IMPLEMENTS = 49;
  public static final int T_ARRAY_CAST = 120;
  public static final int T_TIMES = 109;
  public static final int T_DOLLAR = 140;
  public static final int T_DOUBLE_CAST = 118;
  public static final int T_BOOL_CAST = 122;
  public static final int T_PRECENT = 111;
  public static final int T_LNUMBER = 4;
  public static final int T_CURLY_OPEN = 63;
  public static final int T_DEFINE = 67;
  public static final int T_QUESTION_MARK = 90;
  public static final int T_END_NOWDOC = 144;
  public static final int T_USE = 39;
  public static final int T_KOVA = 95;
  public static final int T_IF = 3;
  public static final int T_MUL_EQUAL = 81;
  public static final int T_ARRAY = 53;
  public static final int T_LGREATER = 104;
  public static final int T_SEMICOLON = 91;
  public static final int T_NEKUDOTAIM = 139;
  public static final int T_VAR_COMMENT = 66;
  public static final int T_CONCAT_EQUAL = 83;
  public static final int T_AND_EQUAL = 85;
  public static final int T_DNUMBER = 5;
  public static final int T_MINUS = 108;
  public static final int T_FOREACH = 20;
  public static final int T_EXIT = 2;
  public static final int T_DECLARE = 22;
  public static final int T_STRING_VARNAME = 7;
  public static final int T_EXTENDS = 48;
  public static final int T_METHOD_C = 55;
  public static final int T_INT_CAST = 117;
  public static final int T_ISSET = 43;
  public static final int T_LOGICAL_AND = 76;
  public static final int error = 1;
  public static final int T_RETURN = 35;
  public static final int T_DEFAULT = 30;
  public static final int T_SR = 106;
  public static final int T_EQUAL = 78;
  public static final int T_SL = 105;
  public static final int T_END_HEREDOC = 60;
  public static final int T_DOUBLE_ARROW = 51;
  public static final int T_STRING_CAST = 119;
  public static final int T_STRING = 6;
  public static final int T_PLUS_EQUAL = 79;
  public static final int T_INSTANCEOF = 24;
  public static final int T_DIV_EQUAL = 82;
  public static final int T_NUM_STRING = 9;
  public static final int T_HALT_COMPILER = 45;
}

