package org.dlut.mycloudregister.services.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.dlut.mycloudregister.dal.dataobject.RegisterUser;
import org.dlut.mycloudregister.dal.mapper.RegisterUserMapper;
import org.dlut.mycloudregister.services.RegisterUserService;
import org.dlut.mycloudserver.client.common.ErrorEnum;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.springframework.stereotype.Service;

@Service("registerUserService")
public class RegisterUserServiceImpl implements RegisterUserService {

	@Resource
	private RegisterUserMapper registerUserMapper;

	public MyCloudResult<Boolean> createRegUser(RegisterUser user) {

		if (user == null) {
			return MyCloudResult.successResult(Boolean.TRUE);
		}
		if (registerUserMapper.createRegUser(user) != 0) {
			return MyCloudResult.successResult(Boolean.TRUE);
		}
		return MyCloudResult.successResult(Boolean.FALSE);
	}
	
	public MyCloudResult<RegisterUser> getRegUserByAccount(String account){
		RegisterUser registerUser=registerUserMapper.getRegUserByAcount(account);
		if(registerUser!=null)
			return MyCloudResult.successResult(registerUser);
		return MyCloudResult.failedResult(ErrorEnum.USER_NOT_EXIST);
		
			
	}

}
