package teammates.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.appengine.api.modules.ModulesService;
import com.google.appengine.api.modules.ModulesServiceFactory;

/**
 * Represents a version by 3 parts: major version, minor version and patch version.
 */
public class Version {
    private static ModulesService modulesService = ModulesServiceFactory.getModulesService();
    private Integer major;
    private Integer minor;
    private String patch;
    /**
     * The version is represented by 3 parts: major, minor and patch.
     * If the version has fewer than 3 numbers, the numbers will be assigned to major then to minor (if possible).
     * Those without number will be null.
     * 
     * If the version has more than 3 numbers, the first number will be major, the second number 
     * will be minor and the rest will be patch.
     * 
     * For example: 
     * version = 15
     * major = 15, minor = null and patch = null
     * 
     * version = 15.01
     * major = 15, minor = 1 and patch = null
     * 
     * version = 15.01.03
     * major = 15, minor = 1 and patch = "03"
     * 
     * version = 15.01.03.01
     * major = 15, minor = 1 and patch = "03.01"
     * 
     */
    
    
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
     * Gets hash code for this version.
     */
    public int hashCode() {
        return toString().hashCode();
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
        private int compareVersionNumber(Integer num1, Integer num2) {
            if (num1 == null && num2 == null) {
                return 0;
            }
            if (num1 == null) {
                return 1;
            }
            if (num2 == null) {
                return -1;
            }
            return -num1.compareTo(num2);
        }
        
        private int compareVersionString(String s1, String s2) {
            if (s1 == null && s2 == null) {
                return 0;
            }
            if (s1 == null) {
                return 1;
            }
            if (s2 == null) {
                return -1;
            }
            return -s1.compareTo(s2);
        }
        
        @Override
        public int compare(Version v1, Version v2) {
            int majorComparisonResult = compareVersionNumber(v1.getMajorVersion(), v2.getMajorVersion());
            if (majorComparisonResult != 0) {
                return majorComparisonResult;
            }
            int minorComparisonResult = compareVersionNumber(v1.getMinorVersion(), v2.getMinorVersion());
            if (minorComparisonResult != 0) {
                return minorComparisonResult;
            }
            int patchComparisonResult = compareVersionString(v1.getPatchVersion(), v2.getPatchVersion());
            return patchComparisonResult;
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
