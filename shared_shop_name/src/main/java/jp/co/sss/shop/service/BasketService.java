package jp.co.sss.shop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.util.Constant;

/**
 * 商品の在庫数を確認するクラス
 *
 * @author Uzawa_Raiki
 */
@Service
public class BasketService {
	@Autowired
	BeanTools beanTools;

	/**
	 * 在庫状況を確認するメソッド
	 * 
	 * ・在庫数が0の場合はメッセージを登録し、買い物かごから削除する
	 * 
	 * ・注文数が在庫数より多ければ注文数を在庫数にそろえ、メッセージを登録する
	 * 
	 * @param model View リクエストとの受け渡し
	 * @param session View セッションとの受け渡し
	 * 
	 */
	public void stockCheck(Model model, HttpSession session) {
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		if (basketBeans == null) {
			return;
		}

		List<String> itemNameListLessThan = new ArrayList<String>();
		List<String> itemNameListZero = new ArrayList<String>();
		List<BasketBean> deleteBasketBeans = new ArrayList<BasketBean>();

		for (BasketBean basketBean : basketBeans) {
			Integer stock = basketBean.getStock();
			if (stock == 0) {
				itemNameListZero.add(basketBean.getName());
				deleteBasketBeans.add(basketBean);
				continue;
			}
			if (stock < basketBean.getOrderNum()) {
				itemNameListLessThan.add(basketBean.getName());
				basketBean.setOrderNum(stock);
			}
		}

		model.addAttribute("itemNameListLessThan", itemNameListLessThan);
		model.addAttribute("itemNameListZero", itemNameListZero);

		if (deleteBasketBeans.size() != 0) {
			for (BasketBean deleteBean : deleteBasketBeans) {
				basketBeans.remove(deleteBean);
			}
		}
		session.setAttribute("basketBeans", basketBeans);
	}

	/**
	 * 買い物かごに商品を追加
	 * 
	 * ・買い物かごがない場合は新たに作成し、商品を登録
	 * 
	 * ・買い物かごがあり、追加したい商品がすでにある場合は、
	 * 　商品の注文数に1を足す
	 * 
	 * ・買い物かごがあり、追加したい商品がまだない場合は、
	 * 　商品を追加し注文数を1にする
	 * 
	 * セッションに買い物かご情報を登録する
	 * 
	 * @param id 商品ID
	 * @param session View セッションとの受け渡し
	 * @param itemRepository 商品リポジトリ
	 */
	public void addItem(Integer id, HttpSession session, ItemRepository itemRepository) {
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		Item item = itemRepository.getReferenceById(id);
		ItemBean itemBean = beanTools.copyEntityToItemBean(item);
		boolean isItemExisting = false;

		if (basketBeans == null) {
			basketBeans = new ArrayList<BasketBean>();
		} else {
			for (BasketBean basketBean : basketBeans) {
				if (basketBean.getName().equals(itemBean.getName())) {
					basketBean.setOrderNum(basketBean.getOrderNum() + 1);
					isItemExisting = true;
					break;
				}
			}
		}

		if (!isItemExisting) {
			basketBeans.add(
					new BasketBean(item.getId(), item.getName(), item.getStock(), Constant.DEFAULT_BUSKET_ORDER_NUM));
		}

		session.setAttribute("basketBeans", basketBeans);
	}

	/**
	 * 買い物かごの商品を1点減らす
	 * 
	 * ・在庫数が2以上なら在庫を1個減らす
	 * 
	 * ・在庫数が1以下なら商品情報を買い物かごから削除する
	 * 
	 * セッションに買い物かご情報を登録する
	 * 
	 * @param id 商品ID
	 * @param session View セッションとの受け渡し
	 */
	public void deleteItem(Integer id, HttpSession session) {
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		if (basketBeans == null) {
			return;
		}
		int indexNum = -1;
		for (int i = 0; i < basketBeans.size(); i++) {
			if (basketBeans.get(i).getId() == id) {
				indexNum = i;
				break;
			}
		}
		
		if (indexNum == -1) {
			return;
		}

		Integer orderNum = basketBeans.get(indexNum).getOrderNum();
		if (orderNum > 1) {
			basketBeans.get(indexNum).setOrderNum(orderNum - 1);
		} else {
			basketBeans.remove(indexNum);
		}

		session.setAttribute("basketBeans", basketBeans);
	}
	
	/**
	 * 買い物かごの商品を全て削除する
	 * 
	 * セッション内の買い物かご情報を削除する
	 * 
	 * @param session View セッションとの受け渡し
	 */
	public void deleteAllItem(HttpSession session) {
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		if (basketBeans == null) {
			return;
		}
		session.removeAttribute("basketBeans");
	}
	
	
	/**
	 * Dummyメソッド
	 * 
	 * 
	 * 終わったら削除
	 */
	public void dummyItems(ItemRepository itemRepository, HttpSession session) {
		List<BasketBean> basketBeans = new ArrayList<BasketBean>();

		basketBeans.add(new BasketBean());
		Item item1 = itemRepository.getReferenceById(1);
		BeanUtils.copyProperties(item1, basketBeans.get(0));
		basketBeans.get(0).setOrderNum(2);

		basketBeans.add(new BasketBean());
		Item item2 = itemRepository.getReferenceById(2);
		BeanUtils.copyProperties(item2, basketBeans.get(1));
		basketBeans.get(1).setOrderNum(6);

		basketBeans.add(new BasketBean());
		Item item3 = itemRepository.getReferenceById(3);
		BeanUtils.copyProperties(item3, basketBeans.get(2));
		basketBeans.get(2).setOrderNum(4);

		session.setAttribute("basketBeans", basketBeans);
	}
}
