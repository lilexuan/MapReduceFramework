package task;

import java.util.Deque;
import java.util.LinkedList;

public class TaskStatQueue {
    public Deque<TaskStat> taskArray;

    public TaskStatQueue() {
        this.taskArray = new LinkedList<>();
    }

    public TaskStatQueue(Deque<TaskStat> taskArray) {
        this.taskArray = taskArray;
    }

    public int size() {
        return taskArray.size();
    }

    public synchronized TaskStat pop() {
        if (taskArray == null || taskArray.isEmpty()) {
            return null;
        }
        return taskArray.pollFirst();
    }

    public synchronized void push(TaskStat taskStat) {
        if (this.taskArray == null) {
            return;
        }
        taskArray.offerLast(taskStat);
    }

    /**
     * 取出超时的任务
     * @return
     */
    public synchronized Deque<TaskStat> timeOutQueue() {
        Deque<TaskStat> outArray = new LinkedList<>();
        Deque<TaskStat> inArray = new LinkedList<>();
        for (TaskStat taskStat : taskArray) {
            if (taskStat.outOfTime()) {
                outArray.offerLast(taskStat);
            } else {
                inArray.offerLast(taskStat);
            }
        }
        taskArray = inArray;
        return outArray;
    }

    /**
     * 加入整个任务队列, 并清除
     * @param taskArray2
     */
    public synchronized void moveAppend(Deque<TaskStat> taskArray2) {
        taskArray.addAll(taskArray2);
        taskArray2.clear();
    }

    /**
     * 根据fileIndex和partIndex删除任务
     * @param fileIndex
     * @param partIndex
     */
    public synchronized void removeTask(int fileIndex, int partIndex) {
        taskArray.removeIf(taskStat -> taskStat.fileIndex == fileIndex && taskStat.partIndex == partIndex);
    }

}
