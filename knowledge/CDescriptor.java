import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Calendar;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;


/**
    Content description maker.
    Name of file-descriptor of directory is the same name of directory with added .html extension.
    Structure of file-descriptor:
    <!DOCTYPE html>
    <html>
        <head>
            <meta class="UT">timeOfLastRefreshingThisDescription</meta>
        </head>
        <body>
            <ul>
                <li>folder1Name<mark>numberOfArticlesInside1Folder</mark></li>
                <li>folder2Name<mark>numberOfArticlesInside2Folder</mark></li>
                <li>folder3Name<mark>numberOfArticlesInside3Folder</mark></li>
                <li>folder...Name<mark>numberOfArticlesInside...Folder</mark></li>
            </ul>
            <ol>
                <li>article1Name<mark>dateAndTimeOfLastModificationOfThe1Article</mark></li>
                <li>article2Name<mark>dateAndTimeOfLastModificationOfThe2Article</mark></li>
                <li>article3Name<mark>dateAndTimeOfLastModificationOfThe3Article</mark></li>
                <li>article...Name<mark>dateAndTimeOfLastModificationOfThe...Article</mark></li>
            </ol>
        </body>
    </html>
*/
public class CDescriptor {
    
    public static void main(String... args) throws IOException {
        String thisDirAddress = args.length == 0 ? System.getProperty("user.dir") :  args[0];
        String thisDirName = getDirName(thisDirAddress);
        
        // Scanning this directory and making lists of folders and articles inside this dir
        StringBuilder foldersUL = new StringBuilder("\t\t<ul>\n");
        StringBuilder filesOL = new StringBuilder("\t\t<ol>\n");
        boolean thisDirDescriptionExist = false;
        File thisDir = new File(thisDirAddress);
        for (String element: thisDir.list()) {
            File somethingInsideDir = new File(String.format("%s%s%s", thisDirAddress, File.separator, element));
            if (somethingInsideDir.isDirectory()) {
                addFolder(foldersUL, element, somethingInsideDir);
            } else if (!thisDirDescriptionExist && element.equals(thisDirName + ".html")) {
                thisDirDescriptionExist = true;
            } else if (isArticle(element, thisDirName)) {
                addFile(filesOL, element, somethingInsideDir);
            }
        }
        foldersUL.append("\t\t</ul>\n");
        filesOL.append("\t\t</ol>\n");
        StringBuilder body = new StringBuilder();
        body.append(foldersUL);
        body.append(filesOL);
        
        boolean descriptionNeedToRefresh = false;
        if (!thisDirDescriptionExist || !fileContent(thisDirAddress, thisDirName).equals(body.toString())) {
            descriptionNeedToRefresh = true;
        }
        
        if (descriptionNeedToRefresh) createDescriptionFile(thisDirName, thisDirAddress, body);
        
    }
    
    private static void addFolder(StringBuilder foldersUL, String name, File directory) {
        foldersUL.append("\t\t\t<li>");
        foldersUL.append(name);
        foldersUL.append("<mark>");
        foldersUL.append(numberOfFiles(directory));
        foldersUL.append("</mark>");
        foldersUL.append("</li>\n");
        new Thread() {
            
            @Override
            public void run() {
                try {
                    new CDescriptor().main(directory.getPath());
                } catch (IOException ioe) {
                    
                }
            }
            
        }.start();
    }
    
    private static int numberOfFiles(File directory) {
        String address = directory.getPath();
        String dirName = getDirName(address);
        
        String[] ways = directory.list();
        
        int answer = 0;
        for (String way: ways) {
            
            File someFileInsideDir = new File(String.format("%s%s%s", address, File.separator, way));
            
            if (someFileInsideDir.isDirectory()) {
                answer += numberOfFiles(someFileInsideDir);
            } else if (isArticle(way, dirName)) {
                answer++;
            }
        }
        return answer;
    }
    
    private static String getDirName(String address) {
        return address.substring(address.lastIndexOf(File.separator) + 1);
    }
    
    private static boolean isArticle(String fileName, String dirName) {
        return !fileName.startsWith("sname") && 
               !fileName.substring(0, fileName.indexOf(".")).equals(dirName) &&
                fileName.endsWith(".html");
    }
    
    private static void addFile(StringBuilder filesOL, String name, File directory) throws IOException {
        filesOL.append("\t\t\t<li>");
        filesOL.append(name);
        filesOL.append("<mark>");
        filesOL.append(((FileTime) (Files.getAttribute(directory.toPath(), "lastModifiedTime"))).toMillis());
        filesOL.append("</mark>");
        filesOL.append("</li>\n");
    }
    
    private static String fileContent(String dir, String file) throws IOException, FileNotFoundException {
        FileInputStream fin = new FileInputStream(String.format("%s%s%s.html", dir, File.separator, file));
        BufferedReader br = new BufferedReader(new InputStreamReader(fin, StandardCharsets.UTF_8));
        String content = String.join("\n", Files.readAllLines(Paths.get(dir, file + ".html")));
        br.close();
        return content.substring(content.indexOf("\t\t<ul>\n"), content.indexOf("\t\t</ol>\n") + 8);
    }

    private static void createDescriptionFile(String fileName, String address, StringBuilder descriptionOfContent) {
        
        StringBuilder page = new StringBuilder("<!DOCTYPE html>\n<html>\n");
        
        StringBuilder headBlock = new StringBuilder("\t<head>\n");
        headBlock.append(time());
        headBlock.append("\t</head>\n");
        
        StringBuilder bodyBlock = new StringBuilder("\t<body>\n");
        bodyBlock.append(descriptionOfContent);
        bodyBlock.append("\t</body>\n");
        
        page.append(headBlock);
        page.append(bodyBlock);
        page.append("</html>");
        
        try (PrintWriter out = new PrintWriter(
                               new BufferedWriter(
                               new FileWriter(String.format("%s%s%s.html", address, File.separator, fileName))))){
            out.write(page.toString());
            out.flush();
            System.out.println("Successfull: " + Thread.currentThread().getId());
        } catch (IOException ioe) {
            System.out.println("\u0007");
            ioe.printStackTrace();
        }
    }
    
    private static StringBuilder time() {
        StringBuilder t = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        t.append("\t\t<meta class=\"UT\">");
        t.append(calendar.getTimeInMillis());
        t.append("</meta>\n");
        return t;
    }

}


