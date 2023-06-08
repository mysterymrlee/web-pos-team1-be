package com.ssg.webpos.repository.product;

import com.ssg.webpos.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE :currentDate BETWEEN p.salesStartDate AND p.salesEndDate")
    List<Product> findProductsBySalesDate(@Param("currentDate") LocalDateTime currentDate);

    List<Product> findByCategory(String category);
    @Query("select p from Product p join fetch p.event as e" +
            " where e.eventStatus = 1 and p.category =:category")
    List<Product> findByCategoryWithEvent(
            @Param(value = "category") String category
    );

    Product findByProductCode(String productCode);

    // storeId = 0 정렬없이 조회
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id", nativeQuery = true)
    List<Product> findProduct();

    // storeId에 따라서 정렬없이 조회
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id where p.store_id = :storeId", nativeQuery = true)
    List<Product> findProductByStoreId(@Param(value = "storeId") int storeId);

    // 정렬없이 조회, saleState param, storeId param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id where p.sale_state = :saleState and p.store_id = :storeId", nativeQuery = true)
    List<Product> findProductBySaleStateAndStoreId(@Param(value = "saleState") int saleState, @Param(value = "storeId") int storeId);


    // storeId = 0 정렬없이 조회, saleState param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id where p.sale_state = :saleState", nativeQuery = true)
    List<Product> findProductBySalePrice(@Param(value = "saleState") int saleState);

    // sale_price ASC
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id  order by p.sale_price ASC", nativeQuery = true)
    List<Product> findProductsOrderBySalePriceAsc();

    // sale_price ASC, storeId param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id  where p.store_id = :storeId order by p.sale_price ASC", nativeQuery = true)
    List<Product> findProductsOrderBySalePriceAscByStoreId(@Param(value = "storeId") int storeId);

    // sale_price ASC, saleState param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id  where p.sale_state = :saleState order by p.sale_price ASC", nativeQuery = true)
    List<Product> findProductsOrderBySalePriceAscBySaleState(@Param(value = "saleState") int saleState);

    // sale_price ASC, saleState param, storeId param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id  where p.sale_state = :saleState and p.store_id = :storeId order by p.sale_price ASC", nativeQuery = true)
    List<Product> findProductsOrderBySalePriceAscBySaleStateAndStoreId(@Param(value = "saleState") int saleState, @Param(value = "storeId") int storeId);


    // sale_price DESC
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id  order by p.sale_price DESC", nativeQuery = true)
    List<Product> findProductsOrderBySalePriceDesc();

    // sale_price DESC, saleState param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id  where p.sale_state = :saleState order by p.sale_price DESC", nativeQuery = true)
    List<Product> findProductsOrderBySalePriceDescBySaleState(@Param(value = "saleState") int saleState);

    // sale_price DESC, storeId param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id  where p.store_id = :storeId order by p.sale_price DESC", nativeQuery = true)
    List<Product> findProductsOrderBySalePriceDescByStoreId(@Param(value = "storeId") int storeId);

    // sale_price DESC, saleState param, storeId param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id  where p.sale_state = :saleState and p.store_id = :storeId order by p.sale_price DESC", nativeQuery = true)
    List<Product> findProductsOrderBySalePriceDescBySaleStateAndStoreId(@Param(value = "saleState") int saleState, @Param(value = "storeId") int storeId);

    // origin_price ASC
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id  order by p.origin_price ASC", nativeQuery = true)
    List<Product> findProductsOrderByOriginPriceAsc();

    // origin_price ASC, saleState param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id  where p.sale_state = :saleState order by p.origin_price ASC", nativeQuery = true)
    List<Product> findProductsOrderByOriginPriceAscBySaleState(@Param(value = "saleState") int saleState);

    // origin_price ASC, storeId param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id  where p.store_id = :storeId order by p.origin_price ASC", nativeQuery = true)
    List<Product> findProductsOrderByOriginPriceAscByStoreId(@Param(value = "storeId") int storeId);

    // origin_price ASC, saleState param, storeId param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id  where p.sale_state = :saleState and p.store_id = :storeId order by p.origin_price ASC", nativeQuery = true)
    List<Product> findProductsOrderByOriginPriceAscBySaleStateAndStoreId(@Param(value = "saleState") int saleState, @Param(value = "storeId") int storeId);

    // origin_price DESC
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id  order by p.origin_price DESC", nativeQuery = true)
    List<Product> findProductsOrderByOriginPriceDesc();

    // origin_price DESC, saleState param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id where p.sale_state = :saleState order by p.origin_price DESC", nativeQuery = true)
    List<Product> findProductsOrderByOriginPriceDescBySaleState(@Param(value = "saleState") int saleState);

    // origin_price DESC, storeId param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id where p.store_id = :storeId order by p.origin_price DESC", nativeQuery = true)
    List<Product> findProductsOrderByOriginPriceDescByStoreId(@Param(value = "storeId") int storeId);

    // origin_price DESC, saleState param, storeId param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id where p.sale_state = :saleState and p.store_id = :storeId order by p.origin_price DESC", nativeQuery = true)
    List<Product> findProductsOrderByOriginPriceDescBySaleStateAndStoreId(@Param(value = "saleState") int saleState, @Param(value = "storeId") int storeId);

    // stock_price ASC
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id  order by p.stock ASC", nativeQuery = true)
    List<Product> findProductsOrderByStockAsc();

    // stock_price ASC, saleState param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id where p.sale_state = :saleState order by p.stock ASC", nativeQuery = true)
    List<Product> findProductsOrderByStockAscBySaleState(@Param(value = "saleState") int saleState);

    // stock_price ASC, storeId param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id where p.store_id = :storeId order by p.stock ASC", nativeQuery = true)
    List<Product> findProductsOrderByStockAscByStoreId(@Param(value = "storeId") int storeId);

    // stock_price ASC, saleState param, storeId param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id where p.sale_state = :saleState and p.store_id = :storeId order by p.stock ASC", nativeQuery = true)
    List<Product> findProductsOrderByStockAscBySaleStateAndStoreId(@Param(value = "saleState") int saleState, @Param(value = "storeId") int storeId);

    // stock_price DESC
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id  order by p.stock DESC", nativeQuery = true)
    List<Product> findProductsOrderByStockDesc();

    // stock_price DESC, saleState param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id where p.sale_state = :saleState order by p.stock DESC", nativeQuery = true)
    List<Product> findProductsOrderByStockDescBySaleState(@Param(value = "saleState") int saleState);

    // stock_price DESC, saleState param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id where p.store_id = :storeId order by p.stock DESC", nativeQuery = true)
    List<Product> findProductsOrderByStockDescByStoreId(@Param(value = "storeId") int storeId);

    // stock_price DESC, saleState param, storeId param
    @Query(value = "SELECT p.*\n" +
            "FROM stock_report sr\n" +
            "JOIN product p ON sr.product_id = p.product_id where p.sale_state = :saleState and p.store_id = :storeId order by p.stock DESC", nativeQuery = true)
    List<Product> findProductsOrderByStockDescBySaleStateAndStoreId(@Param(value = "saleState") int saleState, @Param(value = "storeId") int storeId);
}
