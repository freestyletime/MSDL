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

    /**
     * Exception's error code
     * */
    public int eCode = 0;
    /**
     * Exception's http status code
     * */
    public int statusCode = 0;

    public DownLoadException(int eCode){
        super(code2message(eCode));
        this.eCode = eCode;
    }

    public DownLoadException(String msg){
        super(msg);
        this.eCode = DownLoadTaskExceptionCode.MSDL_CODE_OTHER;
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
    private static String code2message(int eCode){
        if(DownLoadTaskExceptionCode.MSDL_CODE_OTHER == eCode)
            throw new RuntimeException("Can't init DownLoadException in eCode of DownLoadTaskExceptionCode.MSDL_CODE_OTHER");

        switch (eCode){
            case DownLoadTaskExceptionCode.MSDL_CODE_URL_INVALID:
                return "Error: URL is invalid";
            case DownLoadTaskExceptionCode.MSDL_CODE_FILE_DISABLE:
                return "Error: File can not be created";
            case DownLoadTaskExceptionCode.MSDL_CODE_DOWNLOAD_ABORT:
                return "Error: Downloading is abort, because the file is not find";
            case DownLoadTaskExceptionCode.MSDL_CODE_DOWNLOAD_FILE_SIZE_INVALID:
                return "Error: Downloaded file's size goes wrong, please Doanload it again";
            case DownLoadTaskExceptionCode.MSDL_CODE_CONNECTION_FAIL:
                return "Error: Connection failed, please get the http statuscode";
            case DownLoadTaskExceptionCode.MSDL_CODE_DOWNLOAD_CANT_READ_FILE_LEN:
                return "Error: Cannot read the file length from the http";
            default:
                return "";
        }
    }
}
