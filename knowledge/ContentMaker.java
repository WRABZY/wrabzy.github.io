import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
//import java.util.stream.Stream;

/**
    @author WRABZY
*/
public class ContentMaker {
    
    private StringBuilder mainBlock = new StringBuilder("<html>\n<body>\n");
    private StringBuilder foldersBlock = new StringBuilder("\t<ul>\n");
    private StringBuilder filesBlock = new StringBuilder("\t<ol>\n");
    
    public static void main(String... args) {
        
        ContentMaker maker = new ContentMaker();
        maker.addTime();
        
        String thisDirAddress = args.length == 0 ? 
                                System.getProperty("user.dir") :
                                args[0];
                                
        File thisDir = new File(thisDirAddress);
        String thisDirName = thisDirAddress.substring(thisDirAddress.lastIndexOf(File.separator) + 1);
        
        for (String element: thisDir.list()) {
            
            File someFileInsideDir = new File(String.format("%s%s%s", thisDirAddress, File.separator, element));
            
            if (someFileInsideDir.isDirectory()) {
                maker.addFolder(element, someFileInsideDir);
            } else if (isHelpful(element, thisDirName)){
                maker.addFile(element);
            }
        }
        
        maker.foldersBlock.append("\t</ul>\n");
        maker.filesBlock.append("\t</ol>\n");
        
        maker.mainBlock.append(maker.foldersBlock);
        maker.mainBlock.append(maker.filesBlock);
        maker.mainBlock.append("</body>\n");
        maker.mainBlock.append("</html>");
        
        maker.createDescriptionFile(thisDirName, thisDirAddress);  
    }
    
    private int numberOfFiles(File directory) {
        String address = directory.getPath();
        String dirName = address.substring(address.lastIndexOf(File.separator) + 1);
        
        String[] ways = directory.list();
        
        int answer = 0;
        for (String way: ways) {
            
            File someFileInsideDir = new File(String.format("%s%s%s", address, File.separator, way));
            
            if (someFileInsideDir.isDirectory()) {
                answer += numberOfFiles(someFileInsideDir);
            } else if (isHelpful(way, dirName)) {
                answer++;
            }
        }
        return answer;
    }
    
    private void addTime() {
        Calendar calendar = Calendar.getInstance();
        mainBlock.append("\t<div class=\"UT\">");
        mainBlock.append(calendar.getTimeInMillis());
        mainBlock.append("</div>\n");
    }
    
    private static boolean isHelpful(String fileName, String dirName) {
        return !fileName.startsWith("sname") && 
               !fileName.substring(0, fileName.indexOf(".")).equals(dirName) &&
                fileName.endsWith(".html");
    }
    
    private void addFile(String name) {
        filesBlock.append("\t\t<li>");
        filesBlock.append(name);
        filesBlock.append("</li>\n");
    }
    
    private void addFolder(String name, File directory) {
        foldersBlock.append("\t\t<li>");
        foldersBlock.append(name);
        foldersBlock.append("<mark>");
        foldersBlock.append(numberOfFiles(directory));
        foldersBlock.append("</mark>");
        foldersBlock.append("</li>\n");
        new Thread() {
            
            @Override
            public void run() {
                new ContentMaker().main(directory.getPath());
            }
            
        }.start();
    }
    
    private void createDescriptionFile(String fileName, String address) {
        
        try (PrintWriter out = new PrintWriter(
                               new BufferedWriter(
                               new FileWriter(String.format("%s%s%s.html", address, File.separator, fileName))))){
            out.write(mainBlock.toString());
            out.flush();
            System.out.println("Successfull: " + Thread.currentThread().getId());
        } catch (IOException oneMoreIoe) {
                System.out.println("\u0007");
                oneMoreIoe.printStackTrace();
        } finally {
            System.out.println("Done.");
        }
    }
    
}