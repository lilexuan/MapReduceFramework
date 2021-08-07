import task.MapTaskStat;
import task.TaskStat;
import task.TaskStatQueue;

import java.io.File;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class MasterRunner {
    public Master run(String[] files, int nReduce) {
        // 创建map任务
        Deque<TaskStat> mapArray = new LinkedList<>();
        for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
            // 因为map任务不需要partIndex，所以随便赋值为0
            mapArray.offerLast(new MapTaskStat(files[fileIndex], fileIndex, 0, nReduce, files.length));
        }

        // 创建缓存文件夹
        File dir = new File("mr-tmp");
        if (dir.isDirectory()) {
            deleteDirFile("mr-tmp");
        }
        dir.mkdirs();

        Master master = new Master(files, nReduce);
        master.mapTaskWaiting = new TaskStatQueue(mapArray);

        // 向rmi注册master
        try {
            RMIable skeleton = (RMIable) UnicastRemoteObject.exportObject(master, 0);
            Registry registry = LocateRegistry.createRegistry(9999);
            registry.rebind("master", skeleton);
            System.out.println("master started.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // 启动一个线程来收集过期的任务
        new Thread(() -> {
            System.out.println("start to collect out-of-time tasks.");
            collectOutOfTime(master);
        }).start();

        return master;
    }

    private void collectOutOfTime(Master master) {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Deque<TaskStat> timesOutTask;
            if (master.reduceTaskRunning != null) {
                timesOutTask = master.reduceTaskRunning.timeOutQueue();
                if (timesOutTask != null && !timesOutTask.isEmpty()) {
                    master.reduceTaskWaiting.moveAppend(timesOutTask);
                }
            }
            if (master.mapTaskRunning != null) {
                timesOutTask = master.mapTaskWaiting.timeOutQueue();
                if (timesOutTask != null && !timesOutTask.isEmpty()) {
                    master.mapTaskWaiting.moveAppend(timesOutTask);
                }
            }
        }
    }

    public void deleteDirFile(String path) {
        File f = new File(path);
        if (f.isDirectory()) {
            String[] files = f.list();
            for (String file : files) {
                deleteDirFile(String.valueOf(Paths.get(path, file)));
            }
        }
        f.delete();
    }

    public static void main(String[] args) {
        MasterRunner masterRunner = new MasterRunner();
        String fileDir = "deemo2"; // 由用户配置
        String[] fileList = new File(fileDir).list();
        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = String.valueOf(Paths.get(fileDir, fileList[i]));
        }
        masterRunner.run(fileList, 10);
    }
}
