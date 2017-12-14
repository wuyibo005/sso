package win.scolia.sso.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import win.scolia.sso.bean.entity.User;

/**
 * Created by scolia on 2017/11/27
 */
@Repository
public interface UserMapper {

    /**
     * 插入一个新的用户
     *
     * @param user 插入的用户对象
     */
    void insertUser(@Param("user") User user);

    /**
     * 根据用户名获得用户信息
     *
     * @param username 要查询的用户名
     * @return 返回查询得到的用户
     */
    User selectUserByUserName(@Param("userName") String username);
}
