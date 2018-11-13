package kr.openmind.restapi.product;

import kr.openmind.restapi.vendor.VendorRoleType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {

    private Integer id;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private ZonedDateTime beginSaleDateTime;

    @NotNull
    private ZonedDateTime closeSaleDateTime;

    @Min(0)
    private Long salePrice;

    @Min(0)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VendorRoleType vendorRoleType = VendorRoleType.THIRD_PARTY;

    @Min(0)
    @Builder.Default
    private Long shippingFee = 0L;
}
