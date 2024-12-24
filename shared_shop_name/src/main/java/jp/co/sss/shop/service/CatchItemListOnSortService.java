package jp.co.sss.shop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 一覧表示機能(一般会員用)ソートサービスクラス
 *
 * @author horikawa
 */

@Service
public class CatchItemListOnSortService {

	/**
	 * 商品情報
	 */
	@Autowired
	private ItemRepository itemRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * 検索条件分岐処理
	 * @param sortType 表示順 (1: 新着順, 2: 売れ筋順)
	 * @param categoryId カテゴリーID
	 * @param loPriceStr 検索価格帯下限
	 * @param hiPriceStr 検索価格帯上限
	 * @param model Viewとの受け渡し
	 * @return 商品リスト
	 */
	public List<ItemBean> creatItemList(int sortType, Integer categoryId, String loPriceStr, String hiPriceStr,
			Model model) {

		// Viewに値を受け渡し
		model.addAttribute("loPrice", loPriceStr);
		model.addAttribute("hiPrice", hiPriceStr);
		
		// 入力値がIntの上限を超えていた場合はnullを返す
		Integer loPrice;
		Integer hiPrice;
		try {
			loPrice = Integer.parseInt(loPriceStr);
			hiPrice = Integer.parseInt(hiPriceStr);
		} catch (NumberFormatException e) {
			return null;
		}

		int switchFlag = Constant.NOT_SELECTED;//カテゴリ検索も価格別検索も選択されていない
		if (categoryId == Constant.DEFAULT_SEARCH_CATEGORY_ID) {
			if (hiPrice != Constant.DEFAULT_PRICE_SEARCH_NUM) {
				switchFlag = Constant.SELECT_PRICE;//価格検索のみ
			}
		} else {
			if (hiPrice == Constant.DEFAULT_PRICE_SEARCH_NUM) {
				switchFlag = Constant.SELECT_CATEGORY_ID;//カテゴリ検索のみ
			} else {
				switchFlag = Constant.SELECT_CATEGORY_ID_AND_PRICE;//カテゴリ検索と価格検索のみ
			}
		}

		// 上限価格が下限価格よりも低い場合はメッセージを表示し、全件表示
		if (hiPrice < loPrice) {
			model.addAttribute("MaxLessThanMin", Constant.PRICE_SEARCH_MAX_LESS_THAN_MIN);
			model.addAttribute("loPrice", Constant.DEFAULT_PRICE_SEARCH_MIN);
			model.addAttribute("hiPrice", Constant.DEFAULT_PRICE_SEARCH_MAX);
			switchFlag = Constant.NOT_SELECTED;
		}

		// 検索価格がマイナス値の場合はメッセージを表示し、全件表示
		if (hiPrice < 0 || loPrice < 0) {
			model.addAttribute("KeyIsMinus", Constant.PRICE_SEARCH_KEY_IS_MINUS);
			model.addAttribute("loPrice", Constant.DEFAULT_PRICE_SEARCH_MIN);
			model.addAttribute("hiPrice", Constant.DEFAULT_PRICE_SEARCH_MAX);
			switchFlag = Constant.NOT_SELECTED;
		}

		//分岐処理
		switch (switchFlag) {
		case Constant.SELECT_PRICE://価格別検索が選択されている
			return PriceSearch(sortType, loPrice, hiPrice);

		case Constant.SELECT_CATEGORY_ID://カテゴリ検索が選択されている
			return CategorySearch(sortType, categoryId);

		case Constant.SELECT_CATEGORY_ID_AND_PRICE://カテゴリ検索と価格別検索の両方が選択されている
			return CategoryAndPriceSearch(sortType, categoryId, loPrice, hiPrice);

		default://カテゴリ検索も価格別検索も選択されていない
			return DefaultSearch(sortType);
		}
	}

	/**
	 * 価格検索処理
	 * @param sortType 表示順 (1: 新着順, 2: 売れ筋順)
	 * @param price 検索価格帯
	 * @return 商品リスト
	 */
	private List<ItemBean> PriceSearch(int sortType, Integer loPrice, Integer hiPrice) {
		if (sortType == Constant.SEARCH_CATEGORY_ID_INSERT_DATE) {
			return beanTools.copyEntityListToItemBeanList(
					itemRepository.findByPriceMinAndPriceMaxAndDeleteFlagOrderByInsertDateDescAndIdDesc(loPrice,
							hiPrice, Constant.NOT_DELETED)); // 新着順
		} else if (sortType == Constant.SEARCH_CATEGORY_ID_TOTAL_QUANTITY) {
			return beanTools.copyEntityListToItemBeanList(
					itemRepository.findByPriceMinAndPriceMaxAndDeleteFlagOrderByTotalQueantityDescAndIdDesc(loPrice,
							hiPrice, Constant.NOT_DELETED)); // 売れ筋準
		} else {// デフォルト: 新着順
			return beanTools.copyEntityListToItemBeanList(
					itemRepository.findByPriceMinAndPriceMaxAndDeleteFlagOrderByInsertDateDescAndIdDesc(loPrice,
							hiPrice, Constant.NOT_DELETED));
		}
	}

	/**
	 * カテゴリ検索処理
	 * @param sortType 表示順 (1: 新着順, 2: 売れ筋順)
	 * @param categoryId カテゴリーID
	 * @return 商品リスト
	 */
	private List<ItemBean> CategorySearch(int sortType, Integer categoryId) {
		if (sortType == Constant.SEARCH_CATEGORY_ID_INSERT_DATE) {
			return beanTools.copyEntityListToItemBeanList(itemRepository
					.findByCategoryIdAndDeleteFlagOrderByInsertDateDescAndIdDesc(categoryId, Constant.NOT_DELETED)); // 新着順
		} else if (sortType == Constant.SEARCH_CATEGORY_ID_TOTAL_QUANTITY) {
			return beanTools.copyEntityListToItemBeanList(
					itemRepository.findByCategoryIdAndDeleteFlagOrderByTotalQueantityDescAndIdDesc(categoryId,
							Constant.NOT_DELETED)); // 売れ筋順
		}
		return beanTools.copyEntityListToItemBeanList(
				itemRepository.findByCategoryIdAndDeleteFlagOrderByInsertDateDescAndIdDesc(categoryId,
						Constant.NOT_DELETED)); // デフォルト: 新着順
	}

	/**
	 * カテゴリ検索 + 価格検索処理
	 * @param sortType 表示順 (1: 新着順, 2: 売れ筋順)
	 * @param categoryId カテゴリーID
	 * @param price 検索価格帯
	 * @return 商品リスト
	 */
	private List<ItemBean> CategoryAndPriceSearch(int sortType, Integer categoryId, Integer loPrice, Integer hiPrice) {
		if (sortType == Constant.SEARCH_CATEGORY_ID_INSERT_DATE) {
			return beanTools.copyEntityListToItemBeanList(itemRepository
					.findByCategoryIdAndPriceMinAndPriceMaxOrderByInsertDateDescAndIdDesc(categoryId, loPrice, hiPrice,
							Constant.NOT_DELETED)); // 新着順
		} else if (sortType == Constant.SEARCH_CATEGORY_ID_TOTAL_QUANTITY) {
			return beanTools.copyEntityListToItemBeanList(itemRepository
					.findByCategoryIdAndPriceMinAndPriceMaxAndDeleteFlagOrderByTotalQueantityDescAndIdDesc(
							categoryId, loPrice, hiPrice, Constant.NOT_DELETED)); // 売れ筋順
		} else {// デフォルト: 新着順
			return beanTools.copyEntityListToItemBeanList(itemRepository
					.findByCategoryIdAndPriceMinAndPriceMaxOrderByInsertDateDescAndIdDesc(categoryId, loPrice, hiPrice,
							Constant.NOT_DELETED));
		}
	}

	/**
	 * 全商品検索処理
	 * @param sortType 表示順 (1: 新着順, 2: 売れ筋順)
	 * @return 商品リスト
	 */
	private List<ItemBean> DefaultSearch(int sortType) {
		if (sortType == Constant.SEARCH_CATEGORY_ID_INSERT_DATE) {
			return beanTools.copyEntityListToItemBeanList(
					itemRepository.findByDeleteFlagOrderByInsertDateDescAndIdDesc(Constant.NOT_DELETED)); // 新着順
		} else if (sortType == Constant.SEARCH_CATEGORY_ID_TOTAL_QUANTITY) {
			return beanTools.copyEntityListToItemBeanList(
					itemRepository.findByDeleteFlagOrderByTotalQueantityDescAndIdDesc(Constant.NOT_DELETED)); // 売れ筋順
		}
		return beanTools.copyEntityListToItemBeanList(
				itemRepository.findByDeleteFlagOrderByInsertDateDescAndIdDesc(Constant.NOT_DELETED)); // デフォルト: 新着順
	}
}
