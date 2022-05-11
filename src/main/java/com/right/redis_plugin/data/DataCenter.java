package com.right.redis_plugin.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import com.right.redis_plugin.window.RedisConnectionResultWindow;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class DataCenter {

    public static final String PROJECT_PATH = DataCenter.class.getResource("/").getPath();
    public static final String USER_HOME = System.getProperty("user.home");
    public static final String REDIS_CLI_PATH = USER_HOME + "/.redis_cli/redis_config.json";
    private static final AtomicInteger atomicInteger = new AtomicInteger();
    private static final Map<Integer, RedisConfig> map = new HashMap<>();
    private static volatile boolean isRefresh;
    private static volatile int currentId;
    private DataCenter(){

    };

    public static String getProjectPath() {
        return PROJECT_PATH;
    }



    public static String getUserHome() {
        return USER_HOME;
    }



    public static String getRedisCliPath() {
        return REDIS_CLI_PATH;
    }


    public static AtomicInteger getAtomicInteger() {
        return atomicInteger;
    }



    public static Map<Integer, RedisConfig> getMap() {
        return map;
    }


    public static boolean isRefresh() {
        return isRefresh;
    }

    public static void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }

//    public static int getCurrentId() {
//        return currentId;
//    }
//
//    public static void setCurrentId(int currentId) {
//        DataCenter.currentId = currentId;
//    }

    public static void reset() {
        map.clear();
    }

    //初始化，把配置和之前写的命令都加载进来
    public static void init() {
        load();
        atomicInteger.addAndGet(map.keySet().stream().max(Integer::compareTo).orElseGet(()->{return 0;}));
        new Timer("DataCenterConfigRefreshToFile", true).schedule(new TimerTask() {
            @Override public void run() {
                if (isRefresh) {
                    refreshFile();
                    //实现的很撮，刷新期间有改动会捕捉不到变动
                    isRefresh = false;
                }
            }
        }, 3000, 3000);
    }

    //刷新下 redis 链接
    public static void refresh() {
        //todo_c
    }

    public  static Integer getInt() {
        return atomicInteger.getAndAdd(1);
    }

    public  static void load() {
        File file = new File(REDIS_CLI_PATH);
        if (!file.exists()) {
            createFile(file);
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file));) {
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            List<RedisConfig> redisConfigs = JSON.parseArray(sb.toString(), RedisConfig.class);
            for (RedisConfig redisConfig : redisConfigs) {
                map.put(redisConfig.getId(), redisConfig);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  static  void refreshFile() {
        //这里用普通的output，懒得用mappfile
        File file = new File(REDIS_CLI_PATH);
        if (!file.exists()) {
            createFile(file);
        }
        try (PrintWriter printWriter = new PrintWriter(file);) {
            String[] excludeProperties = {"jRedis"};
            PropertyPreFilters filters = new PropertyPreFilters();
            PropertyPreFilters.MySimplePropertyPreFilter excludefilter = filters.addFilter();
            excludefilter.addExcludes(excludeProperties);
            String s = JSONObject.toJSONString(map.values(), excludefilter);
            printWriter.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  static void createFile(File file) {
        if (!file.getParentFile().exists()|| !file.getParentFile().isDirectory()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

    }

    public  static void addConfig(RedisConfig redisConfig) throws BeansException {
        if (redisConfig == null) {
            return;
        }
        //实现的很撮，擦，怎么这么多实现很撮？
        RedisConfig r = map.get(redisConfig.getId());
        if (r==null){
            redisConfig.setId(getInt());
            map.put(redisConfig.getId(),redisConfig);
        }else{
            BeanUtils.copyProperties(redisConfig, r);
        }
        isRefresh = true;
    }

    public static  void addConfig(RedisConsole redisConsole) {
        if (redisConsole==null){
            return;
        }
        isRefresh = true;
        //实现的很撮，擦，怎么这么多实现很撮？
        RedisConfig r = map.get(redisConsole.getId());
        BeanUtils.copyProperties(redisConsole, r.getRedisConsole());
        isRefresh = true;
    }
    public static  void changeConsole(Integer id,String content){
        RedisConfig redisConfig = get(id);
        if (redisConfig != null) {
            redisConfig.getRedisConsole().setContent(content);
            isRefresh = true;
        }
    }

    public  static boolean connection(Integer id){
        try {
            RedisConfig redisConfig = map.get(id);
            Jedis jRedis = redisConfig.getJRedis();
            String ping="";
            if (jRedis==null || !"PONG".equals(jRedis.ping())){
                jRedis=new Jedis(redisConfig.getIp(),Integer.valueOf(redisConfig.getPort()),5000);
                if (StringUtils.isNotBlank(redisConfig.getPassword())){
                    String auth = jRedis.auth(redisConfig.getPassword());
                }
            }
            if ("PONG".equals(jRedis.ping())){
                redisConfig.setJRedis(jRedis);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public  static void  sendCommand(Integer id,String command)  {
        if (!connection(id)) {
            RedisConnectionResultWindow.onShow("connction failed");
        }
        List<String> split = Arrays.asList(command.split("\\s+"));
        try {
            Object eval = execRedisCommand(map.get(id).getJRedis(),split.get(0),(String[]) split.subList(1,split.size()).toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回数据
    }

    public  static RedisConfig get(Integer id ){
        RedisConfig redisConfig = map.get(id);
//        if (redisConfig==null){
//            throw new RuntimeException("unknow exception");
//        }
        return redisConfig;

    }
    public static List<String> execRedisCommand(Jedis jedis, String command, String... args) throws InvocationTargetException, IllegalAccessException {
        Protocol.Command cmd = Protocol.Command.valueOf(command.toUpperCase());
        Client client = jedis.getClient();
        Method method = MethodUtils.getMatchingAccessibleMethod(Client.class, "sendCommand", Protocol.Command.class, String[].class);
        method.setAccessible(true);
        method.invoke(client, cmd, args);
        try {
            List<String> respList = new ArrayList<>();
            Object response = client.getOne();
            if (response instanceof List) {
                for (Object itemResp : ((List) response)) {
                    respList.add(new String((byte[]) itemResp));
                }
                return respList;
            } else {
                return Collections.singletonList(new String((byte[]) response));
            }

        } catch (JedisException e) {
            return Collections.singletonList(e.getMessage());
        }
    }

    public  static RedisConfig get(RedisConfig config ){
        if (config == null) {
            return null;
        }
        return get(config.getId());
    }


    public  static RedisConfig remove(Integer id ){
        RedisConfig redisConfig = map.remove(id);
        doUpdateListen();
        return redisConfig;

    }

    public  static RedisConfig remove(RedisConfig config ){
        if (config == null) {
            return null;
        }
        return remove(config.getId());
    }

    public  static void doUpdateListen() {
        isRefresh=true;
    }
}
