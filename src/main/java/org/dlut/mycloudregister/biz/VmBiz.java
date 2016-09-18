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
import org.dlut.mycloudserver.client.common.vmmanage.QueryVmCondition;
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;
import org.dlut.mycloudserver.client.service.vmmanage.IVmManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 类ClassBiz.java的实现描述：TODO 类实现描述
 * 
 * @author xuyizhen Dec 11, 2014 4:27:41 PM
 */
@Service
public class VmBiz {
    private static Logger    log = LoggerFactory.getLogger(VmBiz.class);

    @Resource(name = "vmManageService")
    private IVmManageService vmManageService;

    /**
     * 克隆虚拟机，必须设置vmName,
     * vmVcpu、vmMemory、userAccount、showType、showPassword，classId,
     * isTemplateVM,isPublicTemplate,vmNetworkType 可选：desc
     * 
     * @param destVmDTO
     * @param srcVmUuid
     * @return
     */
    public String cloneVm(VmDTO destVmDTO, String srcVmUuid) {
        MyCloudResult<String> result = this.vmManageService.cloneVm(destVmDTO, srcVmUuid);
        if (!result.isSuccess()) {
            log.warn("调用vmManageService.cloneVm()出错，" + result.getMsgCode() + ":" + result.getMsgInfo());
            return null;
        }
        return result.getModel();
    }



}
