package mycloudregister;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.dlut.mycloudregister.dal.dataobject.RegisterUser;
import org.dlut.mycloudregister.services.RegisterUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
  
@RunWith(SpringJUnit4ClassRunner.class)     //表示继承了SpringJUnit4ClassRunner类  
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})  
  
public class TestMyBatis { 
    private static Logger logger = Logger.getLogger(TestMyBatis.class);  
//  private ApplicationContext ac = null;  
    @Resource (name = "registerUserService")
    private RegisterUserService rus;
  
//  @Before  
//  public void before() {  
//      ac = new ClassPathXmlApplicationContext("applicationContext.xml");  
//      userService = (IUserService) ac.getBean("userService");  
//  }  
  
    @Test  
    
    public void test1() {  
    	RegisterUser user = new RegisterUser();
    	user.setAccount("test");
    	user.setUsername("test");
    	user.setPassword("pwd");
    	user.setRole(1);
    	System.out.println(rus.createRegUser(user).getModel());
        // System.out.println(user.getUserName());  
        // logger.info("值："+user.getUserName());  
        logger.info(JSON.toJSONString(user));  
    }  
}  
