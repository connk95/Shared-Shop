package jp.co.sss.shop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.util.Constant;
import jp.co.sss.shop.util.ItemBeanComparator;

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
	 * @param price 検索価格帯
	 * @return 商品リスト
	 */
	public List<ItemBean> creatItemList(int sortType, Integer categoryId, String price) {
		int switchFlag = 0;
		if (categoryId == 0) {
			if (!price.equals("0")) {
				switchFlag = 1;//価格検索のみ
			}
		} else {
			if (price.equals("0")) {
				switchFlag = 2;//カテゴリ検索のみ
			} else {
				switchFlag = 3;//カテゴリ検索と価格検索のみ
			}
		}
		//分岐処理
		switch (switchFlag) {
		case 1://価格別検索が選択されている
				//sortTypeでさらに処理を分ける
			return PriceSearch(sortType, price);

		case 2://カテゴリ検索が選択されている
				//sortTypeでさらに処理を分ける
			return CategorySearch(sortType, categoryId);

		case 3://カテゴリ検索と価格別検索の両方が選択されている
				//sortTypeでさらに処理を分ける
			return CategoryAndPriceSearch(sortType, categoryId, price);

		default://カテゴリ検索も価格別検索も選択されていない
			//sortTypeでさらに処理を分ける
			return DefaultSearch(sortType);
		}
	}

	/**
	 * 価格検索処理
	 * @param sortType 表示順 (1: 新着順, 2: 売れ筋順)
	 * @param price 検索価格帯
	 * @return 商品リスト
	 */
	private List<ItemBean> PriceSearch(int sortType, String price) {
		List<ItemBean> items;
		if (sortType == 1) {
			if (price.equals("30000")) {
				Integer priceInt = Integer.parseInt(price);
				return beanTools
						.copyEntityListToItemBeanList(
								itemRepository.findByOverPriceQuery(priceInt, Constant.NOT_DELETED)); // 価格30000以上
			} else {
				String[] prices = price.split("～");
				Integer min = Integer.parseInt(prices[0]);
				Integer max = Integer.parseInt(prices[1]);
				return beanTools
						.copyEntityListToItemBeanList(itemRepository.findByPriceQuery(min, max, Constant.NOT_DELETED)); // 価格範囲指定
			}
		} else if (sortType == 2) {
			if (price.equals("30000")) {
				Integer priceInt = Integer.parseInt(price);
				items = beanTools
						.copyEntityListToItemBeanList(
								itemRepository.findByOverPriceQuery(priceInt, Constant.NOT_DELETED)); // 価格30000以上
				items.sort(new ItemBeanComparator());
				return items;
			} else {
				String[] prices = price.split("～");
				Integer min = Integer.parseInt(prices[0]);
				Integer max = Integer.parseInt(prices[1]);
				items = beanTools
						.copyEntityListToItemBeanList(itemRepository.findByPriceQuery(min, max, Constant.NOT_DELETED)); // 価格範囲指定
				items.sort(new ItemBeanComparator());
				return items;
			}
		}
		return beanTools.copyEntityListToItemBeanList(itemRepository.findAllByDeleteFlag(Constant.NOT_DELETED)); // デフォルト: 新着順
	}

	/**
	 * カテゴリ検索処理
	 * @param sortType 表示順 (1: 新着順, 2: 売れ筋順)
	 * @param categoryId カテゴリーID
	 * @return 商品リスト
	 */
	private List<ItemBean> CategorySearch(int sortType, Integer categoryId) {
		List<ItemBean> items;
		if (sortType == 1) {
			return beanTools.copyEntityListToItemBeanList(itemRepository
					.findByCategoryIdAndDeleteFlagOrderByInsertDateDesc(categoryId, Constant.NOT_DELETED)); // 新着順
		} else if (sortType == 2) {
			items = beanTools.copyEntityListToItemBeanList(
					itemRepository.findByCategoryIdAndDeleteFlag(categoryId, Constant.NOT_DELETED)); // 売れ筋順
			items.sort(new ItemBeanComparator());
			return items;
		}
		return beanTools.copyEntityListToItemBeanList(
				itemRepository.findByCategoryIdAndDeleteFlagOrderByInsertDateDesc(categoryId, Constant.NOT_DELETED)); // デフォルト: 新着順
	}

	
	/**
	 * カテゴリ検索 + 価格検索処理
	 * @param sortType 表示順 (1: 新着順, 2: 売れ筋順)
	 * @param categoryId カテゴリーID
	 * @param price 検索価格帯
	 * @return 商品リスト
	 */
	private List<ItemBean> CategoryAndPriceSearch(int sortType, Integer categoryId, String price) {
		List<ItemBean> items;
		//If SortType
		if (sortType == 1) {
			if (price.equals("30000")) {
				Integer priceInt = Integer.parseInt(price);
				return beanTools.copyEntityListToItemBeanList(itemRepository
						.findByCategoryIdAndPriceOverOrderByInsertDateDesc(categoryId, priceInt, Constant.NOT_DELETED)); // 価格30000以上
			} else {
				String[] prices = price.split("～");
				Integer min = Integer.parseInt(prices[0]);
				Integer max = Integer.parseInt(prices[1]);
				return beanTools.copyEntityListToItemBeanList(itemRepository
						.findByCategoryIdAndPriceUnderOrderByInsertDateDesc(categoryId, min, max,
								Constant.NOT_DELETED)); // 価格範囲指定
			}
		} else if (sortType == 2) {
			if (price.equals("30000")) {
				Integer priceInt = Integer.parseInt(price);
				items = beanTools.copyEntityListToItemBeanList(itemRepository
						.findByCategoryIdAndPriceOverOrderByInsertDateDesc(categoryId, priceInt, Constant.NOT_DELETED)); // 価格30000以上
				items.sort(new ItemBeanComparator());
				return items;
			} else {
				String[] prices = price.split("～");
				Integer min = Integer.parseInt(prices[0]);
				Integer max = Integer.parseInt(prices[1]);
				items = beanTools.copyEntityListToItemBeanList(itemRepository
						.findByCategoryIdAndPriceUnderOrderByInsertDateDesc(categoryId, min, max,
								Constant.NOT_DELETED)); // 価格範囲指定
				items.sort(new ItemBeanComparator());
				return items;
			}
		}
		return beanTools.copyEntityListToItemBeanList(itemRepository.findAllByDeleteFlag(Constant.NOT_DELETED)); // デフォルト: 新着順
	}

	/**
	 * 全商品検索処理
	 * @param sortType 表示順 (1: 新着順, 2: 売れ筋順)
	 * @return 商品リスト
	 */
	private List<ItemBean> DefaultSearch(int sortType) {
		List<ItemBean> items;
		if (sortType == 1) {
			return beanTools.copyEntityListToItemBeanList(itemRepository.findNewItems(Constant.NOT_DELETED)); // 新着順
		} else if (sortType == 2) {
			items = beanTools.copyEntityListToItemBeanList(itemRepository.findAllByDeleteFlag(Constant.NOT_DELETED)); // 売れ筋順
			items.sort(new ItemBeanComparator());
			return items;
		}
		return beanTools.copyEntityListToItemBeanList(itemRepository.findAllByDeleteFlag(Constant.NOT_DELETED)); // デフォルト: 新着順
	}
}
