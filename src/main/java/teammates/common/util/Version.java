package teammates.common.util;

/**
 * Represents a version by 3 parts: major version, minor version and patch version.
 */
public class Version implements Comparable<Version> {
    /**
     * The original String of the version. It could be either XX-XX-XXXXX or XX.XX.XXXX format.
     */
    private String originalRepresentation;
    private String major;
    private String minor;
    private String patch;
    private Boolean isRcVersion;
    
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
     * major = "15", minor = null and patch = null
     * 
     * version = 15.01
     * major = "15", minor = "01" and patch = null
     * 
     * version = 15.01.03
     * major = "15", minor = "01" and patch = "03"
     * 
     * version = 15.01.03.01
     * major = "15", minor = "01" and patch = "03.01"
     * 
     * It also support RC versions, which has "rc" appended at the end of the string.
     * For example: 5rc, 4.55rc, 5.55.01rc
     */
    
    /**
     * Creates a new instance of Version from string.
     * It accepts either XX-XX-XXXXX or XX.XX.XXXX format.
     */
    public Version(String versionInString) {
        originalRepresentation = versionInString;
        
        if (versionInString.contains("rc")) {
            versionInString = versionInString.replace("rc", "");
            isRcVersion = true;
        } else {
            isRcVersion = false;
        }
        
        String[] list;
        if (versionInString.contains("-")) {
            list = versionInString.split("-", 3);   // split into at most 3 parts
        } else {
            list = versionInString.split("\\.", 3); // regex escape for dots '.'
        }
        if (list.length > 0) {
            major = list[0];
        }
        if (list.length > 1) {
            minor = list[1];
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
        return originalRepresentation.replace('-', '.');
    }
    
    /**
     * Converts to String in format XX-XX-XXXX
     */
    public String toStringWithDashes() {
        return originalRepresentation.replace('.', '-');
    }
    
    /**
     * Compares version numbers.
     * If their length are different, 0s will be appended in front of shorter string until
     * the length are the same.
     */
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
        while(s1.length() < s2.length()) {
            s1 = "0" + s1;
        }
        while(s2.length() < s1.length()) {
            s2 = "0" + s2;
        }
        return -s1.compareTo(s2);
    }
    
    /**
     * Compares versions by major, minor then by patch.
     * The version with greater major, minor or patch will be smaller.
     */
    @Override
    public int compareTo(Version anotherVersion) {
        int majorComparisonResult = compareVersionString(this.major, anotherVersion.major);
        if (majorComparisonResult != 0) {
            return majorComparisonResult;
        }
        int minorComparisonResult = compareVersionString(this.minor, anotherVersion.minor);
        if (minorComparisonResult != 0) {
            return minorComparisonResult;
        }
        int patchComparisonResult = compareVersionString(this.patch, anotherVersion.patch);
        if (patchComparisonResult != 0) {
            return patchComparisonResult;
        }
        return -isRcVersion.compareTo(anotherVersion.isRcVersion);
    }
}
