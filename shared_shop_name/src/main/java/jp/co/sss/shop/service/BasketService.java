package jp.co.sss.shop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
		//セッションからバスケット情報の取得
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		//選択された商品情報の取得
		ItemBean itemBean = beanTools.copyEntityToItemBean(itemRepository.getReferenceById(id));
		boolean isItemExisting = false;//選択商品がすでにバスケットにあるかを判定する真偽値 初期値:存在しない

		//セッションにバスケット情報がなければバスケットを作成
		if (basketBeans == null) {
			basketBeans = new ArrayList<BasketBean>();
		} else {
			//選択商品がすでにバスケットにあれば注文数を1増やす【isItemExisting】をtrueにする
			for (BasketBean basketBean : basketBeans) {
				if (basketBean.getName().equals(itemBean.getName())) {
					basketBean.setOrderNum(basketBean.getOrderNum() + 1);
					isItemExisting = true;
					break;
				}
			}
		}

		//選択商品がまだバスケットになければバスケットに追加する
		if (!isItemExisting) {
			basketBeans.add(
					new BasketBean(itemBean.getId(), itemBean.getName(), itemBean.getStock(),
							Constant.DEFAULT_BUSKET_ORDER_NUM));
		}

		//セッションにバスケット情報を保存
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
		//セッションからバスケット情報の取得
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		//バスケット情報がなければ戻る
		if (basketBeans == null) {
			return;
		}

		//バスケット内に入っている選択商品のインデックス番号の取得
		int indexNum = -1;
		for (int i = 0; i < basketBeans.size(); i++) {
			if (basketBeans.get(i).getId() == id) {
				indexNum = i;
				break;
			}
		}

		//インデックス番号が見つけられなかった場合は戻る
		if (indexNum == -1) {
			return;
		}

		//選択されたアイテムの注文数が2以上なら1つ減らす
		Integer orderNum = basketBeans.get(indexNum).getOrderNum();
		if (orderNum > 1) {
			basketBeans.get(indexNum).setOrderNum(orderNum - 1);
		} else {
			//1以下ならバスケットから削除
			basketBeans.remove(indexNum);
		}

		//バスケットにアイテムが一つも残ってなければバスケット情報を削除
		if (basketBeans.size() == 0) {
			session.removeAttribute("basketBeans");
			return;
		}

		//セッションにバスケット情報を保存
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
		//セッションからバスケット情報の取得
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		//バスケット情報がなければ戻る
		if (basketBeans == null) {
			return;
		}
		//セッションからバスケットを削除
		session.removeAttribute("basketBeans");
	}

	/**
	 * 在庫状況を確認するメソッド(DB確認)
	 * 
	 * ・在庫数が0の場合はメッセージを登録し、買い物かごから削除する
	 * 
	 * ・注文数が在庫数より多ければ注文数を在庫数にそろえ、メッセージを登録する
	 * 
	 * @param model View リクエストとの受け渡し
	 * @param session View セッションとの受け渡し
	 * @param itemRepository 商品情報用レポジトリ
	 */
	public void stockCheck(Model model, HttpSession session, ItemRepository itemRepository) {

		// セッションからバスケット情報の取得
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		//バスケット情報がなければ戻る
		if (basketBeans == null) {
			return;
		}

		List<String> itemNameListLessThan = new ArrayList<String>();//在庫数が注文数よりも少ない商品名(テンプレートメッセージ用)
		List<String> itemNameListZero = new ArrayList<String>();//在庫数が0の商品名(テンプレートメッセージ用)
		List<BasketBean> deleteBasketBeans = new ArrayList<BasketBean>();//削除リスト(バスケットから削除する商品)

		//バスケット内の各商品の在庫数確認
		for (BasketBean basketBean : basketBeans) {
			Item item = itemRepository.getReferenceById(basketBean.getId());

			Integer stock = item.getStock();
			//在庫数0なら商品名リストと削除リストに追加
			if (stock == 0) {
				itemNameListZero.add(basketBean.getName());
				deleteBasketBeans.add(basketBean);
				continue;
			}
			//在庫数が注文数より少ないなら商品名リストに追加し、注文数を在庫数にそろえる
			if (stock < basketBean.getOrderNum()) {
				itemNameListLessThan.add(basketBean.getName());
				basketBean.setOrderNum(stock);
			}
		}

		//Veiwに商品名を登録
		model.addAttribute("itemNameListLessThan", itemNameListLessThan);
		model.addAttribute("itemNameListZero", itemNameListZero);

		//削除リストに商品が一個以上あればバスケットから該当商品を削除
		if (deleteBasketBeans.size() != 0) {
			for (BasketBean deleteBean : deleteBasketBeans) {
				basketBeans.remove(deleteBean);
			}
		}

		//セッションにバスケット情報を保存
		session.setAttribute("basketBeans", basketBeans);
	}

	/**
	 * 購入手続き時に在庫状況を確認するメソッド(DB確認)
	 * 
	 * ・在庫数が0の場合はメッセージを登録し、買い物かごから削除する
	 * 
	 * ・注文数が在庫数より多ければ注文数を在庫数にそろえ、メッセージを登録する
	 * 
	 * @param model View リクエストとの受け渡し
	 * @param session View セッションとの受け渡し
	 * @param itemRepository 商品情報用レポジトリ
	 * @param redirectAttributes フラッシュとの受け渡し
	 * @return boolean isStockCheckOK 在庫状況の異常検知真偽値
	 */
	public boolean stockCheckOut(Model model, HttpSession session, ItemRepository itemRepository,
			RedirectAttributes redirectAttributes) {

		boolean isStockCheckOK = true;//在庫状況の異常検知真偽値 true:問題なし false:在庫不足

		// セッションからバスケット情報の取得
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		//バスケット情報がなければ戻る
		if (basketBeans == null) {
			session.removeAttribute("orderItemBeans");
			session.removeAttribute("basketBeans");
			return isStockCheckOK = false;
		}

		List<String> itemNameListLessThan = new ArrayList<String>();//在庫数が注文数よりも少ない商品名(テンプレートメッセージ用)
		List<String> itemNameListZero = new ArrayList<String>();//在庫数が0の商品名(テンプレートメッセージ用)
		List<BasketBean> deleteBasketBeans = new ArrayList<BasketBean>();//削除リスト(バスケットから削除する商品)

		//バスケット内の各商品の在庫数確認
		for (BasketBean basketBean : basketBeans) {
			Item item = itemRepository.getReferenceById(basketBean.getId());

			Integer stock = item.getStock();
			//在庫数0なら商品名リストと削除リストに追加
			if (stock == 0) {
				itemNameListZero.add(basketBean.getName());
				deleteBasketBeans.add(basketBean);
				isStockCheckOK = false;
				continue;
			}
			//在庫数が注文数より少ないなら商品名リストに追加し、注文数を在庫数にそろえる
			if (stock < basketBean.getOrderNum()) {
				itemNameListLessThan.add(basketBean.getName());
				isStockCheckOK = false;
				basketBean.setOrderNum(stock);
			}
		}

		//Veiwに商品名を登録
		redirectAttributes.addFlashAttribute("itemNameListLessThan", itemNameListLessThan);
		redirectAttributes.addFlashAttribute("itemNameListZero", itemNameListZero);
		model.addAttribute("itemNameListLessThan", itemNameListLessThan);
		model.addAttribute("itemNameListZero", itemNameListZero);

		//削除リストに商品が一個以上あればバスケットから該当商品を削除
		if (deleteBasketBeans.size() != 0) {
			for (BasketBean deleteBean : deleteBasketBeans) {
				basketBeans.remove(deleteBean);
			}
		}

		//買い物かごの中身が何もない場合、買い物かごと注文商品情報を削除する
		if (basketBeans.size() == 0) {
			model.addAttribute("orderItemBeans", null);
			session.removeAttribute("basketBeans");
			isStockCheckOK = false;
		}

		//セッションにバスケット情報を保存
		session.setAttribute("basketBeans", basketBeans);

		return isStockCheckOK;
	}

}
