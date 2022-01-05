package com.vdata.cloud.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vdata.cloud.admin.biz.ElementBiz;
import com.vdata.cloud.admin.biz.UserBiz;
import com.vdata.cloud.admin.entity.Element;
import com.vdata.cloud.admin.rpc.BaseController;
import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.msg.TableResultResponse;
import com.vdata.cloud.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-23 20:30
 */
@Controller
@RequestMapping("element")
public class ElementController extends BaseController<ElementBiz, Element> {
    @Autowired
    private UserBiz userBiz;

    @Autowired
    private ElementBiz elementBiz;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<Element> page(@RequestParam(defaultValue = "10") int limit,
                                             @RequestParam(defaultValue = "1") int offset,
                                             String name, @RequestParam(defaultValue = "0") String menuId) {
        try {
            LambdaQueryWrapper<Element> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Element::getMenuId, menuId);
            if (CommonUtil.isNotEmpty(name)) {
                wrapper.like(Element::getName, name);

            }
            List<Element> elements = elementBiz.list(wrapper);
            return new TableResultResponse<Element>(elements.size(), elements).setMessages("查询成功").setCode(Constants.RETURN_NORMAL);
        } catch (Exception e) {
            return new TableResultResponse<Element>().setMessages("查询失败").setCode(Constants.RETURN_UNNORMAL);

        }
    }

   /* @RequestMapping(value = "/user", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<Element> getAuthorityElement(String menuId) {
        try {

            int userId = userBiz.getUserByUsername(userName()).getId();
            List<Element> elements = elementBiz.getAuthorityElementByUserId(userId + "", menuId);
            return new ObjectRestResponse<List<Element>>().data(elements).setMessages("查询成功").setCode(Constants.RETURN_NORMAL);
        } catch (Exception e) {
            return new ObjectRestResponse<List<Element>>().setMessages("查询失败").setCode(Constants.RETURN_UNNORMAL);

        }

    }

    @RequestMapping(value = "/user/menu", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<Element> getAuthorityElement() {
        try {
            int userId = userBiz.getUserByUsername(userName()).getId();
            List<Element> elements = elementBiz.getAuthorityElementByUserId(userId + "");
            return new ObjectRestResponse<List<Element>>().data(elements);
        } catch (Exception e) {
            return new ObjectRestResponse<List<Element>>().setMessages("查询失败").setCode(Constants.RETURN_UNNORMAL);

        }
    }*/

}
