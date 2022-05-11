package com.right.redis_plugin.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

public class RedisConsole {
    private Integer id;
    private String name;
    private String content;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RedisConsole))
            return false;
        RedisConsole that = (RedisConsole) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getContent(), that
                .getContent());
    }

    @Override public int hashCode() {
        return Objects.hash(getId(), getName(), getContent());
    }
}
