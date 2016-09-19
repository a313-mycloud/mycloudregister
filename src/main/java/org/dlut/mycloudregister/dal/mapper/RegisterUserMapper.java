package org.dlut.mycloudregister.dal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dlut.mycloudregister.dal.dataobject.RegisterUser;




public interface RegisterUserMapper {

    int createRegUser(RegisterUser record);

    RegisterUser getRegUserByAccount(@Param("account") String account);

}