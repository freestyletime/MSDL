package cn.christian.msdl;


import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @time 14-9-17 下午5:23
 * @describtion Some tools method about download
 */
class DownLoadUtils {

    private static Lock lock = new ReentrantLock();

    /**
     * Find a method of object like void fun(String, DownLoadTaskStatus, long, long, int).
     *
     * @param obj The object you want to find.
     * @return User's method
     */
    public static Method inject(Object obj) {
        lock.lock();
        try {
            Method[] methods = obj.getClass().getDeclaredMethods();
            MSDL_0:
            for (Method method : methods) {
                Class<?> clazz = method.getReturnType();
                if (!(clazz == Void.TYPE)) continue MSDL_0;

                Annotation[] annotations = method.getDeclaredAnnotations();
                if (annotations != null && annotations.length > 0) {
                    MSDL_1:
                    for (Annotation annotation : annotations) {
                        clazz = annotation.annotationType();
                        if (clazz == DownLoadCallback.class) {
                            method.setAccessible(true);
                            Class<?>[] clazzs = method.getParameterTypes();
                            MSDL_2:
                            for (int index = 0; index < clazzs.length; index++) {
                                clazz = clazzs[index];
                                switch (index) {
                                    case 0:
                                        if (clazz == DownLoadUserTask.class)
                                            continue MSDL_2;
                                        else
                                            continue MSDL_1;
                                    default:
                                        continue MSDL_1;
                                }
                            }
                            return method;
                        } else continue MSDL_1;
                    }
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Create a unique id by current time and others.
     *
     * @return Unique id
     */
    public static String uniqueId() {
        lock.lock();
        try {
            StringBuilder sb = new StringBuilder(UUID.randomUUID().toString());
            sb.append("-" + Calendar.getInstance().getTimeInMillis());

            return sb.toString();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Create a file path base on a url.
     *
     * @param basePath Base directory
     * @param url      Download url
     * @return File path
     */
    public static String makePath(String basePath, String url) {
        return basePath + File.separator + url.substring(url.lastIndexOf("/"));
    }

    /**
     * Create a directory and his parent
     *
     * @param dir base directory
     */
    public static void mkdir(File dir) {
        lock.lock();
        try {
            if (!dir.exists()) {
                mkdir(dir.getParentFile());
                dir.mkdir();
            }
        } finally {
            lock.unlock();
        }
    }
}
