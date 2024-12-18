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
 * 会員管理　会員情報更新機能のコントローラークラス
 * @author Yamazaki
 */
@Controller
public class ClientUserUpdateController {
	
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
	 * @param model  Viewとの値受渡し
	 * @return "redirect:/client/user/update/input"  入力画面 表示処理
	 */
	@PostMapping("/client/user/update/input")
	public String updateInput(Model model) {
		
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		
		if (userForm == null) {
			//セッションスコープに情報がなければログインしている会員情報をuserFormに設定する
			User user = userRepository.getReferenceById(((UserBean) session.getAttribute("user")).getId());
			userForm = new UserForm();
			BeanUtils.copyProperties(user, userForm);
			session.setAttribute("userForm", userForm);
		}

		return "redirect:/client/user/update/input";
	}

	/**
	 * 入力画面 表示処理
	 * 
	 * @param model Viewとの値受渡し
	 * @param userForm 入力フォーム
	 * @return "client/user/update_input" 入力画面 表示処理
	 */
	@GetMapping("/client/user/update/input")
	public String getUpdateInput(Model model, UserForm userForm) {

		userForm = (UserForm) session.getAttribute("userForm");
		model.addAttribute("userForm", userForm);
		BindingResult result = (BindingResult) session.getAttribute("result");

		if (result != null) {
			//セッションにエラー情報がある場合、エラー情報をスコープに設定
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			// セッションにエラー情報を削除
			session.removeAttribute("org.springframework.validation.BindingResult.userForm");
		}

		return "client/user/update_input";
	}

	/**
	 * 変更入力確認 処理
	 * 
	 * @param userForm 入力フォーム
	 * @param result 入力値チェックの結果
	 * @return 
	 * 入力値エラーあり："redirect:/client/user/update/input"　入力変更画面 表示処理
	 * 入力値エラーなし："redirect:/client/user/update/input"　変更確認画面 表示処理
	 */
	@PostMapping("/client/user/update/check")
	public String updateCheck(@Valid @ModelAttribute UserForm userForm, BindingResult result) {

		if (result.hasErrors()) {
			// 入力値にエラーがあった場合、エラー情報をセッションに保持
			session.setAttribute("result", result);
			return "redirect:/client/user/update/input";
		}

		session.setAttribute("userForm", userForm);

		return "redirect:/client/user/update/check";
	}

	/**
	 * 変更確認画面 表示処理
	 * 
	 * @param userForm 入力フォーム
	 * @param model Viewとの値受渡
	 * @return "client/user/update_check" 変更確認画面　表示処理
	 */
	@GetMapping("/client/user/update/check")
	public String getUpdateCheck(UserForm userForm, Model model) {

		userForm = (UserForm) session.getAttribute("userForm");
		model.addAttribute("userForm", userForm);

		return "client/user/update_check";
	}

	/**
	 * 情報変更処理
	 * 
	 * @param userForm 入力フォーム
	 * @return "redirect:/client/user/update/complete" 変更完了画面　表示処理
	 */
	@PostMapping("/client/user/update/complete")
	public String updateComplete(UserForm userForm) {

		//入力フォーム情報をUserエンティティにコピー
		userForm = (UserForm) session.getAttribute("userForm");
		User user = new User();
		BeanUtils.copyProperties(userForm, user);

		//削除フラッグをUserエンティティにセット
		user.setDeleteFlag(Constant.NOT_DELETED);

		//会員登録日をUserエンティティにセット
		Date insertDate = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = simpleDateFormat.format(insertDate);
		java.sql.Date insertDate2 = java.sql.Date.valueOf(formattedDate);
		user.setInsertDate(insertDate2);

		//会員情報更新
		user = userRepository.save(user);
		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(user, userBean);
		
		//セッションに保存されている入力フォーム情報を削除
		session.removeAttribute("userForm");
		session.setAttribute("user", userBean);

		return "redirect:/client/user/update/complete";
	}

	/**
	 * 変更完了画面　表示処理
	 * 
	 * @return "client/user/update_complete" 変更完了画面　表示処理
	 */
	@GetMapping("/client/user/update/complete")
	public String getUpdateComplete() {
		
		return "client/user/update_complete";
	}

}
