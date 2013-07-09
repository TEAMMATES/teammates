package teammates.common.util;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestHelper {

	/**
	 * 
	 * @param paramMap A parameter map (e.g., the kind found in HttpServletRequests)
	 * @param key
	 * @return the first value for the key. Returns null if key not found.
	 */
	public static String getValueFromParamMap(Map<String, String[]> paramMap, String key) {
		String[] values = paramMap.get(key);
		return values == null ? null : values[0];
	}

	/**
	 * 
	 * @param paramMap A parameter map (e.g., the kind found in HttpServletRequests)
	 * @param key
	 * @return all values for the key. Returns null if key not found.
	 */
	public static String[] getValuesFromParamMap(Map<String, String[]> paramMap, String key) {
		String[] values = paramMap.get(key);
		return values == null ? null : values;
	}

	public static String printRequestParameters(HttpServletRequest request) {
		String requestParameters = "{";
		for (Enumeration<?> f = request.getParameterNames(); f.hasMoreElements();) {
			String paramet = new String(f.nextElement().toString());
			requestParameters += paramet + "::";
			String[] parameterValues = request.getParameterValues(paramet);
			for (int j = 0; j < parameterValues.length; j++){
				requestParameters += parameterValues[j] + "//";
			}
			requestParameters = requestParameters.substring(0, requestParameters.length() - 2) + ", ";
		}
		if (!requestParameters.equals("{")) {
			requestParameters = requestParameters.substring(0, requestParameters.length() - 2);
		}
		requestParameters += "}";
		return requestParameters;
	}

	/**
	 * @return  the URL used for the HTTP request but without the domain.
	 * e.g. "/page/studentHome?user=james" 
	 */
	public static String getRequestedURL(HttpServletRequest req) {
		String link = req.getRequestURI();
		String query = req.getQueryString();
		if (query != null && !query.trim().isEmpty()){
			link += "?" + query;
		}
		return link;
	}

}
