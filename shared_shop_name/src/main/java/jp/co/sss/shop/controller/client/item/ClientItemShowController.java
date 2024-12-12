package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.ItemBeanComparator;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ClientItemShowController {
	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	* トップ画面 表示処理
	*
	* @param model    Viewとの値受渡し
	* @return "index" トップ画面
	*/
	@RequestMapping(path = "/", method = RequestMethod.GET)
	public String showTopPage(Model model) {
		// 商品一覧を「売れ筋順」で取得
		List<ItemBean> items = beanTools.copyEntityListToItemBeanList(itemRepository.findAllByDeleteFlag(0));
		items.sort(new ItemBeanComparator());
		int totalQuantity = 0;
		for (ItemBean itemBean : items) {
			totalQuantity += itemBean.getTotalQuantity();
		}

		// 売れ筋順の商品がない場合は「新着順」で取得
		if (totalQuantity == 0) {
			items = beanTools.getNewItems();
			System.out.println("新着順");
			model.addAttribute("sortType", 1); // 新着順
		} else {
			model.addAttribute("sortType", 2); // 売れ筋順

		}

		// 商品一覧を画面に渡す
		model.addAttribute("items", items);

		return "index";
	}

	/**
	 * 商品一覧表示（新着順、売れ筋順、カテゴリ検索対応）
	 *
	 * @param sortType   表示順 (1: 新着順, 2: 売れ筋順)
	 * @param categoryId カテゴリーID (null または 0 の場合は全カテゴリ)
	 * @param model Viewとの値受渡し
	 * @return 商品一覧画面
	 */
	@RequestMapping(path = "client/item/list/{sortType}", method = RequestMethod.GET)
	public String clientItemList(@PathVariable("sortType") int sortType,
			@RequestParam(value = "categoryId", required = false, defaultValue = "0") Integer categoryId,
			Model model) { //categoryId が0の場合は全カテゴリで表示されるように設定
		List<ItemBean> items;
		// カテゴリ検索の条件分岐
		if (categoryId != null && categoryId != 0) {
			if (sortType == 1) {
				// カテゴリ指定の新着順
				items = beanTools.getItemsByCategorySortedByLatest(categoryId);
			} else if (sortType == 2) {
				// カテゴリ指定の売れ筋順
				List<Item> categoryItems = itemRepository.findByCategoryIdAndDeleteFlag(categoryId, 0); // 該当カテゴリのアイテム取得
	            items = beanTools.copyEntityListToItemBeanList(categoryItems); // アイテムをBeanに変換
	            items.sort(new ItemBeanComparator()); // 売れ筋順に並び替え
			} else {
				// デフォルト：カテゴリ指定の新着順
				items = beanTools.getItemsByCategorySortedByLatest(categoryId);
			}
		} else {
			if (sortType == 1) {
				// 全カテゴリの新着順
				items = beanTools.getNewItems();
			} else if (sortType == 2) {
				// 全カテゴリの売れ筋順
				items = beanTools.copyEntityListToItemBeanList(itemRepository.findAllByDeleteFlag(0));
				items.sort(new ItemBeanComparator());
			} else {
				// デフォルト：全カテゴリの新着順
				items = beanTools.getNewItems();
			}
		}
		// モデルにデータを設定
		model.addAttribute("items", items);
		model.addAttribute("sortType", sortType);
		model.addAttribute("categoryId", categoryId);
		return "client/item/list"; // 商品一覧画面
	}
}
