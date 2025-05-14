package mh.project_one.domain.dto.product;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductUpdateRequest {

    private String productName;

    private String productDescription;

    private BigDecimal productPrice;

    private Integer stockQuantity;

    private Boolean isAvailable;

    private LocalDate releaseDate;

    private String categoryCode;

    private String manufacturerInfo;

}
