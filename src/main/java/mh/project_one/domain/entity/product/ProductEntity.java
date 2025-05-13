package mh.project_one.domain.entity.product;

import jakarta.persistence.*;
import lombok.*;
import mh.project_one.domain.entity.common.BaseTimeEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter // getter 메서드 자동 생성
@Setter // setter 메서드 자동 생성
@NoArgsConstructor // protected 기본 생성자 자동 생성
@Builder // 빌더 패턴 사용 가능
@ToString // toString 메서드 자동 생성
@Entity // 이 클래스가 entity라고 선언 -> JPA가 관리하는 객체가 됨
@Table(name="product") // 엔티티를 데이터베이스 product 테이블에 매핑
public class ProductEntity extends BaseTimeEntity {

    @Id // primary key
    @Column(name = "product_id", columnDefinition = "BIGINT") // 매핑할 테이블 컬럼 명, columnDefinition 데이터 타입은 BIGINT(명확하면 생략 가능)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // JPA에게 해당 필드는 데이터베이스가 알아서 생성할 것이니 데이터베이스에 INSERT 쿼리를 보낸 후 생성된 ID 값을 가져와서 필드에 채워 넣도록 하라는 어노테이션
    private Long productId; // Long(java) = BIGINT(mysql) = BIGSERIAL(postgresql)

    @Column(name="product_name", nullable = false, length = 255, columnDefinition = "VARCHAR(255)") // nullable 기본값은 true, length 길이제약
    private String productName;

    @Column(name="product_description", nullable = true, columnDefinition = "TEXT") // nullable true는 생략가능, 길이 대신 text 타입 지정
    private String productDescription;

    @Column(name = "product_price", nullable = false, precision = 10, scale = 2, columnDefinition = "NUMERIC(10, 2)") // NUMERIC(precision 전체자릿수, scale 소수점이하자릿수)
    private BigDecimal productPrice; // 소수점 연산 자료형

    @Column(name = "stock_quantity", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer stockQuantity = 0; // DEFAULT 0 초기값 설정

    @Column(name = "is_available", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isAvailable = true; // DEFAULT true 초기값 설정

    @Column(name = "release_date", columnDefinition = "DATE")
    private LocalDate releaseDate; // LocalDate(java) = DATE(postgresql)

    @Column(name = "category_code", length = 10, columnDefinition = "VARCHAR(10)")
    private String categoryCode;

    @Column(name = "rating_average", columnDefinition = "DOUBLE DECISION DEFAULT 0.0")
    private Double ratingAverage = 0.0; // Double(java) = DOUBLE PRECISION(postgresql)

    @Column(name = "manufacturer_info", columnDefinition = "JSONB")
    private String manufacturerInfo;

}
