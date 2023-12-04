package Launcher.base;

public interface LaunchListener {
    /**
     * @since FlashLauncher 0.2
     */
    default void preLaunch() {}

    /**
     * @since FlashLauncher 0.2
     */
    default void launch() {}

    /**
     * @since FlashLauncher 0.2.3
     */
    default void cancel() {}

    /**
     * @since FlashLauncher 0.2.3
     */
    default void out(final char[] out, final int length) {}

    /**
     * @since FlashLauncher 0.2.3
     */
    default void outLine(final String line) {}

    /**
     * @since FlashLauncher 0.2.3
     */
    default void closeOut() {}

    /**
     * @since FlashLauncher 0.2.3
     */
    default void err(final char[] out, final int length) {}

    /**
     * @since FlashLauncher 0.2.3
     */
    default void errLine(final String line) {}

    /**
     * @since FlashLauncher 0.2.3
     */
    default void closeErr() {}

    /**
     * @since FlashLauncher 0.2.3
     */
    default void exit(final int code) {}
}