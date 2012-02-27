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
package org.netbeans.modules.bugtracking;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.netbeans.api.keyring.Keyring;
import org.netbeans.modules.bugtracking.kenai.spi.KenaiUtil;
import org.netbeans.modules.bugtracking.spi.RepositoryInfo;
import org.netbeans.modules.bugtracking.api.Repository;
import org.netbeans.modules.bugtracking.util.BugtrackingUtil;
import org.openide.util.NbPreferences;

/**
 *
 * @author Tomas Stupka
 */
public class RepositoryRegistry {

    /**
     * A repository was created or removed, where old value is a Collection of all repositories 
     * before the change and new value a Collection of all repositories after the change.
     */
    public final static String EVENT_REPOSITORIES_CHANGED = "bugtracking.repositories.changed"; // NOI18N
    
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    
    private static final String BUGTRACKING_REPO  = "bugracking.repository_";   // NOI18N
    private static final String DELIMITER         = "<=>";                      // NOI18N    
    
    private static final Object REPOSITORIES_LOCK = new Object();
    
    private static RepositoryRegistry instance;

    private RepositoriesMap repositories;
        
    private RepositoryRegistry() {}
    
    /**
     * Returns the singleton RepositoryRegistry instance
     * 
     * @return 
     */
    public static synchronized RepositoryRegistry getInstance() {
        if(instance == null) {
            instance = new RepositoryRegistry();
        }
        return instance;
    }
    
    /**
     * Returns all repositories
     * 
     * @return 
     */
    public Repository[] getRepositories() {
        synchronized(REPOSITORIES_LOCK) {
            List<Repository> l = getStoredRepositories().getRepositories();
            return l.toArray(new Repository[l.size()]);
        }
    }

    /**
     * Returns all repositories for the connector with the given ID
     * 
     * @param connectorID
     * @return 
     */
    public Repository[] getRepositories(String connectorID) {
        synchronized(REPOSITORIES_LOCK) {
            final Map<String, Repository> m = getStoredRepositories().get(connectorID);
            if(m != null) {
                Collection<Repository> c = m.values();
                return c.toArray(new Repository[c.size()]);
            } else {
                return new Repository[0];
            }
        }
    }

    public Repository getRepository(String connectorId, String repoId) {
        Repository[] repos = getRepositories(connectorId);
        for (Repository repo : repos) {
            if(repo.getId().equals(repoId)) {
                return repo;
            }
        }
        return null;
    }

    
    /**
     * Add the given repository
     * 
     * @param repository 
     */
    public void addRepository(Repository repository) {
        assert repository != null;
        if(KenaiUtil.isKenai(repository) && !BugtrackingUtil.isNbRepository(repository)) {
            // we don't store kenai repositories - XXX  shouldn't be even called
            return;        
        }
        Collection<Repository> oldRepos;
        Collection<Repository> newRepos;
        synchronized(REPOSITORIES_LOCK) {
            oldRepos = Collections.unmodifiableCollection(new LinkedList<Repository>(getStoredRepositories().getRepositories()));
            getStoredRepositories().put(repository); // cache
            putRepository(repository); // persist
            newRepos = Collections.unmodifiableCollection(getStoredRepositories().getRepositories());

        }
        fireRepositoriesChanged(oldRepos, newRepos);
    }    

    /**
     * Remove the given repository
     * 
     * @param repository 
     */
    public void removeRepository(Repository repository) {
        Collection<Repository> oldRepos;
        Collection<Repository> newRepos;
        synchronized(REPOSITORIES_LOCK) {
            oldRepos = Collections.unmodifiableCollection(getStoredRepositories().getRepositories());
            RepositoryInfo info = APIAccessor.IMPL.getInfo(repository);
            String connectorID = info.getConnectorId();  
            // persist remove
            getPreferences().remove(getRepositoryKey(info)); 
            // remove from cache
            getStoredRepositories().remove(connectorID, repository);
            
            newRepos = Collections.unmodifiableCollection(getStoredRepositories().getRepositories());
        }
        fireRepositoriesChanged(oldRepos, newRepos);
    }
    
    /**
     * Returns all known repositories incl. the Kenai ones
     *
     * @param pingOpenProjects if {@code false}, search only Kenai projects
     *                          that are currently open in the Kenai dashboard;
     *                          if {@code true}, search also all Kenai projects
     *                          currently opened in the IDE
     * @return repositories
     */
    public Repository[] getKnownRepositories(boolean pingOpenProjects) {
        Repository[] kenaiRepos = KenaiUtil.getRepositories(pingOpenProjects);
        Repository[] otherRepos = getRepositories();
        Repository[] ret = new Repository[kenaiRepos.length + otherRepos.length];
        System.arraycopy(kenaiRepos, 0, ret, 0, kenaiRepos.length);
        System.arraycopy(otherRepos, 0, ret, kenaiRepos.length, otherRepos.length);
        return ret;
    }
    
    /**
     * remove a listener from this connector
     * @param listener
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Add a listener to this connector to listen on events
     * @param listener
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // private 
    ////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * for testing
     */
    void flushRepositories() {
        repositories = null;
    }

    private String getRepositoryKey(RepositoryInfo info) {
        return BUGTRACKING_REPO + info.getConnectorId() + DELIMITER + info.getId();
    }
    
    private RepositoriesMap getStoredRepositories() {
        if (repositories == null) {
            repositories = new RepositoriesMap();
            
            migrateBugzilla();
            migrateJira();
            
            String[] ids = getRepositoryIds();
            if (ids == null || ids.length == 0) {
                return repositories;
            }
            DelegatingConnector[] connectors = BugtrackingManager.getInstance().getConnectors();
            for (String id : ids) {
                String[] idArray = id.split(DELIMITER);
                String connectorId = idArray[0].substring(BUGTRACKING_REPO.length());
                for (DelegatingConnector c : connectors) {
                    if(c.getID().equals(connectorId)) {
                        RepositoryInfo info = SPIAccessor.IMPL.read(getPreferences(), id);
                        if(info != null) {
                            Repository repo = c.createRepository(info);
                            if (repo != null) {
                                repositories.put(repo);
                            }
                        }
                    }
                }
            }
        }
        return repositories;
    }
  
    private String[] getRepositoryIds() {
        return getKeysWithPrefix(BUGTRACKING_REPO);
    }
    
    /**
     * package private for testing 
     */
    void putRepository(Repository repository) {
        RepositoryInfo info = APIAccessor.IMPL.getInfo(repository);
        final String key = getRepositoryKey(info);
        SPIAccessor.IMPL.store(getPreferences(), info, key);

        char[] password = info.getPassword();
        char[] httpPassword = info.getHttpPassword();
        BugtrackingUtil.savePassword(password, null, info.getUsername(), info.getUrl());
        BugtrackingUtil.savePassword(httpPassword, "http", info.getHttpUsername(), info.getUrl()); // NOI18N
    }
    
    private Preferences getPreferences() {
        return NbPreferences.forModule(RepositoryRegistry.class);
    }   
    
    private String[] getKeysWithPrefix(String prefix) {
        String[] keys = null;
        try {
            keys = getPreferences().keys();
        } catch (BackingStoreException ex) {
            BugtrackingManager.LOG.log(Level.SEVERE, null, ex); // XXX
        }
        if (keys == null || keys.length == 0) {
            return new String[0];
        }
        List<String> ret = new ArrayList<String>();
        for (String key : keys) {
            if (key.startsWith(prefix)) {
                ret.add(key);
            }
        }
        return ret.toArray(new String[ret.size()]);
    }
    
    /**
     *
     * @param oldRepositories - lists repositories which were available for the connector before the change
     * @param newRepositories - lists repositories which are available for the connector after the change
     */
    private void fireRepositoriesChanged(Collection<Repository> oldRepositories, Collection<Repository> newRepositories) {
        changeSupport.firePropertyChange(EVENT_REPOSITORIES_CHANGED, oldRepositories, newRepositories);
    }

    private class RepositoriesMap extends HashMap<String, Map<String, Repository>> {
        public void remove(String connectorID, Repository repository) {
            Map<String, Repository> m = get(connectorID);
            if(m != null) {
                m.remove(repository.getId());
            }
        }
        public void put(Repository repository) {
            String connectorID = APIAccessor.IMPL.getInfo(repository).getConnectorId();
            Map<String, Repository> m = get(connectorID);
            if(m == null) {
                m = new HashMap<String, Repository>();
                put(connectorID, m);
            }
            m.put(repository.getId(), repository);
        }
        List<Repository> getRepositories() {
            List<Repository> ret = new LinkedList<Repository>();
            for (Entry<String, Map<String, Repository>> e : entrySet()) {
                ret.addAll(e.getValue().values());
            }
            return ret;
        }
        
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final String JIRA_REPO_ID                    = "jira.repository_";           // NOI18N 
    private static final String BUGZILLA_REPO_ID                = "bugzilla.repository_";       // NOI18N
    private static final String NB_BUGZILLA_USERNAME            = "nbbugzilla.username";        // NOI18N
    private static final String NB_BUGZILLA_PASSWORD            = "nbbugzilla.password";        // NOI18N
    private static final String REPOSITORY_SETTING_SHORT_LOGIN  = "bugzilla.shortLoginEnabled"; // NOI18N
    
    private void migrateBugzilla() {
        Preferences preferences = getBugzillaPreferences();
        String[] repoIds = getRepoIds(preferences, BUGZILLA_REPO_ID);
        for (String id : repoIds) {
            migrateBugzillaRepository(preferences, id);
            preferences.remove(BUGZILLA_REPO_ID + id);
        }
        preferences.remove(NB_BUGZILLA_USERNAME);
    }
    
    private void migrateJira() {
        Preferences preferences = getJiraPreferences();
        String[] repoIds = getRepoIds(preferences, JIRA_REPO_ID);
        for (String id : repoIds) {
            migrateJiraRepository(preferences, id);
            preferences.remove(JIRA_REPO_ID + id);
        }
    }
    
    private String[] getRepoIds(Preferences preferences, String repoId) {
        String[] keys = null;
        try {
            keys = preferences.keys();
        } catch (BackingStoreException ex) {
            BugtrackingManager.LOG.log(Level.SEVERE, null, ex); 
        }
        if (keys == null || keys.length == 0) {
            return new String[0];
        }
        List<String> ret = new ArrayList<String>();
        for (String key : keys) {
            if (key.startsWith(repoId)) {
                ret.add(key.substring(repoId.length()));
            }
        }
        return ret.toArray(new String[ret.size()]);
    }    
    private void migrateBugzillaRepository(Preferences preferences, String repoID) {
        String[] values = getRepositoryValues(preferences, BUGZILLA_REPO_ID, repoID);
        if(values == null) {
            return;
        }
        assert values.length == 3 || values.length == 6 || values.length == 7;
        String url = values[0];
        
        String user;
        char[] password;
        if(BugtrackingUtil.isNbRepository(url)) {
            user = getNBUsername();
            char[] psswdArray = getNBPassword();
            password = psswdArray != null ? psswdArray : new char[0];
        } else {
            user = values[1];
            password = BugtrackingUtil.readPassword(values[2], null, user, url);
        }
        String httpUser = values.length > 3 ? values[3] : null;
        char[] httpPassword = values.length > 3 ? BugtrackingUtil.readPassword(values[4], "http", httpUser, url) : new char[0]; // NOI18N
        
        String shortNameEnabled = "false"; // NOI18N
        if (values.length > 5) {
            shortNameEnabled = values[5];
        }
        
        String name;
        if (values.length > 6) {
            name = values[6];
        } else {
            name = repoID;
        }
        RepositoryInfo info = new RepositoryInfo(
                repoID, 
                "org.netbeans.modules.bugzilla", // NOI18N
                url, 
                name, 
                name, 
                user, 
                httpUser, 
                password, 
                httpPassword); 
        info.putValue(REPOSITORY_SETTING_SHORT_LOGIN, shortNameEnabled);
        SPIAccessor.IMPL.store(getPreferences(), info, getRepositoryKey(info));
    }
    
    private void migrateJiraRepository(Preferences preferences, String repoID) {
        String[] values = getRepositoryValues(preferences, JIRA_REPO_ID, repoID);
        String url = values[0];
        String user = values[1];
        String password = new String(BugtrackingUtil.readPassword(values[2], null, user, url));
        String httpUser = values.length > 3 ? values[3] : null;
        String httpPassword = new String(values.length > 3 ? BugtrackingUtil.readPassword(values[4], "http", httpUser, url) : null); // NOI18N
        
        String repoName;
        if(values.length > 5) {
            repoName = values[5];
        } else {
            repoName = repoID;
        }
        
        RepositoryInfo info = new RepositoryInfo(
                repoID, 
                "org.netbeans.modules.jira", // NOI18N
                url, 
                repoName, 
                repoName, 
                user, 
                httpUser, 
                password.toCharArray(), 
                httpPassword.toCharArray());
        SPIAccessor.IMPL.store(getPreferences(), info, getRepositoryKey(info));
    }

    private static String[] getRepositoryValues(Preferences preferences, String repoPrefix, String repoID) {
        String repoString = preferences.get(repoPrefix + repoID, "");         // NOI18N
        if(repoString.equals("")) {                                           // NOI18N
            return null;
        }
        return repoString.split(DELIMITER);
    }
    
    private static Preferences getBugzillaPreferences() {
        return NbPreferences.root().node("org/netbeans/modules/bugzilla"); // NOI18N
    }
    
    private static Preferences getJiraPreferences() {
        return NbPreferences.root().node("org/netbeans/modules/jira"); // NOI18N
    }

    private static String getNBUsername() {
        String user = getBugzillaPreferences().get(NB_BUGZILLA_USERNAME, ""); // NOI18N
        return user;                         
    }
    
    private static char[] getNBPassword() {
        return Keyring.read(NB_BUGZILLA_PASSWORD);
    }    
    
}
