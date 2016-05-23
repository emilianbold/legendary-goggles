/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */

//----------------------------------------------------
// The following code was generated by CUP v0.11a beta 20060608
// Fri May 20 08:13:41 CEST 2016
//----------------------------------------------------

package org.netbeans.modules.php.editor.parser;

/** CUP generated interface containing symbol constants. */
public interface ASTPHP5Symbols {
  /* terminals */
  public static final int T_BOOLEAN_AND = 100;
  public static final int T_INLINE_HTML = 10;
  public static final int T_EMPTY = 47;
  public static final int T_PROTECTED = 143;
  public static final int T_CLOSE_RECT = 134;
  public static final int T_IS_NOT_EQUAL = 105;
  public static final int T_INCLUDE = 75;
  public static final int T_QUATE = 149;
  public static final int T_GLOBAL = 43;
  public static final int T_PRINT = 84;
  public static final int T_OR_EQUAL = 93;
  public static final int T_LOGICAL_XOR = 82;
  public static final int T_COALESCE = 158;
  public static final int T_FUNCTION = 33;
  public static final int T_STATIC = 139;
  public static final int T_NEKUDA = 122;
  public static final int T_THROW = 40;
  public static final int T_CLASS = 49;
  public static final int T_ABSTRACT = 140;
  public static final int T_ENCAPSED_AND_WHITESPACE = 11;
  public static final int T_MOD_EQUAL = 91;
  public static final int T_BREAK = 30;
  public static final int T_WHILE = 15;
  public static final int T_DO = 14;
  public static final int T_CONST = 34;
  public static final int T_CONTINUE = 31;
  public static final int T_FUNC_C = 59;
  public static final int T_DIV = 118;
  public static final int T_LOGICAL_OR = 81;
  public static final int T_DIR = 71;
  public static final int T_OPEN_PARENTHESE = 145;
  public static final int T_REFERENCE = 103;
  public static final int T_COMMA = 80;
  public static final int T_FINALLY = 41;
  public static final int T_ELSE = 138;
  public static final int T_IS_EQUAL = 104;
  public static final int T_LIST = 55;
  public static final int T_NAMESPACE = 69;
  public static final int T_NS_SEPARATOR = 72;
  public static final int T_OR = 101;
  public static final int T_IS_IDENTICAL = 106;
  public static final int T_INC = 123;
  public static final int T_ELSEIF = 137;
  public static final int T_TRY = 38;
  public static final int T_START_NOWDOC = 151;
  public static final int T_PRIVATE = 142;
  public static final int T_UNSET_CAST = 131;
  public static final int T_INCLUDE_ONCE = 76;
  public static final int T_ENDIF = 136;
  public static final int T_SR_EQUAL = 96;
  public static final int EOF = 0;
  public static final int T_PUBLIC = 144;
  public static final int T_OBJECT_OPERATOR = 53;
  public static final int T_TILDA = 121;
  public static final int T_PAAMAYIM_NEKUDOTAYIM = 68;
  public static final int T_IS_SMALLER_OR_EQUAL = 108;
  public static final int T_ELLIPSIS = 157;
  public static final int T_XOR_EQUAL = 94;
  public static final int T_ENDFOREACH = 20;
  public static final int T_CONSTANT_ENCAPSED_STRING = 12;
  public static final int T_BACKQUATE = 150;
  public static final int T_AT = 132;
  public static final int T_AS = 25;
  public static final int T_CURLY_CLOSE = 67;
  public static final int T_ENDDECLARE = 22;
  public static final int T_CATCH = 39;
  public static final int T_CASE = 28;
  public static final int T_VARIABLE = 8;
  public static final int T_INSTEADOF = 154;
  public static final int T_NEW = 135;
  public static final int T_MINUS_EQUAL = 87;
  public static final int T_PLUS = 115;
  public static final int T_SL_EQUAL = 95;
  public static final int T_ENDWHILE = 16;
  public static final int T_ENDFOR = 18;
  public static final int T_TRAIT = 153;
  public static final int T_CLONE = 24;
  public static final int T_BOOLEAN_OR = 99;
  public static final int T_UNSET = 45;
  public static final int T_INTERFACE = 50;
  public static final int T_SWITCH = 26;
  public static final int T_IS_GREATER_OR_EQUAL = 109;
  public static final int T_OPEN_RECT = 133;
  public static final int T_CURLY_OPEN_WITH_DOLAR = 65;
  public static final int T_FINAL = 141;
  public static final int T_REQUIRE = 78;
  public static final int T_FILE = 61;
  public static final int T_DEC = 124;
  public static final int T_CLOSE_PARENTHESE = 146;
  public static final int T_CLASS_C = 57;
  public static final int T_EVAL = 77;
  public static final int T_POW = 155;
  public static final int T_RGREATER = 111;
  public static final int T_IS_NOT_IDENTICAL = 107;
  public static final int T_NOT = 120;
  public static final int T_REQUIRE_ONCE = 79;
  public static final int T_POW_EQUAL = 156;
  public static final int T_NS_C = 70;
  public static final int T_DOLLAR_OPEN_CURLY_BRACES = 64;
  public static final int T_SPACESHIP = 110;
  public static final int T_VAR = 44;
  public static final int T_START_HEREDOC = 62;
  public static final int T_ENDSWITCH = 27;
  public static final int T_OBJECT_CAST = 129;
  public static final int T_ECHO = 13;
  public static final int T_LINE = 60;
  public static final int T_FOR = 17;
  public static final int T_IMPLEMENTS = 52;
  public static final int T_ARRAY_CAST = 128;
  public static final int T_DOLLAR = 148;
  public static final int T_TIMES = 117;
  public static final int T_DOUBLE_CAST = 126;
  public static final int T_BOOL_CAST = 130;
  public static final int T_PRECENT = 119;
  public static final int T_LNUMBER = 4;
  public static final int T_CURLY_OPEN = 66;
  public static final int T_DEFINE = 74;
  public static final int T_QUESTION_MARK = 97;
  public static final int T_END_NOWDOC = 152;
  public static final int T_USE = 42;
  public static final int T_KOVA = 102;
  public static final int T_IF = 3;
  public static final int T_MUL_EQUAL = 88;
  public static final int T_ARRAY = 56;
  public static final int T_LGREATER = 112;
  public static final int T_SEMICOLON = 98;
  public static final int T_NEKUDOTAIM = 147;
  public static final int T_VAR_COMMENT = 73;
  public static final int T_CONCAT_EQUAL = 90;
  public static final int T_YIELD = 36;
  public static final int T_AND_EQUAL = 92;
  public static final int T_DNUMBER = 5;
  public static final int T_MINUS = 116;
  public static final int T_FOREACH = 19;
  public static final int T_EXIT = 2;
  public static final int T_DECLARE = 21;
  public static final int T_STRING_VARNAME = 7;
  public static final int T_EXTENDS = 51;
  public static final int T_METHOD_C = 58;
  public static final int T_INT_CAST = 125;
  public static final int T_ISSET = 46;
  public static final int T_LOGICAL_AND = 83;
  public static final int error = 1;
  public static final int T_RETURN = 35;
  public static final int T_DEFAULT = 29;
  public static final int T_SR = 114;
  public static final int T_YIELD_FROM = 37;
  public static final int T_EQUAL = 85;
  public static final int T_SL = 113;
  public static final int T_END_HEREDOC = 63;
  public static final int T_DOUBLE_ARROW = 54;
  public static final int T_STRING_CAST = 127;
  public static final int T_STRING = 6;
  public static final int T_PLUS_EQUAL = 86;
  public static final int T_INSTANCEOF = 23;
  public static final int T_DIV_EQUAL = 89;
  public static final int T_NUM_STRING = 9;
  public static final int T_HALT_COMPILER = 48;
  public static final int T_GOTO = 32;
}

