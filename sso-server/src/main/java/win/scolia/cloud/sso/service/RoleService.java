package win.scolia.cloud.sso.service;

import com.github.pagehelper.PageInfo;
import win.scolia.cloud.sso.bean.entity.Role;

import java.util.Set;

public interface RoleService {

    /**
     * 创建一个角色
     * @param roleName 角色名
     */
    void createRole(String roleName);

    /**
     * 为用户添加角色
     * @param userName 用户名
     * @param roleName 角色名
     */
    void addRoleToUser(String userName, String roleName);

    /**
     * 删除一个角色, 同时将其在映射表中的记录也删除掉
     * @param roleName 角色名
     */
    void removeRole(String roleName);

    /**
     * 为用户移除一个角色
     * @param userName 用户名
     * @param roleName 角色名
     */
    void romeUserRole(String userName, String roleName);

    /**
     * 更新角色名称
     * @param current 旧角色名
     * @param target 新角色名
     */
    void changeRoleName(String current, String target);

    /**
     * 根据用户名获取用户的角色
     * @param username 用户名
     * @return 返回角色列表
     */
    Set<String> getUserRolesByUserName(String username);

    /**
     * 根据角色名获取角色
     * @param roleName 角色名
     * @return 角色对象或null
     */
    Role getRoleByRoleName(String roleName);

    /**
     * 获取所有的角色信息
     * @param pageNum 页码
     * @return 角色列表
     */
    PageInfo<Role> listRoles(Integer pageNum);
}
