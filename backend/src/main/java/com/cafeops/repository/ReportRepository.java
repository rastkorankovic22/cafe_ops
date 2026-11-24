package com.cafeops.repository;

import com.cafeops.domain.CafeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface ReportRepository extends JpaRepository<CafeOrder, Long> {

    @Query(
            value = """
            SELECT COUNT(*) FROM cafe_order o
            JOIN cafe_table t ON t.id = o.table_id
            WHERE t.cafe_id = :cafeId
              AND o.status = 'CLOSED'
              AND o.closed_at >= :from
              AND o.closed_at < :to
            """,
            nativeQuery = true
    )
    long countClosedOrders(
            @Param("cafeId") Long cafeId,
            @Param("from") Instant from,
            @Param("to") Instant to
    );

    @Query(
            value = """
            SELECT COALESCE(SUM(ol.quantity * ol.unit_price), 0)
            FROM order_line ol
            JOIN cafe_order o ON o.id = ol.order_id
            JOIN cafe_table t ON t.id = o.table_id
            WHERE t.cafe_id = :cafeId
              AND o.status = 'CLOSED'
              AND o.closed_at >= :from
              AND o.closed_at < :to
            """,
            nativeQuery = true
    )
    BigDecimal sumRevenue(
            @Param("cafeId") Long cafeId,
            @Param("from") Instant from,
            @Param("to") Instant to
    );

    @Query(
            value = """
            SELECT ol.menu_item_id, mi.name, SUM(ol.quantity) AS qty
            FROM order_line ol
            JOIN cafe_order o ON o.id = ol.order_id
            JOIN cafe_table t ON t.id = o.table_id
            JOIN menu_item mi ON mi.id = ol.menu_item_id
            WHERE t.cafe_id = :cafeId
              AND o.status = 'CLOSED'
              AND o.closed_at >= :from
              AND o.closed_at < :to
            GROUP BY ol.menu_item_id, mi.name
            ORDER BY qty DESC
            LIMIT 3
            """,
            nativeQuery = true
    )
    List<Object[]> topItems(
            @Param("cafeId") Long cafeId,
            @Param("from") Instant from,
            @Param("to") Instant to
    );
}