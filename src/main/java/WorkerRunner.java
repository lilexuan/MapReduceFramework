import mr.Mapper;
import mr.Reducer;
import user.UserMR_deemo1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerRunner {
    public void run(Mapper mapper, Reducer reducer, int workerNum) {
        ExecutorService threadPool = Executors.newFixedThreadPool(workerNum);
        for (int i = 0; i < workerNum; i++) {
            threadPool.execute(() -> {
                new Worker().run(mapper, reducer);
                System.out.println("worker run: " + Thread.currentThread().getId());
            });
        }
    }

    public static void main(String[] args) {
        int nWorker = 4;
        new WorkerRunner().run(UserMR_deemo1::mapf, UserMR_deemo1::reducef, nWorker);
    }
}
