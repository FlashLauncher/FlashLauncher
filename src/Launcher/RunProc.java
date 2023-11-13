package Launcher;

import Launcher.base.IAccount;
import Launcher.base.IProfile;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RunProc {
    int statusWidth = 0;

    final ArrayList<TaskGroup> groups = new ArrayList<>();
    public final FlashLauncher launcher;
    public final IAccount account;
    public final IProfile profile;

    public final ConcurrentLinkedQueue<String>
            beginArgs = new ConcurrentLinkedQueue<>(),
            args = new ConcurrentLinkedQueue<>(),
            endArgs = new ConcurrentLinkedQueue<>()
    ;

    public final ConcurrentHashMap<String, Object> generalObjects = new ConcurrentHashMap<>();
    public File workDir = null;

    public RunProc(final FlashLauncher launcher, final IAccount account, final IProfile profile) {
        this.launcher = launcher;
        this.account = account;
        this.profile = profile;
    }

    public int getStatusWidth() { return statusWidth; }

    public void addTaskGroup(final TaskGroup group) {
        synchronized (group.tasks) {
            if (group.tasks.isEmpty())
                return;
        }
        synchronized (groups) {
            groups.add(group);
            groups.notifyAll();
        }
        synchronized (FLCore.groups) {
            FLCore.groups.add(group);
            FLCore.groups.notifyAll();
        }
    }

    public TaskGroup[] getTaskGroups() {
        synchronized (groups) {
            return groups.toArray(new TaskGroup[0]);
        }
    }
}