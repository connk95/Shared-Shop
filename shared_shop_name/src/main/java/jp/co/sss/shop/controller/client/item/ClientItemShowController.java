package jp.co.sss.shop.controller.client.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.service.BeanTools;

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
	@RequestMapping(path = "/" , method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model) {
	
		return "index";
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
	
	



	

}
