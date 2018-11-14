package kr.openmind.restapi.product;

import kr.openmind.restapi.testsupport.StableProduct;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTest {

    @Test
    public void constructor() {
        String name = "openmind";
        Product product = new Product();
        product.setName(name);

        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getShippingFee()).isEqualTo(0L);
    }

    @Test
    public void getSaleStatusType() {
        assertThat(StableProduct.builder().build().getSaleStatus())
            .isEqualTo(ProductSaleStatusType.ACTIVE);

        assertThat(StableProduct.builder().quantity(0).build().getSaleStatus())
            .isEqualTo(ProductSaleStatusType.SOLD_OUT);
        assertThat(StableProduct.builder().quantity(1).build().getSaleStatus())
            .isNotEqualTo(ProductSaleStatusType.SOLD_OUT);

        assertThat(StableProduct.builder().beginSaleDateTime(ZonedDateTime.now().plusDays(1)).build().getSaleStatus())
            .isEqualTo(ProductSaleStatusType.READY);
        assertThat(StableProduct.builder().closeSaleDateTime(ZonedDateTime.now().minusDays(1)).build().getSaleStatus())
            .isEqualTo(ProductSaleStatusType.STOP);
    }
}
