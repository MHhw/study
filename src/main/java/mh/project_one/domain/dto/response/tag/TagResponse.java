package mh.project_one.domain.dto.response.tag;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponse {

    private Long id; // 태그 ID (선택 사항, 이름만으로도 충분할 수 있음)
    private String name; // 태그 이름
}