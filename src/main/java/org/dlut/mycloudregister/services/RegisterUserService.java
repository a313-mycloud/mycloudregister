package org.dlut.mycloudregister.services;

import org.dlut.mycloudregister.dal.dataobject.RegisterUser;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.springframework.stereotype.Service;


@Service
public interface RegisterUserService {

	public MyCloudResult<Boolean> createRegUser(RegisterUser user);
	public MyCloudResult<RegisterUser> getRegUserByAccount(String account);
	
}
