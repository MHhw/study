package mh.project_one.domain.repository.product;

import mh.project_one.domain.entity.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository // 해당 인터페이스가 데이터 접근 계층(repository)의 컴포넌트임을 명시
public interface ProductRepository extends JpaRepository<ProductEntity, Long> { // JpaRepository<A, B> ... A: 엔티티 클래스, B: 엔티티클래스의 기본키 타입

    // JpaRepository 에서 기본적인 메서드들을 제공
    // - save(Product entity): 저장 및 수정
    // - findById(Long id): ID로 조회 (Optional<Product> 반환)
    // - findAll(): 모든 데이터 조회 (List<Product> 반환)
    // - deleteById(Long id): ID로 삭제
    // - count(): 엔티티 총 개수 반환
    // - existsById(Long id): ID로 존재 여부 확인
    // 등등...
    
    // 나머지 작성 후 JUNIT TEST하고나서 다른 테이블과 조인하는 메서드 아래에 작성
    /*
        - findBy... : 조회 메서드임을 알림
        - ...And... , ...Or... : 여러 조건 조합 시 사용
        - ...Containing : 문자열 포함 여부 (= LIKE '%keyword%')
        - ...StartingWith : 문자열 시작 여부 (= LIKE '%keyword')
        - ...EndingWith : 문자열 끝 여부 (= LIKE 'keyword%')
        - ...IgnoreCase : 대소문자 구분없이 비교
        - ...IsNull, ...IsNotNull : null 또는 not null 조건
        - ...True, ...False : boolean 필드가 true 또는 false인 조건
        - ...GreaterThan, ...LessThan, ...Between : 숫자나 날짜 비교
        - ...OrderBy...Asc, ...OrderBy...Desc : 정렬조건
     */

    // 상품명으로 상품 목록 조회
    List<ProductEntity> findByProductName(String productName);

    // 상품명에 특정 키워드가 포함된 상품 목록 조회
    List<ProductEntity> findByProductNameContaining(String keyword);

    // 특정 카테고리 코드 해당하는 상품 찾기
    List<ProductEntity> findByCategoryCode(String categoryCode);

    // 판매 가능하고 특정 카테고리에 속하는 상품 목록 조회
    List<ProductEntity> findByIsAvailableTrueAndCategoryCode(String categoryCode);

    // 특정 가격보다 비싼 상품 목록 조회
    List<ProductEntity> findByProductPriceGreaterThan(BigDecimal price);
        
}
