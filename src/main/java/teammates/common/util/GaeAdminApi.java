package teammates.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.appengine.api.modules.ModulesService;
import com.google.appengine.api.modules.ModulesServiceFactory;

/**
 * A wrapper class for GAE application admin API. e.g. version management
 */
public class GaeAdminApi {
    private static final ModulesService modulesService = ModulesServiceFactory.getModulesService();
    
    /**
     * Gets all available versions.
     */
    public static List<Version> getAvailableVersions() {
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
    public static Version getCurrentVersion() {
        return new Version(modulesService.getCurrentVersion());
    }
    
}
