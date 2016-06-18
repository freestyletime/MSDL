package cn.christian.msdl;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @Time: 2015/11/2 9:39
 * @describtion The task request method.
 */
public enum DownLoadTaskMethod {
    GET("GET"), POST("POST");

    private String value;

    DownLoadTaskMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
