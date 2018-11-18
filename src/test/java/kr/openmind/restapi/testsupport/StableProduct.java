package kr.openmind.restapi.testsupport;

import kr.openmind.restapi.product.Product;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

public class StableProduct {

    public static StableProductBuilder builder() {
        return new StableProductBuilder();
    }

    public static class StableProductBuilder {

        private String name;
        private Integer quantity;
        private ZonedDateTime beginSaleDateTime;
        private ZonedDateTime closeSaleDateTime;

        public StableProductBuilder name(String name) {
            this.name = name;
            return this;
        }

        public StableProductBuilder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public StableProductBuilder beginSaleDateTime(ZonedDateTime beginSaleDateTime) {
            this.beginSaleDateTime = beginSaleDateTime;
            return this;
        }

        public StableProductBuilder closeSaleDateTime(ZonedDateTime closeSaleDateTime) {
            this.closeSaleDateTime = closeSaleDateTime;
            return this;
        }

        public Product build() {
            ZonedDateTime minimumDateTime = ZonedDateTime.of(LocalDateTime.of(2001, 1, 1, 0, 0), ZoneId.systemDefault());
            ZonedDateTime maximumDateTime = ZonedDateTime.of(LocalDateTime.of(9999, 12, 31, 23, 59, 59), ZoneId.systemDefault());

            Product product = new Product();
            product.setName(Optional.ofNullable(name).orElse("anyName"));
            product.setDescription("anyDescription");
            product.setBeginSaleDateTime(Optional.ofNullable(beginSaleDateTime).orElse(minimumDateTime));
            product.setCloseSaleDateTime(Optional.ofNullable(closeSaleDateTime).orElse(maximumDateTime));
            product.setSalePrice(2_000_000L);
            product.setQuantity(Optional.ofNullable(quantity).orElse(30_000));

            return product;
        }
    }
}
