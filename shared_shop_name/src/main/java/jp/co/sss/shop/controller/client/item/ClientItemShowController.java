package jp.co.sss.shop.controller.client.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;

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
	@RequestMapping(path = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model) {

		return "index";
	}

	/**
	 * トップ画面　価格別検索
	 * 
	 * @param model　
	 * @param price
	 * @return "client/item/list" 一覧表示画面
	 */
	@GetMapping("/client/item/list/{sortType}")
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
