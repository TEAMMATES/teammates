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
        for (String versionInString : versionListInString) {
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
     * Gets a number of most recent versions
     * @return a list of versions.
     */
    public List<String> getMostRecentVersions(int numVersions) {
        List<Version> versionList = getAvailableVersions();
        Version currentVersion = getCurrentVersion();
        
        List<String> resultVersions = new ArrayList<String>();
        try {
            int currentVersionIndex = versionList.indexOf(currentVersion);
            resultVersions = getSublistOfVersionList(versionList, currentVersionIndex, numVersions);
        } catch (Exception e) {
            resultVersions.add(currentVersion.toStringWithDashes());
            Utils.getLogger().severe(e.getMessage());
        }
        return resultVersions;
    }

    /**
     * Finds a sublist of versionList, starting from startIndex and at most `maxAmount` elements.
     * @param startIndex starting position to get versions
     * @param amount 
     */
    private List<String> getSublistOfVersionList(List<Version> versionList, int startIndex, int maxAmount) {
        int endIndex = Math.min(startIndex + maxAmount, versionList.size());
        List<Version> versionSubList = versionList.subList(startIndex, endIndex);
        List<String> versionListInString = new ArrayList<String>();
        for (Version version : versionSubList) {
            versionListInString.add(version.toStringWithDashes());
        }
        return versionListInString;
    }
}
