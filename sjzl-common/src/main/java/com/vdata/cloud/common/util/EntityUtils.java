package com.vdata.cloud.common.util;

import com.vdata.cloud.common.context.BaseContextHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Date;


/**
 * 实体类相关工具类
 * 解决问题： 1、快速对实体的常驻字段，如：crtUser、crtHost、updUser等值快速注入
 *
 * @author Ace
 * @version 1.0
 * @date 2016年4月18日
 * @since 1.7
 */
@Slf4j
public class EntityUtils {
    /**
     * 快速将bean的crtUser、crtHost、crtTime、updUser、updHost、updTime附上相关值
     *
     * @param entity 实体bean
     * @author 王浩彬
     */
    public static <T> void setCreatAndUpdatInfo(T entity) {
        setCreateInfo(entity);
        setUpdatedInfo(entity);
    }

    /**
     * 快速将bean的crtUser、crtHost、crtTime附上相关值
     *
     * @param entity 实体bean
     * @author 王浩彬
     */
    public static <T> void setCreateInfo(T entity) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String hostIp = "";
            String name = "";
            String id = "";
            if (request != null) {
                hostIp = StringUtils.defaultIfBlank(request.getHeader("userHost"), ClientUtil.getClientIp(request));
                name = StringUtils.trimToEmpty(request.getHeader("userName"));
                name = URLDecoder.decode(name);
                id = StringUtils.trimToEmpty(request.getHeader("userId"));
            }
            if (StringUtils.isBlank(name)) {
                name = BaseContextHandler.getUsername();
            }
            if (StringUtils.isBlank(id)) {
                id = BaseContextHandler.getUserID();
            }

            // 默认属性
            String[] fields = null;
            Object[] value = null;
            if (ReflectionUtils.hasField(entity, "crtTime")) {
                fields = new String[]{"crtName", "crtUser", "crtHost", "crtTime"};
                value = new Object[]{name, CommonUtil.objToInteger(id), hostIp, new Date()};
            } else {
                if (!CommonUtil.isEmpty(name)) {
                    fields = new String[]{"crtStffId", "crtTm"};
                    value = new Object[]{CommonUtil.objToInteger(id), new Date()};
                }

            }
            if (fields != null) {
                // 填充默认属性值
                setDefaultValues(entity, fields, value);
            }
        } catch (Exception e) {
            log.error("创建人设置出错！", e);
        }
    }

    /**
     * 快速将bean的updUser、updHost、updTime附上相关值
     *
     * @param entity 实体bean
     * @author 王浩彬
     */
    public static <T> void setUpdatedInfo(T entity) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String hostIp = "";
            String name = "";
            String id = "";
            if (request != null) {
                hostIp = StringUtils.defaultIfBlank(request.getHeader("userHost"), ClientUtil.getClientIp(request));
                name = StringUtils.trimToEmpty(request.getHeader("userName"));
                name = URLDecoder.decode(name);
                id = StringUtils.trimToEmpty(request.getHeader("userId"));
            }

            if (StringUtils.isBlank(name)) {
                name = BaseContextHandler.getUsername();
            }
            if (StringUtils.isBlank(id)) {
                id = BaseContextHandler.getUserID();
            }
            Integer userId = null;
            if (CommonUtil.isNotEmpty(id)) {
                userId = Integer.parseInt(id);
            }
            // 默认属性
            String[] fields = null;
            Object[] value = null;
            if (ReflectionUtils.hasField(entity, "updTime")) {
                fields = new String[]{"updName", "updUser", "updHost", "updTime"};
                value = new Object[]{name, userId, hostIp, new Date()};
            } else {
                fields = new String[]{"modStffId", "modTm"};
                value = new Object[]{CommonUtil.objToInteger(ReflectionUtils.getFieldValue(entity, "modStffId")), new Date()};
            }
            // 填充默认属性值
            setDefaultValues(entity, fields, value);
        } catch (Exception e) {
            log.error("更新人设置出错！", e);
        }
    }

    /**
     * 依据对象的属性数组和值数组对对象的属性进行赋值
     *
     * @param entity 对象
     * @param fields 属性数组
     * @param value  值数组
     * @author 王浩彬
     */
    private static <T> void setDefaultValues(T entity, String[] fields, Object[] value) {
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            if (ReflectionUtils.hasField(entity, field)) {
                ReflectionUtils.invokeSetter(entity, field, value[i]);
            }
        }
    }

    /**
     * 根据主键属性，判断主键是否值为空
     *
     * @param entity
     * @param field
     * @return 主键为空，则返回false；主键有值，返回true
     * @author 王浩彬
     * @date 2016年4月28日
     */
    public static <T> boolean isPKNotNull(T entity, String field) {
        if (!ReflectionUtils.hasField(entity, field)) {
            return false;
        }
        Object value = ReflectionUtils.getFieldValue(entity, field);
        return value != null && !"".equals(value);
    }
}
