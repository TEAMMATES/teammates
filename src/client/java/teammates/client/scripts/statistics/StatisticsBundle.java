package teammates.client.scripts.statistics;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A bundle that consists of the usages of the application for each institute.
 */
public class StatisticsBundle {

    private InstitutesStatsMetadata institutesStatsMetadata;

    private Map<String, InstituteStats> institutesStats = new HashMap<>();

    /**
     * Gets the stats for a certain institute.
     *
     * @return a empty stats if institute is not found in the bundle.
     */
    private InstituteStats getInstituteStats(String institute) {
        return institutesStats.computeIfAbsent(institute, instituteName -> {
            InstituteStats instituteStats = new InstituteStats();
            instituteStats.name = instituteName;
            return instituteStats;
        });
    }

    /**
     * Adds a student {@code email} to the stats associated with the {@code institute}.
     *
     * <p>If the institute or email is from test data, ignore.
     *
     * <p>If the student email is already in the stats of the institute, ignore.
     */
    public void addStudentEmailToInstitute(String institute, String email) {
        if (isTestingData(institute, email)) {
            return;
        }
        getInstituteStats(institute).studentEmails.add(email);
    }

    /**
     * Adds a instructor {@code email} to the stats associated with the {@code institute}.
     *
     * <p>If the institute or email is from test data, ignore.
     *
     * <p>If the instructor email is already in the stats of the institute, ignore.
     */
    public void addInstructorEmailToInstitute(String institute, String email) {
        if (isTestingData(institute, email)) {
            return;
        }
        getInstituteStats(institute).instructorEmails.add(email);
    }

    /**
     * Determines if the {@code institute} or {@code email} is of the form of test data.
     *
     * <p>See 'src/test/resources/data' for more information.
     */
    private boolean isTestingData(String institute, String email) {
        if (email.toLowerCase().endsWith(".tmt")) {
            return true;
        }
        return institute.contains("TEAMMATES Test Institute");
    }

    /**
     * Gets a list of institute stats sorted by the number of students inside institute.
     */
    public List<InstituteStats> getInstituteStatsSortByTotalStudentsDescending() {
        List<InstituteStats> instituteStatsList = new ArrayList<>(institutesStats.values());
        instituteStatsList.sort(Comparator.comparing(InstituteStats::getStudentTotal).reversed());
        return instituteStatsList;
    }

    /**
     * Gets number of unique students emails in the bundle.
     */
    public int getNumOfUniqueStudentEmails() {
        Set<String> emailSet = new HashSet<>();
        institutesStats.values().forEach(instituteStats -> emailSet.addAll(instituteStats.studentEmails));
        return emailSet.size();
    }

    /**
     * Gets number of unique instructor emails in the bundle.
     */
    public int getNumOfUniqueInstructorEmails() {
        Set<String> emailSet = new HashSet<>();
        institutesStats.values().forEach(instituteStats -> emailSet.addAll(instituteStats.instructorEmails));
        return emailSet.size();
    }

    public Instant getStatsSince() {
        return institutesStatsMetadata.statsSince;
    }

    public void setStatsSince(Instant time) {
        institutesStatsMetadata.statsSince = time;
    }

    public void setInstitutesStatsMetadata(InstitutesStatsMetadata institutesStatsMetadata) {
        this.institutesStatsMetadata = institutesStatsMetadata;
    }

    public InstitutesStatsMetadata getInstitutesStatsMetadata() {
        return institutesStatsMetadata;
    }

    public void setInstitutesStats(Map<String, InstituteStats> institutesStats) {
        this.institutesStats = institutesStats;
    }

    public Map<String, InstituteStats> getInstitutesStats() {
        return institutesStats;
    }

    /**
     * Stats that indicates the number of unique (uniqueness is determined by email)
     * students and instructors in the institute.
     */
    public static class InstituteStats {

        private String name;
        private Set<String> studentEmails = new HashSet<>();
        private Set<String> instructorEmails = new HashSet<>();

        public String getName() {
            return name;
        }

        public int getStudentTotal() {
            return studentEmails.size();
        }

        public int getInstructorTotal() {
            return instructorEmails.size();
        }
    }

    /**
     * Metadata for the bundle.
     */
    public static class InstitutesStatsMetadata {
        private Instant statsSince;

        public InstitutesStatsMetadata() {
            // time when TEAMMATES project begins
            statsSince = Instant.parse("2010-01-01T00:00:00.000Z");
        }
    }
}
