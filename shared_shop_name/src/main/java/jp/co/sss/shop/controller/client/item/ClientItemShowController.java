package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


import jp.co.sss.shop.bean.ItemBean;
import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.ItemBeanComparator;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller

public class ClientItemShowController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;
	
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

//	@RequestMapping(path = "/", method = { RequestMethod.GET, RequestMethod.POST })
//	public String index(Model model) {
//

	}

	/**
	 * 商品名リンククリック→詳細表示
	 */
	
	@GetMapping("/client/item/detail/{id}")
	public String detail(@PathVariable Integer id, Model model){
		Item item = itemRepository.getReferenceById(id);
		model.addAttribute("item", item);
		return "client/item/detail";
	}

  
  //　修正しないといけない
  
	/**
	 * トップ画面　価格別検索
	 * 
	 * @param model　
	 * @param price
	 * @return "client/item/list" 一覧表示画面
	 */
	@GetMapping("/client/item/list/{price}")
	public String showPrice(@PathVariable Integer sortType, String price, Model model) {
		//文字列を区別して条件検索を行う
		if (price.equals("30000")) {
			Integer over = Integer.parseInt(price);
			model.addAttribute("items", itemRepository.findByOverPriceQuery(over));
		} else if (price.equals("0")) {

			model.addAttribute("items", itemRepository.findAllByOrderByPriceAsc());
		} else {

			String[] prices = price.split("～");
			Integer min = Integer.parseInt(prices[0]);
			Integer max = Integer.parseInt(prices[1]);
			model.addAttribute("items", itemRepository.findByPriceQuery(min, max));

		}
		return "client/item/list";
	}

}
