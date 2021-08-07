package task;

public class TaskStat {
    public long beginTime;
    public String fileName;
    public int fileIndex;
    public int partIndex;
    public int nReduce;
    public int nFiles;

    public TaskStat(long beginTime, String fileName, int fileIndex, int partIndex, int nReduce, int nFiles) {
        this.beginTime = beginTime;
        this.fileName = fileName;
        this.fileIndex = fileIndex;
        this.partIndex = partIndex;
        this.nReduce = nReduce;
        this.nFiles = nFiles;
    }

    public TaskStat(String fileName, int fileIndex, int partIndex, int nReduce, int nFiles) {
        this.fileName = fileName;
        this.fileIndex = fileIndex;
        this.partIndex = partIndex;
        this.nReduce = nReduce;
        this.nFiles = nFiles;
    }

    public TaskStat(int fileIndex, int partIndex, int nReduce, int nFiles) {
        this.fileIndex = fileIndex;
        this.partIndex = partIndex;
        this.nReduce = nReduce;
        this.nFiles = nFiles;
    }

    public TaskInfo generateTaskInfo() {
        return null;
    }

    public boolean outOfTime() {
        // 设置超时时间为1分钟
        return System.currentTimeMillis() - beginTime > 60 * 1000L;
    }

    public void setNow() {
        this.beginTime = System.currentTimeMillis();
    }
}
