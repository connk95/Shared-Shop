package jp.co.sss.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Tracking;

/**
 * trackingテーブル用リポジトリ
 * 
 * @author Connor_Ketcheson
 */
@Repository
public interface TrackingRepository extends JpaRepository<Tracking, Integer> {
	Tracking findByOrderId(Integer orderId);
}
