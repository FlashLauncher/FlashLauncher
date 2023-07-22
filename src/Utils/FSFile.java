package Utils;

import java.io.File;

public interface FSFile {
    String getName();

    default String getNameWithoutExt() {
        final String n = getName();
        final int i = n.lastIndexOf('.');
        return i == -1 ? n : n.substring(0, i);
    }

    boolean isDir();
}
