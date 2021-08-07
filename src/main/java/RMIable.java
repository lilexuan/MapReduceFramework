import task.TaskInfo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIable extends Remote {
    TaskInfo askTask(TaskInfo taskInfo) throws RemoteException;
    TaskInfo taskDone(TaskInfo taskInfo) throws RemoteException;
}
