package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.PriceCalc;

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

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	OrderItemRepository orderItemRepository;

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

	@ModelAttribute("orderForm")
	public OrderForm setupOrderForm() {
		return new OrderForm();
	}

	@ModelAttribute("orderItemBeans")
	public List<OrderItemBean> setupBasketBeans(HttpSession session) {
		Object basketBeans = session.getAttribute("basketBeans");
		List<BasketBean> basketBeanList = (List<BasketBean>) basketBeans;
		List<OrderItemBean> orderItemBeans = basketBeanList.stream().map(basketBean -> {
			Item item = itemRepository.findById(basketBean.getId()).orElse(null);
			return beanTools.generateOrderItemBean(item, basketBean);
		}).collect(Collectors.toList());

		return new ArrayList<>(orderItemBeans);
	}

	@RequestMapping(path = "/client/order/address/input", method = { RequestMethod.GET, RequestMethod.POST })
	public String orderAddressInput(@ModelAttribute OrderForm orderForm, Model model) {

		return "client/order/address_input";
	}
	
	@RequestMapping(path = "/client/order/payment/back", method = { RequestMethod.GET, RequestMethod.POST })
	public String orderAddressBack(@ModelAttribute OrderForm orderForm, Model model) {
		
		return "client/order/address_input";
	}

	@RequestMapping(path = "/client/order/payment/input", method = { RequestMethod.GET, RequestMethod.POST })
	public String orderPaymentInput(@Valid @ModelAttribute OrderForm orderForm, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "client/order/address_input";
		}

		return "client/order/payment_input";
	}

	@RequestMapping(path = "/client/order/check", method = { RequestMethod.GET, RequestMethod.POST })
	public String orderCheck(@Valid @ModelAttribute OrderForm orderForm,
			@ModelAttribute("orderItemBeans") List<OrderItemBean> orderItemBeans, BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			return "client/order/payment_input";
		}

		OrderBean orderBean = new OrderBean();

		BeanUtils.copyProperties(orderForm, orderBean);

		UserBean user = (UserBean) session.getAttribute("user");
		String userName = user.getName();

		orderBean.setUserName(userName);
		orderBean.setTotal(priceCalc.orderItemBeanPriceTotal(orderItemBeans));

		model.addAttribute("total", orderBean.getTotal());
		model.addAttribute("orderBean", orderBean);

		return "client/order/check";
	}

	@RequestMapping(path = "/client/order/complete", method = { RequestMethod.GET, RequestMethod.POST })
	public String orderComplete(@Valid @ModelAttribute OrderForm orderForm, @ModelAttribute OrderBean orderBean,
			@ModelAttribute("orderItemBeans") List<OrderItemBean> orderItemBeans, BindingResult result, Model model, SessionStatus status) {
		if (result.hasErrors()) {
			return "client/order/check";
		}
		
		Order order = new Order();
		BeanUtils.copyProperties(orderBean, order);
		
		UserBean userBean = (UserBean) session.getAttribute("user");
		Integer userId = userBean.getId();
		
		User user = userRepository.findById(userId).orElse(null);
		order.setUser(user);

		orderRepository.save(order);
		
		Object basketBeans = session.getAttribute("basketBeans");
		List<BasketBean> basketBeanList = (List<BasketBean>) basketBeans;
		
		basketBeanList.forEach(basketItem -> {
			
			OrderItem orderItem = new OrderItem();
			orderItem.setQuantity(basketItem.getOrderNum());
			
			Integer itemId = basketItem.getId();
			Item item = itemRepository.findById(itemId).orElse(null);
			orderItem.setItem(item);
			
			orderItem.setOrder(order);
			
			orderItem.setPrice(item.getPrice());
			
			orderItemRepository.save(orderItem);
		});
			
		status.setComplete();
		return "client/order/complete";
	}
}
