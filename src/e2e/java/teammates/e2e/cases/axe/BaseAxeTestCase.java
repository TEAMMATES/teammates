package teammates.e2e.cases.axe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.deque.html.axecore.results.CheckedNode;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;

import teammates.e2e.cases.BaseE2ETestCase;

/**
 * Base class for all accessibility tests using Axe.
 */
abstract class BaseAxeTestCase extends BaseE2ETestCase {

    /**
     * List of rules to be disabled for accessibility tests:
     * 1. colour-contrast - disabled as Bootstrap default colours are used as much as possible throughout the website.
     */
    private static final List<String> COMMON_DISABLED_RULES = Arrays.asList(
            "color-contrast"
    );

    /**
     * Builder for accessibility tests.
     */
    AxeBuilder getAxeBuilder(String... additionalDisabledRules) {
        List<String> disabledRules = new ArrayList<>();
        disabledRules.addAll(COMMON_DISABLED_RULES);
        disabledRules.addAll(Arrays.asList(additionalDisabledRules));
        return new AxeBuilder().disableRules(disabledRules);
    }

    /**
     * Formats accessibility violations into a readable string.
     */
    static String formatViolations(Results results) {
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
