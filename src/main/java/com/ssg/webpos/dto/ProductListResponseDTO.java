package com.ssg.webpos.dto;

import com.ssg.webpos.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    public ProductListResponseDTO(Product product) {
        this.product_id = product.getId();
        this.product_code = product.getProductCode();
        this.name = product.getName();
        this.price = product.getSalePrice();
        this.image_url = product.getImageUrl();
        this.description = product.getDescription();
        this.qty = product.getStock();
        this.isEvent = (product.getEvent() == null) ? false : true;
    }
}
