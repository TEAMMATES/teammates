package teammates.e2e.util;

import com.deque.html.axecore.results.CheckedNode;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;

/**
 * Utility class for accessibility tests.
 */
public final class AxeUtil {

    /**
     * Builder for accessibility tests.
     */
    public static final AxeBuilder AXE_BUILDER = new AxeBuilder();

    private AxeUtil() {
        // Utility class
    }

    static {
        // Disable checking of colour contrast
        AXE_BUILDER.setOptions("{ \"rules\": { \"color-contrast\": { \"enabled\": false } } }");
    }

    /**
     * Formats accessibility violations into a readable string.
     */
    public static String formatViolations(Results results) {
        int ruleCounter = 1;
        StringBuilder builder = new StringBuilder(500);
        for (Rule rule : results.getViolations()) {
            int nodeCounter = 1;
            builder.append(ruleCounter).append(". ").append(rule.getId()).append(" - ").append(rule.getDescription())
                    .append(" (").append(rule.getHelpUrl()).append(")\n");
            for (CheckedNode checkedNode : rule.getNodes()) {
                builder.append(ruleCounter).append('.').append(nodeCounter).append(". ")
                        .append(checkedNode.getTarget()).append('\n')
                        .append(checkedNode.getFailureSummary()).append('\n');
                nodeCounter++;
            }
            ruleCounter++;
        }
        return builder.toString();
    }
}
