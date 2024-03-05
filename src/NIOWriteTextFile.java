import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.*;
import java.util.ArrayList;
/**
 *
 * @author wulft
 * 
 * Demonstrates how to use Java NIO, a thread safe File IO library
 * to write a text file
 */
public class NIOWriteTextFile 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
       // Test data the lines of the file we will write
       ArrayList <String>recs = new ArrayList<>(); 
       recs.add("Sample data for our file writing example.");
       recs.add("Sample data Line 2.");
       recs.add("Sample data Line 3.");
       recs.add("Sample data Line 4.");
       recs.add("Sample data Line 5.");
       
       
       // uses a fixed known path:
       //  Path file = Paths.get("c:\\My Documents\\data.txt");
        
       // use the toolkit to get the current working directory of the IDE
       // will create the file within the Netbeans project src folder 
       // (may need to adjust for other IDE)
       // Not sure if the toolkit is thread safe...
       File workingDirectory = new File(System.getProperty("user.dir"));
       Path file = Paths.get(workingDirectory.getPath() + "\\src\\data.txt");
       
       try
       {
           // Typical java pattern of inherited classes
           // we wrap a BufferedWriter around a lower level BufferedOutputStream
           OutputStream out =
                   new BufferedOutputStream(Files.newOutputStream(file, CREATE));
           BufferedWriter writer = 
                   new BufferedWriter(new OutputStreamWriter(out));
           
           // Finally can write the file LOL!
           
           for(String rec : recs)
           {
               writer.write(rec, 0, rec.length());  // stupid syntax for write rec
                            // 0 is where to start (1st char) the write
                            // rec. length() is how many chars to write (all)
               writer.newLine();  // adds the new line
               
           }
           writer.close(); // must close the file to seal it and flush buffer
           System.out.println("Data file written!");
       }
       catch (IOException e)
       {
           e.printStackTrace();
       }
       
    }
    
}
