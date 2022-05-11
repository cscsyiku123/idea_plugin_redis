package com.right.redis_plugin.data;

import java.util.Objects;

public class RedisDB {
    private Integer id;
    private String name;

    public RedisDB(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RedisDB))
            return false;
        RedisDB redisDB = (RedisDB) o;
        return Objects.equals(getId(), redisDB.getId()) && Objects.equals(getName(), redisDB.getName());
    }

    @Override public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override public String toString() {
        return "RedisDB{" + "id=" + id + ", name=" + name + '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
