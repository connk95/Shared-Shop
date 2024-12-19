package jp.co.sss.shop.bean;

public class TrackingBean {
	/**
	 * 配達ID
	 */
	private Integer id;
	/**
	 * 注文個数
	 */
	private Integer orderNum;
	/**
	 * 配達状態
	 */
	private Integer status;
	/**
	 * 配達番号
	 */
	private Integer trackingNumber;

	/**
	 * 配達IDの取得
	 * @return 配達ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 配達Dのセット
	 * @param id 注文商品ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 注文個数の取得
	 * @return 注文個数
	 */
	public Integer getOrderNum() {
		return orderNum;
	}

	/**
	 * 注文個数のセット
	 * @param orderNum 注文個数
	 */
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	/**
	 * 配達状態の取得
	 * @return 配達状態
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * 配達状態のセット
	 * @return 配達状態
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * 配達番号の取得
	 * @return 配達番号
	 */
	public Integer getTrackingNumber() {
		return trackingNumber;
	}

	/**
	 * 配達番号のセット
	 * @return 配達番号
	 */
	public void setTrackingNumber(Integer trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
}
