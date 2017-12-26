package win.scolia.sso.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import win.scolia.sso.bean.entity.User;
import win.scolia.sso.bean.entity.UserSafely;
import win.scolia.sso.bean.vo.entry.UserEntryVO;
import win.scolia.sso.bean.vo.export.UserExportVO;
import win.scolia.sso.bean.vo.export.MessageExportVO;
import win.scolia.sso.exception.DuplicateUserException;
import win.scolia.sso.service.PermissionService;
import win.scolia.sso.service.RoleService;
import win.scolia.sso.service.UserService;
import win.scolia.sso.util.MessageUtils;
import win.scolia.sso.util.ShiroUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 主要负责登录注册等功能
 */
@Controller
@RequestMapping(value = "account")
public class AccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    /**
     * 用户注册
     *
     * @param userEntryVO   用户的信息
     * @param bindingResult 数据校验的结果
     * @return 201 表示注册成功, 400 参数错误
     */
    @PostMapping("register")
    public ResponseEntity<MessageExportVO> register(@Validated(UserEntryVO.Register.class) UserEntryVO userEntryVO,
                                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            MessageExportVO messageExportVO = MessageUtils.makeValidMessage(bindingResult);
            return ResponseEntity.badRequest().body(messageExportVO);
        }
        try {
            userService.createUser(userEntryVO);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Register user: {}", userEntryVO.getUserName());
            }
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DuplicateUserException e) {
            MessageExportVO vo = new MessageExportVO();
            MessageUtils.putMessage(vo, "error", "该用户已被占用");
            return ResponseEntity.badRequest().body(vo);
        }
    }

    /**
     * 注册时, 检查用户名是否可用
     *
     * @param userName 用户名
     * @return 200 可用, 409 不可用, 400 参数错误
     */
    @PostMapping("register/check")
    public ResponseEntity<Void> checkRepeatUserName(@RequestParam String userName) {
        if (userService.checkUserNameUsable(userName)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Check Repeat user: {}", userName);
            }
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * 使用用户名和密码登录
     *
     * @param userEntryVO   用户信息
     * @param bindingResult 数据校验的结果
     * @return 200 登录成功, 400 参数错误/登录失败
     */
    @PostMapping("login")
    public ResponseEntity<MessageExportVO> login(@Validated(UserEntryVO.LoginByPassword.class) UserEntryVO userEntryVO,
                                                 @RequestParam boolean rememberMe, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            MessageExportVO messageExportVO = MessageUtils.makeValidMessage(bindingResult);
            return ResponseEntity.badRequest().body(messageExportVO);
        }
        try {
            Subject subject = SecurityUtils.getSubject();
            AuthenticationToken token = new UsernamePasswordToken(userEntryVO.getUserName(), userEntryVO.getPassword(), rememberMe);
            subject.login(token);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Login user: {}", userEntryVO.getUserName());
            }
            return ResponseEntity.ok().build();
        } catch (AuthenticationException e) {
            MessageExportVO vo = new MessageExportVO();
            MessageUtils.putMessage(vo, "error", "用户名或密码错误");
            return ResponseEntity.badRequest().body(vo);
        }
    }

    /**
     * 登出
     *
     * @return 200 成功 401 未登录
     */
    @GetMapping("logout")
    @RequiresUser
    public ResponseEntity<Void> logout() {
        Subject subject = SecurityUtils.getSubject();
        if (LOGGER.isInfoEnabled()) {
            User user = (User) subject.getPrincipal();
            LOGGER.info("User logout: {}", user.getUserName());
        }
        subject.logout();
        return ResponseEntity.ok().build();
    }

    /**
     * 获取当前用户信息
     *
     * @return 200 成功, 401 未登录
     */
    @GetMapping("current")
    @RequiresUser
    public ResponseEntity<UserExportVO> current() {
        User user = ShiroUtils.getCurrentUser();
        UserSafely userSafely = new UserSafely();
        BeanUtils.copyProperties(user, userSafely);
        Set<String> roles = roleService.getUserRolesByUserName(userSafely.getUserName());
        Set<String> permissions = new HashSet<>();
        for (String roleName : roles) {
            permissions.addAll(permissionService.getPermissionsByRoleName(roleName));
        }
        UserExportVO vo = new UserExportVO(userSafely, roles, permissions);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("User get current info: {}", user.getUserName());
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 修改密码
     *
     * @param userEntryVO   用户信息
     * @param bindingResult 数据校验的结果
     * @return 200 表示成功, 400 参数错误/旧密码错误 401 未登录
     */
    @PutMapping("current/password")
    @RequiresUser
    public ResponseEntity<MessageExportVO> changePassword(@Validated(UserEntryVO.ChangePassword.class) UserEntryVO userEntryVO,
                                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            MessageExportVO messageExportVO = MessageUtils.makeValidMessage(bindingResult);
            return ResponseEntity.badRequest().body(messageExportVO);
        }
        boolean success = userService.changePasswordByOldPassword(userEntryVO.getUserName(), userEntryVO.getOldPassword(),
                userEntryVO.getNewPassword());
        if (success) {
            this.logout();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Change user password: {}", userEntryVO.getUserName());
            }
            return ResponseEntity.ok().build();
        } else {
            MessageExportVO vo = new MessageExportVO();
            MessageUtils.putMessage(vo, "error", "用户名或密码错误");
            return ResponseEntity.badRequest().body(vo);
        }
    }
}
