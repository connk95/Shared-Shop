package jp.co.sss.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * 配達情報クラス
 *
 * @author Connor_Ketcheson
 */

@Entity
@Table(name = "tracking")
public class Tracking {
	/**
	 * 配達ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tracking_gen")
	@SequenceGenerator(name = "seq_tracking_gen", sequenceName = "seq_tracking", allocationSize = 1)
	private Integer id;

	/**
	 * 注文エンティティ(外部参照)
	 */
	@ManyToOne
	@JoinColumn(name = "order_id", referencedColumnName = "id")
	private Order order;

	/**
	 * 配達状況 0:注文済み 1:発送済み 2:配達中 3:配達済み
	 */
	@Column
	private Integer status;

	/**
	 * 配達番号
	 */
	@Column
	private String trackingNumber;

	/**
	 * 配達IDの取得
	 * @return 配達ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 配達IDのセット
	 * @return 配達ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 注文エンティティの取得
	 * @return 注文エンティティ
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * 注文エンティティのセット
	 * @param order 注文エンティティ
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * 配達状況の取得
	 * @return 配達状況
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * 配達状況のセット
	 * @return 配達状況
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * 配達番号の取得
	 * @return 配達番号
	 */
	public String getTrackingNumber() {
		return trackingNumber;
	}

	/**
	 * 配達番号のセット
	 * @return 配達番号
	 */
	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
}
