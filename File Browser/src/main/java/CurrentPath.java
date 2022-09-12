import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class CurrentPath {
    private Path currentPath;
    private List<Path> filesFoundInPath;

    public Path getCurrentPath() {
        return currentPath;
    }

    public List<Path> getFilesFoundInPath() {
        return filesFoundInPath;
    }

    public CurrentPath(Path currentPath) throws IOException {
        this.currentPath = currentPath;
        filesFoundInPath = Files.list(currentPath).collect(Collectors.toList());
    }
}
