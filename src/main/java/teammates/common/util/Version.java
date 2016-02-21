package teammates.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.appengine.api.modules.ModulesService;
import com.google.appengine.api.modules.ModulesServiceFactory;

public class Version {
    private static ModulesService modulesService = ModulesServiceFactory.getModulesService();
    private Integer major;  //  the first number
    private Integer minor;  //  the second number
    private String patch;   //  the rest of version
    
    /**
     * Creates a new instance of Version from string.
     * It accepts either XX-XX-XXXXX or XX.XX.XXXX format.
     */
    public Version(String versionInString) {
        String[] list;
        if (versionInString.contains("-")) {
            list = versionInString.split("-", 3);   // split into at most 3 parts
        } else {
            list = versionInString.split(".", 3);
        }
        if (list.length > 0) {
            major = Integer.parseInt(list[0]);
        }
        if (list.length > 1) {
            minor = Integer.parseInt(list[1]);
        }
        if (list.length > 2) {
            patch = list[2];
        }
    }
    
    /**
     * Compares by string representation.
     */
    public boolean equals(Object anotherVersion) {
        return toString().equals(anotherVersion.toString());
    }
    
    /**
     * Converts Version to String in format XX.XX.XXXX
     */
    public String toString() {
        String version = "";
        if (major != null) {
            version += String.valueOf(major);
        }
        if (minor != null) {
            String minorInString = String.valueOf(minor);
            if (minorInString.length() < 2) {
                minorInString = "0" + minorInString;
            }
            version += "." + minorInString;
        }
        if (patch != null) {
            version += "." + patch;
        }
        return version;
    }
    
    /**
     * Converts to String in format XX-XX-XXXX
     */
    public String toStringForQuery() {
        return toString().replace('.', '-');
    }
    
    /**
     * Gets all available versions.
     */
    public static List<Version> getAvailableVersions() {
        List<String> versionListInString = new ArrayList<String>(modulesService.getVersions(null)); // null == default module
        List<Version> versionList = new ArrayList<Version>();
        for(String versionInString : versionListInString) {
            versionList.add(new Version(versionInString));
        }
        Collections.sort(versionList, new VersionComparator());
        return versionList;
    }
    
    /**
     * Comparator for version strings.
     * It sorts versions in DESCENDING order by major version then by minor version then by patch version
     */
    private static class VersionComparator implements Comparator<Version> {
        @Override
        public int compare(Version v1, Version v2) {
            Integer major1 = v1.getMajorVersion();
            Integer major2 = v2.getMajorVersion();
            if (major1 == null && major2 == null) {
                return 0;
            }
            if (major1 == null) {
                return 1;
            }
            if (major2 == null) {
                return -1;
            }
            if (!major1.equals(major2)) {
                return -major1.compareTo(major2);
            }
            
            Integer minor1 = v1.getMinorVersion();
            Integer minor2 = v2.getMinorVersion();
            if (minor1 == null && minor2 == null) {
                return 0;
            }
            if (minor1 == null) {
                return 1;
            }
            if (minor2 == null) {
                return -1;
            }
            if (!minor1.equals(minor2)) {
                return -minor1.compareTo(minor2);
            }
            
            String patch1 = v1.getPatchVersion();
            String patch2 = v2.getPatchVersion();
            if (patch1 == null && patch2 == null) {
                return 0;
            }
            if (patch1 == null) {
                return 1;
            }
            if (patch2 == null) {
                return -1;
            }
            return -patch1.compareTo(patch2);
        }
        
    }
    
    /**
     * Gets the current version of the application.
     */
    public static Version getCurrentVersion() {
        return new Version(modulesService.getCurrentVersion());
    }
    
    /**
     * Gets the major version number of the version.
     */
    private Integer getMajorVersion() {
        return major;
    }
    
    /**
     * Gets the minor version number of the version.
     */
    private Integer getMinorVersion() {
        return minor;
    }
    
    /**
     * Gets the patch part of the version.
     */
    private String getPatchVersion() {
        return patch;
    }
}
