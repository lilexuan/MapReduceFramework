import task.*;

public class Master implements RMIable{
    private String[] filenames;
    public TaskStatQueue reduceTaskWaiting;
    public TaskStatQueue reduceTaskRunning;
    public TaskStatQueue mapTaskWaiting;
    public TaskStatQueue mapTaskRunning;
    private boolean isDone;
    private int nReduce;

    public Master() {
    }

    public Master(String[] filenames, int nReduce, TaskStatQueue mapTaskWaiting) {
        this.filenames = filenames;
        this.nReduce = nReduce;
        this.isDone = false;
        this.reduceTaskWaiting = new TaskStatQueue();
        this.reduceTaskRunning = new TaskStatQueue();
        this.mapTaskRunning = new TaskStatQueue();
        this.mapTaskWaiting = mapTaskWaiting;
    }

    /**
     * 响应worker的请求
     * @param taskInfo
     * @return
     */
    @Override
    public TaskInfo askTask(TaskInfo taskInfo) {
        // 检查是否所有任务已经完成了
        if (this.isDone) {
            taskInfo.state = State.TASK_END;
            return taskInfo;
        }

        // 检查Reduce任务
        ReduceTaskStat reduceTask = (ReduceTaskStat) reduceTaskWaiting.pop();
        if (reduceTask != null) {
            // 存在可用的reduceTask， 记录开始时间
            reduceTask.setNow();
            // 将任务加入运行队列
            reduceTaskRunning.push(reduceTask);
            // 设置返回值
            TaskInfo respTaskInfo = reduceTask.generateTaskInfo();
            System.out.printf("Distributing reduce task on part %d\n", respTaskInfo.partIndex);
            return respTaskInfo;
        }

        // 检查Map任务
        MapTaskStat mapTask = (MapTaskStat) mapTaskWaiting.pop();
        if (mapTask != null) {
            // 存在可用的mapTask， 记录开始时间
            mapTask.setNow();
            // 将任务加入运行队列
            mapTaskRunning.push(mapTask);
            // 设置返回值
            TaskInfo respTaskInfo = mapTask.generateTaskInfo();
            System.out.printf("Distributing map task on %dth file %s\n", respTaskInfo.fileIndex, respTaskInfo.fileName);
            return respTaskInfo;
        }

        // 没有正在等待的map或者reduce任务, 而且有任务正在运行, 那么让worker等待
        if (mapTaskRunning.size() > 0 || reduceTaskRunning.size() > 0) {
            taskInfo.state = State.TASK_WAIT;
            return taskInfo;
        }

        // 所有任务都完成了
        taskInfo.state = State.TASK_END;
        isDone = true;
        return taskInfo;
    }

    /**
     * worker向master反馈任务已经完成了
     * @param taskInfo
     * @return
     */
    @Override
    public TaskInfo taskDone(TaskInfo taskInfo) {
        switch (taskInfo.state) {
            case TASK_MAP:
                System.out.printf("Map task on %dth file %s completed\n", taskInfo.fileIndex, taskInfo.fileName);
                mapTaskRunning.removeTask(taskInfo.fileIndex, taskInfo.partIndex);
                // 如果map任务全部完成了, 那么启动reduce任务
                if (mapTaskRunning.size() == 0 && mapTaskWaiting.size() == 0) {
                    distributeReduce();
                }
                break;
            case TASK_REDUCE:
                System.out.printf("Reduce task on %dth part completed\n", taskInfo.partIndex);
                reduceTaskRunning.removeTask(taskInfo.fileIndex, taskInfo.partIndex);
                break;
            default:
                try {
                    throw new Exception("Task Done error!!!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return taskInfo;
    }

    private void distributeReduce() {
        System.out.println("start distributeReduce");
        // 将所有分区的ReduceTask加入到ReduceTask等待队列中, 因为reduce任务中fileIndex用不到,所以随便赋值即可
        for (int reduceIndex = 0; reduceIndex < this.nReduce; reduceIndex++) {
            reduceTaskWaiting.push(new ReduceTaskStat(0, reduceIndex, nReduce, filenames.length));
        }
    }

    public boolean done() {
        return isDone;
    }
}
