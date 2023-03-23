package teammates.e2e.util;

import java.util.Arrays;
import java.util.List;

import com.deque.html.axecore.results.CheckedNode;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;

/**
 * Utility class for accessibility tests.
 */
public final class AxeUtil {

    /**
     * List of rules to be disabled for accessibility tests:
     * 1. colour-contrast - disabled as Bootstrap default colours are used as much as possible throughout the website.
     * 2. empty-table-header - disabled for the instructor student enroll page,
     * as the page uses Handsontable which does not yet support accessibility.
     * 3. landmark-complementary-is-top-level - temporarily disabled due to a bug in TinyMCE,
     * and will be fixed in a future update. See https://github.com/tinymce/tinymce/issues/7639
     * 4. landmark-unique - disabled for instructor feedback edit page, likely also caused by TinyMCE.
     */
    public static final List<String> DISABLED_RULES = Arrays.asList(
            "color-contrast", "empty-table-header", "landmark-complementary-is-top-level", "landmark-unique"
    );

    /**
     * Builder for accessibility tests.
     */
    public static final AxeBuilder AXE_BUILDER = new AxeBuilder().disableRules(DISABLED_RULES);

    private AxeUtil() {
        // Utility class
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
