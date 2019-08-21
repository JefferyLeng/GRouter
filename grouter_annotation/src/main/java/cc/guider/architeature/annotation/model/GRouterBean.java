package cc.guider.architeature.annotation.model;

import javax.lang.model.element.Element;

/**
 * 每次router跳转的事件对象封装
 * 路由路径Path的封装bean
 * @author JefferyLeng
 * @date 2019-08-01
 */
public class GRouterBean {

    /**
     * grouter注解的作用域 （支持的类型）
     */
    public enum Type {
        ACTIVITY,
        BIZCALL,
    }

    /**
     * 类描述信息 对象
     */
    Element element;

    Class<?> clazz;
    /**
     * 每个访问group
     */
    String group;
    /**
     * 完整的访问路径
     */
    String path;

    Type type;

    private GRouterBean(Builder builder) {
        this.clazz = builder.clazz;
        this.group = builder.group;
        this.path = builder.path;
        this.type =builder.type;
        this.element = builder.element;
    }

    public void setType(Type type) {
        this.type = type;
    }


    public void setGroup(String group) {
        this.group = group;
    }


    public Class<?> getClazz() {
        return clazz;
    }

    public String getGroup() {
        return group;
    }

    public String getPath() {
        return path;
    }

    public Type getType() {
        return type;
    }

    public Element getElement() {
        return element;
    }


    public static class Builder {

        Type type;

        Class<?> clazz;
        /**
         * 每个访问group
         */
        String group;
        /**
         * 完整的访问路径
         */
        String path;

        Element element;

        public Builder setClazz(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder setGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        public Builder setElement(Element element) {
            this.element = element;
            return this;
        }


        public GRouterBean build() {
            return new GRouterBean(this);
        }
    }

    @Override
    public String toString() {
        return "GRouterBean{" +
                "group='" + group + '\'' +
                ", path='" + path + '\'' +
                ", type=" + type +
                '}';
    }
}
