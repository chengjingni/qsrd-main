package com.vdata.cloud.admin.rpc;

import com.vdata.cloud.admin.entity.User;
import com.vdata.cloud.admin.rpc.service.PermissionService;
import com.vdata.cloud.auth.common.util.jwt.IJWTInfo;
import com.vdata.cloud.auth.util.ShiroUtils;
import com.vdata.cloud.client.jwt.UserAuthUtil;
import com.vdata.cloud.common.context.BaseContextHandler;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.common.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;


@Slf4j
public class BaseUserController {
    @Autowired
    protected HttpServletRequest request;

    /*   @Autowired
       private IUserService userService;*/
    @Autowired
    private UserAuthUtil userAuthUtil;

    @Autowired
    private PermissionService permissionService;


    public UserVO getUserByToken(String token) throws Exception {
        IJWTInfo ijwtInfo = userAuthUtil.getInfoFromToken(token);
        return new UserVO(ijwtInfo.getUniqueName(), ijwtInfo.getId(), ijwtInfo.getName());
    }


    public UserVO getUser() {
        User userEntity = ShiroUtils.getUserEntity();

        String nickname = null;
        String username = null;
        String userId = null;
        if (userEntity != null) {
            nickname = userEntity.getNickname();
            username = userEntity.getUsername();
            userId = userEntity.getId().toString();
        } else {
            nickname = CommonUtil.isEmpty(BaseContextHandler.getName()) ? "未知" : BaseContextHandler.getName();
            username = CommonUtil.isEmpty(BaseContextHandler.getUsername()) ? "未知" : BaseContextHandler.getUsername();
            userId = CommonUtil.isEmpty(BaseContextHandler.getUserID()) ? "-1" : BaseContextHandler.getUserID();
        }


        UserVO userVO = new UserVO(username, userId, nickname);
        return userVO;
    }


    public String userName() {
        setUser();
        String username = BaseContextHandler.getUsername();

        return username == null ? "admin" : username;
    }


    public String nickName() {
        setUser();
        String nickName = BaseContextHandler.getName();

        return nickName == null ? "未知" : nickName;
    }


    private void setUser() {
        try {
            if (CommonUtil.isEmpty(BaseContextHandler.getUserID()) || CommonUtil.isEmpty(BaseContextHandler.getUsername())) {
                Cookie[] cookies = request.getCookies();
                if (CommonUtil.isNotEmpty(cookies)) {
                    Optional<Cookie> first = Arrays.stream(cookies).filter(cookie -> "Admin-Token".equals(cookie.getName())).findFirst();
                    if (first.isPresent()) {
                        String token = first.get().getValue();
                        UserVO user = getUserByToken(token);
                        BaseContextHandler.setUsername(user.getUsername());
                        BaseContextHandler.setUserID(user.getUserId());
                        BaseContextHandler.setName(user.getName());
                    }
                }
            }
        } catch (Exception e) {
            log.error("用户获取失败", e);
        }
    }

    public Integer userId() {
        setUser();
        String userId = BaseContextHandler.getUserID();
        if (CommonUtil.isNotEmpty(userId)) {
            return Integer.parseInt(userId);
        } else {
            return null;
        }
    }

}
