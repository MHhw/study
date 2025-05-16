package mh.project_one.domain.service.product;

import lombok.RequiredArgsConstructor;
import mh.project_one.domain.dto.product.ProductResponse;
import mh.project_one.domain.entity.product.ProductEntity;
import mh.project_one.domain.repository.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자 자동생성(생성자 주입)
public class ProductService {

    private final ProductRepository productRepository;

    // 특정 id에 해당하는 상품 정보
    @Transactional(readOnly = true)
    public ProductResponse getProductId(Long productId){
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(()->new IllegalArgumentException(productId + "상품을 찾을 수 없습니다."));

        return ProductResponse.fromEntity(productEntity);
    }

    // 모든 상품 목록 조회 (페이징 미적용)
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(){
        List<ProductEntity> productEntities = productRepository.findAll();

        return productEntities.stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 모든 상품 목록 조회 (페이징 적용)
    @Transactional(readOnly = true)
    public 

}
