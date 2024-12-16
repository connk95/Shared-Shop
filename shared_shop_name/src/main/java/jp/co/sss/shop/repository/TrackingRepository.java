package jp.co.sss.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.shop.entity.Tracking;

public interface TrackingRepository extends JpaRepository<Tracking, Integer> {
	Tracking findByOrderId(Integer orderId);
}
