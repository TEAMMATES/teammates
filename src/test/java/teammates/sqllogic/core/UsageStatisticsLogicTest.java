package teammates.sqllogic.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.storage.sqlapi.UsageStatisticsDb;
import teammates.storage.sqlentity.UsageStatistics;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link UsageStatisticsLogic}.
 */
public class UsageStatisticsLogicTest extends BaseTestCase {

    private final UsageStatisticsLogic usageStatisticsLogic = UsageStatisticsLogic.inst();

    private UsageStatisticsDb usageStatisticsDb;

    @BeforeMethod
    public void setUpMethod() {
        usageStatisticsDb = mock(UsageStatisticsDb.class);
        usageStatisticsLogic.initLogicDependencies(usageStatisticsDb);
    }

    // ==================== GET Tests ====================

    @Test
    public void testGetUsageStatisticsForTimeRange_statisticsExist_success() {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2024-01-02T00:00:00Z");
        assertTrue(startTime.isBefore(endTime));

        UsageStatistics stats1 = new UsageStatistics(
                startTime, 1, 100, 10, 50, 5, 2, 20, 30);
        UsageStatistics stats2 = new UsageStatistics(
                startTime.plusSeconds(3600), 1, 150, 15, 60, 8, 3, 25, 40);
        List<UsageStatistics> expectedStats = List.of(stats1, stats2);

        when(usageStatisticsDb.getUsageStatisticsForTimeRange(startTime, endTime)).thenReturn(expectedStats);

        List<UsageStatistics> result = usageStatisticsLogic.getUsageStatisticsForTimeRange(startTime, endTime);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(stats1, result.get(0));
        assertEquals(stats2, result.get(1));
        assertEquals(100, result.get(0).getNumResponses());
        assertEquals(150, result.get(1).getNumResponses());
        assertEquals(10, result.get(0).getNumCourses());
        assertEquals(15, result.get(1).getNumCourses());
        assertEquals(50, result.get(0).getNumStudents());
        assertEquals(60, result.get(1).getNumStudents());
        verify(usageStatisticsDb, times(1)).getUsageStatisticsForTimeRange(startTime, endTime);
    }

    @Test
    public void testGetUsageStatisticsForTimeRange_noStatistics_returnsEmptyList() {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2024-01-02T00:00:00Z");

        when(usageStatisticsDb.getUsageStatisticsForTimeRange(startTime, endTime)).thenReturn(new ArrayList<>());

        List<UsageStatistics> result = usageStatisticsLogic.getUsageStatisticsForTimeRange(startTime, endTime);

        assertTrue(result.isEmpty());
        verify(usageStatisticsDb, times(1)).getUsageStatisticsForTimeRange(startTime, endTime);
    }

    @Test
    public void testGetUsageStatisticsForTimeRange_singleStatistic_success() {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2024-01-02T00:00:00Z");

        UsageStatistics stats = new UsageStatistics(
                startTime, 1, 100, 10, 50, 5, 2, 20, 30);
        List<UsageStatistics> expectedStats = List.of(stats);

        when(usageStatisticsDb.getUsageStatisticsForTimeRange(startTime, endTime)).thenReturn(expectedStats);

        List<UsageStatistics> result = usageStatisticsLogic.getUsageStatisticsForTimeRange(startTime, endTime);

        assertNotNull(result);
        assertEquals(1, result.size());
        UsageStatistics resultStat = result.get(0);
        assertEquals(stats, resultStat);
        assertEquals(startTime, resultStat.getStartTime());
        assertEquals(1, resultStat.getTimePeriod());
        assertEquals(100, resultStat.getNumResponses());
        assertEquals(10, resultStat.getNumCourses());
        assertEquals(50, resultStat.getNumStudents());
        assertEquals(5, resultStat.getNumInstructors());
        assertEquals(2, resultStat.getNumAccountRequests());
        assertEquals(20, resultStat.getNumEmails());
        assertEquals(30, resultStat.getNumSubmissions());
        assertNotNull(resultStat.getId());
    }

    @Test
    public void testGetUsageStatisticsForTimeRange_wideTimeRange_success() {
        Instant startTime = Instant.parse("2020-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2024-12-31T23:59:59Z");
        assertTrue(startTime.isBefore(endTime));

        UsageStatistics stats = new UsageStatistics(
                Instant.parse("2022-06-15T00:00:00Z"), 1, 500, 50, 200, 25, 10, 100, 150);
        List<UsageStatistics> expectedStats = List.of(stats);

        when(usageStatisticsDb.getUsageStatisticsForTimeRange(startTime, endTime)).thenReturn(expectedStats);

        List<UsageStatistics> result = usageStatisticsLogic.getUsageStatisticsForTimeRange(startTime, endTime);

        assertNotNull(result);
        assertEquals(1, result.size());
        UsageStatistics resultStat = result.get(0);
        assertEquals(500, resultStat.getNumResponses());
        assertEquals(50, resultStat.getNumCourses());
        assertEquals(200, resultStat.getNumStudents());
        assertEquals(25, resultStat.getNumInstructors());
        assertEquals(10, resultStat.getNumAccountRequests());
        assertEquals(100, resultStat.getNumEmails());
        assertEquals(150, resultStat.getNumSubmissions());
        verify(usageStatisticsDb, times(1)).getUsageStatisticsForTimeRange(startTime, endTime);
    }

    @Test
    public void testGetUsageStatisticsForTimeRange_invalidTimeRange_throwsException() {
        Instant startTime = Instant.parse("2024-01-02T00:00:00Z");
        Instant endTime = Instant.parse("2024-01-01T00:00:00Z");
        assertTrue(startTime.isAfter(endTime));

        // Should throw AssertionError due to assertion in the method
        assertThrows(AssertionError.class, () -> {
            usageStatisticsLogic.getUsageStatisticsForTimeRange(startTime, endTime);
        });
        verify(usageStatisticsDb, never()).getUsageStatisticsForTimeRange(any(), any());
    }

    // ==================== CALCULATE Tests ====================

    @Test
    public void testCalculateEntitiesStatisticsForTimeRange_returnsStatistics() {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2024-01-02T00:00:00Z");
        assertTrue(startTime.isBefore(endTime));

        UsageStatistics result = usageStatisticsLogic.calculateEntitiesStatisticsForTimeRange(startTime, endTime);

        assertNotNull(result);
        assertEquals(startTime, result.getStartTime());
        assertEquals(1, result.getTimePeriod());
        // Current implementation returns 0 for all counts (commented out in source)
        assertEquals(0, result.getNumResponses());
        assertEquals(0, result.getNumCourses());
        assertEquals(0, result.getNumStudents());
        assertEquals(0, result.getNumInstructors());
        assertEquals(0, result.getNumAccountRequests());
        assertEquals(0, result.getNumEmails());
        assertEquals(0, result.getNumSubmissions());
        assertNotNull(result.getId());
    }

    @Test
    public void testCalculateEntitiesStatisticsForTimeRange_invalidTimeRange_throwsException() {
        Instant startTime = Instant.parse("2024-01-02T00:00:00Z");
        Instant endTime = Instant.parse("2024-01-01T00:00:00Z");
        assertTrue(startTime.isAfter(endTime));

        // Should throw AssertionError due to assertion in the method
        assertThrows(AssertionError.class, () -> {
            usageStatisticsLogic.calculateEntitiesStatisticsForTimeRange(startTime, endTime);
        });
    }

    @Test
    public void testCalculateEntitiesStatisticsForTimeRange_distantPast_returnsStatistics() {
        Instant startTime = Instant.parse("2010-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2010-12-31T23:59:59Z");

        UsageStatistics result = usageStatisticsLogic.calculateEntitiesStatisticsForTimeRange(startTime, endTime);

        assertNotNull(result);
        assertEquals(startTime, result.getStartTime());
    }

    @Test
    public void testCalculateEntitiesStatisticsForTimeRange_recentTime_returnsStatistics() {
        Instant startTime = Instant.now().minusSeconds(86400); // 1 day ago
        Instant endTime = Instant.now();

        UsageStatistics result = usageStatisticsLogic.calculateEntitiesStatisticsForTimeRange(startTime, endTime);

        assertNotNull(result);
        assertEquals(startTime, result.getStartTime());
    }

    // ==================== CREATE Tests ====================

    @Test
    public void testCreateUsageStatistics_validStatistics_success() {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");
        UsageStatistics stats = new UsageStatistics(
                startTime, 1, 100, 10, 50, 5, 2, 20, 30);

        when(usageStatisticsDb.createUsageStatistics(stats)).thenReturn(stats);

        UsageStatistics result = usageStatisticsLogic.createUsageStatistics(stats);

        assertNotNull(result);
        assertEquals(stats, result);
        assertEquals(stats.getId(), result.getId());
        assertEquals(startTime, result.getStartTime());
        assertEquals(1, result.getTimePeriod());
        assertEquals(100, result.getNumResponses());
        assertEquals(10, result.getNumCourses());
        assertEquals(50, result.getNumStudents());
        assertEquals(5, result.getNumInstructors());
        assertEquals(2, result.getNumAccountRequests());
        assertEquals(20, result.getNumEmails());
        assertEquals(30, result.getNumSubmissions());
        verify(usageStatisticsDb, times(1)).createUsageStatistics(stats);
    }

    @Test
    public void testCreateUsageStatistics_withZeroCounts_success() {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");
        UsageStatistics stats = new UsageStatistics(
                startTime, 1, 0, 0, 0, 0, 0, 0, 0);

        when(usageStatisticsDb.createUsageStatistics(stats)).thenReturn(stats);

        UsageStatistics result = usageStatisticsLogic.createUsageStatistics(stats);

        assertNotNull(result);
        assertEquals(stats, result);
        assertEquals(startTime, result.getStartTime());
        assertEquals(1, result.getTimePeriod());
        assertEquals(0, result.getNumResponses());
        assertEquals(0, result.getNumCourses());
        assertEquals(0, result.getNumStudents());
        assertEquals(0, result.getNumInstructors());
        assertEquals(0, result.getNumAccountRequests());
        assertEquals(0, result.getNumEmails());
        assertEquals(0, result.getNumSubmissions());
    }

    @Test
    public void testCreateUsageStatistics_withHighCounts_success() {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");
        UsageStatistics stats = new UsageStatistics(
                startTime, 24, 100000, 5000, 50000, 2500, 1000, 75000, 80000);

        when(usageStatisticsDb.createUsageStatistics(stats)).thenReturn(stats);

        UsageStatistics result = usageStatisticsLogic.createUsageStatistics(stats);

        assertEquals(stats, result);
        assertEquals(100000, result.getNumResponses());
        assertEquals(5000, result.getNumCourses());
        assertEquals(50000, result.getNumStudents());
        assertEquals(2500, result.getNumInstructors());
        assertEquals(1000, result.getNumAccountRequests());
        assertEquals(75000, result.getNumEmails());
        assertEquals(80000, result.getNumSubmissions());
        assertEquals(24, result.getTimePeriod());
    }

    @Test
    public void testCreateUsageStatistics_multipleCreations_success() {
        Instant startTime1 = Instant.parse("2024-01-01T00:00:00Z");
        Instant startTime2 = Instant.parse("2024-01-02T00:00:00Z");

        UsageStatistics stats1 = new UsageStatistics(
                startTime1, 1, 100, 10, 50, 5, 2, 20, 30);
        UsageStatistics stats2 = new UsageStatistics(
                startTime2, 1, 200, 20, 100, 10, 4, 40, 60);

        when(usageStatisticsDb.createUsageStatistics(stats1)).thenReturn(stats1);
        when(usageStatisticsDb.createUsageStatistics(stats2)).thenReturn(stats2);

        UsageStatistics result1 = usageStatisticsLogic.createUsageStatistics(stats1);
        UsageStatistics result2 = usageStatisticsLogic.createUsageStatistics(stats2);

        assertEquals(stats1, result1);
        assertEquals(stats2, result2);
        verify(usageStatisticsDb, times(1)).createUsageStatistics(stats1);
        verify(usageStatisticsDb, times(1)).createUsageStatistics(stats2);
    }

    // ==================== EDGE CASE Tests ====================

    @Test
    public void testGetUsageStatisticsForTimeRange_boundaryTimes_success() {
        Instant startTime = Instant.EPOCH;
        Instant endTime = Instant.parse("2030-12-31T23:59:59Z");

        when(usageStatisticsDb.getUsageStatisticsForTimeRange(startTime, endTime)).thenReturn(new ArrayList<>());

        List<UsageStatistics> result = usageStatisticsLogic.getUsageStatisticsForTimeRange(startTime, endTime);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCreateUsageStatistics_withDifferentTimePeriods_success() {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");

        // Test with hourly time period (1 hour = 1)
        UsageStatistics hourlyStats = new UsageStatistics(
                startTime, 1, 10, 1, 5, 1, 0, 2, 3);
        when(usageStatisticsDb.createUsageStatistics(hourlyStats)).thenReturn(hourlyStats);
        UsageStatistics result1 = usageStatisticsLogic.createUsageStatistics(hourlyStats);
        assertEquals(1, result1.getTimePeriod());

        // Test with daily time period (24 hours = 24)
        UsageStatistics dailyStats = new UsageStatistics(
                startTime, 24, 100, 10, 50, 5, 2, 20, 30);
        when(usageStatisticsDb.createUsageStatistics(dailyStats)).thenReturn(dailyStats);
        UsageStatistics result2 = usageStatisticsLogic.createUsageStatistics(dailyStats);
        assertEquals(24, result2.getTimePeriod());

        // Test with weekly time period (168 hours = 168)
        UsageStatistics weeklyStats = new UsageStatistics(
                startTime, 168, 700, 70, 350, 35, 14, 140, 210);
        when(usageStatisticsDb.createUsageStatistics(weeklyStats)).thenReturn(weeklyStats);
        UsageStatistics result3 = usageStatisticsLogic.createUsageStatistics(weeklyStats);
        assertEquals(168, result3.getTimePeriod());
    }

    @Test
    public void testUsageStatistics_verifyAllFieldsAccessible() {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");
        int timePeriod = 1;
        int numResponses = 100;
        int numCourses = 10;
        int numStudents = 50;
        int numInstructors = 5;
        int numAccountRequests = 2;
        int numEmails = 20;
        int numSubmissions = 30;

        UsageStatistics stats = new UsageStatistics(
                startTime, timePeriod, numResponses, numCourses,
                numStudents, numInstructors, numAccountRequests, numEmails, numSubmissions);

        assertEquals(startTime, stats.getStartTime());
        assertEquals(timePeriod, stats.getTimePeriod());
        assertEquals(numResponses, stats.getNumResponses());
        assertEquals(numCourses, stats.getNumCourses());
        assertEquals(numStudents, stats.getNumStudents());
        assertEquals(numInstructors, stats.getNumInstructors());
        assertEquals(numAccountRequests, stats.getNumAccountRequests());
        assertEquals(numEmails, stats.getNumEmails());
        assertEquals(numSubmissions, stats.getNumSubmissions());
        assertNotNull(stats.getId());
    }
}
