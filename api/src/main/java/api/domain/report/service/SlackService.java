package api.domain.report.service;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import db.domain.report.ReportDocument;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlackService {

    @Value("${webhook.slack.url}")
    private String SLACK_WEBHOOK_URL;

    private final Slack slackClient = Slack.getInstance();

    @Async
    public void sendReportNotificationAsync(ReportDocument reportDocument) {

        try {
            String message = "*:rotating_light: 문의가 접수되었습니다!* \n" +
                "> *내용* : " + reportDocument.getContent() + "\n" +
                "> *기기* : " + reportDocument.getDeviceName() + "\n" +
                "> *버전* : " + reportDocument.getVersion() + "\n" +
                "> *등록 날짜* : " + reportDocument.getRegisteredAt();

            Payload payload = Payload.builder()
                .text(message)
                .build();

            slackClient.send(SLACK_WEBHOOK_URL, payload);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
