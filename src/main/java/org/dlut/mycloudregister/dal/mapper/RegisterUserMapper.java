package org.dlut.mycloudregister.dal.mapper;

import org.dlut.mycloudregister.dal.dataobject.RegisterUser;


public interface RegisterUserMapper {
    int delRegUserByAccount(String account);

    int createRegUser(RegisterUser record);

    RegisterUser getRegUserByAcount(String account);

    int updateRegUserByAccountSelective(RegisterUser record);

    int updateRegUserByAccount(RegisterUser record);
}