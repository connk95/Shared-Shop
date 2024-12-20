package jp.co.sss.shop.controller.client.item;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.CatchItemListOnSortService;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller

public class ClientItemShowController {
	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * 注文情報
	 */
	@Autowired
	OrderRepository orderRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * 商品一覧ソートサービス
	 */
	@Autowired
	CatchItemListOnSortService catchItemListOnSortService;

	/**
	* トップ画面 表示処理
	*
	* @param model    Viewとの値受渡し
	* @return "index" トップ画面
	*/
	@RequestMapping(path = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public String showTopPage(Model model) {
		// 注文一覧の取得
		List<Order> order = orderRepository.findAll();

		List<ItemBean> items = new ArrayList<ItemBean>();// 注文情報格納用ビーンリスト

		if (order.size() == 0) {
			// 注文が一つもないなら新着順で取得
			items = beanTools.copyEntityListToItemBeanList(
					itemRepository.findByDeleteFlagOrderByInsertDateDescAndIdDesc(Constant.NOT_DELETED));
			model.addAttribute("sortType", Constant.SEARCH_CATEGORY_ID_INSERT_DATE); // 新着順
		} else {
			//注文が一つ以上あるなら売れ筋準で取得
			items = beanTools.copyEntityListToItemBeanList(
					itemRepository.findByDeleteFlagOrderByTotalQueantityDescAndIdDesc(Constant.NOT_DELETED));
			model.addAttribute("sortType", Constant.SEARCH_CATEGORY_ID_TOTAL_QUANTITY); // 売れ筋順
		}

		// 商品一覧を画面に渡す
		model.addAttribute("items", items);

		return "index";
	}

	/**
	 * 商品一覧表示（新着順、売れ筋順、カテゴリ検索対応）
	 *
	 * @param sortType  表示順 (1: 新着順, 2: 売れ筋順)
	 * @param categoryId カテゴリーID (null または 0 の場合は全カテゴリ)
	 * @param price 検索価格帯
	 * @param model Viewとの値受渡し
	 * @return 商品一覧画面
	 */
	@RequestMapping(path = "client/item/list/{sortType}", method = { RequestMethod.GET, RequestMethod.POST })
	public String clientItemList(@PathVariable("sortType") int sortType,
			@RequestParam(value = "categoryId", required = false, defaultValue = "0") Integer categoryId,
			@RequestParam(value = "loPrice", required = false, defaultValue = "0") Integer loPrice,
			@RequestParam(value = "hiPrice", required = false, defaultValue = "0") Integer hiPrice,
			Model model) { //categoryId・price が0の場合は全商品が表示されるように設定

		// モデルにデータを設定
		model.addAttribute("items", catchItemListOnSortService.creatItemList(sortType, categoryId, loPrice, hiPrice));
		model.addAttribute("sortType", sortType);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("loPrice", loPrice);
		model.addAttribute("hiPrice", hiPrice);
		return "client/item/list"; // 商品一覧画面
	}

	/**
	 * 商品名リンククリック→詳細表示
	 * @param id 商品ID
	 * @param model Viewとの値受渡し
	 * @return 商品詳細画面
	 */
	@GetMapping("/client/item/detail/{id}")
	public String detail(@PathVariable Integer id, Model model) {
		Item item = itemRepository.getReferenceById(id);
		model.addAttribute("item", item);
		return "client/item/detail";
	}

}
