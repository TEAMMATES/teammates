package teammates.ui.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data model for a HTML element tag (e.g. &lt;a attr1="value1" attr2="value2"&gt;content&lt;/a&gt;).
 * Contains <i>attributes</i> mapped to their respective values.
 * Can also contain the content between opening and closing tags.
 * If <i>content</i> is set to null, it represents a self-closing tag.
 */
public class ElementTag {
    private String content;
    private Map<String, String> attributes;
    private List<ElementTag> nestedElements = new ArrayList<>();

    /**
     * Constructs an element tag.
     * The first element is used as the content if there are an odd number of elements passed in.
     * Otherwise, it is treated as a self-closing tag and has null for content.
     * @param attributePairs Sets of attribute names and values; includes content as the first variable if any.
     */
    public ElementTag(String... attributePairs) {
        boolean isSelfClosing = attributePairs.length % 2 == 0;

        this.content = isSelfClosing ? null : attributePairs[0];

        int startIndex = isSelfClosing ? 0 : 1;
        this.attributes = new HashMap<>();
        for (int i = startIndex; i < attributePairs.length; i += 2) {
            this.attributes.put(attributePairs[i], attributePairs[i + 1]);
        }
    }

    public String getContent() {
        return content;
    }

    public boolean isSelfClosing() {
        return content == null;
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Associates the specified value to the specified attribute name.
     * @return the previous value associated with attributeName or null if it had none
     */
    public String setAttribute(String attributeName, String attributeValue) {
        return attributes.put(attributeName, attributeValue);
    }

    /**
     * Removes the mapping for the specified attribute name.
     * @return the previous value associated with attributeName or null if it had none
     */
    public String removeAttribute(String attributeName) {
        return attributes.remove(attributeName);
    }

    public List<ElementTag> getNestedElements() {
        return nestedElements;
    }

    public void setNestedElements(List<ElementTag> nestedElements) {
        this.nestedElements = nestedElements;
    }

    public void addNestedElement(ElementTag element) {
        this.nestedElements.add(element);
    }

    /**
     * Returns all attributes joined into a string for HTML purposes with a space in front;
     *         attribute added if and only if it has a non-null value,
     *         empty strings will still be treated as empty strings.
     */
    public String getAttributesToString() {
        if (attributes == null || attributes.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        attributes.forEach((key, value) -> {
            sb.append(' ').append(key);
            if (value != null) {
                sb.append("=\"" + value + "\"");
            }
        });
        return sb.toString();
    }
}
