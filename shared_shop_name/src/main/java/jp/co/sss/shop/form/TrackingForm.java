package jp.co.sss.shop.form;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 配達状況入力フォーム
 */
public class TrackingForm implements Serializable {

	/**
	 * 配達番号
	 */
	@NotBlank
	@Size(min = 10, max = 10, message = "配達番号10桁を入力してください。")
	private String trackingNumber;

	/**
	 * 配達状況
	 */
	private Integer status;

	/**
	 * 配達番号
	 * @return 配達番号
	 */
	public String getTrackingNumber() {
		return trackingNumber;
	}

	/**
	 * 配達番号
	 * @param 配達番号
	 */
	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	/**
	 * 配達状況
	 * @return 配達状況
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * 配達状況
	 * @param 配達状況
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

}
