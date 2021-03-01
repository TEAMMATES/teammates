package teammates.logic.core;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.Instant;
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

import com.google.cloud.tasks.v2.AppEngineHttpRequest;
import com.google.cloud.tasks.v2.CloudTasksClient;
import com.google.cloud.tasks.v2.HttpMethod;
import com.google.cloud.tasks.v2.QueueName;
import com.google.cloud.tasks.v2.Task;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;

import teammates.common.exception.TeammatesException;
import teammates.common.util.AppUrl;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.TaskWrapper;

/**
 * Handles operations related to task queues.
 */
public class TaskQueuesLogic {

    private static final Logger log = Logger.getLogger();

    /**
     * Adds the given task to the specified queue.
     *
     * @param task the task object containing the details of task to be added
     */
    public void addTask(TaskWrapper task) {
        addDeferredTask(task, 0);
    }

    /**
     * Adds the given task, to be run after the specified time, to the specified queue.
     *
     * @param task the task object containing the details of task to be added
     * @param countdownTime the time delay for the task to be executed
     */
    public void addDeferredTask(TaskWrapper task, long countdownTime) {
        if (Config.isDevServer()) {
            // In dev server, task queues are either not active (i.e. they will not be executed even if queued)
            // or they will be executed immediately without going through any kind of "queue"

            if (!Config.TASKQUEUE_ACTIVE) {
                return;
            }
            HttpPost post = new HttpPost(createBasicUri(
                    "http://localhost:8080" + task.getWorkerUrl(), task.getParamMap()));

            if (task.getRequestBody() != null) {
                StringEntity entity = new StringEntity(
                        JsonUtils.toCompactJson(task.getRequestBody()), Charset.forName(Const.ENCODING));
                post.setEntity(entity);
            }

            post.addHeader("X-AppEngine-QueueName", task.getQueueName());
            post.addHeader("X-Google-DevAppserver-SkipAdminCheck", "true");

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                httpClient.execute(post);
            } catch (IOException e) {
                log.severe("Error when executing HTTP request: " + TeammatesException.toStringWithStackTrace(e));
            }
            return;
        }

        try (CloudTasksClient client = CloudTasksClient.create()) {
            String queuePath = QueueName.of(Config.APP_ID, Config.APP_REGION, task.getQueueName()).toString();

            AppEngineHttpRequest.Builder requestBuilder =
                    AppEngineHttpRequest.newBuilder()
                            .setHttpMethod(HttpMethod.POST);

            if (task.getRequestBody() == null) {
                String relativeUrl = "http://place.holder"; // the value is not important
                AppUrl url = new AppUrl(relativeUrl + task.getWorkerUrl());
                task.getParamMap().forEach((key, value) -> url.withParam(key, value));

                requestBuilder.setRelativeUri(url.toString());
            } else {
                String requestBody = JsonUtils.toCompactJson(task.getRequestBody());
                requestBuilder.putHeaders("Content-type", "application/json; charset=utf-8");
                requestBuilder.setRelativeUri(task.getWorkerUrl())
                        .setBody(ByteString.copyFrom(requestBody, Charset.forName(Const.ENCODING)));
            }

            Task.Builder taskBuilder = Task.newBuilder().setAppEngineHttpRequest(requestBuilder.build());
            if (countdownTime > 0) {
                taskBuilder.setScheduleTime(
                        Timestamp.newBuilder()
                                .setSeconds(Instant.now().plusMillis(countdownTime).getEpochSecond()));
            }

            client.createTask(queuePath, taskBuilder.build());
        } catch (IOException e) {
            log.severe("Cannot create Cloud Tasks client: " + TeammatesException.toStringWithStackTrace(e));
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
