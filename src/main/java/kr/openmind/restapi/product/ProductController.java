package kr.openmind.restapi.product;

import kr.openmind.restapi.account.Account;
import kr.openmind.restapi.account.CurrentAccount;
import kr.openmind.restapi.common.ErrorResource;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping(value = "/api/product", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class ProductController {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    public ProductController(ModelMapper modelMapper, ProductRepository productRepository) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
    }

    private static final ProductController CONTROLLER_LINK = methodOn(ProductController.class);

    @GetMapping
    public ResponseEntity list(Pageable pageable, PagedResourcesAssembler<Product> assembler, @CurrentAccount Account account) {
        if (account == null) {
            log.debug("allowed account doesn't exist");
        } else {
            log.debug("allowed account. id: {}, email: {}, role: {}", account.getId(), account.getEmail(), account.getRoles());
        }

        Page<Product> products = productRepository.findAll(pageable);

        PagedResources<ProductResource> productResources = assembler.toResource(products, entity -> new ProductResource(entity));
        productResources.add(linkTo(CONTROLLER_LINK.get(null, account)).withRel("product"));
        productResources.add(linkTo(CONTROLLER_LINK.create(null, null, account)).withRel("product-create"));
        productResources.add(createProfileLink("resources-product-get-list"));

        return ResponseEntity.ok(productResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable Integer id, @CurrentAccount Account account) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Product product = optionalProduct.get();

        ProductResource productResource = new ProductResource(product, createProfileLink("resources-product-get"));
        productResource.add(linkTo(CONTROLLER_LINK.list(null, null, account)).withRel("products"));
        productResource.add(linkTo(CONTROLLER_LINK.update(product.getId(), null, null, account)).withRel("update"));

        return ResponseEntity.ok(productResource);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid ProductRequestDto productRequestDto, Errors errors,
                                 @CurrentAccount Account account) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorResource(errors));
        }

        Product product = modelMapper.map(productRequestDto, Product.class);
        Product savedProduct = productRepository.save(product);

        URI location = linkTo(ProductController.class).slash(savedProduct.getId()).toUri();
        ProductResource productResource = new ProductResource(savedProduct, createProfileLink("resources-product-post"));
        productResource.add(linkTo(CONTROLLER_LINK.get(savedProduct.getId(), account)).withRel("product"));
        productResource.add(linkTo(CONTROLLER_LINK.update(savedProduct.getId(), null, null, account)).withRel("update"));

        return ResponseEntity.created(location).body(productResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id,
                                 @RequestBody @Valid ProductRequestDto productRequestDto, Errors errors,
                                 @CurrentAccount Account account) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorResource(errors));
        }

        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Product product = optionalProduct.get();
        modelMapper.map(productRequestDto, product);

        ProductResource productResource = new ProductResource(product, createProfileLink("resources-product-put"));
        productResource.add(linkTo(CONTROLLER_LINK.list(null, null, account)).withRel("products"));
        productResource.add(linkTo(CONTROLLER_LINK.get(product.getId(), account)).withRel("product"));

        return ResponseEntity.ok(productResource);
    }

    private Link createProfileLink(String anchor) {
        return new Link("/docs/index.html#".concat(anchor), "profile");
    }
}
