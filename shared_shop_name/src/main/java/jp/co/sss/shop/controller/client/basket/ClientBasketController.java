package jp.co.sss.shop.controller.client.basket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientBasketController {
	
	@Autowired
	ItemRepository itemRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	OrderItemRepository orderItemRepository;

}
