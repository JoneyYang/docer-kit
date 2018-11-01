package me.joney.plugin.coderkit.genemybatis.bean;

/**
 *
 * @author yang.qiang
 * @date 2018/11/01
 */
public class MapperResult {

    private String column;
    private String property;

    public MapperResult(String column, String property) {
        this.column = column;
        this.property = property;
    }

    @Override
    public String toString() {
        return property;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
