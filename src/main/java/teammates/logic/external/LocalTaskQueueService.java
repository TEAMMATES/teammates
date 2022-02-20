package teammates.logic.external;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.TaskWrapper;

/**
 * Holds functions for operations related to task queue in local dev environment.
 */
public class LocalTaskQueueService implements TaskQueueService {

    private static final Logger log = Logger.getLogger();

    @Override
    public void addDeferredTask(TaskWrapper task, long countdownTime) {
        // In dev server, task queues are either not active (i.e. they will not be executed even if queued)
        // or they will be executed immediately without going through any kind of "queue"

        if (!Config.TASKQUEUE_ACTIVE) {
            return;
        }
        HttpPost post = new HttpPost(createBasicUri(
                "http://localhost:" + Config.getPort() + task.getWorkerUrl(), task.getParamMap()));

        if (task.getRequestBody() != null) {
            StringEntity entity = new StringEntity(
                    JsonUtils.toCompactJson(task.getRequestBody()), Const.ENCODING);
            post.setEntity(entity);
        }

        post.addHeader("X-AppEngine-QueueName", task.getQueueName());
        post.addHeader("X-Google-DevAppserver-SkipAdminCheck", "true");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            httpClient.execute(post);
        } catch (IOException e) {
            log.severe("Error when executing HTTP request", e);
        }
    }

    private static URI createBasicUri(String url, Map<String, String> params) {
        List<NameValuePair> postParameters = new ArrayList<>();
        if (params != null) {
            params.forEach((key, value) -> postParameters.add(new BasicNameValuePair(key, value)));
        }

        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.addParameters(postParameters);

            return uriBuilder.build();
        } catch (URISyntaxException e) {
            return null;
        }
    }

}
