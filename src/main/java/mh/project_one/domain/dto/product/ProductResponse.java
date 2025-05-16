package mh.project_one.domain.dto.product;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import mh.project_one.domain.entity.product.ProductEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class ProductResponse {
    private final Long productId;

    private final String productName;

    private final String productDescription;

    private final BigDecimal productPrice;

    private final Integer stockQuantity;

    private final Boolean isAvailable;

    private final LocalDate releaseDate;

    private final String categoryCode;

    private final Double ratingAverage;

    private final String manufacturerInfo;

    // 생성일시 (BaseTimeEntity에서 상속받은 필드)
    private final LocalDateTime createdAt;

    // 수정일시 (BaseTimeEntity에서 상속받은 필드)
    private final LocalDateTime updatedAt;

    /**
     * ProductEntity 객체로부터 ProductResponse DTO 객체를 생성하는 정적 팩토리 메서드입니다.
     * 이 메서드는 엔티티의 정보를 DTO로 옮겨 담는 역할을 합니다.
     * @param entity ProductEntity 원본 객체
     * @return ProductResponse 변환된 DTO 객체
     */
    public static ProductResponse fromEntity(ProductEntity entity) {
        // entity가 null인 경우를 대비한 방어 코드 (선택 사항)
        if (entity == null) {
            return null;
        }

        // 빌더 패턴을 사용하여 ProductResponse 객체 생성
        return ProductResponse.builder()
                .productId(entity.getProductId()) // 상품 ID 매핑
                .productName(entity.getProductName()) // 상품명 매핑
                .productDescription(entity.getProductDescription()) // 상품 설명 매핑
                .productPrice(entity.getProductPrice()) // 상품 가격 매핑
                .stockQuantity(entity.getStockQuantity()) // 재고 수량 매핑
                .isAvailable(entity.getIsAvailable()) // 판매 가능 여부 매핑
                .releaseDate(entity.getReleaseDate()) // 출시일 매핑
                .categoryCode(entity.getCategoryCode()) // 카테고리 코드 매핑
                .ratingAverage(entity.getRatingAverage()) // 평균 평점 매핑
                .manufacturerInfo(entity.getManufacturerInfo()) // 제조사 정보 매핑
                .createdAt(entity.getCreatedAt()) // 생성일시 매핑 (BaseTimeEntity 가정)
                .updatedAt(entity.getUpdatedAt()) // 수정일시 매핑 (BaseTimeEntity 가정)
                .build(); // 최종적으로 객체 생성 완료
    }

}
