package problem1;

import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Target;
import java.nio.file.*;
import java.sql.Time;
import java.util.*;


public class fhm {
    static final String input = "/tmpfolder1";
    static final String target = "/secured";
    static final String archive = "/archive";

    public static void main(String[] args) throws IOException, InterruptedException {
        //how to handle two scheduler running on the same folder at a time. Need to use synchronization here.
        Timer time = new Timer(); // Instantiate Timer Object
        CopyTask ct = new CopyTask(input, target); // Instantiate SheduledTask class
        time.schedule(ct, 0, 1000); // Create Repetitively task for every 1 secs
        MonitTask mt = new MonitTask(target, archive); // Instantiate SheduledTask class
        time.schedule(mt, 0, 2500); // Create Repetitively task for every 1 secs
        AutoDeleteTask tk = new AutoDeleteTask(target);
    }
}

class AutoDeleteTask implements Runnable {
    String Target;
    WatchService watchService;
    public AutoDeleteTask(String target) throws IOException {
        this.Target = target;
        Path path = Paths.get(target);
        FileSystem fileSystem = FileSystems.getDefault();
        watchService = fileSystem.newWatchService();
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
        this.run();
    }

    public void run() {
        while (true) {
            WatchKey watchKey = null;
            try {
                watchKey = this.watchService.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
            for (WatchEvent<?> we : watchEvents) {
                if (we.kind() == StandardWatchEventKinds.ENTRY_CREATE || we.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    System.out.println("Created: " + we.context());
                    Path path = Paths.get(this.Target + "/" + we.context());
                    System.out.println(path.toAbsolutePath());
                    if(Files.isExecutable(path)){
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("not an exec");
                    }
                }
            }
            if (!watchKey.reset()) {
                break;
            }
        }
    }
}

class CopyTask extends TimerTask {
    String Source;
    String Target;

    public CopyTask(String source, String target) {
        this.Source = source;
        this.Target = target;
    }

    public void run() {
        try {
            FileUtils.copyDirectory(FileUtils.getFile(Source), FileUtils.getFile(Target));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class MonitTask extends TimerTask {
    String monitFolder;
    String archiveFolder;

    public MonitTask(String monitFolder, String archiveFolder) {
        this.monitFolder = monitFolder;
        this.archiveFolder = archiveFolder;
    }

    public static Comparator<File> ModifiedComparator =  new Comparator<File>() {
        public int compare(File object1, File object2) {
            return (int) (object2.lastModified() - object1.lastModified());
        }
    };

    public void run() {
        File monitFolderFiles = FileUtils.getFile(this.monitFolder);
        File archiveFolder = FileUtils.getFile(this.archiveFolder);

        long monitFolderSize = monitFolderFiles.length();
        long remainFolderSize = monitFolderSize;
        if(sizeInMb(monitFolderSize) < 100) {
            return;
        }

        List<File> files = getAllFiles(null, monitFolderFiles);
        Collections.sort(files, ModifiedComparator);
        for(File file : files) {
            long currentFileSize = file.length();
            try {
                FileUtils.moveFileToDirectory(file, archiveFolder, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            remainFolderSize = remainFolderSize - currentFileSize;
            if(sizeInMb(remainFolderSize) < 100) {
                break;
            }
        }
    }

    public double sizeInMb(double length) {
        double bytes = length;
        double kilobytes = (bytes / 1024);
        return (kilobytes / 1024);
    }

    public List<File> getAllFiles(List<File> files, File dir) {
        if (files == null)
            files = new LinkedList<File>();
        if (!dir.isDirectory()) {
            files.add(dir);
            return files;
        }

        for (File file : dir.listFiles())
            getAllFiles(files, file);
        return files;
    }
}


