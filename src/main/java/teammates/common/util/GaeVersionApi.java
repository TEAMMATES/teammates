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
    
}
