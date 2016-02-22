package teammates.common.util;

/**
 * Represents a version by 3 parts: major version, minor version and patch version.
 */
public class Version implements Comparable<Version> {
    /**
     * The original String of the version. It could be either XX-XX-XXXXX or XX.XX.XXXX format.
     */
    private String originalRepresentation;
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
        originalRepresentation = versionInString;
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
        return originalRepresentation.replaceAll("-", ".");
    }
    
    /**
     * Converts to String in format XX-XX-XXXX
     */
    public String toStringForQuery() {
        return originalRepresentation.replaceAll(".", "-");
    }
    
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
    
    /**
     * Compares versions by major, minor then by patch.
     * The version with greater major, minor or patch will be smaller.
     */
    @Override
    public int compareTo(Version anotherVersion) {
        int majorComparisonResult = compareVersionNumber(this.getMajorVersion(), anotherVersion.getMajorVersion());
        if (majorComparisonResult != 0) {
            return majorComparisonResult;
        }
        int minorComparisonResult = compareVersionNumber(this.getMinorVersion(), anotherVersion.getMinorVersion());
        if (minorComparisonResult != 0) {
            return minorComparisonResult;
        }
        int patchComparisonResult = compareVersionString(this.getPatchVersion(), anotherVersion.getPatchVersion());
        return patchComparisonResult;
    }
    
    /**
     * Gets the major version number of the version.
     */
    public Integer getMajorVersion() {
        return major;
    }
    
    /**
     * Gets the minor version number of the version.
     */
    public Integer getMinorVersion() {
        return minor;
    }
    
    /**
     * Gets the patch part of the version.
     */
    public String getPatchVersion() {
        return patch;
    }
}
