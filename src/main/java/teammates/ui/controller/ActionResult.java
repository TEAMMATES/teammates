package teammates.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.StatusMessage;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;

/**
 * The result of executing an {@link Action}.
 */
public abstract class ActionResult {

    /** The URI that represents the result.
     * e.g., "/page/instructorHome" "/jsp/instructorHome.jsp"
     */
    public String destination;

    /** True if the action did not complete successfully. */
    public boolean isError;

    /** The 'nominal' user for whom the action was executed. */
    protected AccountAttributes account;

    /** A list of status messages to be shown to the user. */
    protected List<StatusMessage> statusToUser = new ArrayList<StatusMessage>();

    /**
     * Parameters to be sent with the result. These will be automatically added
     * to the {@code destination} of the result. For example, if the {@code destination}
     * is {@code /page/instructorHome} and if we have {@code user=abc} in this map,
     * the result will be sent to {@code /page/instructorHome?user=abc}
     */
    protected Map<String, String> responseParams = new HashMap<String, String>();

    public ActionResult(
            String destination,
            AccountAttributes account,
            List<StatusMessage> status) {

        this.destination = destination;
        this.account = account;
        this.statusToUser = status;
    }

    /**
     * Returns Concatenated version of the status messages collected during the
     *         execution of the action. Messages are separated by {@code '<br>'}
     */
    public String getStatusMessage() {
        List<String> statusMessageTexts = new ArrayList<String>();

        for (StatusMessage msg : statusToUser) {
            statusMessageTexts.add(msg.getText());
        }

        return StringHelper.toString(statusMessageTexts, "<br>");
    }

    public String getStatusMessageColor() {
        return statusToUser == null || statusToUser.isEmpty() ? "info" : statusToUser.get(0).getColor();
    }

    /**
     * Add a (key,value) pair ot the list of response parameters.
     */
    public void addResponseParam(String key, String value) {
        responseParams.put(key, value);
    }

    /**
     * Returns Destination of the result, including parameters.
     *         e.g. {@code /page/instructorHome?user=abc}
     */
    public String getDestinationWithParams() {
        return appendParameters(destination, responseParams);
    }

    /**
     * Sends the result to the intended URL.
     */
    public abstract void send(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException;

    private String appendParameters(String url, Map<String, String> params) {
        String returnValue = url;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            returnValue = Url.addParamToUrl(returnValue, entry.getKey(), entry.getValue());
        }
        return returnValue;
    }
}
