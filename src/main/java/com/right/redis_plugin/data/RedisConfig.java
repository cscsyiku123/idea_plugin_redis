package com.right.redis_plugin.data;

import lombok.Data;
import lombok.experimental.Accessors;
import redis.clients.jedis.Jedis;

import java.util.Objects;

public class RedisConfig {
    private Integer id;
    private String name;
    private String ip;
    private String port;
    private String password;
    private RedisConsole redisConsole;
    private Jedis jRedis;

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RedisConfig))
            return false;
        RedisConfig that = (RedisConfig) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getIp(), that
                .getIp()) && Objects.equals(getPort(), that.getPort()) && Objects.equals(getPassword(), that.getPassword()) && Objects
                       .equals(getRedisConsole(), that.getRedisConsole()) && Objects.equals(jRedis, that.jRedis);
    }

    @Override public int hashCode() {
        return Objects.hash(getId(), getName(), getIp(), getPort(), getPassword(), getRedisConsole(), jRedis);
    }

    public static void main(String[] args){
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RedisConsole getRedisConsole() {
        return redisConsole;
    }

    public void setRedisConsole(RedisConsole redisConsole) {
        this.redisConsole = redisConsole;
    }

    public Jedis getJRedis() {
        return jRedis;
    }

    public void setJRedis(Jedis jRedis) {
        this.jRedis = jRedis;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override public String toString() {
        return name;
    }
}
