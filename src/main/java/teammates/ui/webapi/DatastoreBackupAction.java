package teammates.ui.webapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.auth.oauth2.GoogleCredentials;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;

/**
 * Cron job: performs Datastore backup.
 *
 * @see <a href="https://cloud.google.com/datastore/docs/export-import-entities">https://cloud.google.com/datastore/docs/export-import-entities</a>
 * @see <a href="https://cloud.google.com/datastore/docs/reference/admin/rest/v1/projects/export">https://cloud.google.com/datastore/docs/reference/admin/rest/v1/projects/export</a>
 */
public class DatastoreBackupAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {
        if (Config.IS_DEV_SERVER) {
            log.info("Skipping backup in dev server.");
            return new JsonResult("Successful");
        }
        if (!Config.ENABLE_DATASTORE_BACKUP) {
            log.info("Skipping backup by system admin's choice.");
            return new JsonResult("Successful");
        }
        String appId = Config.APP_ID;

        HttpPost post = new HttpPost("https://datastore.googleapis.com/v1/projects/" + appId + ":export");
        post.setHeader("Content-Type", "application/json");

        try {
            String accessToken = GoogleCredentials.getApplicationDefault()
                    .createScoped("https://www.googleapis.com/auth/datastore")
                    .refreshAccessToken()
                    .getTokenValue();
            post.setHeader("Authorization", "Bearer " + accessToken);
        } catch (IOException e) {
            log.severe("Failed to obtain credentials: " + e.getMessage());
            return new JsonResult("Failure");
        }

        Map<String, Object> body = new HashMap<>();
        String timestamp = Instant.now().toString();
        // Documentation is wrong; the param name is output_url_prefix instead of outputUrlPrefix
        body.put("output_url_prefix", "gs://" + Config.BACKUP_GCS_BUCKETNAME + "/datastore-backups/" + timestamp);

        StringEntity entity = new StringEntity(JsonUtils.toCompactJson(body), Const.ENCODING);
        post.setEntity(entity);

        try (CloseableHttpClient client = HttpClients.createDefault();
                CloseableHttpResponse resp = client.execute(post);
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(resp.getEntity().getContent(), Const.ENCODING))) {
            String output = br.lines().collect(Collectors.joining(System.lineSeparator()));
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                log.info("Backup request successful:" + System.lineSeparator() + output);
            } else {
                log.severe("Backup request failure:" + System.lineSeparator() + output);
            }
        } catch (IOException e) {
            log.severe("Backup request failure: " + e.getMessage());
        }
        return new JsonResult("Successful");
    }

}
