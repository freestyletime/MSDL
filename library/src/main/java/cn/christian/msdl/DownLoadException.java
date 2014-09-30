package cn.christian.msdl;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @time 14-9-16 下午1:16
 * @describtion A exception occur when the download is doing.
 */
public class DownLoadException extends RuntimeException {

    public DownLoadException(int errorCode){
        super(code2message(errorCode));
    }

    public DownLoadException(String msg){
        super(msg);
    }

    public DownLoadException(Throwable e){
        super(e);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }

    /**
     * This method can switch the errorCode to exception message.
     *
     * @param errorCode The code the user received when the download is doing.
     * @return The result of the exception
     *
     * */
    public static String code2message(int errorCode){
        switch (errorCode){
//            case 1000:
//                return "Error: the object you injected must have a method like void fun(String id, DownLoadTaskStatus status, long length, long process, int errorCode)";
            case 1001:
                return "Error: SDcard is not unmount";
            case 1002:
                return "Error: URL is invalid";
            case 1003:
                return "Error: File can not be created, please check the base path you send";
            case 1004:
                return "Error: IOException, check out your current network environmnet";
            case 1005:
                return "Error: Downloading is abort, because the file is not find";
            case 1006:
                return "Error: Downloaded file's size goes wrong, please Doanload it again";
            default:
                return "Error: Connection failed, the http statuscode is "+ errorCode;
        }
    }
}
