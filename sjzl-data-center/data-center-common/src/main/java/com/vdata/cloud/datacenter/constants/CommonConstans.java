package com.vdata.cloud.datacenter.constants;

/**
 *  * @ProjectName:    wru-master
 *  * @Package:        com.vdata.cloud.datacenter.constants
 *  * @ClassName:      CommonConstans
 *  * @Author:         Torry
 *  * @Description:    ${Description}
 *  * @Date:            2020/9/15 14:09
 *  * @Version:    1.0
 *  
 */
public class CommonConstans {

    public final static String ADD = "add";
    public final static String UPDATE = "update";
    public final static String DELETE = "delete";

    public static class AL_STATUS {//API_LOG.STATUS
        public static final String RUNNING = "0";
        public static final String SUCCESS = "1";
        public static final String ALL_FAIL = "2";
        public static final String PART_FAIL = "3";
    }


    //燃煤机点位 redis
    public final static String PULVERIZER_POINT_REDIS = "pulverizer_point";
    //燃煤机点位 redis
    public final static String PULVERIZER_POINT_DCS_REDIS = "pulverizer_point_dcs";
    //燃煤机器持续运行天数  redis
    public final static String PULVERIZER_RUNNING_LIST_REDIS = "pulverizerRunnings";

    //数据字典存储redis
    public final static String BASE_DICT_REDIS = "base_dict";


    //燃煤机运行检测数据  websocket
    public final static String PULVERIZER_WEBSOCKET = "pulverizer";

    //燃煤运行情况  websocket
    public final static String REALTIMEALARM_WEBSOCKET = "realTimeAlarm";

    //燃煤机报警信息 websocket
    public final static String ALARMINFO_WEBSOCKET = "alarmInfo";


    //服务IP以及端口
    public final static String DCSSERVERINFO_REDIS = "dcsServerInfo";


    //点位当前状态
    public final static String POINT_STATUS_REDIS = "point_status";


    public static enum SocketType {
        REALTIMEALARM("运行情况", "1"),
        RUNDATA("运行数据", "2"),
        ALARMINFO("报警处理信息", "3");


        private String name;
        private String value;

        private SocketType(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

}
