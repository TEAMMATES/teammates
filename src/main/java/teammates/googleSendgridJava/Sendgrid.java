/**
 * Imported from https://github.com/sendgrid/sendgrid-google-java
 * 
 * The link to the Github repo is provided in the documentation for Bulk Mail With Analytics
 * via Sendgrid for Java: https://cloud.google.com/appengine/docs/java/mail/sendgrid
 */

package teammates.googleSendgridJava;

import java.net.HttpURLConnection;
import java.util.*;
import java.io.IOException;
import java.util.Iterator;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.labs.repackaged.org.json.JSONArray;

public class Sendgrid {
    
    private String from;
    private String fromName;
    private String replyTo;
    private String subject;
    private String text;
    private String html;
    
    private String serverResponse = "";
    private ArrayList<String> toList  = new ArrayList<String>();
    private ArrayList<String> toNameList  = new ArrayList<String>();
    private ArrayList<String> bccList = new ArrayList<String>();
    private JSONObject headerList = new JSONObject();

    protected String domain = "https://sendgrid.com/";
    protected String endpoint= "api/mail.send.json";
    protected String username;
    protected String password;

    public Sendgrid(String username, String password) throws JSONException {
        this.username = username;
        this.password = password;      
        this.setCategory("google_sendgrid_java_lib");
    }

    /**
     * @return  List of recipients
     */
    public ArrayList<String> getTos() {
        return this.toList;
    }

    /**
     * Initialize a single email for the recipient 'to' field
     * Destroy previous recipient 'to' data.
     *
     * @param    email   A list of email addresses
     * @return           The SendGrid object.
     */
    public Sendgrid setTo(String email) {
        this.toList = new ArrayList<String>();
        this.addTo(email);

        return this;
    }

    /**
     * Append an email address to the existing list of addresses
     * Preserve previous recipient 'to' data.
     *
     * @param    email   Recipient email address
     * @param    name    Recipient name
     * @return           The SendGrid object.
     */
    public Sendgrid addTo(String email, String name) {
        String toAddress = (name.length() > 0) ? name + "<" + email + ">" : email;
        this.toList.add(toAddress);
        
        return this;
    }

    /**
     * Make the second parameter("name") of "addTo" method optional
     *
     * @param   email   A single email address 
     * @return          The SendGrid object.  
     */
    public Sendgrid addTo(String email) {
        return addTo(email, "");
    }

    /**
     * @return  List of names of recipients
     */
    public ArrayList<String> getToNames() {
        return this.toNameList;
    }

    /**
     * @return  Sender email address
     */
    public String getFrom() {
        return this.from;
    }

    /**
     * Set sender email
     *
     * @param    email   An email address
     * @return           The SendGrid object.
     */
    public Sendgrid setFrom(String email) {
        this.from = email;

        return this;
    }

    /**
     * @return  Sender name
     */
    public String getFromName() {
        return this.fromName;
    }

    /**
     * Set sender name
     *
     * @param    name    The name
     * @return           The SendGrid object.
     */
    public Sendgrid setFromName(String name) {
        this.fromName = name;

        return this;
    }

    /**
     * @return Reply-to address
     */
    public String getReplyTo() {
        return this.replyTo;
    }

    /**
     * Set reply-to address
     *
     * @param  email   the email to reply to
     * @return         the SendGrid object.
     */
    public Sendgrid setReplyTo(String email) {
      this.replyTo = email;

      return this;
    }

    /**
     * @return List of BCC recipients
     */
    public ArrayList<String> getBccs() {
        return this.bccList;
    }

    /**
     * Initialize the list of BCC recipients
     * destroy previous recipient BCC data
     *
     * @param  email   an email address
     * @return         the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid setBcc(String email) throws JSONException {
        this.bccList = new ArrayList<String>();
        this.bccList.add(email);

        this.addFilterSetting("bcc", "enable", "1");
        this.addFilterSetting("bcc", "email", email);

        return this;
    }

    /** 
     * @return  Email subject
     */
    public String getSubject() {
        return this.subject;
    }

    /** 
     * Set email subject
     * 
     * @param    subject   The email subject
     * @return             The SendGrid object
     */
    public Sendgrid setSubject(String subject) {
        this.subject = subject;
      
        return this;
    }

    /** 
     * @return   Plain text email body
     */
    public String getText() {
        return this.text;
    }

    /** 
     * Set plain text email body
     * 
     * @param   text   The plain text of the email
     * @return         The SendGrid object.
     */
    public Sendgrid setText(String text) {
        this.text = text;

        return this;
    }
    
    /** 
     * @return   HTML email body
     */
    public String getHtml() {
        return this.html;
    }

    /** 
     * Set HTML email body
     * 
     * @param   html   The HTML part of the email
     * @return         The SendGrid object.
     */
    public Sendgrid setHtml(String html) {
        this.html = html;

        return this;
    }

    /**
     * Clears the category list and adds the given category
     *
     * @param  category   the new category to append
     * @return            the SendGrid object.
     * @throws JSONException
     */
    private Sendgrid setCategory(String category) throws JSONException {
        JSONArray jsonCategory = new JSONArray(new String[]{category});
        this.headerList.put("category", jsonCategory);
        this.addCategory("google_sendgrid_java_lib");

        return this;
    }

    /**
     * Append a category to the list of categories
     *
     * @param  category   the new category to append
     * @return            the SendGrid object.
     * @throws JSONException
     */
    private Sendgrid addCategory(String category) throws JSONException {
        if (this.headerList.has("category")) {
            ((JSONArray) this.headerList.get("category")).put(category);
        } else {
            this.setCategory(category);
        }

        return this;
    }

    /** 
     * Append a filter setting to the list of filter settings
     *
     * @param  filteName       filter name
     * @param  parameterName    parameter name
     * @param  parameterValue   setting value
     * @throws JSONException
     */
    private Sendgrid addFilterSetting(String filterName, String parameterName, String parameterValue) throws JSONException {
        if (!this.headerList.has("filters")) {
            this.headerList.put("filters", new JSONObject());
        }
        
        if (!((JSONObject) this.headerList.get("filters")).has(filterName)) {
            ((JSONObject) this.headerList.get("filters")).put(filterName, new JSONObject());
        }
        
        if (!((JSONObject) ((JSONObject) this.headerList.get("filters")).get(filterName)).has("settings")) {
            ((JSONObject) ((JSONObject) this.headerList.get("filters")).get(filterName)).put("settings", new JSONObject());
        }
        
        ((JSONObject) ((JSONObject) ((JSONObject) this.headerList.get("filters"))
                                        .get(filterName)).get("settings")).put(parameterName, parameterValue);

        return this;
    }

    /**
     * @return JSONObject with headers
     */
    private JSONObject getHeaders() {
        return this.headerList;
    }

    /**
     * Sets the list headers
     * destroys previous header data
     *
     * @param  keyValuePairs   the list of header data
     * @return                   the SendGrid object.
     */
    private Sendgrid setHeaders(JSONObject keyValuePairs) {
        this.headerList = keyValuePairs;

        return this;
    }

    /**
     * @return  Server response message
     */
    public String getServerResponse() {
        return this.serverResponse;
    }

    /**
     * Converts an ArrayList to a url friendly string
     *
     * @param  array   the array to convert
     * @param  token   the name of parameter
     * @return         a url part that can be concatenated to a url request
     * @throws UnsupportedEncodingException 
     */
    protected String arrayToUrlPart(ArrayList<String> array, String token) throws UnsupportedEncodingException {
        String string = "";
        for (int i = 0;i < array.size(); i++) {
            string += "&" + token + "[]=" + URLEncoder.encode(array.get(i), "UTF-8");
        }

        return string;
    }

    /**
     * Takes the mail message and returns a url friendly querystring
     *
     * @return the data query string to be posted
     * @throws JSONException 
     */
    protected Map<String, String> prepareMessageData() throws JSONException {
        Map<String,String> params = new HashMap<String, String>();

        params.put("api_user", this.username);
        params.put("api_key", this.password);
        params.put("subject", this.getSubject());
        
        if (this.getHtml() != null) {
            params.put("html", this.getHtml());
        }
        
        if (this.getFromName() != null) {
            params.put("fromname", this.getFromName());
        }
        
        params.put("text",this.getText());
        params.put("from", this.getFrom());

        if (this.getReplyTo() != null) {
            params.put("replyto", this.getReplyTo());
        }

        JSONObject headers = this.getHeaders();
        params.put("to", this.getFrom());
        JSONArray tos_json = new JSONArray(this.getTos());
        headers.put("to", tos_json);
        this.setHeaders(headers);
        params.put("x-smtpapi", _escapeUnicode(this.getHeaders().toString()));
        
        return params;
    }

    /**
     * Invoked when a warning is returned from the server that
     * isn't critical
     */
    public static interface WarningListener {
        public void warning(String serverResponse, Throwable t);
    }

    /**
     * Send an email
     *
     * @throws JSONException
     * @throws UnsupportedEncodingException 
     */
    public void send() throws JSONException, UnsupportedEncodingException {
        send(new WarningListener() {
            public void warning(String w, Throwable t) {
                serverResponse = w;
            }
        });
    }

    /**
     * Send an email
     *
     * @param w callback that will receive warnings
     * @throws JSONException
     * @throws UnsupportedEncodingException 
     */
    public void send(WarningListener w) throws JSONException, UnsupportedEncodingException {
        Map<String,String> data = new HashMap<String, String>();

        data = this.prepareMessageData();
        StringBuffer requestParams = new StringBuffer();
        Iterator<String> paramIterator = data.keySet().iterator();
        
        while (paramIterator.hasNext()) {
            final String key = paramIterator.next();
            final String value = data.get(key);
            
            if (key.equals("to") && this.getTos().size() > 0) {
                requestParams.append("to=" + URLEncoder.encode(value, "UTF-8") + "&");               
            } else {
                if (key.equals("toname") && this.getToNames().size() > 0) {
                    requestParams.append(this.arrayToUrlPart(this.getToNames(), "toname").substring(1)+"&");
                } else {
                    try {
                        requestParams.append(URLEncoder.encode(key, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        w.warning("Unsupported Encoding Exception", e);
                    }
                    
                    requestParams.append("=");
                    
                    try {
                        requestParams.append(URLEncoder.encode(value, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        w.warning("Unsupported Encoding Exception", e);
                    }
                    requestParams.append("&");
                }
            }
        }
        
        String request = this.domain + this.endpoint;

        if (this.getBccs().size() > 0) {
            request += "?" +this.arrayToUrlPart(this.getBccs(), "bcc").substring(1);
        }
        
        try {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(requestParams.toString());
            // Get the response
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line, response = "";

            while ((line = reader.readLine()) != null) {
                // Process line
                response += line;
            }
            reader.close();
            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // OK
                serverResponse = "success";
            } else {
                // Server returned HTTP error code.
                JSONObject apiResponse = new JSONObject(response);
                JSONArray errorsObj = (JSONArray) apiResponse.get("errors");
                
                for (int i = 0; i < errorsObj.length(); i++) {
                    if (i != 0) {
                        serverResponse += ", ";
                    }
                    serverResponse += errorsObj.get(i);
                }
                w.warning(serverResponse, null);
            }
        } catch (MalformedURLException e) {
            w.warning("Malformed URL Exception", e);
        } catch (IOException e) {
            w.warning("IO Exception", e);
        }
    }

    private String _escapeUnicode(String input) {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < input.length(); i++) {
          int code = Character.codePointAt(input, i);
          sb.append(String.format((code > 127) ? "\\u%x" : "%c", code)); 
        }
        
        return sb.toString();
      }
}
