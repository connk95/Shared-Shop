package jp.co.sss.shop.controller.client.user;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

/**
 * 会員管理 登録機能(非会員)のコントローラクラス
 * @author Yamazaki
 */
@Controller
public class ClientUserRegistController {

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
	 * 入力画面　新規登録リンク押下時の処理
	 * 
	 * @param model Viewとの値受渡し
	 * @return "redirect:/client/user/regist/input" 入力画面 表示処理
	 */
	@GetMapping("/client/user/regist/input/init")
	public String registInputInit(Model model) {

		UserForm userForm = new UserForm();
		session.setAttribute("userForm", userForm);
		return "redirect:/client/user/regist/input";
	}

	/**
	 * 入力画面　Post送信で受け取った際の新規会員登録表示処理
	 * 
	 * @return "redirect:/client/user/regist/input" 入力画面 表示処理
	 */
	@PostMapping("/client/user/regist/input")
	public String registInput() {

		return "redirect:/client/user/regist/input";
	}

	/**
	 * 入力画面 表示処理
	 * 
	 * @param userForm 入力フォーム
	 * @param model Viewとの値受渡し
	 * @return "client/user/regist_input" 入力画面 表示処理
	 */
	@GetMapping("/client/user/regist/input")
	public String registInput(UserForm userForm, Model model) {

		BindingResult result = (BindingResult) session.getAttribute("result");

		if (result != null) {
			//セッションにエラー情報がある場合、エラー情報をスコープに設定
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			// セッションにエラー情報を削除
			session.removeAttribute("org.springframework.validation.BindingResult.userForm");
		} else {
			userForm = (UserForm) session.getAttribute("userForm");
			model.addAttribute("userForm", userForm);
		}

		return "client/user/regist_input";
	}

	/**
	 * 登録入力確認 処理
	 * 
	 * @param userForm 入力フォーム
	 * @param result 入力値チェックの結果
	 * @return 
	 * 入力値エラーあり："redirect:/client/user/regist/input" 入力登録画面 表示処理
	 * 入力値エラーなし："redirect:/client/user/regist/check" 登録確認画面 表示処理
	 */
	@PostMapping("/client/user/regist/check")
	public String registInputCheck(@Valid @ModelAttribute UserForm userForm, BindingResult result) {

		session.setAttribute("userForm", userForm);

		if (result.hasErrors()) {
			//// 入力値にエラーがあった場合、エラー情報をセッションに保持
			session.setAttribute("result", result);
			session.setAttribute("UserForm", userForm);

			return "redirect:/client/user/regist/input";
		}

		return "redirect:/client/user/regist/check";
	}

	/**
	 * 登録確認画面 表示処理
	 * 
	 * @param model 
	 * @return Viewとの値受渡し
	 */
	@GetMapping("/client/user/regist/check")
	public String registCheck(Model model) {

		UserForm userForm = (UserForm) session.getAttribute("userForm");
		model.addAttribute("userForm", userForm);
		return "/client/user/regist_check";
	}

	/**
	 * 情報登録処理
	 * 
	 * @return "redirect:/client/user/regist/complete" 登録完了画面　表示処理
	 */
	@PostMapping("/client/user/regist/complete")
	public String registComplete(UserForm userForm, Model model) {

		//削除フラグをUserエンティティセット
		userForm = (UserForm) session.getAttribute("userForm");
		User user = new User();
		BeanUtils.copyProperties(userForm, user, "id");
		user.setDeleteFlag(Constant.NOT_DELETED);

		//会員登録日をUserエンティティにセットする
		Date insertDate = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = simpleDateFormat.format(insertDate);
		java.sql.Date insertDate2 = java.sql.Date.valueOf(formattedDate);
		user.setInsertDate(insertDate2);

		//会員登録
		user = userRepository.save(user);
		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(user, userBean);

		//会員登録後、ログイン状態にする
		session.removeAttribute("userForm");
		session.setAttribute("user", userBean);

		return "redirect:/client/user/regist/complete";
	}

	/**
	 * 登録完了画面　表示処理
	 * 
	 * @return "client/user/regist_complete" 登録完了画面　表示
	 */
	@GetMapping("/client/user/regist/complete")
	public String RegistCompleteFinish(UserForm userForm) {

		return "client/user/regist_complete";
	}
}