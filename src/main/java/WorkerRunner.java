import mr.Mapper;
import mr.Reducer;
import user.UserMR_deemo1;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerRunner {
    public void run(Mapper mapper, Reducer reducer, int workerNum) {
        ExecutorService threadPool = Executors.newFixedThreadPool(workerNum);
        CountDownLatch countDownLatch = new CountDownLatch(workerNum);
        for (int i = 0; i < workerNum; i++) {
            threadPool.execute(() -> {
                System.out.println("worker" + Thread.currentThread().getId() + " started");
                new Worker().run(mapper, reducer);
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("All workers shut down");
    }

    public static void main(String[] args) {
        int nWorker = 4;
        new WorkerRunner().run(UserMR_deemo1::mapf, UserMR_deemo1::reducef, nWorker);
    }
}
