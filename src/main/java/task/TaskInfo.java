package task;

import java.io.Serializable;

public class TaskInfo implements Serializable {
    public State state;
    public String fileName;
    public int partIndex;
    public int fileIndex;
    public int nReduce;
    public int nFiles;

    public TaskInfo(State state, String fileName, int partIndex, int fileIndex, int nReduce, int nFiles) {
        this.state = state;
        this.fileName = fileName;
        this.partIndex = partIndex;
        this.fileIndex = fileIndex;
        this.nReduce = nReduce;
        this.nFiles = nFiles;
    }

    public TaskInfo() {
    }
}
