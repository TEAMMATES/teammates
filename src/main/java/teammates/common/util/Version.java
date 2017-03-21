package teammates.common.util;

/**
 * Represents a version by 3 parts: major version, minor version and patch version.
 *
 * <p>If the version has fewer than 3 numbers, the numbers will be assigned to major then to minor (if possible).
 * Those without number will be null.
 *
 * <p>If the version has more than 3 numbers, the first number will be major, the second number
 * will be minor and the rest will be patch.
 *
 * <p>For example:
 * <ul>
 * <li>version = 15: major = "15", minor = null and patch = null</li>
 * <li>version = 15.01: major = "15", minor = "01" and patch = null</li>
 * <li>version = 15.01.03: major = "15", minor = "01" and patch = "03"</li>
 * <li>version = 15.01.03.01: major = "15", minor = "01" and patch = "03.01"</li>
 * </ul>
 *
 * <p>It also support RC versions, which has "rc" appended at the end of the string.
 * For example: 5rc, 4.55rc, 5.55.01rc
 */
public class Version implements Comparable<Version> {
    /**
     * The original String of the version. It could be either XX-XX-XXXXX or XX.XX.XXXX format.
     */
    private String originalRepresentation;
    private String major;
    private String minor;
    private String patch;
    private boolean isRcVersion;

    /**
     * Creates a new instance of Version from string.
     * It accepts either XX-XX-XXXXX or XX.XX.XXXX format.
     */
    public Version(String versionInString) {
        originalRepresentation = versionInString;
        isRcVersion = versionInString.endsWith("rc");

        String[] list = versionInString.contains("-") // split to at most 3 parts
                      ? versionInString.replace("rc", "").split("-", 3)
                      : versionInString.replace("rc", "").split("\\.", 3); // regex escape for dots '.'
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
    @Override
    public boolean equals(Object anotherVersion) {
        if (anotherVersion == null) {
            return false;
        }
        return toString().equals(anotherVersion.toString());
    }

    /**
     * Gets hash code for this version.
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Converts Version to String in format XX.XX.XXXX.
     */
    @Override
    public String toString() {
        return originalRepresentation.replace('-', '.');
    }

    /**
     * Converts to String in format XX-XX-XXXX.
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
        String convertedS1;
        String convertedS2;
        if (s1.length() == s2.length()) {
            convertedS1 = s1;
            convertedS2 = s2;
        } else if (s1.length() > s2.length()) {
            convertedS1 = s1;
            convertedS2 = StringHelper.generateStringOfLength(s1.length() - s2.length(), '0') + s2;
        } else {
            convertedS1 = StringHelper.generateStringOfLength(s2.length() - s1.length(), '0') + s1;
            convertedS2 = s2;
        }
        return convertedS2.compareTo(convertedS1);
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
        return Boolean.compare(anotherVersion.isRcVersion, isRcVersion);
    }
}
