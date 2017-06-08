package ca.jfbconception.slack.api;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class SlackAttachment {

    private String title;
    private String titleLink;
    private String fallback;
    private String text;
    private String pretext;
    private String thumb_url;
    private String author_name;
    private String author_link;
    private String author_icon;
    private String footer;
    private String footer_icon;
    private String image_url;
    private String color;
    private Long ts;

    @Singular
    private Map<String, String> miscRootFields;

    @Singular
    private List<SlackField> fields;
    @JsonProperty("markdown_in")
    private List<String> markdownIn;

}
