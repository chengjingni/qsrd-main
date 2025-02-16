package com.vdata.cloud.auth.config;

/**
 * @Author: qiuwei@19pay.com.cn
 * @Version 1.0.0
 */
/*@Configuration
@Slf4j
@Data
@ConfigurationProperties(
        prefix = "spring.redis"
)*/
public class ShiroConfig {

   /* private String host;
    private int port;
    private Duration timeout;


    *//**
     * Filter工厂，设置对应的过滤条件和跳转条件
     *
     * @return ShiroFilterFactoryBean
     *//*
    @Bean
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {


        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 过滤器链定义映射
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        *//*
     * anon:所有url都都可以匿名访问，authc:所有url都必须认证通过才可以访问;
     * 过滤链定义，从上向下顺序执行，authc 应放在 anon 下面
     * *//*
        filterChainDefinitionMap.put("/auth/login", "anon");
        filterChainDefinitionMap.put("/auth/getPlatformAuthorization", "anon");
        // 配置不会被拦截的链接 顺序判断，如果前端模板采用了thymeleaf，这里不能直接使用 ("/static/**", "anon")来配置匿名访问，必须配置到每个静态目录
//        filterChainDefinitionMap.put("/css/**", "anon");
//        filterChainDefinitionMap.put("/fonts/**", "anon");
//        filterChainDefinitionMap.put("/img/**", "anon");
//        filterChainDefinitionMap.put("/js/**", "anon");
//        filterChainDefinitionMap.put("/html/**", "anon");
        // 所有url都必须认证通过才可以访问
        filterChainDefinitionMap.put("/**", "authc");

        // 配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了, 位置放在 anon、authc下面
        filterChainDefinitionMap.put("/auth/logout", "logout");

        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        // 配器shirot认登录累面地址，前后端分离中登录累面跳转应由前端路由控制，后台仅返回json数据, 对应LoginController中unauth请求
        shiroFilterFactoryBean.setLoginUrl("/auth/un_auth");

        // 登录成功后要跳转的链接, 此项目是前后端分离，故此行注释掉，登录成功之后返回用户基本信息及token给前端
        // shiroFilterFactoryBean.setSuccessUrl("/index");

        // 未授权界面, 对应LoginController中 unauthorized 请求
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }


    *//**
     * RedisSessionDAO shiro sessionDao层的实现 通过redis, 使用的是shiro-redis开源插件
     *
     * @return RedisSessionDAO
     *//*
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        redisSessionDAO.setSessionIdGenerator(sessionIdGenerator());
        redisSessionDAO.setExpire(1800);
        return redisSessionDAO;
    }

    *//**
     * Session ID 生成器
     *
     * @return JavaUuidSessionIdGenerator
     *//*
    @Bean
    public JavaUuidSessionIdGenerator sessionIdGenerator() {
        return new JavaUuidSessionIdGenerator();
    }

    *//**
     * 自定义sessionManager
     *
     * @return SessionManager
     *//*
    @Bean
    public SessionManager sessionManager() {
        MySessionManager mySessionManager = new MySessionManager();
        mySessionManager.setSessionDAO(redisSessionDAO());
        return mySessionManager;
    }

    *//**
     * 配置shiro redisManager, 使用的是shiro-redis开源插件
     *
     * @return RedisManager
     *//*
    private RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host + ":" + port);
//        redisManager.setPort(port);
        redisManager.setTimeout(2000);
        return redisManager;
    }

    *//**
     * cacheManager 缓存 redis实现, 使用的是shiro-redis开源插件
     *
     * @return RedisCacheManager
     *//*
    @Bean
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        // 必须要设置主键名称，shiro-redis 插件用过这个缓存用户信息
        redisCacheManager.setPrincipalIdFieldName("id");
        return redisCacheManager;
    }


    *//**
     * 权限管理，配置主要是Realm的管理认证
     *
     * @return SecurityManager
     *//*
    @Bean
    public SecurityManager securityManager(ShiroRealm shiroRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm);
        // 自定义session管理 使用redis
        securityManager.setSessionManager(sessionManager());
        // 自定义缓存实现 使用redis
        securityManager.setCacheManager(cacheManager());
        return securityManager;
    }


    *//*
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     *//*
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }


    @Bean
    public SimpleCookie cookie() {
        // cookie的name,对应的默认是 JSESSIONID
        SimpleCookie cookie = new SimpleCookie("SHARE_JSESSIONID");
        cookie.setHttpOnly(true);
        //  path为 / 用于多个系统共享 JSESSIONID
        cookie.setPath("/");
        return cookie;
    }

    *//* 此项目使用 shiro 场景为前后端分离项目，这里先注释掉，统一异常处理已在 GlobalExceptionHand.java 中实现 */

}
