package jp.co.sss.shop.bean;

/**
 * 配達情報Beanクラス
 *
 * @author Connor_Ketcheson
 */

public class TrackingBean {
	/**
	 * 配達ID
	 */
	private Integer id;
	
	/**
	 * 注文番号
	 */
	private Integer orderNum;
	
	/**
	 * 配達状況 0:注文済み 1:発送済み 2:配達中 3:配達済み
	 */
	private Integer status;
	
	/**
	 * 配達番号
	 */
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
	 * @param id 注文商品ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 注文番号の取得
	 * @return 注文番号
	 */
	public Integer getOrderNum() {
		return orderNum;
	}

	/**
	 * 注文番号のセット
	 * @param orderNum 注文番号
	 */
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
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
