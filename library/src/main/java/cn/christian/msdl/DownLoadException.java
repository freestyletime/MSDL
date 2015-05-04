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

    private static DownLoadException e;

    public int eCode = 0;
    public int statusCode = 0;

    public DownLoadException(int eCode){
        super(code2message(eCode));
        this.eCode = eCode;
        e = this;
    }

    public DownLoadException(String msg){
        super(msg);
    }

    public DownLoadException(Throwable e){
        super(e);
        this.eCode = DownLoadTaskExceptionCode.MSDL_CODE_OTHER;
    }

    public DownLoadException(int eCode, int statusCode){
        this(eCode);
        this.statusCode = statusCode;
    }

    /**
     * This method can switch the errorCode to exception message.
     *
     * @param eCode The code the user received when the download is doing.
     * @return The result of the exception
     *
     * */
    public static String code2message(int eCode){
        switch (eCode){
            case DownLoadTaskExceptionCode.MSDL_CODE_OTHER:
                return e.getMessage();
            case DownLoadTaskExceptionCode.MSDL_CODE_URL_INVALID:
                return "Error: URL is invalid";
            case DownLoadTaskExceptionCode.MSDL_CODE_FILE_DISABLE:
                return "Error: File can not be created";
            case DownLoadTaskExceptionCode.MSDL_CODE_DOWNLOAD_ABORT:
                return "Error: Downloading is abort, because the file is not find";
            case DownLoadTaskExceptionCode.MSDL_CODE_DOWNLOAD_FILE_SIZE_INVALID:
                return "Error: Downloaded file's size goes wrong, please Doanload it again";
            case DownLoadTaskExceptionCode.MSDL_CODE_CONNECTION_FAIL:
                return "Error: Connection failed";
            default:
                return "";
        }
    }
}
