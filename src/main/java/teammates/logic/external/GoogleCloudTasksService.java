package teammates.logic.external;

import java.io.IOException;
import java.time.Instant;

import com.google.cloud.tasks.v2.AppEngineHttpRequest;
import com.google.cloud.tasks.v2.AppEngineRouting;
import com.google.cloud.tasks.v2.CloudTasksClient;
import com.google.cloud.tasks.v2.HttpMethod;
import com.google.cloud.tasks.v2.QueueName;
import com.google.cloud.tasks.v2.Task;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;

import teammates.common.util.AppUrl;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.TaskWrapper;

/**
 * Holds functions for operations related to Google Cloud Tasks.
 */
public class GoogleCloudTasksService implements TaskQueueService {

    private static final Logger log = Logger.getLogger();

    @Override
    public void addDeferredTask(TaskWrapper task, long countdownTime) {
        try (CloudTasksClient client = CloudTasksClient.create()) {
            String queuePath = QueueName.of(Config.APP_ID, Config.APP_REGION, task.getQueueName()).toString();

            AppEngineHttpRequest.Builder requestBuilder =
                    AppEngineHttpRequest.newBuilder()
                            .setAppEngineRouting(AppEngineRouting.newBuilder()
                                    .setVersion(Config.APP_VERSION)
                                    .build())
                            .setHttpMethod(HttpMethod.POST);

            if (task.getRequestBody() == null) {
                String relativeUrl = "http://place.holder"; // the value is not important
                AppUrl url = new AppUrl(relativeUrl + task.getWorkerUrl());
                task.getParamMap().forEach((key, value) -> url.withParam(key, value));

                requestBuilder.setRelativeUri(url.toString());
            } else {
                String requestBody = JsonUtils.toCompactJson(task.getRequestBody());
                requestBuilder.putHeaders("Content-Type", "application/json; charset=UTF-8")
                        .setRelativeUri(task.getWorkerUrl())
                        .setBody(ByteString.copyFrom(requestBody, Const.ENCODING));
            }

            Task.Builder taskBuilder = Task.newBuilder().setAppEngineHttpRequest(requestBuilder.build());
            if (countdownTime > 0) {
                taskBuilder.setScheduleTime(
                        Timestamp.newBuilder()
                                .setSeconds(Instant.now().plusMillis(countdownTime).getEpochSecond()));
            }

            client.createTask(queuePath, taskBuilder.build());
        } catch (IOException e) {
            log.severe("Cannot create Cloud Tasks client", e);
        }
    }

}
