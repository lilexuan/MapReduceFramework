import user.UserMR;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

public class Driver {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InterruptedException, FileNotFoundException {
        String fileDir = UserMR.fileDir;
        int nWorker = UserMR.nWorker;
        int nReduce = UserMR.nReduce;

//        将日志打印到文件中
//        PrintStream ps = new PrintStream(new FileOutputStream("log.txt"));
//        System.setOut(ps);
        String[] fileList = new File(fileDir).list();
        assert fileList != null;
        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = String.valueOf(Paths.get(fileDir, fileList[i]));
        }
        new MasterRunner().run(fileList, nReduce);
        new WorkerRunner().run(UserMR::mapf, UserMR::reducef, nWorker);
        System.out.println("exit");
        System.exit(0);
    }
}
