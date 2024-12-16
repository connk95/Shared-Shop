package jp.co.sss.shop.util;

/**
 * 定数定義用クラス
 *
 * @author SystemShared
 */
public class Constant {
	/** 削除フラグの値(未削除状態) */
	public static final int NOT_DELETED = 0;

	/** 削除フラグの値(削除状態) */
	public static final int DELETED = 1;

	/** インデックス番号の初期値 */
	public static final int DEFAULT_INDEX = 1;

	/** 表示順の初期値(新着順) */
	public static final int DEFAULT_SORT_TYPE = 1;

	/** 支払方法初期値(クレジットカード) */
	public static final int DEFAULT_PAYMENT_METHOD = 1;

	/** 戻るフラグの値（戻るボタン押下時） */
	public static final int BACK_FLG_TRUE = 1;

	/**
	 * 商品画像のアップロード先 (注意) 
	 * プロジェクト名直下にimagesフォルダを作成することを想定しています。
	 */
	public static final String FILE_UPLOAD_PATH = "images";

	/** CSS保存用フォルダの名前 */
	public static final String CSS_FOLDER = "/css/";

	/** 商品画像ファイル保存用フォルダの名前 */
	public static final String IMAGE_FOLDER = "/images/";

	/** 会員情報の最大登録数 */
	public static final Long USERS_MAX_COUNT = 999999L;
	/** カテゴリ情報の最大登録数 */
	public static final Long CATEGORIES_MAX_COUNT = 99L;
	/** アイテム情報の最大登録する */
	public static final Long ITEMS_MAX_COUNT = 999999L;
	/** オーダー情報の最大登録数 */
	public static final Long ORDERS_MAX_COUNT = 999999L;
	/** オーダーアイテム情報の最大登録数 */
	public static final Long ORDER_ITEMS_MAX_COUNT = 999999L;

	/** 権限の値(システム管理者) */
	public static final int AUTH_SYSTEM = 0;
	/** 権限の値(運用管理者) */
	public static final int AUTH_ADMIN = 1;
	/** 権限の値(一般会員) */
	public static final int AUTH_CLIENT = 2;

	/** 買い物かご注文数初期値 */
	public static final int DEFAULT_BUSKET_ORDER_NUM = 1;

	/** カテゴリ別検索 未選択初期値 */
	public static final int DEFAULT_SEARCH_CATEGORY_ID = 0;
	
	/** カテゴリ別検索 新着順 */
	public static final int SEARCH_CATEGORY_ID_INSERT_DATE = 1;
	
	/** カテゴリ別検索 売れ筋順 */
	public static final int SEARCH_CATEGORY_ID_TOTAL_QUANTITY = 2;

	/** 価格別検索 未選択初期値 */
	public static final Integer DEFAULT_PRICE_SEARCH_NUM = 0;
	
	/** SwitchFlag 検索処理 選択なし */
	public static final int NOT_SELECTED = 0;
	
	/** SwitchFlag 検索処理 価格別 */
	public static final int SELECT_PRICE = 1;
	
	/** SwitchFlag 検索処理 カテゴリ別 */
	public static final int SELECT_CATEGORY_ID = 2;
	
	/** SwitchFlag 検索処理 選択なし */
	public static final int SELECT_CATEGORY_ID_AND_PRICE = 3;
	
}
