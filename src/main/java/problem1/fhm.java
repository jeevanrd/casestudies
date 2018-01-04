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
    static final String input = "/Users/jeevan/test/tmp";
    static final String target = "/Users/jeevan/test/secured";
    static final String archive = "/Users/jeevan/test/archive";
    static final Integer CopySchedulerFrequency = 1000;
    static final Integer MonitSchedulerFrequency = 5000;

    public static void main(String[] args) throws IOException, InterruptedException {
        //how to handle two scheduler running on the same folder at a time. Need to use synchronization here.
        Timer time = new Timer(); // Instantiate Timer Object
        CopyTask ct = new CopyTask(input, target); // Instantiate SheduledTask class
        time.schedule(ct, 0, CopySchedulerFrequency); // Create Repetitively task for every 1 secs
        MonitTask mt = new MonitTask(target, archive); // Instantiate SheduledTask class
        time.schedule(mt, 0, MonitSchedulerFrequency); // Create Repetitively task for every 1 secs
        AutoDeleteTask tk = new AutoDeleteTask(target);
    }
}

class AutoDeleteTask implements Runnable {
    String Target;
    WatchService watchService;
    public AutoDeleteTask(String target) throws IOException {
        this.Target = target;
        Path path = Paths.get(target);
        if(!path.toFile().exists()) {
            System.out.printf("target path with %s  doesn't exist. Please create target folder", target);
        }
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
                            System.out.printf("file with name %s deleted", path.getFileName());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
        System.out.println("copy task invoked");
        File src = FileUtils.getFile(Source);
        if(!src.exists()) {
            System.out.printf("Dir with path doesn't exist", Source);
            return;
        }

        File tgt = FileUtils.getFile(Target);
        if(!tgt.exists()) {
            System.out.printf("Dir with path doesn't exist", Target);
            return;
        }

        try {
            FileUtils.copyDirectory(src, tgt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class MonitTask extends TimerTask {
    String monitFolder;
    String archiveFolder;
    static final Integer MaxFolderSize = 50 * 1024;

    public MonitTask(String monitFolder, String archiveFolder) {
        this.monitFolder = monitFolder;
        this.archiveFolder = archiveFolder;
    }

    public static Comparator<File> ModifiedComparator =  new Comparator<File>() {
        public int compare(File object1, File object2) {
            return (int) (object1.lastModified() - object2.lastModified());
        }
    };

    public void run() {
        System.out.println("MonitTask invoked");
        File monitFolderFiles = FileUtils.getFile(this.monitFolder);
        File archiveFolder = FileUtils.getFile(this.archiveFolder);

        if(!monitFolderFiles.exists()) {
            System.out.printf("Dir with path doesn't exist", monitFolder);
            return;
        }

        if(!archiveFolder.exists()) {
            System.out.printf("Dir with path doesn't exist", archiveFolder);
            return;
        }

        long monitFolderSize = FileUtils.sizeOfDirectory(monitFolderFiles);
        long remainFolderSize = monitFolderSize;

        if(monitFolderSize < MaxFolderSize) {
            return;
        }

        List<File> files = getAllFiles(null, monitFolderFiles);
        Collections.sort(files, ModifiedComparator);

        for(File file : files) {
            long currentFileSize = file.length();
            try {
                System.out.println("move file -" +  file.getName());
                File archiveFile = new File(this.archiveFolder + "/" + file.getName());
                if(archiveFile.exists()) {
                    archiveFile.delete();
                }
                FileUtils.moveFileToDirectory(file, archiveFolder, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            remainFolderSize = remainFolderSize - currentFileSize;
            if(remainFolderSize < MaxFolderSize) {
                break;
            }
        }
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