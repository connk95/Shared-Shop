package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員管理　会員詳細表示機能のコントローラクラス
 * @author Yamazaki_Taisei
 */
@Controller
public class ClientUserShowController {

	/**
	 * 会員情報　リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * 会員詳細表示画面に遷移させる機能
	 * 
	 * @return "client/user/detail" 会員詳細画面　表示処理
	 */
	@RequestMapping(path = "/client/user/detail", method = { RequestMethod.GET, RequestMethod.POST })
	public String clientUserShow(Model model, UserForm form) {

		User user = new User();
		UserBean userBean = new UserBean();

		//セッションからログインしているユーザの情報を取得
		user = userRepository.getReferenceById(((UserBean) session.getAttribute("user")).getId());
		BeanUtils.copyProperties(user, userBean);
		model.addAttribute("userBean", userBean);
		return "client/user/detail";
	}
}
