package task;

import java.time.LocalDateTime;

public class MapTaskStat extends TaskStat {
    public MapTaskStat(long beginTime, String fileName, int fileIndex, int partIndex, int nReduce, int nFiles) {
        super(beginTime, fileName, fileIndex, partIndex, nReduce, nFiles);
    }

    public MapTaskStat(String fileName, int fileIndex, int partIndex, int nReduce, int nFiles) {
        super(fileName, fileIndex, partIndex, nReduce, nFiles);
    }

    @Override
    public TaskInfo generateTaskInfo() {
        return new TaskInfo(State.TASK_MAP, fileName, partIndex, fileIndex, nReduce, nFiles);
    }
}
