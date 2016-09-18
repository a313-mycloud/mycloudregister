/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */

package org.dlut.mycloudregister.biz;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.usermanage.QueryUserCondition;
import org.dlut.mycloudserver.client.common.usermanage.RoleEnum;
import org.dlut.mycloudserver.client.common.usermanage.UserCreateReqDTO;
import org.dlut.mycloudserver.client.common.usermanage.UserDTO;
import org.dlut.mycloudserver.client.service.usermanage.IUserManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author xuyizhen
 */
@Service
public class UserBiz {

    private static Logger      log = LoggerFactory.getLogger(UserBiz.class);

    @Resource(name = "userManageService")
    private IUserManageService userManageService;

    /**
     * @param account
     * @return
     */
    public UserDTO getUserByAccount(String account) {
        MyCloudResult<UserDTO> result = this.userManageService.getUserByAccount(account);
        if (!result.isSuccess()) {
            log.warn("调用userManageService.getUserByAccount()出错");
            return null;
        }
        return result.getModel();
    }

    /**
     * 创建新用户，如果账号存在，则返回false
     * 
     * @param account
     * @param password
     * @param roleEnum
     * @return
     */
    public boolean createUser(UserCreateReqDTO userCreateReqDTO) {
        MyCloudResult<Boolean> result = this.userManageService.createUser(userCreateReqDTO);
        if (!result.isSuccess()) {
            log.warn("调用userManageService.createUser()出错");
            return false;
        }
        return result.getModel();
    }
}
