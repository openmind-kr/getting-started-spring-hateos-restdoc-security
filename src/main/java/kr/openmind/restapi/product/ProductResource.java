package kr.openmind.restapi.product;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class ProductResource extends Resource<Product> {

    public ProductResource(Product content, Link... links) {
        super(content, links);
        add(linkTo(ProductController.class).slash(content.getId()).withSelfRel());
    }
}
