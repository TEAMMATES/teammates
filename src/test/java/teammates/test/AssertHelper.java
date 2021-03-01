package teammates.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;

import teammates.common.util.TimeHelperExtension;

/**
 * Provides additional assertion methods that are often used during testing.
 */
public final class AssertHelper {

    private AssertHelper() {
        // utility class
    }

    /**
     * Assert instant is now +- 1 min.
     */
    public static void assertInstantIsNow(Instant instant) {
        assertInstantWithinRange(instant, TimeHelperExtension.getInstantMinutesOffsetFromNow(-1),
                TimeHelperExtension.getInstantMinutesOffsetFromNow(1));
    }

    private static void assertInstantWithinRange(Instant instant, Instant start, Instant end) {
        assertTrue(!(instant.isBefore(start) || instant.isAfter(end)));
    }

    /**
     * Asserts that the superstringActual contains the exact occurrence of
     * substringExpected. Display the difference between the two on failure.
     */
    public static void assertContains(String substringExpected,
            String superstringActual) {
        if (!superstringActual.contains(substringExpected)) {
            assertEquals(substringExpected, superstringActual);
        }
    }

    /**
     * Asserts that the two given lists have the same contents, ignoring their order.
     */
    public static void assertSameContentIgnoreOrder(List<?> a, List<?> b) {

        String expectedListAsString = Joiner.on("\t").join(a);
        String actualListAsString = Joiner.on("\t").join(b);

        List<String> expectedStringTypeList = new ArrayList<>(Arrays.asList(expectedListAsString.split("\t")));
        List<String> actualStringTypeList = new ArrayList<>(Arrays.asList(actualListAsString.split("\t")));

        expectedStringTypeList.sort(null);
        actualStringTypeList.sort(null);

        assertEquals(expectedStringTypeList, actualStringTypeList);

    }

}
