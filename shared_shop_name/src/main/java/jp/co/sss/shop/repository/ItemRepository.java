package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Item;

/**
 * itemsテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

	/**
	 * 商品情報を登録日付順に取得 管理者機能で利用
	 * @param deleteFlag 削除フラグ
	 * @param pageable ページング情報
	 * @return 商品エンティティのページオブジェクト
	 */
	@Query("SELECT i FROM Item i INNER JOIN i.category c WHERE i.deleteFlag =:deleteFlag ORDER BY i.insertDate DESC,i.id DESC")
	Page<Item> findByDeleteFlagOrderByInsertDateDescPage(
			@Param(value = "deleteFlag") int deleteFlag, Pageable pageable);

	/**
	 * 商品IDと削除フラグを条件に検索（管理者機能で利用）
	 * @param id 商品ID
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByIdAndDeleteFlag(Integer id, int deleteFlag);

	/**
	 * 商品名と削除フラグを条件に検索 (ItemValidatorで利用)
	 * @param name 商品名
	 * @param notDeleted 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByNameAndDeleteFlag(String name, int notDeleted);

	/** 1
	 * 売れ筋順検索 全商品 (非会員・一般会員機能で利用)
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	@Query("SELECT i FROM Item i INNER JOIN i.category c LEFT JOIN i.orderItemList oi WHERE i.deleteFlag =:deleteFlag GROUP BY i ORDER BY SUM(oi.quantity) DESC NULLS LAST,i.id DESC")
	List<Item> findByDeleteFlagOrderByTotalQueantityDescAndIdDesc(@Param(value = "deleteFlag") int deleteFlag);

	/** 2
	 * 新着順検索 全商品 (非会員・一般会員機能で利用)
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	@Query("SELECT i FROM Item i WHERE i.deleteFlag = :deleteFlag ORDER BY i.insertDate DESC,i.id DESC")
	List<Item> findByDeleteFlagOrderByInsertDateDescAndIdDesc(@Param("deleteFlag") int deleteFlag);

	/** 3
	 * カテゴリ別検索 売れ筋順 (非会員・一般会員機能で利用)
	 * @param categoryId カテゴリーID
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	@Query("SELECT i FROM Item i INNER JOIN i.category c LEFT JOIN i.orderItemList oi WHERE i.category.id = :categoryId AND i.deleteFlag =:deleteFlag GROUP BY i ORDER BY SUM(oi.quantity) DESC NULLS LAST,i.id DESC")
	List<Item> findByCategoryIdAndDeleteFlagOrderByTotalQueantityDescAndIdDesc(
			@Param(value = "categoryId") Integer categoryId, @Param(value = "deleteFlag") int deleteFlag);

	/** 4
	 * カテゴリ別検索 新着順 (非会員・一般会員機能で利用)
	 * @param categoryId カテゴリーID
	 * @return 商品エンティティ
	 */
	@Query("SELECT i FROM Item i WHERE i.category.id = :categoryId AND i.deleteFlag = :deleteFlag ORDER BY i.insertDate DESC,i.id DESC")
	List<Item> findByCategoryIdAndDeleteFlagOrderByInsertDateDescAndIdDesc(
			@Param(value = "categoryId") Integer categoryId, @Param(value = "deleteFlag") int deleteFlag);

	/** 5
	 * 価格別検索 売れ筋順 (非会員・一般会員機能で利用)
	 * @param min 下限価格
	 * @param max 上限価格
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	@Query("SELECT i FROM Item i INNER JOIN i.category c LEFT JOIN i.orderItemList oi WHERE i.price >= :min AND i.price <= :max AND i.deleteFlag =:deleteFlag GROUP BY i ORDER BY SUM(oi.quantity) DESC NULLS LAST,i.id DESC")
	List<Item> findByPriceMinAndPriceMaxAndDeleteFlagOrderByTotalQueantityDescAndIdDesc(
			@Param(value = "min") Integer min, @Param(value = "max") Integer max,
			@Param(value = "deleteFlag") int deleteFlag);

	/** 6
	 * 価格別検索 新着順 (非会員・一般会員機能で利用)
	 * @param min 下限価格
	 * @param max 上限価格
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	@Query("SELECT i FROM Item i  WHERE i.price >= :min AND i.price <= :max AND i.deleteFlag =:deleteFlag GROUP BY i ORDER BY i.insertDate DESC,i.id DESC")
	List<Item> findByPriceMinAndPriceMaxAndDeleteFlagOrderByInsertDateDescAndIdDesc(
			@Param(value = "min") Integer min, @Param(value = "max") Integer max,
			@Param(value = "deleteFlag") int deleteFlag);

	/** 7
	 * カテゴリ別＆価格別検索 売れ筋順 (非会員・一般会員機能で利用)
	 * @param min 下限価格
	 * @param max 上限価格
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	@Query("SELECT i FROM Item i INNER JOIN i.category c LEFT JOIN i.orderItemList oi WHERE i.category.id = :categoryId AND i.price >= :min AND i.price <= :max AND i.deleteFlag =:deleteFlag GROUP BY i ORDER BY SUM(oi.quantity) DESC NULLS LAST,i.id DESC")
	List<Item> findByCategoryIdAndPriceMinAndPriceMaxAndDeleteFlagOrderByTotalQueantityDescAndIdDesc(
			@Param("categoryId") Integer categoryId,
			@Param(value = "min") Integer min, @Param(value = "max") Integer max,
			@Param(value = "deleteFlag") int deleteFlag);

	/** 8
	 * カテゴリ別＆価格別検索 新着順 (非会員・一般会員機能で利用)
	 * @param categoryId カテゴリーID
	 * @param min 下限価格
	 * @param max 上限価格
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	@Query("SELECT i FROM Item i WHERE i.category.id = :categoryId AND i.price >= :min AND i.price <= :max AND i.deleteFlag = :deleteFlag ORDER BY i.insertDate DESC, i.id DESC")
	List<Item> findByCategoryIdAndPriceMinAndPriceMaxOrderByInsertDateDescAndIdDesc(
			@Param("categoryId") Integer categoryId,
			@Param("min") Integer min, @Param("max") Integer max, @Param("deleteFlag") int deleteFlag);

}
