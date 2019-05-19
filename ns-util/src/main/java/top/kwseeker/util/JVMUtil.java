package top.kwseeker.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class JVMUtil {

    public static void jvmStackDump(FileOutputStream stream) {
        Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
        Iterator<Map.Entry<Thread, StackTraceElement[]>> ite = map.entrySet().iterator();
        try {
            while (ite.hasNext()) {
                Map.Entry<Thread, StackTraceElement[]> entry = ite.next();
                StackTraceElement[] elements = entry.getValue();
                if (elements != null && elements.length > 0) {
                    String threadName = entry.getKey().getName();
                    stream.write(("Thread Name :[" + threadName + "]\n").getBytes());
                    for (StackTraceElement el : elements) {
                        String stack = el.toString() + "\n";
                        stream.write(stack.getBytes());
                    }
                    stream.write("\n".getBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}