/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.test.gui.web.util;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.net.Socket;
import java.net.ServerSocket;


import org.netbeans.jemmy.Waitable;
import org.netbeans.core.NbTopManager;

public class JSPServletResponseWaitable implements Waitable {
    private String id = null;
    private String real = null;
    private String userAgent = null;
    private String answer = null;
    private int port = -1;
    private boolean started = false;
    public JSPServletResponseWaitable(String id, String answer, int port) {
	this.id = id;
	this.answer = answer;
	this.port = port;

	(new Thread () {
		public void run() {
		    try {
			String answer = getDefaultAnswer();
			int port = getDefaultPort();
			ServerSocket ss = new ServerSocket(port);
			System.out.println("Listen on " + port);
			started = true;
			Socket s = ss.accept();
			InputStream is = s.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = br.readLine();
			while(!(line == null)) {
			    System.out.println("READ: \"" + line + "\"");
			    if(line.startsWith("ID")) {
				setId(line);
			    }
			    if(line.startsWith("UserAgent")) {
				setUserAgent(line);
			    }
			    line = br.readLine();
			    if(line.equals("")) {
				line = null;
			    }
			}
			OutputStream os = s.getOutputStream();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
			bw.write(answer,0,answer.length());
			System.out.println("CL is " + answer.length());
			bw.flush();
			s.close();
			ss.close();
		    }catch(Exception e) {
			real = "Exception at READ/WRITE http opeartions";
			System.out.println("ERROR!");
			e.printStackTrace();
		    }
		}
	    }).start();
	while(!started);
    }
    
    public Object actionProduced(Object o) {
	if((real!=null)&&(real.indexOf(id)!=-1)) {
	    return Boolean.TRUE;
	}
	if((real!=null)&&(real.indexOf(id)==-1)) {
	    System.out.println("ID is " + real + " instead of " + id);
	}
	return null;
    }

    public String getDescription() {
	return "Waiter for ID: " + id;
    }

    private int getDefaultPort() {
	return port;
    }
    private String getDefaultAnswer() {
	return answer;
    }
    private void setId(String id) {
	real = id;
    }

    private void setUserAgent(String agent) {
	userAgent = agent;
    }
    private String getUserAgent() {
	return userAgent;
    }
    public String getId() {
	return real;
    }
}

