package api.domain.topic.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicStatusResponse {

    private boolean generalNewsTopic;

    private boolean scholarshipNewsTopic;

    private boolean eventNewsTopic;

    private boolean academicNewsTopic;

    private boolean employmentNewsTopic;

}
