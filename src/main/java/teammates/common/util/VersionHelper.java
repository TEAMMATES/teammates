package teammates.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.appengine.api.modules.ModulesService;
import com.google.appengine.api.modules.ModulesServiceFactory;

public class VersionHelper {
    private static ModulesService modulesService = ModulesServiceFactory.getModulesService();
    
    /**
     * Gets all available versions.
     * @return
     */
    public static List<String> getAvailableVersions() {
        List<String> versionList = new ArrayList<String>(modulesService.getVersions(null)); // null == default module
        Collections.sort(versionList, new VersionComparator());
        return versionList;
    }
    
    /**
     * Comparator for version strings.
     * It sorts versions in descending order by major version number then by their representations in string. 
     */
    private static class VersionComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            Integer majorVersionOfS1 = getMajorVersion(s1);
            Integer majorVersionOfS2 = getMajorVersion(s2);
            if (majorVersionOfS1.equals(majorVersionOfS2)) {
                return -s1.compareTo(s2);   // descending order
            } else {
                return -majorVersionOfS1.compareTo(majorVersionOfS2);   // descending order
            }
        }
        
        /**
         * Gets the major version number of the version.
         * The version is stored as "major-minor" or "major-minor-patch".
         */
        private Integer getMajorVersion(String version) {
            int firstDashIndex = version.indexOf('-');
            if (firstDashIndex == -1) {
                return Integer.parseInt(version);
            } else {
                String majorVersion = version.substring(0, firstDashIndex);
                return Integer.parseInt(majorVersion);
            }
        }
    }
    
    /**
     * Gets the current version of the application.
     */
    public static String getCurrentVersion() {
        return modulesService.getCurrentVersion();
    }
}
