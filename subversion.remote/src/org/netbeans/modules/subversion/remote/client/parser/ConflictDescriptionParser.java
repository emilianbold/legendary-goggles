/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.subversion.remote.client.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.subversion.remote.api.SVNConflictDescriptor;
import org.netbeans.modules.subversion.remote.api.SVNConflictDescriptor.Action;
import org.netbeans.modules.subversion.remote.api.SVNConflictDescriptor.Operation;
import org.netbeans.modules.subversion.remote.api.SVNConflictDescriptor.Reason;
import org.netbeans.modules.subversion.remote.api.SVNConflictVersion;

/**
 *
 * @author ondra
 */
class ConflictDescriptionParser {

    private final List<ParserConflictDescriptor> conflicts;
    private static final String TOKEN_CONFLICT = "conflict"; //NOI18N
    private static final String TOKEN_VERSION = "version"; //NOI18N
    private static final char DELIMITER_SPACE = ' ';
    private static final char DELIMITER_OPEN_BRACKET = '(';
    private static final char DELIMITER_CLOSING_BRACKET = ')';

    private static final Logger LOG = Logger.getLogger(ConflictDescriptionParser.class.getName());

    private static final HashMap<String, Action> ACTIONS;
    private static final HashMap<String, Reason> REASONS;
    private static final HashMap<String, Operation> OPERATIONS;
    private static final HashMap<String, SVNConflictVersion.NodeKind> NODE_KINDS;
    static {
        ACTIONS = new HashMap<String, Action>(3);
        ACTIONS.put("edited", Action.edit); //NOI18N
        ACTIONS.put("deleted", Action.delete); //NOI18N
        ACTIONS.put("added", Action.add); //NOI18N

        REASONS = new HashMap<String, Reason>(6);
        REASONS.put("edited", Reason.edited); //NOI18N
        REASONS.put("deleted", Reason.deleted); //NOI18N
        REASONS.put("missing", Reason.missing); //NOI18N
        REASONS.put("obstructed", Reason.obstructed); //NOI18N
        REASONS.put("added", Reason.added); //NOI18N
        REASONS.put("unversioned", Reason.unversioned); //NOI18N

        OPERATIONS = new HashMap<String, Operation>(4);
        OPERATIONS.put("none", Operation._none); //NOI18N
        OPERATIONS.put("update", Operation._update); //NOI18N
        OPERATIONS.put("switch", Operation._switch); //NOI18N
        OPERATIONS.put("merge", Operation._merge); //NOI18N

        NODE_KINDS = new HashMap<String, SVNConflictVersion.NodeKind>(3);
        NODE_KINDS.put("file", SVNConflictVersion.NodeKind.file); //NOI18N
        NODE_KINDS.put("directory", SVNConflictVersion.NodeKind.directory); //NOI18N
        NODE_KINDS.put("none", SVNConflictVersion.NodeKind.none); //NOI18N
    }

    private ConflictDescriptionParser() {
        conflicts = new LinkedList<ParserConflictDescriptor>();
    }

    static ConflictDescriptionParser parseDescription (String description) {
        ConflictDescriptionParser parser = new ConflictDescriptionParser();
        try {
            parser.parse(description);
        } catch (IOException ex) {
            LOG.log(Level.INFO, "Error parsing: " + description, ex); //NOI18N
        }
        return parser;
    }

    /**
     * Returns all conflicts created while parsing.
     * @return list of parsed conflicts
     */
    List<ParserConflictDescriptor> getConflicts() {
        return conflicts;
    }

    private void parse (String description) throws IOException {
        StringReader sr = new StringReader(description);
        if (sr.read() == DELIMITER_OPEN_BRACKET) {
            while (true) {
                int c;
                while ((c = sr.read()) != -1 && c != DELIMITER_OPEN_BRACKET && c != DELIMITER_CLOSING_BRACKET); // wait for a bracket opening new conflict
                if (c == DELIMITER_CLOSING_BRACKET) { // end of description
                    break;
                } else if (c != DELIMITER_OPEN_BRACKET) { // error
                    throw new IOException("Error parsing description: " + description); //NOI18N
                }
                ParserConflictDescriptor conflict = readConflict(sr);
                if (conflict != null) {
                    conflicts.add(conflict);
                }
            }
        }
    }

    private ParserConflictDescriptor readConflict (Reader input) throws IOException {
        ParserConflictDescriptor conflict = null;
        String startToken = readToken(input, DELIMITER_SPACE);
        if (TOKEN_CONFLICT.equals(startToken)) { // prefix of a conflict
            String fileName = readString(input);
            String nodeKind = readToken(input, DELIMITER_SPACE); // prop or file?
            int operation = readToken(input, OPERATIONS, DELIMITER_SPACE); // update, merge, switch?
            int action = readToken(input, ACTIONS, DELIMITER_SPACE); // action on server
            int reason = readToken(input, REASONS, DELIMITER_SPACE); // local action
            SVNConflictVersion versionLeft = readVersion(input); // version in repo
            SVNConflictVersion versionRight = readVersion(input); // local base? version
            conflict = new ParserConflictDescriptor(fileName, null, action, reason, operation, versionLeft, versionRight);
            readUntil(input, DELIMITER_CLOSING_BRACKET);
        } else {
            throw new IOException("token 'conflict' expected"); //NOI18N
        }
        return conflict;
    }

    private String readString (Reader input) throws IOException {
        int c;
        String s = readToken(input, DELIMITER_SPACE);
        if (Character.isDigit(s.charAt(0))) { // next token is prefixed with its length
            try {
                int len = Integer.parseInt(s);
                s = readToken(input, len);
            } catch (NumberFormatException ex) {
                throw new IOException("Unexpected token, should be a number: " + s, ex); //NOI18N
            }
        }
        return s;
    }

    /**
     * Reads next token bounded by its length
     */
    private String readToken(Reader input, int len) throws IOException {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; ++i) {
            sb.append((char) input.read());
        }
        return sb.toString();
    }

    /**
     * Reads next token bounded with a given delimiter.
     */
    private String readToken (Reader input, char delimiter) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c = skip(input, DELIMITER_SPACE);
        sb.append((char)c);
        while ((c = input.read()) != -1 && c != delimiter) {
            if (c == -1) {
                throw new IOException("Unexpected end of input"); // NOI18N
            }
            sb.append((char)c);
        }
        return sb.toString();
    }

    private SVNConflictVersion readVersion (Reader input) throws IOException {
        SVNConflictVersion version = null;
        readUntil(input, DELIMITER_OPEN_BRACKET);
        String startToken = readToken(input, DELIMITER_SPACE);
        if (TOKEN_VERSION.equals(startToken)) { // prefix
            String url = readString(input); // repository root url
            String s = readString(input); // peg revision prefixed with its length
            long pegRevision;
            try {
                pegRevision = Long.parseLong(s);
            } catch (NumberFormatException ex) {
                throw new IOException("Unexpected token, should be a number: " + s, ex); //NOI18N
            }
            String repoPath = readString(input); // path in repository
            int nodeKind = readToken(input, NODE_KINDS, DELIMITER_CLOSING_BRACKET); // file or dir
            version = new SVNConflictVersion(url, pegRevision, repoPath, nodeKind);
        } else {
            throw new IOException("token 'version' expected"); //NOI18N
        }
        return version;
    }

    /**
     * Reads next token and return its integer value in a given map or <code>0</code> if no such mapping exists
     */
    private int readToken (Reader input, Map<String, Integer> mapping, char delimiter) throws IOException {
        String token = readToken(input, delimiter);
        Integer retval = mapping.get(token);
        return retval == null ? 0 : retval;
    }

    /**
     * Skips all characters in input until a given breakpoint character is found
     */
    private void readUntil(Reader input, char breakpoint) throws IOException {
        int c;
        while ((c = input.read()) != -1 && c != breakpoint);
        if (c == -1) {
            throw new IOException("Unexpected end of input"); // NOI18N
        }
    }

    /**
     * Skips given character and returns the next character
     */
    private int skip(Reader input, char characterToSkip) throws IOException {
        int c;
        while ((c = input.read()) == characterToSkip); // skip characters
        return c;
    }

    static final class ParserConflictDescriptor extends SVNConflictDescriptor {
        private final String fileName;

        private ParserConflictDescriptor (String fileName, String path, SVNConflictDescriptor.Action action, SVNConflictDescriptor.Reason reason, SVNConflictDescriptor.Operation operation, SVNConflictVersion left, SVNConflictVersion right) {
            super(path, action, reason, operation, left, right);
            this.fileName = fileName;
        }

        public String getFileName () {
            return fileName;
        }
    }
}
