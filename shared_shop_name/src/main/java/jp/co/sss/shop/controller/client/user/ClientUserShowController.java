package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserShowController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	/**
	 * 会員詳細表示画面に遷移させる機能
	 * 
	 * @return
	 */
	@GetMapping("/client/user/detail")
	public String clientUserShow(Model model, UserForm form) {
		
		User user = new User();
		UserBean userBean = new UserBean();
		
		//
		user = userRepository.getReferenceById(((UserBean) session.getAttribute("user")).getId());
		BeanUtils.copyProperties(user, userBean);
		model.addAttribute("userBean", userBean);
		return "client/user/detail";
	}
}
