package kr.openmind.restapi.product;

import kr.openmind.restapi.vendor.VendorRoleType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {

    @Id
    @GeneratedValue
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

    public ProductSaleStatusType getSaleStatus() {
        if (quantity == 0) {
            return ProductSaleStatusType.SOLD_OUT;
        }
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        if (now.isBefore(beginSaleDateTime)) {
            return ProductSaleStatusType.READY;
        }
        if (now.isAfter(closeSaleDateTime)) {
            return ProductSaleStatusType.STOP;
        }
        return ProductSaleStatusType.ACTIVE;
    }
}
