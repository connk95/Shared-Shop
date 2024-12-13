package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員管理　会員情報削除機能のコントローラークラス
 * @author Yamazaki
 */
@Controller
public class ClientUserDeleteController {

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
	 * 削除確認画面　表示処理
	 * 
	 * @return "redirect:/client/user/delete/check" 削除確認画面　表示処理
	 */
	@PostMapping("/client/user/delete/check")
	public String deleteCheck() {

		User user = userRepository.getReferenceById(((UserBean) session.getAttribute("user")).getId());
		UserForm userForm = new UserForm();
		BeanUtils.copyProperties(user, userForm);
		session.setAttribute("userForm", userForm);

		return "redirect:/client/user/delete/check";
	}

	/**
	 * 削除確認画面　表示処理
	 * 
	 * @param userForm 入力情報フォーム
	 * @param model Viewとの値受渡
	 * @return "/client/user/delete_check" 削除確認画面　表示処理
	 */
	@GetMapping("/client/user/delete/check")
	public String getDeleteCheck(UserForm userForm, Model model) {

		userForm = (UserForm) session.getAttribute("userForm");
		model.addAttribute("userForm", userForm);
		return "/client/user/delete_check";
	}

	/**
	 * 情報削除処理
	 * 
	 * @param userForm 入力情報フォーム
	 * @return "redirect:/client/user/delete/complete" 削除完了画面　表示処理
	 */
	@PostMapping("/client/user/delete/complete")
	public String deleteComplete(UserForm userForm) {

		userForm = (UserForm) session.getAttribute("userForm");
		User user = new User();
		BeanUtils.copyProperties(userForm, user);

		userRepository.deleteById(user.getId());

		session.removeAttribute("userForm");
		session.removeAttribute("user");

		return "redirect:/client/user/delete/complete";
	}

	/**
	 * 削除完了画面　表示処理
	 * 
	 * @return "/client/user/delete_complete" 削除完了画面　表示処理
	 */
	@GetMapping("/client/user/delete/complete")
	public String getDeleteComplete() {

		return "/client/user/delete_complete";
	}
}
