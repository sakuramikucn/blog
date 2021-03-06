package cn.sakuramiku.lightblog.controller;

import cn.hutool.core.util.StrUtil;
import cn.sakuramiku.lightblog.common.Result;
import cn.sakuramiku.lightblog.common.exception.ApiException;
import cn.sakuramiku.lightblog.common.util.*;
import cn.sakuramiku.lightblog.entity.User;
import cn.sakuramiku.lightblog.exception.BusinessException;
import cn.sakuramiku.lightblog.service.CommonService;
import cn.sakuramiku.lightblog.service.UserService;
import cn.sakuramiku.lightblog.util.Constant;
import cn.sakuramiku.lightblog.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 首页相关的方法集
 *
 * @author lyy
 */
@Api(tags = "页面方法集")
@CrossOrigin("*")
@RestController
public class IndexController {

    @Resource
    private UserService userService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private HttpServletRequest request;
    @Resource
    private CommonService commonService;

    @PostMapping("/login")
    @ApiOperation("登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", dataTypeClass = String.class, value = "用户名"),
            @ApiImplicitParam(name = "password", dataTypeClass = String.class, value = "登录密码"),
    })
    public Result<String> login(String username, String password) throws ApiException, BusinessException {
        ValidateUtil.isEmpty(username, "用户名为空");
        ValidateUtil.isEmpty(password, "登录密码为空");
        String ipAddr = WebUtil.getIpAddr(request);
        password = SecurityUtil.md5(password);
        User user = userService.login(username, password,ipAddr);
        if (null != user) {
            String token = JwtUtil.genToken(user);
            // 用于Token刷新
            redisUtil.set(Constant.PREFIX_REFRESH_TOKEN_REFRESH + username, token, 30 * 60L);
            return RespResult.ok(token);
        }
        return RespResult.fail("用户名或密码错误");
    }

    @RequiresRoles("register")
    @PostMapping("/register")
    @ApiOperation("注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", dataTypeClass = String.class, value = "用户名"),
            @ApiImplicitParam(name = "password", dataTypeClass = String.class, value = "登录密码")
    })
    public Result<String> register(String username, String password) throws ApiException {
        ValidateUtil.isEmpty(username, "用户名为空");
        ValidateUtil.isEmpty(password, "登录密码为空");
        User user = userService.getUser(username);
        if (null != user) {
            return RespResult.fail("用户名重复");
        }
        User user1 = userService.register(username, password);
        if (null == user1) {
            return RespResult.fail("注册失败");
        }
        return RespResult.ok();
    }

    @RequiresAuthentication
    @ApiOperation("注销")
    @GetMapping("/logout")
    public Result<Object> logout() {
        String token = (String) SecurityUtils.getSubject().getPrincipal();
        Claims claims = JwtUtil.getClaims(token);
        String id = claims.getId();
        Date expiration = claims.getExpiration();
        // 黑名单
        redisUtil.set(Constant.PREFIX_REFRESH_TOKEN_BAN + id, token, expiration.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        return RespResult.ok();
    }

    @GetMapping("/check/{username}")
    @ApiOperation("检查用户名是否可用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", dataTypeClass = String.class, value = "用户名", required = true)
    })
    public Result<Boolean> check(@PathVariable("username") String username) {
        User user = userService.getUser(username);
        return RespResult.ok(null == user);
    }

    @PostMapping("/views/increment/{id}")
    public RespResult<Long> incrementViews(@PathVariable("id") Long id) throws ApiException {
        ValidateUtil.isNull(id,"参数id为空");
        Long increment = redisUtil.increment(Constant.PREFIX_ARTICLE_VIEWS + id);
        return RespResult.ok(increment);
    }

    @GetMapping("/views/{id}")
    public RespResult<Long> views(@PathVariable("id") Long id) throws ApiException {
        ValidateUtil.isNull(id,"参数id为空");
        Object o = redisUtil.get(Constant.PREFIX_ARTICLE_VIEWS + id);
        long increment = 0L;
        if (o != null){
            increment = Long.parseLong(o.toString());
        }
        return RespResult.ok(increment);
    }

    @RequiresAuthentication
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        String url = commonService.upload(new BufferedInputStream(inputStream), file.getContentType());
        if (StrUtil.isBlank(url)){
            return RespResult.fail("上传失败");
        }
        return RespResult.ok(url);
    }
}
