package org.dlut.mycloudregister.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.dlut.mycloudregister.biz.ClassBiz;
import org.dlut.mycloudregister.biz.UserBiz;
import org.dlut.mycloudregister.biz.VmBiz;
import org.dlut.mycloudregister.common.Constant;
import org.dlut.mycloudregister.dal.dataobject.RegisterUser;
import org.dlut.mycloudregister.services.RegisterUserService;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.classmanage.ClassDTO;
import org.dlut.mycloudserver.client.common.classmanage.QueryClassCondition;
import org.dlut.mycloudserver.client.common.usermanage.RoleEnum;
import org.dlut.mycloudserver.client.common.usermanage.UserCreateReqDTO;
import org.dlut.mycloudserver.client.common.usermanage.UserDTO;
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RegisterUserController {

	private static Logger log = LoggerFactory
			.getLogger(RegisterUserController.class);

	@Resource(name = "registerUserService")
	private RegisterUserService registerUserService;

	@Resource(name = "classBiz")
	private ClassBiz classBiz;

	@Resource(name = "userBiz")
	private UserBiz userBiz;

	@Resource(name = "vmBiz")
	private VmBiz vmBiz;

	@RequestMapping(value = Constant.REGISTER_URL, method = {
			RequestMethod.POST, RequestMethod.GET })
	public String register(HttpServletRequest request,
			HttpServletResponse response, ModelMap model,
			@ModelAttribute("user") RegisterUser regUser) {
		// 表单默认post方式，get方式就重新登陆
		if (request.getMethod().equals("GET")) {
			return Constant.REGISTER_URL;
		}
		model.put("registerUser", regUser);// 错误返回时的重填
		String errorDesc = check(regUser);
		if (!StringUtils.isBlank(errorDesc)) {
			model.put("errorDesc", errorDesc);
			return Constant.REGISTER_URL;
		}
		UserDTO userDTO = userBiz.getUserByAccount(regUser.getAccount().trim());
		MyCloudResult<RegisterUser> result = this.registerUserService
				.getRegUserByAccount(regUser.getAccount().trim());
		if (userDTO != null || result.isSuccess()) {
			model.put("errorDesc", "账号已注册!");
			return Constant.REGISTER_URL;
		}
		
		

		regUser.setRole(RoleEnum.STUDENT.getStatus());
		// 写入注册数据表
		MyCloudResult<Boolean> result2 = registerUserService
				.createRegUser(regUser);
		if (!result2.isSuccess()) {
			model.put("errorDesc", "写入注册数据表失败!");
			return Constant.REGISTER_URL;
		}
		// 为新建用户添加Vm
		errorDesc = addStudentToClass(regUser);
		if (!StringUtils.isBlank(errorDesc)) {
			model.put("errorDesc", errorDesc);
			return Constant.REGISTER_URL;
		}
		return "redirect:" + Constant.LOGIN_HTML;
	}

	private String check(RegisterUser regUser) {
		if (StringUtils.isBlank(regUser.getAccount())
				|| StringUtils.isBlank(regUser.getUsername())
				|| StringUtils.isBlank(regUser.getPassword())
				|| StringUtils.isBlank(regUser.getEmail())
				|| StringUtils.isBlank(regUser.getTeleNum()))
			return "相关信息不能为空";
		if (regUser.getAccount().length() > 10)
			return "账号不能超過10個字符";
		if (regUser.getPassword().length() > 20)
			return "密碼不能超過20個字符";
		if (!regUser.getTeleNum().matches("^[0-9]{11}$"))
			return "手机号码格式不正确";
		if (!regUser
				.getEmail()
				.matches(
						"^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$"))
			return "邮箱格式不正确";
		return "";
	}

	/**
	 * 把指定账号添加到teacher账户的register课程
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	private String addStudentToClass(RegisterUser regUser) {
		QueryClassCondition queryClassCondition = new QueryClassCondition();
		queryClassCondition.setClassName("register");
		queryClassCondition.setTeacherAccount("teacher");
		;
		Pagination<ClassDTO> result = this.classBiz.query(queryClassCondition);
		if (result.getTotalCount() != 1) {
			log.error("teacher的register课程不存在");
			return "注册失败:teacher的register课程不存在";
		}
		ClassDTO classDTO = result.getList().get(0);
		int classId = classDTO.getClassId();

		String account = regUser.getAccount();
		String username = regUser.getUsername();
		/** 以下应该使用事物 ****/
		// 如果账号不存在，则创建学生
		UserCreateReqDTO userCreateReqDTO = new UserCreateReqDTO();
		userCreateReqDTO.setAccount(regUser.getAccount().trim());
		userCreateReqDTO.setUserName(regUser.getUsername().trim());
		userCreateReqDTO.setPassword(regUser.getPassword().trim());
		userCreateReqDTO.setRole(RoleEnum.STUDENT);
		if (!this.userBiz.createUser(userCreateReqDTO)) {
			log.error("创建学生用户" + account + "失败");
			return "注册失败:创建用户" + account + "失败";
		}
		log.info("创建学生用户" + account + username + "成功");

		// 为该学生添加对应课程的虚拟机
		Pagination<VmDTO> pagination = this.classBiz.getTemplateVmsInOneClass(
				classId, 0, 1000);
		if (pagination != null) {
			List<VmDTO> vmDTOs = pagination.getList();
			for (VmDTO srcVmDTO : vmDTOs) {
				VmDTO destVmDTO = new VmDTO();
				destVmDTO.setVmName(srcVmDTO.getVmName());
				destVmDTO.setVmVcpu(srcVmDTO.getVmVcpu());
				destVmDTO.setVmMemory(srcVmDTO.getVmMemory());
				destVmDTO.setShowType(srcVmDTO.getShowType());
				destVmDTO.setShowPassword(srcVmDTO.getShowPassword());
				destVmDTO.setClassId(classId);
				destVmDTO.setUserAccount(account);
				destVmDTO.setVmNetworkType(srcVmDTO.getVmNetworkType());
				destVmDTO.setIsTemplateVm(false);
				destVmDTO.setIsPublicTemplate(false);
				destVmDTO.setDesc("克隆自"
						+ this.classBiz.getClassById(classId).getClassName());
				if (StringUtils.isBlank(this.vmBiz.cloneVm(destVmDTO,
						srcVmDTO.getVmUuid()))) {
					log.error(account + "关联" + srcVmDTO.getVmName() + "模板虚拟机失败");
				}
			}
		}
		// 将学生与课程相互关联
		if (!this.classBiz.addStudentInOneClass(account, classId)) {
			log.error("添加学生--" + account + "--" + username + "--到课程《"
					+ classDTO.getClassName() + "》失败");
			return "注册失败：添加账户--" + account + "--到课程《" + classDTO.getClassName()
					+ "》失败";
		}
		log.info("添加学生" + account + "--" + username + "到课程《"
				+ classDTO.getClassName() + "》成功");

		return "";
		/** 以上应该使用事物 ****/
	}

}
