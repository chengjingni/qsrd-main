package com.vdata.cloud.auth.config;

import com.vdata.cloud.admin.biz.UserBiz;
import com.vdata.cloud.admin.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Author: qiuwei@19pay.com.cn
 * @Version 1.0.0
 */
@Slf4j
@Component
public class ShiroRealm extends AuthorizingRealm {


    @Autowired
    private UserBiz userBiz;


    /**
     * 授权
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        log.info("开始执行授权操作.......");

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        /**
         * 查询用户角色
         * 如果身份认证的时候没有传入User对象，这里只能取到userName
         * 也就是SimpleAuthenticationInfo构造的时候第一个参数传递需要User对象
         */
        User user = (User) principalCollection.getPrimaryPrincipal();

        if (user == null) {
            log.error("用户不存在");
            throw new UnknownAccountException("用户不存在");
        }

        //TODO 是否为超级管理员   是  全部菜单权限


        return authorizationInfo;
    }


    /**
     * 认证
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        log.info("开始进行身份认证......");

        //获取用户的输入的账号.
        String username = (String) authenticationToken.getPrincipal();

        //通过username从数据库中查找 User对象.
        //实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
//        User user = userMapper.findByUsername(username);

        User user = userBiz.getUserByUsername(username);
        if (Objects.isNull(user)) {
            return null;
        }

        return new SimpleAuthenticationInfo(
                // 这里传入的是user对象，比对的是用户名，直接传入用户名也没错，但是在授权部分就需要自己重新从数据库里取权限
                user,
                // 密码
                user.getPassword(),
                // salt = username + salt
//                ByteSource.Util.bytes(user.getSalt()),
                // realm name
                getName()
        );
    }


    /**
     * 将自己的验证方式加入容器
     * <p>
     * 凭证匹配器（由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了）
     *
     * @param credentialsMatcher
     */
    @Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();

        /**
         * 散列算法:这里可以使用MD5算法 也可以使用SHA-256
         */
//        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        // 散列的次数，比如散列16次，相当于 md5(md5(""));
//        hashedCredentialsMatcher.setHashIterations(ShiroUtils.hashIterations);
        super.setCredentialsMatcher(new AllowAllCredentialsMatcher());
    }

}
