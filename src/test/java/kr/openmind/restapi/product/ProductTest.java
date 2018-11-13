package kr.openmind.restapi.product;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTest {

    @Test
    public void builder() {
        String name = "bigdeal";
        Product product = Product.builder()
            .name(name)
            .build();

        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getShippingFee()).isEqualTo(0L);
    }

    @Test
    public void constructor() {
        String name = "openmind";
        Product product = new Product();
        product.setName(name);

        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getShippingFee()).isEqualTo(0L);
    }
}
