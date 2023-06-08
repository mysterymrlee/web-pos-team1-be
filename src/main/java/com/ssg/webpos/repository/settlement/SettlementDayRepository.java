package com.ssg.webpos.repository.settlement;

import com.ssg.webpos.domain.SettlementDay;
import jdk.jfr.Unsigned;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SettlementDayRepository extends JpaRepository<SettlementDay, Long> {
    //store_id별 조회(HQ 활용)
    // @Query("SELECT s FROM SettlementDay s where s.storeId = :storeId")
    List<SettlementDay> findByStoreId(Long storeId);
    //일별 조회(HQ 활용)
    List<SettlementDay> findBySettlementDate(LocalDate settlementDate);
    // store_id, 일별 조회(점장 활용)
    List<SettlementDay> findByStoreIdAndSettlementDate(Long storeId, LocalDate settlementDate);
    // store_id, 기간별 조회
    List<SettlementDay> findByStoreIdAndSettlementDateBetween(Long storeId, LocalDate startDate, LocalDate endDate);
    // 전체 기간별 조회
    List<SettlementDay> findBySettlementDateBetween(LocalDate startDate, LocalDate endDate);
    // 어제
    // 어제의 모든 백화점 일일정산의 매출 합
    @Query(value = "select SUM(sd.settlement_price) from settlement_day sd where settlement_date  = DATE_SUB(CURDATE(), INTERVAL 1 DAY)", nativeQuery = true)
    int sumOfAllSettlementPrice();
    // 어제의 모든 백화점 일일정산 수수료 합
    @Query(value = "select SUM(sd.charge) from settlement_day sd where settlement_date  = DATE_SUB(CURDATE(), INTERVAL 1 DAY)", nativeQuery = true)
    int sumOfAllCharge();
    // 어제의 모든 백화점 일일정산 이익 합
    @Query(value = "select SUM(sd.profit) from settlement_day sd where settlement_date  = DATE_SUB(CURDATE(), INTERVAL 1 DAY)", nativeQuery = true)
    int sumOfAllProfit();
    // 어제의 store_id별 일일정산 매출
    @Query(value = "select sd.settlement_price from settlement_day sd where settlement_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY) and store_id = :storeId", nativeQuery = true)
    int settlementDaySettlementPrice(@Param("storeId") int storeId);
    // 어제의 store_id별 일일정산 수수료
    @Query(value = "select sd.charge from settlement_day sd where settlement_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY) and store_id = :storeId", nativeQuery = true)
    int settlementDayCharge(@Param("storeId") int storeId);
    // 어제의 store_id별 일일정산 이익
    @Query(value = "select sd.profit from settlement_day sd where settlement_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY) and store_id = :storeId", nativeQuery = true)
    int settlementDayProfit(@Param("storeId") int storeId);

    // 이번주

    // 이번주의 모든 백화점 일일정산 매출 합
    @Query(value = "select SUM(sd.settlement_price) from settlement_day sd WHERE WEEK(settlement_date, 1) = WEEK(CURDATE())", nativeQuery = true)
    int sumOfThisWeekAllSettlementPrice();
    // 이번주의 모든 백화점 일일정산 수수료 합
    @Query(value = "select SUM(sd.charge) from settlement_day sd WHERE WEEK(settlement_date, 1) = WEEK(CURDATE())", nativeQuery = true)
    int sumOfThisWeekAllSettlemetCharge();
    // 이번주의 모든 백화점 일일정산 이익 합
    @Query(value = "select SUM(sd.charge) from settlement_day sd WHERE WEEK(settlement_date, 1) = WEEK(CURDATE())", nativeQuery = true)
    int sumOfThisWeekAllSettlemetProfit();

    // 이번주의 store_id별 일일정산 매출 합
    @Query(value = "select SUM(sd.settlement_price) from settlement_day sd WHERE WEEK(settlement_date, 1) = WEEK(CURDATE()) AND store_id = :storeId", nativeQuery = true)
    int sumOfThisWeekAllSettlementPriceByStoreId(@Param("storeId") int storeId);
    // 이번주의 store_id별 일일정산 수수료 합
    @Query(value = "select SUM(sd.charge) from settlement_day sd WHERE WEEK(settlement_date, 1) = WEEK(CURDATE()) AND store_id = :storeId", nativeQuery = true)
    int sumOfThisWeekAllSettlemetChargeByStoreId(@Param("storeId") int storeId);
    // 이번주의 store_id별 일일정산 이익 합
    @Query(value = "select SUM(sd.charge) from settlement_day sd WHERE WEEK(settlement_date, 1) = WEEK(CURDATE()) AND store_id = :storeId", nativeQuery = true)
    int sumOfThisWeekAllSettlemetProfitByStoreId(@Param("storeId") int storeId);

    //이번달, 1일부터 어제까지의 일별정산내역 합

    // 이번달 모든 백화점 매출합
    @Query(value = "SELECT SUM(sd.settlement_price) FROM settlement_day sd  " +
            " WHERE sd.settlement_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01') " +
            " AND sd.settlement_date < CURDATE() ",  nativeQuery = true)
    int sumOfThisMonthSettlementPrice();
    // 이번달 모든 백화점 수수료합
    @Query(value = "SELECT SUM(sd.charge) FROM settlement_day sd  " +
            " WHERE sd.settlement_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01') " +
            " AND sd.settlement_date < CURDATE() ",  nativeQuery = true)
    int sumOfThisMonthCharge();
    // 이번달 모든 백화점 영업이익합
    @Query(value = "SELECT SUM(sd.profit) FROM settlement_day sd  " +
            " WHERE sd.settlement_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01') " +
            " AND sd.settlement_date < CURDATE() ",  nativeQuery = true)
    int sumOfThisMonthProfit();
    // 이번달 store_id별 백화점 매출합
    @Query(value = "SELECT SUM(sd.settlement_price) FROM settlement_day sd  " +
            " WHERE store_id = :storeId ANd sd.settlement_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01') " +
            " AND sd.settlement_date < CURDATE() ",  nativeQuery = true)
    int sumOfThisMonthSettlementPriceAndStoreId(@Param("storeId") int storeId);
    // 이번달 store_id별 백화점 수수료합
    @Query(value = "SELECT SUM(sd.charge) FROM settlement_day sd  " +
            " WHERE store_id = :storeId ANd sd.settlement_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01') " +
            " AND sd.settlement_date < CURDATE() ",  nativeQuery = true)
    int sumOfThisMonthChargeAndStoreId(@Param("storeId") int storeId);
    // 이번달 store_id별 백화점 영업이익합
    @Query(value = "SELECT SUM(sd.profit) FROM settlement_day sd  " +
            " WHERE store_id = :storeId ANd sd.settlement_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01') " +
            " AND sd.settlement_date < CURDATE() ", nativeQuery = true)
    int sumOfThisMonthProfitAndStoreId(@Param("storeId") int storeId);


    // 테스트용 1 settlement_day, sesttlement_month 활용
    @Query(value = "SELECT COALESCE(SUM(sm.settlement_price), 0) + COALESCE(SUM(sd.settlement_price), 0) AS total_price\n" +
            "FROM settlement_month sm\n" +
            "LEFT JOIN settlement_day sd ON sm.settlement_date = DATE_FORMAT(sd.settlement_date, '%Y-%m-%d')\n" +
            "WHERE sm.settlement_date >= DATE_FORMAT(CURDATE(), '%Y-01-01')\n" +
            "  AND sm.settlement_date <= DATE_SUB(CURDATE(), INTERVAL 1 DAY)", nativeQuery = true)
    int test1();
    // 테스트용 2 sum(orders.final_order_price)
    @Query(value = "select sum(o.final_total_price) from orders o", nativeQuery = true)
    int test2();
    // 테스트용 3 settlement_day와 settlement_month 활용한 매서드를 2개 만들어서 그 뒤에 합하는 과정
    @Query(value = "SELECT SUM(settlement_price)\n" +
            "FROM settlement_month\n" +
            "WHERE settlement_date >= DATE_FORMAT(CURDATE(), '%Y-01-01')\n" +
            "  AND settlement_date < DATE_FORMAT(CURDATE(), '%Y-%m-01')",nativeQuery = true)
    int test3a();

    @Query(value = "SELECT SUM(settlement_price)\n" +
            "FROM settlement_day\n" +
            "WHERE settlement_date >= DATE_FORMAT(CURDATE(), '%Y-%m-01')\n" +
            "  AND settlement_date < DATE_FORMAT(CURDATE(), '%Y-%m-%d')",nativeQuery = true)
    int test3b();

    // 어제의 일주일 전부터 어제까지의 settlement_day 조회(store_id 고려안한 매서드)
    @Query(value = "SELECT *\n" +
            "FROM settlement_day sd \n" +
            "WHERE settlement_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)\n" +
            "AND settlement_date <= CURDATE() - INTERVAL 1 DAY",nativeQuery = true)
    List<SettlementDay> selectSettlementDayBetweenYesterday1WeekAgoAndYesterday();

    // 어제의 일주일 전부터 어제까지의 settlement_day 조회(날짜별로 합침)
    @Query(value = "SELECT DATE(settlement_date) AS date, SUM(settlement_price) AS total_settlement_price\n" +
            "FROM settlement_day\n" +
            "WHERE settlement_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND settlement_date <= DATE_SUB(CURDATE(), INTERVAL 1 DAY)\n" +
            "GROUP BY DATE(settlement_date)",nativeQuery = true)
    List<Object[]> settlementDay1Week();

    // 어제의 일주일 전부터 어제까지의 settlement_day 조회(날짜별로 합침), storeId로 조회
    @Query(value = "SELECT DATE(settlement_date) AS date, SUM(settlement_price) AS total_settlement_price\n" +
            "FROM settlement_day\n" +
            "WHERE store_id = :storeId AND settlement_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND settlement_date <= DATE_SUB(CURDATE(), INTERVAL 1 DAY)\n" +
            "GROUP BY DATE(settlement_date)",nativeQuery = true)
    List<Object[]> settlementDay1WeekByStoreId(@Param("storeId") int storeId);

    // 어제의 한달 전부터 어제까지의 settlement_day 조회(날짜별로 합침)
    @Query(value = "SELECT DATE(settlement_date) AS date, SUM(settlement_price) AS total_settlement_price\n" +
            "FROM settlement_day\n" +
            "WHERE settlement_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) AND settlement_date <= DATE_SUB(CURDATE(), INTERVAL 1 DAY)\n" +
            "GROUP BY DATE(settlement_date)",nativeQuery = true)

    List<Object[]> settlementDay1Month();

    // 어제의 한달 전부터 어제까지의 settlement_day 조회(날짜별로 합침), storeId로 조회
    @Query(value = "SELECT DATE(settlement_date) AS date, SUM(settlement_price) AS total_settlement_price\n" +
            "FROM settlement_day\n" +
            "WHERE store_id = :storeId AND settlement_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) AND settlement_date <= DATE_SUB(CURDATE(), INTERVAL 1 DAY)\n" +
            "GROUP BY DATE(settlement_date)",nativeQuery = true)

    List<Object[]> settlementDay1MonthByStoreId(@Param("storeId") int storeId);

    // 어제의 세달 전부터 어제까지의 settlement_day 조회(날짜별로 합침)
    @Query(value = "SELECT DATE(settlement_date) AS date, SUM(settlement_price) AS total_settlement_price\n" +
            "FROM settlement_day\n" +
            "WHERE settlement_date >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH) AND settlement_date <= DATE_SUB(CURDATE(), INTERVAL 1 DAY)\n" +
            "GROUP BY DATE(settlement_date)",nativeQuery = true)
    List<Object[]> settlementDay3Month();

    // 어제의 세달 전부터 어제까지의 settlement_day 조회(날짜별로 합침)
    @Query(value = "SELECT DATE(settlement_date) AS date, SUM(settlement_price) AS total_settlement_price\n" +
            "FROM settlement_day\n" +
            "WHERE store_id = :storeId AND settlement_date >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH) AND settlement_date <= DATE_SUB(CURDATE(), INTERVAL 1 DAY)\n" +
            "GROUP BY DATE(settlement_date)",nativeQuery = true)
    List<Object[]> settlementDay3MonthByStoreId(@Param("storeId") int storeId);

    // 기간별 조회(날짜별로 합침)
    @Query(value = "SELECT DATE(settlement_date) AS date, SUM(settlement_price) AS total_settlement_price\n" +
            "FROM settlement_day\n" +
            "WHERE settlement_date BETWEEN :startDate AND :endDate\n" +
            "GROUP BY DATE(settlement_date)",nativeQuery = true)
    List<Object[]> settlementDayTerm(@Param("startDate") String startDate, @Param("endDate") String endDate);

    // 기간별 조회(날짜별로 합침), storeId별 조회
    @Query(value = "SELECT DATE(settlement_date) AS date, settlement_price AS total_settlement_price\n" +
            "FROM settlement_day\n" +
            "WHERE store_id = :storeId AND settlement_date BETWEEN :startDate AND :endDate\n" +
            "GROUP BY DATE(settlement_date)",nativeQuery = true)
    List<Object[]> settlementDayTermByStoreId(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("storeId") int storeId);

    // 어제의 일주일 전부터 어제까지의 settlement_day 조회, store_id별
    @Query(value = "SELECT *\n" +
            "FROM settlement_day sd \n" +
            "WHERE store_id = :storeId AND settlement_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)\n" +
            "AND settlement_date <= CURDATE() - INTERVAL 1 DAY",nativeQuery = true)
    List<SettlementDay> selectSettlementDayBetweenYesterday1WeekAgoAndYesterdayByStoreId(@Param("storeId") int storeId);

    // 1주일 지점별 매출 파이차트 조회용 매서드
    @Query(value = "select sum(settlement_price), store_id from settlement_day\n" +
            "where settlement_date between DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND DATE_SUB(CURDATE(), INTERVAL 1 DAY)\n" +
            "group by store_id", nativeQuery = true)
    List<Object[]> Sale1WeekForPieChart();

    // 1달 지점별 매출 파이차트 조회용 매서드
    @Query(value = "select sum(settlement_price), store_id from settlement_day\n" +
            "where settlement_date between DATE_SUB(CURDATE(), INTERVAL 1 MONTH) AND DATE_SUB(CURDATE(), INTERVAL 1 DAY)\n" +
            "group by store_id", nativeQuery = true)
    List<Object[]> Sale1MonthForPieChart();

    // 3달 지점별 매출 파이차트 조회용 매서드
    @Query(value = "select sum(settlement_price), store_id from settlement_day\n" +
            "where settlement_date between DATE_SUB(CURDATE(), INTERVAL 3 MONTH) AND DATE_SUB(CURDATE(), INTERVAL 1 DAY)\n" +
            "group by store_id", nativeQuery = true)
    List<Object[]> Sale3MonthForPieChart();

    // 기간별 매출 파이차트 조회용 매서드
    @Query(value = "select sum(settlement_price), store_id from settlement_day\n" +
            "where settlement_date between :startDate AND :endDate \n" +
            "group by store_id", nativeQuery = true)
    List<Object[]> SaleTermForPieChart(@Param("startDate") String startDate, @Param("endDate") String endDate);

}
