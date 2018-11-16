package kr.openmind.restapi.product;

import kr.openmind.restapi.common.ErrorResource;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/product", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class ProductController {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    public ProductController(ModelMapper modelMapper, ProductRepository productRepository) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid ProductRequestDto productRequestDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorResource(errors));
        }

        Product product = modelMapper.map(productRequestDto, Product.class);
        Product savedProduct = productRepository.save(product);

        URI location = linkTo(ProductController.class).slash(savedProduct.getId()).toUri();
        ProductResource productResource = createProductCreateResource(savedProduct);
        return ResponseEntity.created(location).body(productResource);
    }

    private ProductResource createProductCreateResource(Product product) {
        ProductResource productResource = new ProductResource(product);
        productResource.add(linkTo(ProductResource.class).withRel("product"));
        productResource.add(linkTo(ProductResource.class).slash(product.getId()).withRel("update"));
        productResource.add(new Link("/docs/index.html#resources-product-post", "profile"));
        return productResource;
    }
}
