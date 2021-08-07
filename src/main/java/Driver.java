import mr.KeyValue;
import user.UserMR_deemo1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.List;

public class Driver {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InterruptedException, FileNotFoundException {
        String fileDir = "demo2"; // 由用户配置
        int nWorker = 4; // 由用户配置
        int nReduce = 10; // 由用户配置

//        PrintStream ps = new PrintStream(new FileOutputStream("log.txt"));
//        System.setOut(ps);
        String[] fileList = new File(fileDir).list();
        assert fileList != null;
        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = String.valueOf(Paths.get(fileDir, fileList[i]));
        }
        new MasterRunner().run(fileList, nReduce);
        Thread.sleep(2000);
        new WorkerRunner().run(UserMR_deemo1::mapf, UserMR_deemo1::reducef, nWorker); // 由用户配置
    }
}
