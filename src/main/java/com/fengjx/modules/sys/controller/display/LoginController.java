
package com.fengjx.modules.sys.controller.display;

import com.fengjx.commons.system.security.shiro.session.SessionDAO;
import com.fengjx.commons.utils.CookieUtils;
import com.fengjx.commons.utils.LogUtil;
import com.fengjx.commons.utils.WebUtil;
import com.fengjx.commons.web.MyExecuteCallback;
import com.fengjx.modules.common.constants.AppConfig;
import com.fengjx.modules.common.controller.MyController;
import com.fengjx.modules.sys.entity.SysUserEntity;
import com.fengjx.modules.sys.model.SysUser;
import com.fengjx.modules.sys.security.FormAuthenticationFilter;
import com.fengjx.modules.sys.security.SystemAuthorizingRealm;
import com.fengjx.modules.sys.utils.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户登录注册
 *
 * @author fengjx.
 * @date：2015/5/16 0016
 */
@Controller
public class LoginController extends MyController {

    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private SessionDAO sessionDAO;

    @Autowired
    private SysUser sysUser;


    /**
     * 登录的逻辑已经交给shiro，登录错误后会跳到这里
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public String signin(HttpServletRequest request) {
        SystemAuthorizingRealm.Principal principal = UserUtil.getPrincipal();
        // 如果已经登录，则跳转到管理首页
        if (principal != null) {
            return retSuccess();
        } else {
            String exception = (String) request
                    .getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
            String message = (String) request.getAttribute(AppConfig.REQUEST_ERROE_MSG_KEY);
            if (StringUtils.isBlank(message) || StringUtils.equals(message, "null")) {
                message = "用户或密码错误, 请重试.";
            }
            if (LOG.isDebugEnabled()) {
                LogUtil.debug(LOG,
                        "login fail, active session size: {}, message: {}, exception: {}",
                        sessionDAO.getActiveSessions(false).size(), message, exception);
            }
            return retFail(message);
        }
        // // 测试环境忽略掉验证码校验
        // if (!AppConfig.isTest()) {
        // Map<String, String> res = compareValidCode(request, valid_code);
        // if ("0".equals(res.get("code"))) {
        // return res;
        // }
        // }
        // SysUserEntity loginUser = sysUser.signin(user.getUsername(),
        // user.getPwd());
        // LogUtil.debug(LOG, "查询到登陆用户：" + loginUser);
        // if (null == loginUser) {
        // return retFail("用户名或密码错误！");
        // }
        // if (SysUserEntity.NOT_ALIVE.equals(loginUser.getIs_valid())) {
        // return retFail("账号未激活，请查激活邮件！");
        // }
        // if (SysUserEntity.FREEZE_ALIVE.equals(loginUser.getIs_valid())) {
        // return retFail("账号已经锁定，不允许登录！");
        // }
        // request.getSession().setAttribute(AppConfig.LOGIN_FLAG, loginUser);
        // return retSuccess();
    }

    @RequestMapping(value = "/toLogin", method = RequestMethod.GET)
    public String toLogin() {
        return "sys/display/login";
    }

    private static final String LAST_URI = "last_uri";

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void login(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CookieUtils.setCookie(response, LAST_URI,
                WebUtil.getUriWidthParam(request).replace(request.getContextPath(), ""), 60 * 60);
        // 如果是ajax请求
        if (WebUtil.validAjax(request)) {
            request.getRequestDispatcher("/common/loginTimeoutAjax").forward(request, response);
        } else {
            request.getRequestDispatcher("/common/loginTimeout").forward(request, response);
        }
    }

    @RequestMapping(value = "/loginout")
    public String loginOut(final HttpServletRequest request) {
        SysUserEntity sysUser = (SysUserEntity) request.getSession().getAttribute(
                AppConfig.LOGIN_FLAG);
        if (null != sysUser) {
            request.getSession().removeAttribute(AppConfig.LOGIN_FLAG);
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/validEmail")
    @ResponseBody
    public String validEmail(String email) {
        boolean flag = sysUser.validEmail(email);
        return flag + "";
    }

    @RequestMapping(value = "/validUser")
    @ResponseBody
    public String validUser(String username) {
        boolean flag = sysUser.validUsername(username);
        return flag + "";
    }

    /**
     * 注册页面跳转
     *
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerView() {
        return "sys/display/register";
    }

    /**
     * 注册数据提交
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public String register(final HttpServletRequest request) {
        MyExecuteCallback callback = new MyExecuteCallback() {
            @Override
            public void execute() throws Exception {
                sysUser.register(getRecord(SysUser.class, request));
            }
        };
        return doResult(callback, "注册用户失败！");
    }

    /**
     * 注册用户激活
     *
     * @param ser
     * @return
     */
    @RequestMapping(value = "/activate")
    public String activate(String ser) {
        if (sysUser.activate(ser)) {
            return "sys/display/activate-ok";
        }
        return "sys/display/activate-error";
    }

}
