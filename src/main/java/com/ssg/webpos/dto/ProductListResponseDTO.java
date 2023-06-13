package com.ssg.webpos.dto;

import com.ssg.webpos.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponseDTO {
    private Long product_id;
    private String product_code;
    private String name;
    private int price;
    private String image_url;
    private String description;
    private int qty;
    private boolean isEvent;
    private int origin_price;

    public ProductListResponseDTO(Product product, boolean isEvent) {
        this.product_id = product.getId();
        this.product_code = product.getProductCode();
        this.name = product.getName();
        this.price = product.getSalePrice();
        this.image_url = product.getImageUrl();
        this.description = product.getDescription();
        this.qty = product.getStock();
        this.origin_price = product.getOriginPrice();
        this.isEvent = isEvent;
    }
}
