package jp.co.sss.shop.form;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 配達状況入力フォーム
 * 
 * @author Connor_Ketcheson
 */
public class TrackingForm implements Serializable {

	/**
	 * 配達番号
	 */
	@NotBlank
	@Size(min = 12, max = 12, message = "{trackingRegist.numberdigits.message}")
	@Pattern(regexp = "^[0-9]+$", message = "{trackingRegist.numberpattern.message}")
	private String trackingNumber;

	/**
	 * 配達状況 0:注文済み 1:発送済み 2:配達中 3:配達済み
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
