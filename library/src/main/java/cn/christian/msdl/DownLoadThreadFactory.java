package cn.christian.msdl;


import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @time 14-9-17 下午3:47
 * @describtion Internal threadFactory
 */
class DownLoadThreadFactory implements ThreadFactory {

    private AtomicInteger index = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "#MSDL Thread_#" + index.getAndIncrement());
        t.setPriority(Thread.MAX_PRIORITY);
        return t;
    }
}