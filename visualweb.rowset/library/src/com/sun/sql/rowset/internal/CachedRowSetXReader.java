/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package com.sun.sql.rowset.internal;

import java.sql.*;
import javax.sql.*;
import javax.naming.*;
import java.io.*;
import java.lang.reflect.*;

//import com.sun.rowset.*;
import javax.sql.rowset.*;
import javax.sql.rowset.spi.*;

import com.sun.sql.rowset.CachedRowSetX;
import com.sun.sql.rowset.CachedRowSetXImpl;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * An implementation of RowSetReader to work with CachedRowSetX rowsets.
 *
 * Based on the Sun reference implementation of CachedRowSetReader.
 *
 * @see javax.sql.rowset.spi.SyncProvider
 * @see javax.sql.rowset.spi.SyncFactory
 * @see javax.sql.rowset.spi.SyncFactoryException
 */
public class CachedRowSetXReader implements RowSetReader, Serializable {

    private static ResourceBundle rb =
            ResourceBundle.getBundle("com.sun.sql.rowset.internal.Bundle", Locale.getDefault()); // NOI18N

    /**
     * The field that keeps track of whether the writer associated with
     * this <code>CachedRowSetXReader</code> object's rowset has been called since
     * the rowset was populated.
     * <P>
     * When this <code>CachedRowSetXReader</code> object reads data into
     * its rowset, it sets the field <code>writerCalls</code> to 0.
     * When the writer associated with the rowset is called to write
     * data back to the underlying data source, its <code>writeData</code>
     * method calls the method <code>CachedRowSetXReader.reset</code>,
     * which increments <code>writerCalls</code> and returns <code>true</code>
     * if <code>writerCalls</code> is 1. Thus, <code>writerCalls</code> equals
     * 1 after the first call to <code>writeData</code> that occurs
     * after the rowset has had data read into it.
     *
     * @serial
     */
    private int writerCalls = 0;

    private boolean userCon = false;

    private int startPosition;


    /**
     * Reads data from a data source and populates the given
     * <code>RowSet</code> object with that data.
     * This method is called by the rowset internally when
     * the application invokes the method <code>execute</code>
     * to read a new set of rows.
     * <P>
     * After clearing the rowset of its contents, if any, and setting
     * the number of writer calls to <code>0</code>, this reader calls
     * its <code>connect</code> method to make
     * a connection to the rowset's data source. Depending on which
     * of the rowset's properties have been set, the <code>connect</code>
     * method will use a <code>DataSource</code> object or the
     * <code>DriverManager</code> facility to make a connection to the
     * data source.
     * <P>
     * Once the connection to the data source is made, this reader
     * executes the query in the calling <code>CachedRowSet</code> object's
     * <code>command</code> property. Then it calls the rowset's
     * <code>populate</code> method, which reads data from the
     * <code>ResultSet</code> object produced by executing the rowset's
     * command. The rowset is then populated with this data.
     * <P>
     * This method's final act is to close the connection it made, thus
     * leaving the rowset disconnected from its data source.
     *
     * @param caller a <code>RowSet</code> object that has implemented
     *               the <code>RowSetInternal</code> interface and had
     *               this <code>CachedRowSetXReader</code> object set as
     *               its reader
     * @throws SQLException if there is a database access error, there is a
     *         problem making the connection, or the command property has not
     *         been set
     */
    public void readData(RowSetInternal caller) throws SQLException {
        Connection con = null;
        boolean printStatements = ( caller instanceof CachedRowSetX) ? ((CachedRowSetX)caller).getPrintStatements() : false  ;
        try {
            CachedRowSet crs = (CachedRowSet)caller;

            // Get rid of the current contents of the rowset.

            /**
             * Checking added to verify whether page size has been set or not.
             * If set then do not close the object as certain parameters need
             * to be maintained.
             */

            if(crs.getPageSize() == 0 && crs.size() >0 ) {
                // When page size is not set,
                // crs.size() will show the total no of rows.
                crs.close();
            }

            writerCalls = 0;

            // Get a connection.  This reader assumes that the necessary
            // properties have been set on the caller to let it supply a
            // connection.
            userCon = false;

            con = this.connect(caller);

            // Check our assumptions.
            if (con == null || crs.getCommand() == null)
                throw new SQLException("Internal Error in RowSetReader: no connection or command.");

            try {
                con.setTransactionIsolation(crs.getTransactionIsolation());
            } catch (Exception ex) {
                if ( printStatements) {
                    System.out.println("setTransactionIsolation() failed " + crs.getTransactionIsolation() );
                }
            }
            // Use JDBC to read the data.
            PreparedStatement pstmt = con.prepareStatement(crs.getCommand());
            // Pass any input parameters to JDBC.

            decodeParams(caller.getParams(), pstmt);

            StringBuffer msg = null ;
            if ( printStatements ) {
                msg = new StringBuffer(200) ;
                msg.append(crs.getCommand() );
                msg.append("\n" );
                Object[] params = caller.getParams() ;
                if ( params.length > 0 ) {
                    for ( int i = 0 ; i < params.length ; i++ ) {
                        msg.append("  Param[" + (i+1) +"]=(" );
                        if ( params[i] != null ) {
                            msg.append(params[i].getClass().getName() + "," + params[i].toString() +")" );
                        } else {
                            msg.append("null)" ) ;
                        }
                    }
                }
                else {
                    msg.append(" No Params") ;
                }
            }
            // drivers may not support the following, so ignore exceptions.
            try {
                pstmt.setMaxRows( crs.getMaxRows() );
            } catch (Exception ex) { ; }
            try {
                pstmt.setMaxFieldSize(crs.getMaxFieldSize());
            } catch (Exception ex) { ; }
            try {
                pstmt.setEscapeProcessing(crs.getEscapeProcessing());
            } catch (Exception ex) { ; }
            try {
                pstmt.setQueryTimeout(crs.getQueryTimeout());
            } catch (Exception ex) { ; }

            if(crs.getCommand().toLowerCase().indexOf("select") != -1) {  //NOI18N
                // can be (crs.getCommand()).indexOf("select")) == 0 //NOI18N
                // because we will be getting resultset when
                // it may be the case that some false select query with
                // select coming in between instead of first.

                // if ((crs.getCommand()).indexOf("?")) does not return -1 //NOI18N
                // implies a Prepared Statement like query exists.

                ResultSet rs ;
                if(crs.getPageSize() == 0){
                    if ( printStatements ) {
                        System.out.println("Reader executing query ps=0 " + msg  );
                    }
                    rs = pstmt.executeQuery();
                    crs.populate(rs);
                    if ( printStatements ) {
                        System.out.println("Reader executing finished"  );
                    }
                } else {
                    /**
                     * If page size has been set then create a ResultSet object that is scrollable using a
                     * PreparedStatement handle.Also call the populate(ResultSet,int) function to populate
                     * a page of data as specified by the page size.
                     */
                    pstmt = con.prepareStatement(crs.getCommand(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    decodeParams(caller.getParams(), pstmt);

                    // drivers may not support the following, so ignore exceptions.
                    try {
                        pstmt.setMaxRows(crs.getMaxRows());
                    } catch (Exception ex) { ; }
                    try {
                        pstmt.setMaxFieldSize(crs.getMaxFieldSize());
                    } catch (Exception ex) { ; }
                    try {
                        pstmt.setEscapeProcessing(crs.getEscapeProcessing());
                    } catch (Exception ex) { ; }
                    try {
                        pstmt.setQueryTimeout(crs.getQueryTimeout());
                    } catch (Exception ex) { ; }

                    if ( printStatements ) {
                        System.out.println("Reader executing query ps="+crs.getPageSize() + " " + msg );
                    }
                    rs = pstmt.executeQuery();
                    crs.populate(rs,startPosition);
                    if ( printStatements ) {
                        System.out.println("Reader executing finished"  );
                    }
                }
                rs.close();
            } else  {
                if ( printStatements ) {
                    System.out.println("Reader executing non-select " + msg );
                }
                pstmt.executeUpdate();
                if ( printStatements ) {
                    System.out.println("Reader executing finished"  );
                }
            }

            // Get the data.
            pstmt.close();
            try {
                con.commit();
            } catch (SQLException ex) {
                if ( printStatements ) {
                    ex.printStackTrace() ;
                }
            }
        } catch (SQLException ex) {
            // Throw an exception if reading fails for any reason.
            if ( printStatements ) {
                ex.printStackTrace() ;
            }
            throw ex;
        } finally {
            try {
                // only close connections we created...
                if (con != null && getCloseConnection() == true) {
                    try {
                        if (!con.getAutoCommit()) {
                            con.rollback();
                        }
                    } catch (Exception dummy) {
                        /*
                         * not an error condition, we're closing anyway, but
                         * we'd like to clean up any locks if we can since
                         * it is not clear the connection pool will clean
                         * these connections in a timely manner
                         */
                    }
                    con.close();
                    con = null;
                }
            } catch (SQLException e) {
                // will get exception if something already went wrong, but don't
                // override that exception with this one
            }
        }
    }

    /**
     * Checks to see if the writer associated with this reader needs
     * to reset its state.  The writer will need to initialize its state
     * if new contents have been read since the writer was last called.
     * This method is called by the writer that was registered with
     * this reader when components were being wired together.
     *
     * @return <code>true</code> if writer associated with this reader needs
     *         to reset the values of its fields; <code>false</code> otherwise
     * @throws SQLException if an access error occurs
     */
    public boolean reset() throws SQLException {
        writerCalls++;
        return writerCalls == 1;
    }

    /**
     * Establishes a connection with the data source for the given
     * <code>RowSet</code> object.  If the rowset's <code>dataSourceName</code>
     * property has been set, this method uses the JNDI API to retrieve the
     * <code>DataSource</code> object that it can use to make the connection.
     * If the url, username, and password properties have been set, this
     * method uses the <code>DriverManager.getConnection</code> method to
     * make the connection.
     * <P>
     * This method is used internally by the reader and writer associated with
     * the calling <code>RowSet</code> object; an application never calls it
     * directly.
     *
     * @param caller a <code>RowSet</code> object that has implemented
     *               the <code>RowSetInternal</code> interface and had
     *               this <code>CachedRowSetXReader</code> object set as
     *               its reader
     * @return a <code>Connection</code> object that represents a connection
     *         to the caller's data source
     * @throws SQLException if an access error occurs
     */
    public Connection connect(RowSetInternal caller) throws SQLException {

        // Get a JDBC connection.
        if (caller.getConnection() != null) {
            // A connection was passed to execute(), so use it.
            // As we are using a connection the user gave us we
            // won't close it.
            userCon = true;
            return caller.getConnection();
        } else if (((RowSet)caller).getDataSourceName() != null) {
            // Connect using JNDI.
            try {
                Context ctx = new InitialContext();
                DataSource ds = (DataSource)ctx.lookup
                        (((RowSet)caller).getDataSourceName());

                // Check for username, password,
                // if it exists try getting a Connection handle through them
                // else try without these
                // else throw SQLException

                if(((RowSet)caller).getUsername() != null) {
                    return ds.getConnection(((RowSet)caller).getUsername(),
                            ((RowSet)caller).getPassword());
                } else {
                    return ds.getConnection();
                }
            } catch (javax.naming.NamingException ex) {
                SQLException sqlEx = new SQLException();
                sqlEx.initCause(ex);
                throw sqlEx;
            }
        } else if (((RowSet)caller).getUrl() != null) {
            // Connect using the driver manager.
            return DriverManager.getConnection(((RowSet)caller).getUrl(),
                    ((RowSet)caller).getUsername(),
                    ((RowSet)caller).getPassword());
        } else {
            return null;
        }
    }

    /**
     * Sets the parameter placeholders
     * in the rowset's command (the given <code>PreparedStatement</code>
     * object) with the parameters in the given array.
     * This method, called internally by the method
     * <code>CachedRowSetXReader.readData</code>, reads each parameter, and
     * based on its type, determines the correct
     * <code>PreparedStatement.setXXX</code> method to use for setting
     * that parameter.
     *
     * @param params an array of parameters to be used with the given
     *               <code>PreparedStatement</code> object
     * @param pstmt  the <code>PreparedStatement</code> object that is the
     *               command for the calling rowset and into which
     *               the given parameters are to be set
     * @throws SQLException if an access error occurs
     */
    private void decodeParams(Object[] params,
            PreparedStatement pstmt) throws SQLException {
        // There is a corresponding decodeParams in JdbcRowSetImpl
        // which does the same as this method. This is a design flaw.
        // Update the JdbcRowSetImpl.decodeParams when you update
        // this method.

        // Adding the same comments to JdbcRowSetImpl.decodeParams.

        int arraySize;
        Object[] param = null;

        for (int i=0; i < params.length; i++) {
            if (params[i] instanceof Object[]) {
                param = (Object[])params[i];

                if (param.length == 2) {
                    if (param[0] == null) {
                        pstmt.setNull(i + 1, ((Integer)param[1]).intValue());
                        continue;
                    }

                    if (param[0] instanceof java.sql.Date ||
                            param[0] instanceof java.sql.Time ||
                            param[0] instanceof java.sql.Timestamp) {
                        System.err.println(rb.getString("DETECTED_A_DATE")); //NOI18N
                        if (param[1] instanceof java.util.Calendar) {
                            System.err.println(rb.getString("DETECTED_A_CALENDAR")); //NOI18N
                            pstmt.setDate(i + 1, (java.sql.Date)param[0],
                                    (java.util.Calendar)param[1]);
                            continue;
                        } else {
                            throw new SQLException(rb.getString("UNABLE_TO_DEDUCE_PARAM_TYPE")); //NOI18N
                        }
                    }

                    if (param[0] instanceof Reader) {
                        pstmt.setCharacterStream(i + 1, (Reader)param[0],
                                ((Integer)param[1]).intValue());
                        continue;
                    }

                    /*
                     * What's left should be setObject(int, Object, scale)
                     */
                    if (param[1] instanceof Integer) {
                        pstmt.setObject(i + 1, param[0], ((Integer)param[1]).intValue());
                        continue;
                    }

                } else if (param.length == 3) {

                    if (param[0] == null) {
                        pstmt.setNull(i + 1, ((Integer)param[1]).intValue(),
                                (String)param[2]);
                        continue;
                    }

                    if (param[0] instanceof java.io.InputStream) {
                        switch (((Integer)param[2]).intValue()) {
                            case CachedRowSetXImpl.UNICODE_STREAM_PARAM:
                                pstmt.setUnicodeStream(i + 1,
                                        (java.io.InputStream)param[0],
                                        ((Integer)param[1]).intValue());
                            case CachedRowSetXImpl.BINARY_STREAM_PARAM:
                                pstmt.setBinaryStream(i + 1,
                                        (java.io.InputStream)param[0],
                                        ((Integer)param[1]).intValue());
                            case CachedRowSetXImpl.ASCII_STREAM_PARAM:
                                pstmt.setAsciiStream(i + 1,
                                        (java.io.InputStream)param[0],
                                        ((Integer)param[1]).intValue());
                            default:
                                throw new SQLException(rb.getString("UNABLE_TO_DEDUCE_PARAM_TYPE")); //NOI18N
                        }
                    }

                    /*
                     * no point at looking at the first element now;
                     * what's left must be the setObject() cases.
                     */
                    if (param[1] instanceof Integer && param[2] instanceof Integer) {
                        pstmt.setObject(i + 1, param[0], ((Integer)param[1]).intValue(),
                                ((Integer)param[2]).intValue());
                        continue;
                    }

                    throw new SQLException(rb.getString("UNABLE_TO_DEDUCE_PARAM_TYPE")); //NOI18N

                } else {
                    // common case - this catches all SQL92 types
                    pstmt.setObject(i + 1, params[i]);
                    continue;
                }
            }  else {
                // Try to get all the params to be set here
                pstmt.setObject(i + 1, params[i]);

            }
        }
    }

    /**
     * Assists in determining whether the current connection was created by this
     * CachedRowSet to ensure incorrect connections are not prematurely terminated.
     *
     * @return a boolean giving the status of whether the connection has been closed.
     */
    protected boolean getCloseConnection() {
        if (userCon == true)
            return false;

        return true;
    }

    /**
     *  This sets the start position in the ResultSet from where to begin. This is
     * called by the Reader in the CachedRowSetXImpl to set the position on the page
     * to begin populating from.
     * @param pos integer indicating the position in the <code>ResultSet</code> to begin
     *        populating from.
     */
    public void setStartPosition(int pos){
        startPosition = pos;
    }

}
