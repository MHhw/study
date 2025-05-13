package mh.project_one.domain.repository.product;

import mh.project_one.domain.entity.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

}
