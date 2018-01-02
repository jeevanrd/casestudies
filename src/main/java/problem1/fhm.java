package problem1;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;


public class fhm {
    public static void main(String[] args) throws IOException, InterruptedException {
        //pass input dir
        //pass output dir
        //pass time window

        //use standard input for 3 inputs
        //for auto delete use nio
        //use newSingleThreadScheduledExecutor for 2 min copy scheduler
        //use newSingleThreadScheduledExecutor for 5 min monit scheduler
        //how to handle two scheduler running on the same folder at a time. Need to use synchronization here.

        Path path = Paths.get("D:/cp");
        FileSystem fileSystem = FileSystems.getDefault();
        WatchService watchService = fileSystem.newWatchService();
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);
        while(true){
            WatchKey watchKey = watchService.take();
            List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
            for (WatchEvent<?> we: watchEvents){
                if(we.kind() == StandardWatchEventKinds.ENTRY_CREATE){
                    System.out.println("Created: "+we.context());
                }else if (we.kind() == StandardWatchEventKinds.ENTRY_DELETE){
                    System.out.println("Deleted: "+we.context());
                } else if(we.kind() == StandardWatchEventKinds.ENTRY_MODIFY){
                    System.out.println("Modified :"+we.context());
                }
            }
            if(!watchKey.reset()){
                break;
            }
        }
    }

}

//import java.util.concurrent.Executors;
//        import java.util.concurrent.ScheduledExecutorService;
//        import java.util.concurrent.TimeUnit;
//
//public class Task3 {
//
//    public static void main(String[] args) {
//
//        Runnable runnable = new Runnable() {
//            public void run() {
//                // task to run goes here
//                System.out.println("Hello !!");
//            }
//        };
//
//        ScheduledExecutorService service = Executors
//                .newSingleThreadScheduledExecutor();
//        service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
//    }
//}