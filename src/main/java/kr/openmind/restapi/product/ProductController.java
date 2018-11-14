package kr.openmind.restapi.product;

import org.modelmapper.ModelMapper;
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
@RequestMapping("/api/product")
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
            return ResponseEntity.badRequest().body(errors);
        }

        Product product = modelMapper.map(productRequestDto, Product.class);

        Product savedProduct = productRepository.save(product);
        URI location = linkTo(ProductController.class).slash(savedProduct.getId()).toUri();
        return ResponseEntity.created(location).body(savedProduct);
    }
}
