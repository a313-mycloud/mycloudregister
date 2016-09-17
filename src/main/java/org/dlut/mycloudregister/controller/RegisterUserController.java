package org.dlut.mycloudregister.controller;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.dlut.mycloudmanage.common.utils.MyJsonUtils;
import org.dlut.mycloudregister.common.constant.Constant;
import org.dlut.mycloudregister.dal.dataobject.RegisterUser;
import org.dlut.mycloudregister.services.RegisterUserService;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.classmanage.ClassDTO;
import org.dlut.mycloudserver.client.common.usermanage.RoleEnum;
import org.dlut.mycloudserver.client.common.usermanage.UserCreateReqDTO;
import org.dlut.mycloudserver.client.common.usermanage.UserDTO;
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;
import org.dlut.mycloudserver.client.service.classmanage.IClassManageService;
import org.dlut.mycloudserver.client.service.usermanage.IUserManageService;
import org.dlut.mycloudserver.client.service.vmmanage.IVmManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/")
public class RegisterUserController {

	private static Logger log = LoggerFactory
			.getLogger(RegisterUserController.class);

	@Resource(name = "userManageService")
	private IUserManageService userManageService;

	@Resource(name = "classManageService")
	private IClassManageService classManageService;

	@Resource(name = "registerUserService")
	private RegisterUserService registerUserService;

	@Resource(name = "vmManageService")
	private IVmManageService vmManageService;
	
	@RequestMapping(value="/register")
	public String registerForm(){
		return "index.jsp";
	}

	@RequestMapping(value = "/register.do", produces = { "application/json;charset=UTF-8" })
	public String register(@ModelAttribute("user") RegisterUser regUser) {
		JSONObject json = new JSONObject();
		MyCloudResult<UserDTO> result = userManageService
				.getUserByAccount(regUser.getAccount().trim());
		if (result.getModel() != null) {
			return MyJsonUtils.getFailJsonString(json, "该账号已经被注册！");
		}
		regUser.setRole(RoleEnum.STUDENT.getStatus());
		// 写入注册数据表
		MyCloudResult<Boolean> result2 = registerUserService
				.createRegUser(regUser);
		if (!result2.isSuccess()) {
			return MyJsonUtils.getFailJsonString(json, "注册失败！");
		}
		// //写入用户数据表
		// MyCloudResult<Boolean> result3 = addRegUserToUser(regUser);
		// if(!result3.isSuccess()){
		// return MyJsonUtils.getFailJsonString(json, "注册失败！");
		// }
		// 为新建用户添加Vm,
		json = addVmForRegUser(regUser);
		return json.toString();
	}

	// private MyCloudResult<Boolean> addRegUserToUser(RegisterUser regUser){
	// UserCreateReqDTO reqDTO = new UserCreateReqDTO();
	// return userManageService.createUser(reqDTO);
	// }

	private JSONObject addVmForRegUser(RegisterUser regUser) {
		int classId = Constant.REGISTER_CLASS_ID;
		String account = regUser.getAccount();
		String username = regUser.getUsername();
		JSONObject json = new JSONObject();
		ClassDTO classDTO = classManageService.getClassById(classId).getModel();

		/** 以下应该使用事物 ****/
		// 创建学生
		UserCreateReqDTO userCreateReqDTO = new UserCreateReqDTO();
		userCreateReqDTO.setAccount(account.trim());
		userCreateReqDTO.setUserName(regUser.getUsername().trim());
		userCreateReqDTO.setPassword(regUser.getPassword().trim());
		userCreateReqDTO.setRole(RoleEnum.STUDENT);
		if (!userManageService.createUser(userCreateReqDTO).isSuccess()) {
			log.error("创建学生用户" + account + "失败");
			json.put("message", "创建学生用户" + account + "失败");
			json.put("isSuccess", false);
			return json;
		}
		log.info("创建学生用户" + account + "--" + regUser.getUsername() + "成功");
		// 为该学生添加对应课程的虚拟机
		Pagination<VmDTO> pagination = this.classManageService
				.getTemplateVmsInOneClass(classId, 0, 1000).getModel();
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
						+ this.classManageService.getClassById(classId)
								.getModel().getClassName());
				if (StringUtils.isBlank(this.vmManageService.cloneVm(destVmDTO,
						srcVmDTO.getVmUuid()).getModel())) {
					log.error(account + "关联" + srcVmDTO.getVmName() + "模板虚拟机失败");
					json.put("message", account + "关联" + srcVmDTO.getVmName()
							+ "模板虚拟机失败");
					json.put("isSuccess", false);
					return json;
				}
			}
		}
		// 将学生与课程相互关联
		if (!this.classManageService.addStudentInOneClass(account, classId)
				.getModel()) {
			log.error("添加学生--" + account + "--" + username + "--到课程《"
					+ classDTO.getClassName() + "》失败");
			json.put("message", "添加学生" + account + "--" + username + "到课程《"
					+ classDTO.getClassName() + "》失败");
			json.put("isSuccess", false);
			return json;
		}
		log.info("添加学生" + account + "--" + username + "到课程《"
				+ classDTO.getClassName() + "》成功");
		json.put("isSuccess", true);
		return json;
		/** 以上应该使用事物 ****/
	}
}
