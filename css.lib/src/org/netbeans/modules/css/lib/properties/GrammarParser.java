/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.css.lib.properties;

import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;
import org.netbeans.modules.css.lib.api.properties.FixedTextGrammarElement;
import org.netbeans.modules.css.lib.api.properties.GrammarElement;
import org.netbeans.modules.css.lib.api.properties.GroupGrammarElement;
import org.netbeans.modules.css.lib.api.properties.Properties;
import org.netbeans.modules.css.lib.api.properties.PropertyDefinition;
import org.netbeans.modules.css.lib.api.properties.TokenAcceptor;
import org.netbeans.modules.css.lib.api.properties.UnitGrammarElement;
import org.netbeans.modules.web.common.api.LexerUtils;

/**
 * Parser of the semi-grammar expressions taken from the w3c.org css specifications.
 * 
 * @author mfukala@netbeans.org
 */
public class GrammarParser {
    
    /**
     * For tests only.
     * 
     * @param expresssion
     * @return 
     */
    public static GroupGrammarElement parse(String expresssion) {
        return parse(expresssion, null);
    }

    public static GroupGrammarElement parse(String expression, String propertyName) {
        return new GrammarParser(expression, propertyName).parse();
    }
    
    private String propertyName;
    private String expression;

    public GrammarParser(String expression, String propertyName) {
        this.expression = expression;
        this.propertyName = propertyName;
    }
    
    private GroupGrammarElement parse() {
        AtomicInteger group_index = new AtomicInteger(0);
        int openedParenthesis = 0;
        GroupGrammarElement root = new GroupGrammarElement(null, group_index.getAndIncrement(), propertyName);
        ParserInput input = new ParserInput(expression);

        parseElements(input, root, false, group_index, openedParenthesis);

        if (openedParenthesis != 0) {
            throw new IllegalStateException(String.format("Property '%s' parsing error - bracket pairs doesn't match: ", propertyName, openedParenthesis));
        }
        return root;
    }
    
    private void parseElements(ParserInput input, GroupGrammarElement parent, boolean ignoreInherits,
            AtomicInteger group_index, int openedParenthesis) {
        GrammarElement last = null;
        for (;;) {
            char c = input.read();
            if (c == Character.MAX_VALUE) {
                return;
            }
            switch (c) {
                case ' ':
                case '\t':
                    //ws, ignore
                    break;
                case '&':
                    char next = input.read();
                    if (next == '&') {
                        //the group is a list
                        parent.setType(GroupGrammarElement.Type.ALL);
                    } else {
                        input.backup(1);
                    }
                    break;
                    
                case '[':
                    openedParenthesis++;
                    //group start
                    last = new GroupGrammarElement(parent, group_index.getAndIncrement());
                    parseElements(input, (GroupGrammarElement) last, false, group_index, openedParenthesis);
                    parent.addElement(last);
                    break;

                case '|':
                    next = input.read();
                    if (next == '|') {
                        //the group is a list
                        parent.setType(GroupGrammarElement.Type.COLLECTION);
                    } else {
                        input.backup(1);
                        parent.setType(GroupGrammarElement.Type.SET);
                        // else it means OR
                    }
                    break;

                case ']':
                    //group end
                    openedParenthesis--;
                    return; //return from parseElements

                case '<':
                    //reference
                    StringBuilder buf = new StringBuilder();
                    for (;;) {
                        c = input.read();
                        if (c == '>') {
                            break;
                        } else {
                            buf.append(c);
                        }
                    }

                    //resolve reference
                    String referredElementName = buf.toString();
                    PropertyDefinition property = Properties.getPropertyDefinition(referredElementName, true);
                    if (property == null) {
                        throw new IllegalStateException(
                                String.format("Property '%s' parsing error: No referred element '%s' found. "
                                + "Read input: %s", propertyName, referredElementName, input.readText())); //NOI18N
                    }

                    ParserInput pinput = new ParserInput(property.getGrammar());
                    String propName = property.getName();
                    last = new GroupGrammarElement(parent, group_index.getAndIncrement(), propName);


                    //ignore inherit tokens in the subtree
                    parseElements(pinput, (GroupGrammarElement) last, true, group_index, openedParenthesis);

                    parent.addElement(last);
                    break;

                case '!':
                    //unit value
                    buf = new StringBuilder();
                    for (;;) {
                        c = input.read();
                        if (c == Character.MAX_VALUE) {
                            break;
                        }
                        if (isEndOfValue(input)) {
                            input.backup(1);
                            break;
                        } else {
                            buf.append(c);
                        }
                    }
                    String unitName = buf.toString();
                    TokenAcceptor acceptor = TokenAcceptor.getAcceptor(unitName);
                    if(acceptor == null) {
                        throw new IllegalStateException(
                                String.format("Property '%s' parsing error - No unit property value acceptor for '%s'. "
                                + "Read input: '%s'",
                                propertyName, unitName, input.readText())); //NOI18N
                    }
                    
                    last = new UnitGrammarElement(parent, acceptor, null);
                    parent.addElement(last);
                    break;

                case '{':
                    //multiplicity range {min,max}
                    StringBuilder text = new StringBuilder();
                    for (;;) {
                        c = input.read();
                        if (c == '}') {
                            break;
                        } else {
                            text.append(c);
                        }
                    }
                    StringTokenizer st = new StringTokenizer(text.toString(), ","); //NOI18N
                    int min = Integer.parseInt(st.nextToken());
                    int max = Integer.parseInt(st.nextToken());

                    last.setMinimumOccurances(min);
                    last.setMaximumOccurances(max);

                    break;

                case '+':
                    //multiplicity 1-infinity
                    last.setMaximumOccurances(Integer.MAX_VALUE);
                    break;

                case '*':
                    //multiplicity 0-infinity
                    last.setMinimumOccurances(0);
                    last.setMaximumOccurances(Integer.MAX_VALUE);
                    break;

                case '?':
                    //multiplicity 0-1
                    last.setMinimumOccurances(0);
                    last.setMaximumOccurances(1);
                    break;

                case '(':
                    //named elements support; syntax: element($name)
                    char ch = input.read();
                    if(ch == '$') {
                        buf = new StringBuilder();
                        for (;;) {
                            ch = input.read();
                            if (ch == ')') {
                                break;
                            } else {
                                buf.append(ch);
                            }
                        }
                        last.setName(buf.toString());
                        break;
                    }
                    
                    input.backup(1);
                    //intentional fallthrough to the default case!
                    //if the bracket ( is not followed by $ the meaning is 
                    //a simple value.

                default:
                    //values
                    buf = new StringBuilder();
                    boolean quotes = isQuoteChar(c);
                    if(quotes) {
                        c = input.read();
                    }
                    
                    for (;;) {
                        if (c == Character.MAX_VALUE) {
                            break;
                        }
                        if(quotes) {
                            //quoted value - anything except the quote is considered
                            //as the value char
                            if(isQuoteChar(c)) {
                                //closing quote, do not backup
                                break; 
                            }
                        } else {
                            //unqouted value - end the value by various characters
                            if(isEndOfValue(input)) {
                                input.backup(1);
                                break;
                            }
                        }
                        
                        //append the char to the value
                        buf.append(c);
                        
                        c = input.read(); //also include the char from main loop

                    }

                    if (!(ignoreInherits && LexerUtils.equals("inherit", buf, true, true))) { //NOI18N
                        last = new FixedTextGrammarElement(parent, buf, null);
                        parent.addElement(last);
                    }
                    break;

            }
        }

    }

    private static boolean isEndOfValue(ParserInput input) {
        char c = input.LA(0);
        switch(c) {
            case ' ': //ws after the element
            case '+': //multiplicity operator
            case '?': //multiplicity operator
            case '*': //multiplicity operator
            case '&': //first char of the and (&&) operator
            case '{': //multiplicity in curly bracket
            case '[': //following group start
            case ']': //current group end
            case '|': //first char of || operator or | operator itself
                return true;
                
            case '(': //element name start, must be followed by $ to be the termination char
                return input.LA(1) == '$';
                
            default:
                return false;
        }
        
    }

    private static boolean isQuoteChar(char c) {
        return c == '\'' || c == '"';
    }

    private static class ParserInput {

        CharSequence text;
        private int pos = 0;

        private ParserInput(CharSequence text) {
            this.text = text;
        }

        public char read() {
            if (pos == text.length()) {
                return Character.MAX_VALUE;
            } else {
                return text.charAt(pos++);
            }
        }
        
        /**
         * lookahead 
         * 
         * la(0) == last read char
         * la(1) == next char, as read() + backup(1)
         */
        public char LA(int lookahead) {
            //when a char is read, the pointer is moved 
            //to the next position so if we want to la(0) 
            //to return the last read char we need to read 
            //from pos - 1 position
            int dec = pos == 0 ? 0 : 1;
            int la_pos = pos - dec + lookahead;
            if(la_pos >= text.length()) {
                return Character.MAX_VALUE;
            } else {
                return text.charAt(la_pos);
            }
        }

        public void backup(int chars) {
            pos -= chars;
        }

        public CharSequence readText() {
            return text.subSequence(0, pos);
        }
    }
}
