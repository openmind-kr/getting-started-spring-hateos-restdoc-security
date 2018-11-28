package kr.openmind.restapi.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import kr.openmind.restapi.account.Account;
import kr.openmind.restapi.account.AccountSerialize;
import kr.openmind.restapi.vendor.VendorRoleType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
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
    private VendorRoleType vendorRoleType = VendorRoleType.THIRD_PARTY;

    @Min(0)
    private Long shippingFee = 0L;

    @ManyToOne
    @JsonSerialize(using = AccountSerialize.class)
    private Account account;

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
