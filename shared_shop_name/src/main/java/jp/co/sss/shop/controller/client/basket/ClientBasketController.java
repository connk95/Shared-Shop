package jp.co.sss.shop.controller.client.basket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BasketService;
import jp.co.sss.shop.service.BeanTools;

/**
 * 買い物かご管理(一般会員用)のコントローラクラス
 *
 * @author Uzawa_Raiki
 */
@Controller
public class ClientBasketController {
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
	 * セッション情報
	 */
	@Autowired
	HttpSession session;

	/**
	 * 在庫管理サービス
	 */
	@Autowired
	BasketService basketService;

	/**
	 * 買い物かご画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @return "client/basket/list" 買い物かご画面
	 */
	@RequestMapping(path = "/client/basket/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String showItemsInBasket(Model model) {
		basketService.stockCheck(model, session);
		return "client/basket/list";
	}

	/**
	 * 買い物かご商品追加
	 *
	 * @param id    商品ID
	 * @return リダイレクト 買い物かご画面
	 */
	@PostMapping("/client/basket/add")
	public String addItemToBasket(Integer id) {
		basketService.addItem(id, session, itemRepository);
		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かご内商品消去(単品)
	 *
	 * @param id    商品ID
	 * @return リダイレクト 買い物かご画面
	 */
	@PostMapping("/client/basket/delete")
	public String deleteItemOfBasket(Integer id) {
		basketService.deleteItem(id, session);
		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かご内商品全消去
	 *
	 * @return リダイレクト 買い物かご画面
	 */
	@PostMapping("/client/basket/allDelete")
	public String deleteAllItemsOfBasket() {
		basketService.deleteAllItem(session);
		return "redirect:/client/basket/list";
	}
}
