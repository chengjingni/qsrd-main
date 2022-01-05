package com.vdata.cloud.auth.controller;

import com.vdata.cloud.admin.rpc.BaseController;
import com.vdata.cloud.auth.biz.ClientBiz;
import com.vdata.cloud.auth.entity.Client;
import com.vdata.cloud.auth.entity.ClientService;
import com.vdata.cloud.common.msg.GroupRestResponse;
import com.vdata.cloud.common.msg.ObjectRestResponse;
import org.springframework.web.bind.annotation.*;

/**
 * @author ace
 * @create 2017/12/26.
 */
@RestController
@RequestMapping("service")
public class ServiceController extends BaseController<ClientBiz, Client> {

    @RequestMapping(value = "/{id}/client", method = RequestMethod.PUT)
    @ResponseBody
    public GroupRestResponse modifyUsers(@PathVariable int id, String clients) {
        baseBiz.modifyClientServices(id, clients);
        return new GroupRestResponse().rel(true);
    }

    @RequestMapping(value = "/{id}/client", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<ClientService> getUsers(@PathVariable int id) {
        return new GroupRestResponse<ClientService>().rel(true).data(baseBiz.getClientServices(id));
    }

}
