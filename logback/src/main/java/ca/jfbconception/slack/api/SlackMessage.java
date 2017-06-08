package ca.jfbconception.slack.api;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Builder
@Data
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class SlackMessage {
    private final String channel;
    private final String message;
    @Singular
    private final List<SlackAttachment> attachments;
    private final String ts;

}
