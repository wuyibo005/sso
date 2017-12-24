package win.scolia.sso.service.Impl;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import win.scolia.sso.bean.entity.Role;
import win.scolia.sso.bean.entity.UserSafely;
import win.scolia.sso.dao.PermissionMapper;
import win.scolia.sso.dao.RoleMapper;
import win.scolia.sso.service.RoleService;
import win.scolia.sso.service.UserService;
import win.scolia.sso.util.CacheUtils;
import win.scolia.sso.util.PageUtils;

import java.util.List;
import java.util.Set;


@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private CacheUtils cacheUtils;

    @Autowired
    private PageUtils pageUtils;

    @Override
    public void createRole(String roleName) {
        roleMapper.insertRole(roleName);
    }

    @Override
    public void addRoleToUser(String userName, String roleName) {
        UserSafely user = userService.getUserSafelyByUserName(userName);
        if (user == null) {
            throw new IllegalArgumentException("User not exist");
        }
        Role role = this.getRoleByRoleName(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Role not exist");
        }
        roleMapper.insertUserRoleMap(user.getUserId(), role.getRoleId());
        cacheUtils.clearUserRoles(userName);
    }

    @Transactional
    @Override
    public void removeRole(String roleName) {
        Role role = this.getRoleByRoleName(roleName);
        if (role == null) {
            return;
        }
        roleMapper.deleteRoleByName(roleName);
        roleMapper.deleteUserRoleMapByRoleId(role.getRoleId()); // 删除 用户-角色 的映射
        permissionMapper.deleteRolePermissionMapByRoleId(role.getRoleId()); // 删除 角色-权限 的映射
        cacheUtils.clearRole(roleName); // 清除 角色 缓存
        cacheUtils.clearAllUserRoles(); // 清除所有的 用户-角色 缓存
        cacheUtils.clearRolePermissions(roleName); // 清除对应的 角色-权限 缓存
    }

    @Override
    public void romeUserRole(String userName, String roleName) {
        UserSafely user = userService.getUserSafelyByUserName(userName);
        if (user == null) {
            throw new IllegalArgumentException("User not exist");
        }
        Role role = this.getRoleByRoleName(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Role not exist");
        }
        roleMapper.deleteUserRoleMapByUserIdAndRoleId(user.getUserId(), role.getRoleId()); // 删除 用户-角色 的映射
        cacheUtils.clearUserRoles(userName); // 清除对应的 用户-角色 缓存
    }

    @Override
    public void changeRoleName(String oldRoleName, String newRoleName) {
        roleMapper.updateRoleByName(oldRoleName, newRoleName);
        cacheUtils.clearRole(oldRoleName);
        cacheUtils.clearAllUserRoles(); // 清除所有的 用户-角色 缓存
        cacheUtils.clearRolePermissions(oldRoleName); // 清除对应的 角色-权限 缓存
    }

    @Override
    public Set<String> getUserRolesByUserName(String userName) {
        Set<String> roles = cacheUtils.getUserRoles(userName);
        if (roles == null) {
            roles = roleMapper.selectUserRolesByUserName(userName);
            cacheUtils.cacheUserRoles(userName, roles);
        }
        return roles;
    }

    @Override
    public Role getRoleByRoleName(String roleName) {
        Role role = cacheUtils.getRole(roleName);
        if (role == null) {
            role = roleMapper.selectRoleByRoleName(roleName);
            cacheUtils.cacheRole(role);
        }
        return role;
    }

    @Override
    public PageInfo<Role> listRoles(Integer pageNum) {
        pageUtils.startPage(pageNum);
        List<Role> roles = roleMapper.selectAllRoles();
        return pageUtils.getPageInfo(roles);
    }


}
