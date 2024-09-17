package kr.co.backend.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.backend.domain.*;
import kr.co.backend.repository.custom.CustomOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements CustomOrderRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ResponseEntity<?> deleteByUser(User user) {
        QOrder order = QOrder.order;
        QDelivery delivery = QDelivery.delivery;

        List<Delivery> deliveryData = null;

        List<Long> orderData = jpaQueryFactory.select(
                        order.orderId
                ).from(order)
                .where(order.user.userId.eq(user.getUserId()))
                .fetch();

        for (Long orderId : orderData) {
            deliveryData = jpaQueryFactory.select(
                            delivery
                    ).from(delivery)
                    .where(delivery.order.orderId.eq(orderId).and(delivery.status.eq(DeliveryStatus.COMP)))
                    .fetch();
        }

        if (deliveryData != null) {
            return ResponseEntity.badRequest().body("이미 발송된 주문이 있어 회원탈퇴가 불가능합니다.");
        } else {
            for (Long orderId : orderData) {
                jpaQueryFactory.delete(
                                delivery
                        ).where(delivery.order.orderId.eq(orderId).and(delivery.status.eq(DeliveryStatus.COMP)))
                        .execute();
            }
            jpaQueryFactory.delete(order)
                    .where(order.user.userId.eq(user.getUserId()))
                    .execute();

            return ResponseEntity.ok().body("주문과 배송정보가 삭제 되었습니다.");
        }
    }
}
