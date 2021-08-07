import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import mr.KeyValue;
import mr.Mapper;
import mr.Reducer;
import task.TaskInfo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Worker {
    private RMIable master;

    public void run(Mapper mapper, Reducer reducer) {
        try {
            // 连接到服务器
            Registry registry = LocateRegistry.getRegistry("localhost", 9999);
            master = (RMIable) registry.lookup("master");
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                TaskInfo taskInfo = callAskTask();
                switch (taskInfo.state) {
                    case TASK_MAP:
                        workerMap(mapper, taskInfo);
                        break;
                    case TASK_REDUCE:
                        workerReduce(reducer, taskInfo);
                        break;
                    case TASK_WAIT:
                        TimeUnit.SECONDS.sleep(1);
                        break;
                    case TASK_END:
                        System.out.println("All task completed. Nothing to do.");
                        return;
                    default:
                        throw new Exception("Invalid Task state from master.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void workerReduce(Reducer reducer, TaskInfo taskInfo) throws IOException {
        System.out.printf("Got assigned reduce task on part %d\n", taskInfo.partIndex);

        // 读取所有分区的文件的同一个分区, 并放入一个列表当中
        List<KeyValue> intermediate = new ArrayList<>();
        int nFiles = taskInfo.nFiles;
        for (int index = 0; index < nFiles; index++) {
            BufferedReader br = new BufferedReader(new FileReader(String.valueOf(Paths.get("mr-tmp", "mr-" + index + "-" + taskInfo.partIndex))));
            String jsonString;
            JSONObject jsonObject;
            while ((jsonString = br.readLine()) != null) {
                jsonObject = JSON.parseObject(jsonString);
                intermediate.add(new KeyValue(jsonObject.getString("key"), jsonObject.getString("value")));
            }
            br.close();
        }

        // 按照键值的字典序进行排序
        intermediate.sort(Comparator.comparing(kv -> kv.key));

        // 将键值相同的聚合到一起, 然后调用reducer.reducef, 最后将结果写到分区文件里面
        BufferedWriter bw = new BufferedWriter(new FileWriter("mr-out-" + taskInfo.partIndex));
        int i = 0;
        while (i < intermediate.size()) {
            int j = i + 1;
            while (j < intermediate.size() && intermediate.get(j).key.equals(intermediate.get(i).key)) {
                j++;
            }
            List<String> values = new ArrayList<>();
            for (int k = i; k < j; k++) {
                values.add(intermediate.get(k).value);
            }
            KeyValue kvOut = reducer.reducef(intermediate.get(i).key, values);
            bw.write(kvOut.key + "\t" + kvOut.value);
            bw.newLine();
            i = j;
        }
        bw.close();

        // 告知master, 任务完成
        callTaskDone(taskInfo);
    }

    private void workerMap(Mapper mapper, TaskInfo taskInfo) throws IOException, ClassNotFoundException {
        System.out.printf("Got assigned map task on %dth file %s\n", taskInfo.fileIndex, taskInfo.fileName);
        String content = new String(Files.readAllBytes(Paths.get(taskInfo.fileName)));
        List<KeyValue> intermediate = mapper.mapf(taskInfo.fileName, content);

        // 准备输出到文件
        int nReduce = taskInfo.nReduce;
        BufferedWriter[] bws = new BufferedWriter[nReduce];
        for (int outIndex = 0; outIndex < nReduce; outIndex++) {
            bws[outIndex] = new BufferedWriter(new FileWriter(String.valueOf(Paths.get("mr-tmp", "mr-" + taskInfo.fileIndex + "-" + outIndex))));
        }

        // 每一个kv对输出为一行json字符串
        for (KeyValue kv : intermediate) {
            int outIndex = Math.abs(kv.key.hashCode()) % nReduce;
            bws[outIndex].write(JSON.toJSONString(kv));
            bws[outIndex].newLine();
        }

        // 关闭流
        for (int outIndex = 0; outIndex < nReduce; outIndex++) {
            bws[outIndex].close();
        }

        // 告知Master, 任务完成
        callTaskDone(taskInfo);
    }

    private void callTaskDone(TaskInfo taskInfo) throws RemoteException {
        master.taskDone(taskInfo);
    }

    private TaskInfo callAskTask() throws RemoteException {
            return master.askTask(new TaskInfo());
    }
}
