package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.PriceCalc;

@Controller
public class ClientOrderShowController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderItemRepository orderItemRepository;

	@Autowired
	HttpSession session;

	@Autowired
	PriceCalc priceCalc;

	@Autowired
	BeanTools beanTools;

	@RequestMapping(path = "/client/order/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String showClientOrderList(Model model, Pageable pageable) {
		UserBean user = (UserBean) session.getAttribute("user");
		Integer userId = user.getId();

		Page<Order> orderList = orderRepository.findOrderByUserIdDescInsertDate(userId, pageable);

		List<OrderBean> orderBeanList = new ArrayList<OrderBean>();
		for (Order order : orderList) {

			OrderBean orderBean = beanTools.copyEntityToOrderBean(order);
			List<OrderItem> orderItemList = order.getOrderItemsList();

			int total = priceCalc.orderItemPriceTotal(orderItemList);

			orderBean.setTotal(total);

			orderBeanList.add(orderBean);
		}

		model.addAttribute("pages", orderList);
		model.addAttribute("orders", orderBeanList);

		return "client/order/list";
	}

	@GetMapping("/client/order/detail/{id}")
	public String showClientOrder(@PathVariable int id, Model model) {
		Order order = orderRepository.getReferenceById(id);
		
		OrderBean orderBean = beanTools.copyEntityToOrderBean(order);

		List<OrderItemBean> orderItemBeanList = beanTools.generateOrderItemBeanList(order.getOrderItemsList());
		
		int total = priceCalc.orderItemBeanPriceTotalUseSubtotal(orderItemBeanList);
		
		model.addAttribute("order", orderBean);
		model.addAttribute("orderItemBeans", orderItemBeanList);
		model.addAttribute("total", total);
		
		return "client/order/detail";
	}
}
