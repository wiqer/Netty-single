package test.java.io.github.wiqer;

import net.openhft.affinity.Affinity;
import net.openhft.affinity.AffinityLock;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AffinityTestMain {
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");

    public AffinityTestMain() {
    }

    public static void main(@NotNull String[] args) {
        int cpus =AffinityLock.cpuLayout().cpus();
        System.out.println(cpus);
        if (args.length == 0) {
            cpus = cpus/ 2;
        } else {
            cpus = Integer.valueOf(args[0]);
        }

        for(int i = 0; i < cpus; ++i) {
            acquireAndDoWork();
        }

    }

    private static void acquireAndDoWork() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                AffinityLock al = Affinity.acquireLock();
                Throwable var2 = null;

                try {
                    String threadName = Thread.currentThread().getName();
                    System.out.println("Thread (" + threadName + ") locked onto cpu " + al.cpuId());

                    while(true) {
                        System.out.println(df.format(new Date()) + " - Thread (" + threadName + ") doing work on cpu " + al.cpuId() + ". IsAllocated = " + al.isAllocated() + ", isBound = " + al.isBound() + ". " + al.toString());

                        try {
                            Thread.sleep(10000L);
                        } catch (InterruptedException var12) {
                        }
                    }
                } catch (Throwable var13) {
                    var2 = var13;
                    throw var13;
                } finally {
                    if (al != null) {
                        if (var2 != null) {
                            try {
                                al.close();
                            } catch (Throwable var11) {
                                var2.addSuppressed(var11);
                            }
                        } else {
                            al.close();
                        }
                    }

                }
            }
        });
        t.start();
    }
}
