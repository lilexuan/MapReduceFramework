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
        String fileDir = "demo1"; // **由用户配置**
        int nWorker = 8; // **由用户配置**
        int nReduce = 10; // **由用户配置**

//        将日志打印到文件中
//        PrintStream ps = new PrintStream(new FileOutputStream("log.txt"));
//        System.setOut(ps);
        String[] fileList = new File(fileDir).list();
        assert fileList != null;
        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = String.valueOf(Paths.get(fileDir, fileList[i]));
        }
        new MasterRunner().run(fileList, nReduce);
        new WorkerRunner().run(UserMR_deemo1::mapf, UserMR_deemo1::reducef, nWorker); // **由用户配置**
        System.out.println("exit");
        System.exit(0);
    }
}
