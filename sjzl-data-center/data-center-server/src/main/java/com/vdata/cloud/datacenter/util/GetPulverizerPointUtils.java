package com.vdata.cloud.datacenter.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vdata.cloud.common.exception.BusinessException;
import com.vdata.cloud.datacenter.constants.CommonConstans;
import com.vdata.cloud.datacenter.entity.DcsServerInfo;
import com.vdata.cloud.datacenter.entity.SyncPoint;
import com.vdata.cloud.datacenter.mapper.DcsServerInfoMapper;
import com.vdata.cloud.datacenter.vo.QueryHistoryVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ProjectName: qsrd-main
 * @Package: com.vdata.cloud.datacenter.util
 * @ClassName: GetPulverizerPointUtils
 * @Author: HK
 * @Description: 获取数据工具类
 * @Date: 2021/11/8 13:53
 * @Version: 1.0
 */
@Component
public class GetPulverizerPointUtils {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private DcsServerInfoMapper dcsServerInfoMapper;

    //    private String ip = "10.24.58.36";
    private String ip = "localhost";
    private int port = 8080;
    private String baseUrl = "http://" + ip + ":" + port + "/rtdb/";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setIpAndPort(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.baseUrl = "http://" + ip + ":" + port + "/rtdb/";
        Map<String, String> map = new HashMap<>();
        map.put("ip", ip);
        map.put("port", String.valueOf(port));
        //存储到redis
        redisTemplate.opsForHash().putAll(CommonConstans.DCSSERVERINFO_REDIS, map);
        //设置过期时间
        redisTemplate.expire(CommonConstans.DCSSERVERINFO_REDIS, 30, TimeUnit.SECONDS);
    }


    public void getIpAndPort() {
        //从redis中获取数据
        Object ip = redisTemplate.opsForHash().get(CommonConstans.DCSSERVERINFO_REDIS, "ip");
        Object port = redisTemplate.opsForHash().get(CommonConstans.DCSSERVERINFO_REDIS, "port");

        if (ip == null || port == null) {
            DcsServerInfo dcsServerInfo = dcsServerInfoMapper.selectOne(null);
            setIpAndPort(dcsServerInfo.getIp(), dcsServerInfo.getPort());
        }

        this.ip = ip != null ? ip.toString() : this.ip;
        this.port = port != null ? Integer.valueOf(port.toString()) : this.port;
    }


    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 查询单点实时值
     *
     * @param tagName
     * @return
     */
    public Map<String, Object> getRealTimeData(String tagName) {
        getIpAndPort();
        if (StringUtils.isBlank(tagName)) {
            throw new BusinessException("传入值不能为空");
        }

        String url = baseUrl + "getRTDataByTagName2?tagName=" + tagName;
        Map<String, Object> map = restTemplate.getForObject(url, Map.class);

        String status = map.get("status").toString();
        if (!status.equals("0")) {
            throw new BusinessException("接口访问失败");
        }
        return map;
    }


    /**
     * 查询批量实时值
     *
     * @param tagNames
     * @return
     */
    public List<Map<String, Object>> postRealTimeDatas(List<String> tagNames) {
        getIpAndPort();
        if (tagNames == null && tagNames.size() == 0) {
            throw new BusinessException("没有传入数据");
        }

        Map<String, Object> requestMap = new HashMap<String, Object>();

        requestMap.put("tagNames", tagNames.stream().collect(Collectors.joining(",")));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        String content = JSON.toJSONString(requestMap);
        HttpEntity<String> request = new HttpEntity<>(content, headers);

        String url = baseUrl + "getRTDataByBatch";
//        List<Map<String, Object>> resultList = restTemplate.postForEntity(url, request,List.class);
        ResponseEntity<List> listResponseEntity = restTemplate.postForEntity(url, request, List.class);
        List<Map<String, Object>> resultList = listResponseEntity.getBody();


        if (resultList == null || resultList.size() == 0) {
            throw new BusinessException("接口访问失败 或没有数据");
        }
        return resultList;
    }


    /**
     * 查询批量实时值
     *
     * @param tagNames
     * @return
     */
    public List<Map<String, Object>> getRealTimeDatas(List<String> tagNames) {
        getIpAndPort();
        if (tagNames == null && tagNames.size() == 0) {
            throw new BusinessException("没有传入数据");
        }


        String tagNamesStr = tagNames.stream().collect(Collectors.joining(","));


        String url = baseUrl + "getRTDataByBatch2?tagNames=" + tagNamesStr;
//        List<Map<String, Object>> resultList = restTemplate.postForEntity(url, request,List.class);
        List<Map<String, Object>> list = restTemplate.getForObject(url, List.class);


        if (list == null || list.size() == 0) {
            throw new BusinessException("接口访问失败 或没有数据");
        }
        return list;
    }


    /**
     * 查询历史时间段数据
     *
     * @param queryHistoryVO
     * @return
     */
    public List<Map<String, Object>> postHistoryDatas(QueryHistoryVO queryHistoryVO) {
        getIpAndPort();
        if (StringUtils.isBlank(queryHistoryVO.getTagName())
                || queryHistoryVO.getStTime() > queryHistoryVO.getEdTime()
        ) {
            throw new BusinessException("传入数据有误" + queryHistoryVO.toString());
        }

        String url = baseUrl + "getSnapshotDataByTagName";
        List<Map<String, Object>> resultList = restTemplate.postForObject(url, queryHistoryVO, List.class);

        if (resultList == null || resultList.size() == 0) {
            throw new BusinessException("接口访问失败 或没有数据");
        }
        return resultList;
    }


    /**
     * 查询历史时间段数据
     *
     * @param queryHistoryVO
     * @return
     */
    public List<Map<String, Object>> getHistoryDatas(QueryHistoryVO queryHistoryVO) {
        getIpAndPort();
        if (StringUtils.isBlank(queryHistoryVO.getTagName())
                || queryHistoryVO.getStTime() > queryHistoryVO.getEdTime()
        ) {
            throw new BusinessException("传入数据有误" + queryHistoryVO.toString());
        }

        String url = baseUrl + "getSnapshotDataByTagName2?tagName=" + queryHistoryVO.getTagName()
                + "&stTime=" + queryHistoryVO.getStTime()
                + "&edTime=" + queryHistoryVO.getEdTime()
                + "&interval=" + queryHistoryVO.getInterval();
//        System.out.println("url:" + url);
        List resultList = restTemplate.getForObject(url, List.class);

        if (resultList == null || resultList.size() == 0) {
            throw new BusinessException("接口访问失败 或没有数据");
        }
        return resultList;
    }


    /**
     * 查询所有测点列表
     *
     * @return
     */
    public List<SyncPoint> getTestPointAll() {
        getIpAndPort();

        String url = baseUrl + "getAllTagInfos2";
        List<SyncPoint> resultList = (List<SyncPoint>) restTemplate.getForObject(url, List.class).
                stream().map(obj -> {
            SyncPoint syncPoint = JSONObject.toJavaObject((JSON) JSONObject.toJSON(obj), SyncPoint.class);
            return syncPoint;
        }).collect(Collectors.toList());

        if (resultList == null || resultList.size() == 0) {
            throw new BusinessException("接口访问失败 或没有数据");
        }


        return resultList;
    }


}
