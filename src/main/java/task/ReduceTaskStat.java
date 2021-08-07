package task;

import task.TaskStat;

import java.time.LocalDateTime;

public class ReduceTaskStat extends TaskStat {
    public ReduceTaskStat(long beginTime, String fileName, int fileIndex, int partIndex, int nReduce, int nFiles) {
        super(beginTime, fileName, fileIndex, partIndex, nReduce, nFiles);
    }

    public ReduceTaskStat(String fileName, int fileIndex, int partIndex, int nReduce, int nFiles) {
        super(fileName, fileIndex, partIndex, nReduce, nFiles);
    }

    public ReduceTaskStat(int fileIndex, int partIndex, int nReduce, int nFiles) {
        super(fileIndex, partIndex, nReduce, nFiles);
    }

    @Override
    public TaskInfo generateTaskInfo() {
        return new TaskInfo(State.TASK_REDUCE, fileName, partIndex, fileIndex, nReduce, nFiles);
    }
}
