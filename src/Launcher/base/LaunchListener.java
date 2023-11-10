package Launcher.base;

public interface LaunchListener {
    default void preLaunch() {}
    default void launch() {}
}