package org.dlut.mycloudregister.dal.mapper;

import org.dlut.mycloudregister.dal.dataobject.RegisterUser;




public interface RegisterUserMapper {

    int createRegUser(RegisterUser record);

    RegisterUser getRegUserByAcount(String account);

}