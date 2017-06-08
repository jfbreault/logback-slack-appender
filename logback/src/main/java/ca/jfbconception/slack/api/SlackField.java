package ca.jfbconception.slack.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SlackField {
    private String  title;
    private String  value;
    private boolean isShort;

}
