/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */

package org.netbeans.modules.groovy.gsp.editor.bracesmatcher;

import javax.swing.text.BadLocationException;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.groovy.gsp.lexer.GspLexerLanguage;
import org.netbeans.modules.groovy.gsp.lexer.GspTokenId;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;

/**
 * Braces matcher for GTags matching and coloring.
 *
 * @author Martin Janicek
 */
public class GspBracesMatcher implements BracesMatcher {

    private MatcherContext context;
    private BaseDocument document;


    GspBracesMatcher(MatcherContext context) {
        this.context = context;
        this.document = (BaseDocument) context.getDocument();
    }

    @Override
    public int[] findOrigin() throws InterruptedException, BadLocationException {
        document.readLock();
        try {
            TokenHierarchy<BaseDocument> tokenHierarchy = TokenHierarchy.get(document);
            TokenSequence<GspTokenId> tokenSequence = tokenHierarchy.tokenSequence(GspLexerLanguage.getLanguage());

            if (tokenSequence == null) {
                return null;
            }

            tokenSequence.move(context.getSearchOffset());
            tokenSequence.moveNext();
            Token<GspTokenId> gspToken = tokenSequence.token();

            if (gspToken == null || isIndependentTag(tokenSequence)) {
                return null;
            }
            return findTagBoundaries(tokenSequence, gspToken);

        } finally {
            document.readUnlock();
        }
    }

    @Override
    public int[] findMatches() throws InterruptedException, BadLocationException {
        document.readLock();
        try {
            TokenHierarchy<BaseDocument> tokenHierarchy = TokenHierarchy.get(document);
            TokenSequence<GspTokenId> tokenSequence = tokenHierarchy.tokenSequence(GspLexerLanguage.getLanguage());

            if (tokenSequence == null) {
                return null;
            }

            tokenSequence.move(context.getSearchOffset());
            tokenSequence.moveNext();
            Token<GspTokenId> gspToken = tokenSequence.token();

            if (gspToken == null) {
                return null;
            }

            Token<GspTokenId> matchingTokenId;
            switch (gspToken.id()) {
                case GTAG_OPENING_START:
                case GTAG_OPENING_NAME:
                case GTAG_OPENING_END:
                case GTAG_ATTRIBUTE_VALUE:
                case GTAG_ATTRIBUTE_NAME:
                    matchingTokenId = findClosingToken(tokenSequence, GspTokenId.GTAG_OPENING_START, GspTokenId.GTAG_CLOSING_START, GspTokenId.GTAG_INDEPENDENT_END); break;
                case GTAG_CLOSING_START:
                case GTAG_CLOSING_NAME:
                case GTAG_CLOSING_END:
                    matchingTokenId = findOpeningToken(tokenSequence, GspTokenId.GTAG_OPENING_END, GspTokenId.GTAG_CLOSING_END); break;
                case COMMENT_JSP_STYLE_START:
                    matchingTokenId = findClosingToken(tokenSequence, GspTokenId.COMMENT_JSP_STYLE_START, GspTokenId.COMMENT_JSP_STYLE_END, null); break;
                case COMMENT_JSP_STYLE_END:
                    matchingTokenId = findOpeningToken(tokenSequence, GspTokenId.COMMENT_JSP_STYLE_START, GspTokenId.COMMENT_JSP_STYLE_END); break;
                case COMMENT_GSP_STYLE_START:
                    matchingTokenId = findClosingToken(tokenSequence, GspTokenId.COMMENT_GSP_STYLE_START, GspTokenId.COMMENT_GSP_STYLE_END, null); break;
                case COMMENT_GSP_STYLE_END:
                    matchingTokenId = findOpeningToken(tokenSequence, GspTokenId.COMMENT_GSP_STYLE_START, GspTokenId.COMMENT_GSP_STYLE_END); break;
                case COMMENT_HTML_STYLE_START:
                    matchingTokenId = findClosingToken(tokenSequence, GspTokenId.COMMENT_HTML_STYLE_START, GspTokenId.COMMENT_HTML_STYLE_END, null); break;
                case COMMENT_HTML_STYLE_END:
                    matchingTokenId = findOpeningToken(tokenSequence, GspTokenId.COMMENT_HTML_STYLE_START, GspTokenId.COMMENT_HTML_STYLE_END); break;
                default:
                    return null;

            }

            return findTagBoundaries(tokenSequence, matchingTokenId);

        } finally {
            document.readUnlock();
        }
    }

    private boolean isIndependentTag(TokenSequence<GspTokenId> tokenSequence) {
        INFINITE_LOOP:
        while (true) {
            if (MatcherContext.isTaskCanceled()) {
                break;
            }

            switch (tokenSequence.token().id()) {
                case GTAG_INDEPENDENT_END:
                    return true;
                case GTAG_OPENING_END:
                case GTAG_CLOSING_END:
                case WHITESPACE:
                    return false;
                default:
                    if (!tokenSequence.moveNext()) {
                        break INFINITE_LOOP;
                    }
            }

        }
        return false;
    }

    private int[] findTagBoundaries(TokenSequence<GspTokenId> tokenSequence, Token<GspTokenId> gspTokenID) {
        TokenHierarchy<BaseDocument> tokenHierarchy = TokenHierarchy.get(document);

        int tagStart;
        int tagEnd;
        switch (gspTokenID.id()) {
            case COMMENT_GSP_STYLE_START:
            case COMMENT_GSP_STYLE_END:
            case COMMENT_JSP_STYLE_START:
            case COMMENT_JSP_STYLE_END:
            case COMMENT_HTML_STYLE_START:
            case COMMENT_HTML_STYLE_END:
                tagStart = gspTokenID.offset(tokenHierarchy);
                tagEnd = tagStart + gspTokenID.length();
                break;
            case GTAG_OPENING_START:
                tagStart = gspTokenID.offset(tokenHierarchy);
                tagEnd = findForwards(tokenSequence, GspTokenId.GTAG_OPENING_END);
                break;
            case GTAG_OPENING_NAME:
            case GTAG_ATTRIBUTE_NAME:
            case GTAG_ATTRIBUTE_VALUE:
                tagStart = findBackwards(tokenSequence, GspTokenId.GTAG_OPENING_START);
                tagEnd = findForwards(tokenSequence, GspTokenId.GTAG_OPENING_END);
                break;
            case GTAG_OPENING_END:
                tagStart = findBackwards(tokenSequence, GspTokenId.GTAG_OPENING_START);
                tagEnd = gspTokenID.offset(tokenHierarchy) + gspTokenID.length();
                break;

            case GTAG_CLOSING_START:
                tagStart = gspTokenID.offset(tokenHierarchy);
                tagEnd = findForwards(tokenSequence, GspTokenId.GTAG_CLOSING_END);
                break;
            case GTAG_CLOSING_NAME:
                tagStart = findBackwards(tokenSequence, GspTokenId.GTAG_CLOSING_START);
                tagEnd = findForwards(tokenSequence, GspTokenId.GTAG_CLOSING_END);
                break;
            case GTAG_CLOSING_END:
                tagStart = findBackwards(tokenSequence, GspTokenId.GTAG_CLOSING_START);
                tagEnd = gspTokenID.offset(tokenHierarchy) + gspTokenID.length();
                break;
            default:
                return null;

        }
        return new int[] {tagStart, tagEnd};
    }

    /**
     * Iterates through the <code>tokenSequence</code> backwards and return the first
     * match with <code>tokenID</code>. If you would like to search forwards, please
     * use {@link #findForwards(org.netbeans.api.lexer.TokenSequence, org.netbeans.modules.groovy.gsp.lexer.GspTokenId)}
     * method instead.
     *
     * @param tokenSequence we are iterating through
     * @param tokenID token we are looking for
     * @return offset of the first match token or zero if nothing has been found
     *         or if the task was canceled
     */
    private int findBackwards(TokenSequence<GspTokenId> tokenSequence, GspTokenId tokenID) {
        while (true) {
            if (MatcherContext.isTaskCanceled()) {
                break;
            }

            Token<GspTokenId> nextGspToken = tokenSequence.token();
            if (nextGspToken.id() == tokenID) {
                return nextGspToken.offset(TokenHierarchy.get(document));
            }
            if (!tokenSequence.movePrevious()) {
                break;
            }
        }
        return 0;
    }

    /**
     * Iterates through the <code>tokenSequence</code> forwards and return the first
     * match with <code>tokenID</code>. If you would like to search backwards, please
     * use {@link #findBackwards(org.netbeans.api.lexer.TokenSequence, org.netbeans.modules.groovy.gsp.lexer.GspTokenId)}
     * method instead.
     *
     * @param tokenSequence we are iterating through
     * @param tokenID token we are looking for
     * @return offset of the first match token or zero if nothing has been found
     *         or if the task was canceled
     */
    private int findForwards(TokenSequence<GspTokenId> tokenSequence, GspTokenId tokenID) {
        while (true) {
            if (MatcherContext.isTaskCanceled()) {
                break;
            }

            Token<GspTokenId> nextToken = tokenSequence.token();
            if (nextToken.id() == tokenID) {
                return nextToken.offset(TokenHierarchy.get(document)) + nextToken.length();
            }
            if (!tokenSequence.moveNext()) {
                break;
            }
        }
        return 0;
    }

    /**
     * Search forwards in the token sequence until a corresponding token of type
     * <code>closingTokenID</code> is found. It doesn't matter whether there are
     * some other pairs of the same Open/Close tokenIDs between. The methods counts
     * the number of pairs inside and find appropriate one.
     *
     * @param tokenSequence sequence that we are iterating over
     * @param openingTokenID tokenID which opens the pair
     * @param closingTokenID tokenID which closes the pair
     * @param independentTokenID tokenID which closes the independent tag
     * @return OffsetRange of the closing tokenID
     */
    private Token<GspTokenId> findClosingToken(
            TokenSequence<GspTokenId> tokenSequence,
            TokenId openingTokenID,
            TokenId closingTokenID,
            TokenId independentTokenID) {
        int balance = 0;

        while (tokenSequence.moveNext()) {
            if (MatcherContext.isTaskCanceled()) {
                return null;
            }

            Token<GspTokenId> token = tokenSequence.token();
            TokenId tokenID = token.id();

            if (tokenID == openingTokenID) {
                balance++;
            } else if (tokenID == independentTokenID) {
                balance--;
            } else if (tokenID == closingTokenID) {
                if (balance == 0) {
                    return token;
                }
                balance--;
            }
        }

        return null;
    }

    /**
     * Search backwards in the token sequence until a corresponding token of type
     * <code>openingTokenID</code> is found. It doesn't matter whether there are
     * some other pairs of the same Open/Close tokenIDs between. The methods counts
     * the number of pairs inside and find appropriate one.
     *
     * @param tokenSequence sequence that we are iterating over
     * @param openingTokenID tokenID which opens the pair
     * @param closingTokenID tokenID which closes the pair
     * @return OffsetRange of the opening tokenID
     */
    private Token<GspTokenId> findOpeningToken(TokenSequence<GspTokenId> tokenSequence, TokenId openingTokenID, TokenId closingTokenID) {
        int balance = 0;

        while (tokenSequence.movePrevious()) {
            if (MatcherContext.isTaskCanceled()) {
                return null;
            }

            Token<GspTokenId> token = tokenSequence.token();
            TokenId tokenID = token.id();

            if (tokenID == openingTokenID) {
                if (balance == 0) {
                    return token;
                }
                balance--;
            } else if (tokenID == closingTokenID) {
                balance++;
            }
        }

        return null;
    }
}
