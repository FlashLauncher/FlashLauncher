package UIL;

import java.io.File;
import java.util.List;

public interface TransferEvent {
    boolean isDrop();

    boolean hasStringSupport();
    boolean hasFileListSupport();

    String getString() throws Exception;
    List<File> getFileList() throws Exception;
}