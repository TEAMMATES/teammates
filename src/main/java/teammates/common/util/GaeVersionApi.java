package teammates.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.appengine.api.modules.ModulesService;
import com.google.appengine.api.modules.ModulesServiceFactory;

/**
 * A wrapper class for GAE application admin API. e.g. version management
 */
public class GaeVersionApi {
    /**
     * Maximum number of versions to query.
     * The current value will include the current version and its 5 preceding versions.
     */
    private static final int MAX_VERSIONS_TO_QUERY = 6;
    
    /**
     * Default constructor.
     */
    public GaeVersionApi() {
    }
    
    /**
     * Gets all available versions.
     */
    public List<Version> getAvailableVersions() {
        ModulesService modulesService = ModulesServiceFactory.getModulesService();
        List<String> versionListInString = new ArrayList<String>(modulesService.getVersions(null)); // null == default module
        List<Version> versionList = new ArrayList<Version>();
        for(String versionInString : versionListInString) {
            versionList.add(new Version(versionInString));
        }
        Collections.sort(versionList);
        return versionList;
    }
    
    /**
     * Gets the current version of the application.
     */
    public Version getCurrentVersion() {
        ModulesService modulesService = ModulesServiceFactory.getModulesService();
        return new Version(modulesService.getCurrentVersion());
    }
    
    /**
     * Gets a few recent versions for log query.
     * @return a list of default versions for query.
     */
    public List<String> getDefaultVersionIdsForLogQuery() {
        List<Version> versionList = getAvailableVersions();
        Version currentVersion = getCurrentVersion();
        
        List<String> defaultVersions = new ArrayList<String>();
        try {
            int currentVersionIndex = versionList.indexOf(currentVersion);
            defaultVersions = getNextFewVersions(versionList, currentVersionIndex);
        } catch (IndexOutOfBoundsException  e) {
            defaultVersions.add(currentVersion.toStringForQuery());
            Utils.getLogger().severe(e.getMessage());
        }
        return defaultVersions;
    }

    /**
     * Finds at most MAX_VERSIONS_TO_QUERY nearest versions.
     * @param currentVersionIndex starting position to get versions to query
     */
    private List<String> getNextFewVersions(List<Version> versionList, int currentVersionIndex) {
        int endIndex = Math.min(currentVersionIndex + MAX_VERSIONS_TO_QUERY, versionList.size());
        List<Version> versionSubList = versionList.subList(currentVersionIndex, endIndex);
        List<String> versionListInString = new ArrayList<String>();
        for(Version version : versionSubList) {
            versionListInString.add(version.toStringForQuery());
        }
        return versionListInString;
    }
}
