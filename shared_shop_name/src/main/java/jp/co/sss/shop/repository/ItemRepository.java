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

	/**
	 *新着順の商品一覧を取得
	 * @return 商品エンティティ
	 */
	@Query("SELECT i FROM Item i WHERE i.deleteFlag = :deleteFlag ORDER BY i.insertDate DESC,i.id DESC")
	List<Item> findNewItems(@Param("deleteFlag") int deleteFlag);

	/**
	 * カテゴリ指定の新着順を検索
	 * @param categoryId カテゴリーID
	 * @return 商品エンティティ
	 */
	List<Item> findByCategoryIdAndDeleteFlagOrderByInsertDateDesc(Integer categoryId,int deleteFlag);

	/**
	 * カテゴリ指定の売れ筋順を検索
	 * @param categoryId カテゴリーID
	 * @return 商品エンティティ
	 */
	@Query("SELECT i FROM Item i WHERE i.category.id = :categoryId AND i.deleteFlag = :deleteFlag")
	List<Item> findByCategoryIdAndDeleteFlag(@Param("categoryId") Integer categoryId, @Param("deleteFlag") int deleteFlag);

	/**
	 * 全検索
	 * @param deleteFrag 削除フラグ
	 * @return 商品エンティティ
	 */
	List<Item> findAllByDeleteFlag(int deleteFrag);

	/**
	 * 価格を条件に検索（\0-1500 ～ \10000-30000を選択後、条件を検索）
	 * @param price
	 * @return 商品エンティティ
	 */
	@Query("SELECT i FROM Item i WHERE i.price >=:min AND i.price<=:max AND i.deleteFlag = :deleteFlag Order By i.price Asc")
	public List<Item> findByPriceQuery(@Param("min") Integer min, @Param("max") Integer max,@Param("deleteFlag") int deleteFlag);

	/**
	 * 価格を条件に検索（\30000以上を選択後、条件を検索）
	 * @param over
	 * @return 商品エンティティ
	 */
	@Query("SELECT i FROM Item i WHERE i.price >=:over AND i.deleteFlag = :deleteFlag Order By i.price Asc")
	public List<Item> findByOverPriceQuery(@Param("over") Integer over, @Param("deleteFlag") int deleteFlag);

	/**
	 * 多分つかわない
	 * 価格を条件に検索（-指定なし-を選択後、全件検索）
	 * @return 商品エンティティ
	 */
	List<Item> findAllByOrderByPriceAsc();
	
	//価格が30,000以上でカテゴリ検索（新着順）
	@Query("SELECT i FROM Item i WHERE i.category.id = :categoryId AND i.price >= :over AND i.deleteFlag = :deleteFlag ORDER BY i.insertDate DESC, i.id DESC")
	List<Item> findByCategoryIdAndPriceOverOrderByInsertDateDesc(@Param("categoryId") Integer categoryId, @Param("over") Integer price, @Param("deleteFlag") int deleteFlag);
	// 価格が30,000以下でカテゴリ検索（新着順）
	@Query("SELECT i FROM Item i WHERE i.category.id = :categoryId AND i.price >= :min AND i.price <= :max AND i.deleteFlag = :deleteFlag ORDER BY i.insertDate DESC, i.id DESC")
	List<Item> findByCategoryIdAndPriceUnderOrderByInsertDateDesc(@Param("categoryId") Integer categoryId, @Param("min") Integer min, @Param("max") Integer max, @Param("deleteFlag") int deleteFlag);
	
	
}
