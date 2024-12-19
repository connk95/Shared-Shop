package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.Tracking;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.TrackingRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.service.BasketService;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.PriceCalc;
import jp.co.sss.shop.util.Constant;

/**
 * 注文手続き機能(一般会員用)のコントローラクラス
 */
@Controller
@SessionAttributes({ "orderForm", "orderItemsBeans", "orderBean" })
public class ClientOrderRegistController {

	/**
	 * 会員情報
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * 注文情報
	 */
	@Autowired
	OrderRepository orderRepository;

	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 *注文の 商品情報
	 */
	@Autowired
	OrderItemRepository orderItemRepository;

	/**
	 *配達情報
	 */
	@Autowired
	TrackingRepository trackingRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * 合計金額計算サービス
	 */
	@Autowired
	PriceCalc priceCalc;

	/**
	 * Entity、Form、Bean間のデータ生成、コピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * 在庫管理サービス
	 */
	@Autowired
	BasketService basketService;

	/**
	 * コントローラー全体で使用するために
	 * orderFormを初期化する
	 * 
	 * @return 全体OrderForm
	 */
	@ModelAttribute("orderForm")
	public OrderForm setupOrderForm() {
		UserBean userBean = (UserBean) session.getAttribute("user");
		Integer userId = userBean.getId();
		User user = userRepository.findById(userId).orElse(null);
		OrderForm orderForm = new OrderForm();

		orderForm.setAddress(user.getAddress());
		orderForm.setName(user.getName());
		orderForm.setPhoneNumber(user.getPhoneNumber());
		orderForm.setPostalCode(user.getPostalCode());

		return orderForm;
	}

	/**
	 * コントローラー全体で使用するために
	 * orderItemBeansを初期化する
	 * 
	 * @return 全体orderItemBeans
	 */
	@ModelAttribute("orderItemBeans")
	public List<OrderItemBean> setupBasketBeans(HttpSession session) {
		Object basketBeans = session.getAttribute("basketBeans");

		if (basketBeans == null) {
			return null;
		}

		List<BasketBean> basketBeanList = (List<BasketBean>) basketBeans;
		List<OrderItemBean> orderItemBeans = basketBeanList.stream().map(basketBean -> {
			Item item = itemRepository.findById(basketBean.getId()).orElse(null);
			return beanTools.generateOrderItemBean(item, basketBean);
		}).collect(Collectors.toList());

		return new ArrayList<>(orderItemBeans);
	}

	/**
	 * 住所情報入力
	 *
	 * @return 住所情報を入力画面
	 */
	@GetMapping("/client/order/address/input")
	public String getOrderAddressInput(@ModelAttribute OrderForm orderForm) {

		return "client/order/address_input";
	}

	/**
	 * 住所情報入力
	 * @param orderForm注文フォーム
	 * @return 支払方法入力画面
	 */
	@PostMapping("/client/order/address/input")
	public String postOrderAddressInput(@Valid @ModelAttribute OrderForm orderForm, BindingResult result) {
		orderForm.setPayMethod(Constant.DEFAULT_PAYMENT_METHOD);
		return "redirect:/client/order/address/input";
	}

	/**
	 * 住所情報・支払方法・確認画面から買い物かごへ戻る
	 *
	 * @return 買い物かご画面
	 */
	@RequestMapping(path = "/client/order/payment/back", method = { RequestMethod.GET, RequestMethod.POST })
	public String orderAddressBack(@ModelAttribute OrderForm orderForm, Model model) {

		return "redirect:/client/order/address/input";
	}

	/**
	 * 支払方法入力
	 *
	 * @return 支払方法入力画面
	 */
	@GetMapping("/client/order/payment/input")
	public String getOrderPaymentInput(@Valid @ModelAttribute OrderForm orderForm, BindingResult result) {
		return "client/order/payment_input";
	}

	/**
	 * 支払方法入力
	 *
	 * @param orderForm注文フォーム
	 * @return 支払方法画面
	 */
	@PostMapping(path = "/client/order/payment/input")
	public String postOrderPaymentInput(@Valid @ModelAttribute OrderForm orderForm, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "client/order/payment_input";
		}

		return "redirect:/client/order/payment/input";
	}

	/**
	 * 注文確認
	 *
	 * @return 注文確認画面
	 */
	@GetMapping("/client/order/check")
	public String getOrderCheck(@Valid @ModelAttribute OrderForm orderForm,
			@ModelAttribute("orderItemBeans") List<OrderItemBean> orderItemBeans, OrderBean orderBean,
			BindingResult result, Model model) {
		if (result.hasErrors()) {

			return "client/order/payment_input";
		}

		// 一時的に注文を orderBean に保存する
		orderBean = new OrderBean();
		BeanUtils.copyProperties(orderForm, orderBean);
		// ユーザーデータを取得する
		UserBean user = (UserBean) session.getAttribute("user");
		String userName = user.getName();
		// ユーザーデータと小計を注文に追加する
		orderBean.setUserName(userName);
		orderBean.setTotal(priceCalc.orderItemBeanPriceTotal(orderItemBeans));
		// 情報を確認画面に渡す
		model.addAttribute("total", orderBean.getTotal());
		model.addAttribute("orderBean", orderBean);

		List<OrderItemBean> orderItemBeanList = (List<OrderItemBean>) model.getAttribute("orderItemBeans");

		if (orderItemBeanList.size() == 0) {
			model.addAttribute("orderItemBeans", null);
			basketService.deleteAllItem(session);
		}

		return "client/order/check";
	}

	/**
	 * 注文確認画面
	 *
	 * @param orderForm注文フォーム
	 * @return 注文完了画面
	 */
	@PostMapping("/client/order/check")
	public String postOrderCheck(@Valid @ModelAttribute OrderForm orderForm,
			@ModelAttribute("orderItemBeans") List<OrderItemBean> orderItemBeans, BindingResult result,
			Model model, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {

			return "client/order/check";
		}

		// 在庫確認
		basketService.stockCheckOut(model, session, itemRepository, redirectAttributes);

		return "redirect:/client/order/check";
	}

	/**
	 * 注文完了
	 *
	 * @return 注文完了画面
	 */
	@GetMapping("/client/order/complete")
	public String getOrderComplete() {
		return "client/order/complete";
	}

	/**
	 * 注文完了画面
	 *
	 * @param orderForm注文フォーム
	 * @return 注文完了画面
	 */
	@PostMapping(path = "/client/order/complete")
	public String postOrderComplete(@ModelAttribute OrderForm orderForm, @ModelAttribute OrderBean orderBean,
			@ModelAttribute("orderItemBeans") List<OrderItemBean> orderItemBeans, Model model,
			SessionStatus status, RedirectAttributes redirectAttributes) {

		boolean isStockCheckOK = true;

		// 在庫確認
		isStockCheckOK = basketService.stockCheckOut(model, session, itemRepository, redirectAttributes);

		if (!isStockCheckOK) {
			return "redirect:/client/order/check";
		}

		// 注文を作成し、orderBean からコピーする
		Order order = new Order();
		BeanUtils.copyProperties(orderBean, order);
		// 注文のためにユーザー情報を取得する
		UserBean userBean = (UserBean) session.getAttribute("user");
		Integer userId = userBean.getId();
		User user = userRepository.findById(userId).orElse(null);
		order.setUser(user);
		// 注文をデータベースに保存する
		orderRepository.save(order);
		// BasketBeans を取得し、各アイテムを order_items テーブルに保存する
		Object basketBeans = session.getAttribute("basketBeans");
		List<BasketBean> basketBeanList = (List<BasketBean>) basketBeans;

		// 在庫更新
		basketBeanList = basketService.completeCheck(basketBeanList, itemRepository);
		basketBeanList.forEach(basketItem -> {

			OrderItem orderItem = new OrderItem();
			orderItem.setQuantity(basketItem.getOrderNum());

			Integer itemId = basketItem.getId();
			Item item = itemRepository.findById(itemId).orElse(null);
			orderItem.setItem(item);

			orderItem.setOrder(order);

			orderItem.setPrice(item.getPrice());

			orderItemRepository.save(orderItem);

			item.setStock(item.getStock() - orderItem.getQuantity());

			itemRepository.save(item);
		});

		// 配達情報を作成
		Tracking tracking = new Tracking();
		tracking.setOrder(order);
		tracking.setStatus(0);
		tracking.setTrackingNumber(null);

		trackingRepository.save(tracking);

		// 一時データをクリアする
		status.setComplete();
		basketService.deleteAllItem(session);
		return "redirect:/client/order/complete";
	}
}