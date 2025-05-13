package mh.project_one;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// JPA Auditing 옵션 활성화
// 데이터베이스의 데이터가 언제, 누구에 의해 생성되고 수정되었는지와 같은 변경 이력을 자동으로 추적하고 기록하는 기능
@EnableJpaAuditing
@SpringBootApplication
public class ProjectOneApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectOneApplication.class, args);
	}

}
