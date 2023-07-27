package Launcher;

import Launcher.base.IAccount;
import Launcher.base.IProfile;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RunProc {
    final ArrayList<TaskGroup> groups = new ArrayList<>();
    public final FlashLauncher launcher;
    public final IAccount account;
    public final IProfile profile;
    public final ConcurrentLinkedQueue<String> arguments = new ConcurrentLinkedQueue<>();
    public File workDir = null;

    public RunProc(final FlashLauncher launcher, final IAccount account, final IProfile profile) {
        this.launcher = launcher;
        this.account = account;
        this.profile = profile;
    }

    public void addTaskGroup(final TaskGroup group) {
        synchronized (groups) {
            groups.add(group);
            groups.notifyAll();
        }
        synchronized (FLCore.groups) {
            FLCore.groups.add(group);
            FLCore.groups.notifyAll();
        }
    }
}