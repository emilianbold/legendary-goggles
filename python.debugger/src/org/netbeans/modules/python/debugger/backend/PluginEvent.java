/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.python.debugger.backend;

/**
 * commuenication event between Debugger Front End and Pluggin editors
 *
 * @author jean-yves Mengant
 */
public class PluginEvent {

  public final static int UNDEFINED = -1;
  public final static int NEWSOURCE = 0;
  public final static int NEWLINE = 1;
  public final static int STARTING = 2;
  public final static int ENDING = 3;
  public final static int ENTERCALL = 4;
  public final static int LEAVECALL = 5;
  public final static int BUSY = 6;
  public final static int NOTBUSY = 7;
  private int _type = UNDEFINED;
  private String _source = null;
  private int _line = UNDEFINED;

  public PluginEvent(int type, String source, int line) {
    _type = type;
    _source = source;
    _line = line;
  }

  public int get_type() {
    return _type;
  }

  public String get_source() {
    return _source;
  }

  public int get_line() {
    return _line;
  }
}
