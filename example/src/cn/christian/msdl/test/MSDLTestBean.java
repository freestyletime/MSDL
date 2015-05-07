package cn.christian.msdl.test;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl.test
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @Time : 5/6/15 1:47 PM
 * @Description :
 */
public class MSDLTestBean {
    public String id;
    public String url;
    public String name;
    public String path;
    public long length;

    public MSDLTestBean() {
    }

    public MSDLTestBean(String id, String url, String name, String path, long length) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.path = path;
        this.length = length;
    }
}
