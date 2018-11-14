package kr.openmind.restapi.product;

import kr.openmind.restapi.testsupport.StableProduct;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void saveAndFind() {
        Product product = StableProduct.builder().build();

        Product savedProduct = productRepository.save(product);

        Product actual = productRepository.findById(savedProduct.getId()).get();

        assertThat(actual.getId()).isEqualTo(savedProduct.getId());
        assertThat(actual.getName()).isEqualTo(product.getName());
        assertThat(actual.getBeginSaleDateTime()).isEqualTo(product.getBeginSaleDateTime());
        assertThat(actual.getCloseSaleDateTime()).isEqualTo(product.getCloseSaleDateTime());
    }
}
