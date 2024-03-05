
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE;
import java.util.ArrayList;
import javax.swing.JFileChooser;

/**
 * Reading a delimited text file with Java NIO
 * 
 * @author wulft
 */
public class NIOReadingDelimitedFile 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
    
        JFileChooser chooser = new JFileChooser();
        File selectedFile;
        String rec = "";
        ArrayList<String> lines = new ArrayList<>();
        
        /*
        
        Here is the data file we are reading:         
        000001, Bilbo, Baggins, Esq., 1060
        000002, Frodo, Baggins, Esq., 1120
        000003, Samwise, Gamgee, Esq., 1125
        000004, Peregrin, Took, Esq., 1126
        000005, Meridoc, Brandybuck, Esq., 1126
     
        */
       
        final int FIELDS_LENGTH = 5;
        
        String id, firstName, lastName, title;
        int yob;
        
       try
       {
        
       // use the toolkit to get the current working directory of the IDE
       // Not sure if the toolkit is thread safe...
       File workingDirectory = new File(System.getProperty("user.dir"));       
       
       chooser.setCurrentDirectory(workingDirectory);
       
       if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
       {
          selectedFile = chooser.getSelectedFile(); 
          Path file = selectedFile.toPath();
           // Typical java pattern of inherited classes
           // we wrap a BufferedWriter around a lower level BufferedOutputStream
           InputStream in =
                   new BufferedInputStream(Files.newInputStream(file, CREATE));
           BufferedReader reader = 
                   new BufferedReader(new InputStreamReader(in));
           
           // Finally we can read the file LOL!
           int line = 0;  // if we want to keep track of the line numbers
           while(reader.ready())
           {
              rec = reader.readLine();
              lines.add(rec);  // read all the lines into memory in an array list
              line++;
              // echo to screen
               System.out.printf("\nLine %4d %-60s ", line, rec);
           }
           reader.close(); // must close the file to seal it and flush buffer
           System.out.println("\n\nData file read!");
           
           // Now process the lines in the arrayList
           // Split the line into the fields by using split with a comma
           // use trim to remove leading and trailing spaces
           // Numbers need to be converted back to numberic values. Here only
           // the last field year of birth yob is an int the rest are strings.
           
           String[] fields;
           for(String l:lines)
           {
               fields = l.split(","); // Split the record into the fields
               
               if(fields.length == FIELDS_LENGTH)
               {
                   id        = fields[0].trim();
                   firstName = fields[1].trim();
                   lastName  = fields[2].trim();
                   title     = fields[3].trim();
                   yob       = Integer.parseInt(fields[4].trim());
                   System.out.printf("\n%-8s%-25s%-25s%-6s%6d", id, firstName, lastName, title, yob);
               }
               else
               {
                   System.out.println("Found a record that may be corrupt: ");
                   System.out.println(l);
               }
           }
           
       }
       else  // user closed the file dialog wihtout choosing
       {
           System.out.println("Failed to choose a file to process");
           System.out.println("Run the program again!");
           System.exit(0);
       }
       }  // end of TRY
       catch (FileNotFoundException e)
       {
           System.out.println("File not found!!!");
           e.printStackTrace();
       }
       catch (IOException e)
       {
           e.printStackTrace();
       }
        
    }
    
}
