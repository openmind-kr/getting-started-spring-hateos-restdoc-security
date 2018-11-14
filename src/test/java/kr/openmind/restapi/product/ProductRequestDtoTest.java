package kr.openmind.restapi.product;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductRequestDtoTest {

    @Test
    public void builder() {
        String name = "bigdeal";
        ProductRequestDto productRequestDto = ProductRequestDto.builder()
            .name(name)
            .build();

        assertThat(productRequestDto.getName()).isEqualTo(name);
        assertThat(productRequestDto.getShippingFee()).isEqualTo(0L);
    }

    @Test
    public void constructor() {
        String name = "openmind";
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setName(name);

        assertThat(productRequestDto.getName()).isEqualTo(name);
        assertThat(productRequestDto.getShippingFee()).isEqualTo(0L);
    }
}