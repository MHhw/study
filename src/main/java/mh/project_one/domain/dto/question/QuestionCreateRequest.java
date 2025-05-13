package mh.project_one.domain.dto.question;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionCreateRequest {
    // @NotEmpty(message = "제목은 필수 항목입니다.")
    private String title;

    // @NotEmpty(message = "내용은 필수 항목입니다.")
    private String content;

    private String tags; // 콤마로 구분된 태그 문자열 (선택 사항)
}