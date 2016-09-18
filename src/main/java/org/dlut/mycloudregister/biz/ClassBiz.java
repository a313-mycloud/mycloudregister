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
import org.dlut.mycloudserver.client.common.classmanage.ClassDTO;
import org.dlut.mycloudserver.client.common.classmanage.QueryClassCondition;
import org.dlut.mycloudserver.client.common.usermanage.UserDTO;
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;
import org.dlut.mycloudserver.client.service.classmanage.IClassManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 类ClassBiz.java的实现描述：TODO 类实现描述
 * 
 * @author xuyizhen Dec 11, 2014 4:27:41 PM
 */
@Service
public class ClassBiz {
    private static Logger       log = LoggerFactory.getLogger(ClassBiz.class);

    @Resource(name = "classManageService")
    private IClassManageService classManageService;

    /**
     * 根据id获取课程信息
     * 
     * @param classId
     * @return
     */
    public ClassDTO getClassById(int classId) {
        MyCloudResult<ClassDTO> result = this.classManageService.getClassById(classId);
        if (!result.isSuccess()) {
            log.warn("调用classManageService.getClassById(" + classId + ")出错，" + result.getMsgCode() + ":"
                    + result.getMsgInfo());
            return null;
        }
        return result.getModel();
    }

    /**
     * 根据条件查询课程列表
     * 
     * @param queryClassCondition
     * @return
     */
    public Pagination<ClassDTO> query(QueryClassCondition queryClassCondition) {
        MyCloudResult<Pagination<ClassDTO>> result = this.classManageService.query(queryClassCondition);

        if (!result.isSuccess()) {
            log.warn("调用classManageService.query()出错，" + result.getMsgCode() + ":" + result.getMsgInfo());
            return null;
        }
        return result.getModel();
    }

    /**
     * 将一个学生添加到一门课程中
     * 
     * @param studentAccount
     * @param classId
     * @return
     */
    public boolean addStudentInOneClass(String studentAccount, int classId) {
        MyCloudResult<Boolean> result = this.classManageService.addStudentInOneClass(studentAccount, classId);
        if (!result.isSuccess()) {
            log.warn("调用classManageService.addStudentInOneClass()出错，" + result.getMsgCode() + ":" + result.getMsgInfo());
            return false;
        }
        return result.getModel();
    }

    /**
     * 获取某个课程下面的所有模板虚拟机
     * 
     * @param classId
     * @param offset
     * @param limit
     * @return
     */
    public Pagination<VmDTO> getTemplateVmsInOneClass(int classId, int offset, int limit) {
        MyCloudResult<Pagination<VmDTO>> result = this.classManageService.getTemplateVmsInOneClass(classId, offset,
                limit);

        if (!result.isSuccess()) {
            log.warn("调用classManageService.getTemplateVmsInOneClass()出错，" + result.getMsgCode() + ":"
                    + result.getMsgInfo());
            return null;
        }
        return result.getModel();
    }

}
