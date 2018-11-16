package kr.openmind.restapi.index;

import kr.openmind.restapi.product.ProductController;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
public class IndexController {

    @GetMapping(value = "/api", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
    public ResourceSupport index() {
        ResourceSupport resource = new ResourceSupport();
        resource.add(linkTo(ProductController.class).withRel("product"));
        return resource;
    }
}
