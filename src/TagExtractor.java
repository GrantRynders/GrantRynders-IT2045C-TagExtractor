import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static java.nio.file.StandardOpenOption.CREATE;

public class TagExtractor extends JFrame
{
    private Map<String, Integer> wordFrequency = new TreeMap<>();
    private Set<String> inputWords = new TreeSet<>();
    private Set<String> stopWords = new TreeSet<>();
    private JPanel mainPanel;
    private JPanel selectionPanel;
    private JOptionPane optionPane;
    private JButton chooseFileButton;
    private JButton chooseStopFileButton;
    private JButton readFileButton;
    private JButton writeFileButton;
    private JScrollPane scrollPane;
    private JTextArea outputArea;
    private File selectedFile;
    private File stopFile;

    public static void main(String[] args)
    {
        TagExtractor tagExtractor = new TagExtractor();
    }
    public TagExtractor()
    {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3,1));
        selectionPanel = new JPanel();
        selectionPanel.setLayout(new GridLayout(1,3));
        optionPane = new JOptionPane();
        chooseFileButton = new JButton("Select a file to analyze");
        chooseFileButton.addActionListener(e ->
        {
            selectedFile = ChooseFile();
        });
        chooseStopFileButton = new JButton("Select stop-word file");
        chooseStopFileButton.addActionListener(e ->
        {
            stopFile = ChooseFile();
        });
        readFileButton = new JButton("RUN");
        readFileButton.addActionListener(e ->
        {
            if (selectedFile != null && stopFile != null)
            {
                CreateStopWordList(stopFile);
                ReadFile(selectedFile);
            }
            else
            {
                    optionPane.showInternalMessageDialog(null, "Missing file selection",
                            "Files not selected", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        writeFileButton = new JButton("Write output to a file");
        writeFileButton.addActionListener(e ->
        {
            if (selectedFile != null && stopFile != null)
            {
                WriteFile(wordFrequency);
            }
            else
            {
                optionPane.showInternalMessageDialog(null, "Missing file selection",
                        "Files not selected", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        outputArea = new JTextArea("Output will appear here:\n word : # of appearances\n");
        scrollPane = new JScrollPane(outputArea);
        selectionPanel.add(chooseFileButton);
        selectionPanel.add(chooseStopFileButton);
        selectionPanel.add(readFileButton);
        mainPanel.add(selectionPanel);
        mainPanel.add(scrollPane);
        mainPanel.add(writeFileButton);
        BuildWindow();
    }
    public void BuildWindow()
    {
        add(mainPanel);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        setSize((screenWidth / 4) * 3, screenHeight);
        setLocation(screenWidth / 8, 0);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    private File ChooseFile()
    {
        File chosenFile = null;
        JFileChooser chooser = new JFileChooser();
        try
        {
            File workingDirectory = new File(System.getProperty("user.dir"));
            chooser.setCurrentDirectory(workingDirectory);
            if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            {
                chosenFile = chooser.getSelectedFile();
                Path file = chosenFile.toPath();
                InputStream in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                optionPane.showInternalMessageDialog(null, "File selected: " + chosenFile.toString(),
                        "File info", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (FileNotFoundException e)
        {
            optionPane.showInternalMessageDialog(null, "File not found!",
                    "Error", JOptionPane.INFORMATION_MESSAGE);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return chosenFile;
    }
    public void ReadFile(File selectedFile)
    {
        JFileChooser chooser = new JFileChooser();
        String rec = "";
        String delims = "\\W+"; // split any non word
        inputWords.clear();
        try
        {
                Path file = selectedFile.toPath();
                InputStream in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                int line = 0;
                while(reader.ready())
                {
                    rec = reader.readLine();
                    line++;
                    String[] words = rec.split(delims);
                    for (String word: words)
                    {
                        word = word.toLowerCase();
                        if (!stopWords.contains(word))
                        {
                            if (wordFrequency.get(word) == null)
                            {
                                wordFrequency.put(word, 1);
                            }
                            else
                            {
                                wordFrequency.put(word, wordFrequency.get(word) + 1);
                            }
                        }
                    }
                }
                reader.close();
        }
        catch (FileNotFoundException e)
        {
            optionPane.showInternalMessageDialog(null, "File not found!",
                    "error", JOptionPane.INFORMATION_MESSAGE);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        for(Map.Entry<String,Integer> processedWord : wordFrequency.entrySet())
        {
            outputArea.append(processedWord.getKey() + " : " + processedWord.getValue().toString() + "\n");
        }
    }
    private void CreateStopWordList(File stopFile)
    {
        JFileChooser chooser = new JFileChooser();
        String rec = "";
        stopWords.clear();
        try
        {
            Path file = stopFile.toPath();
            InputStream in = new BufferedInputStream(Files.newInputStream(file, CREATE));
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            int line = 0;
            while(reader.ready())
            {
                rec = reader.readLine();
                line++;
                stopWords.add(rec);
            }
            reader.close();
        }
        catch (FileNotFoundException e)
        {
            optionPane.showInternalMessageDialog(null, "File not found!",
                    "Error", JOptionPane.INFORMATION_MESSAGE);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void WriteFile(Map<String, Integer> wordFrequencyInstance)
    {
        File workingDirectory = new File(System.getProperty("user.dir"));
        File chosenFile = ChooseFile();
        Path file = chosenFile.toPath();
        try
        {
            // Typical java pattern of inherited classes
            // we wrap a BufferedWriter around a lower level BufferedOutputStream
            OutputStream out =
                    new BufferedOutputStream(Files.newOutputStream(file, StandardOpenOption.TRUNCATE_EXISTING));
            BufferedWriter writer =
                    new BufferedWriter(new OutputStreamWriter(out));

            // Finally can write the file LOL!

            for(Map.Entry<String,Integer> rec : wordFrequencyInstance.entrySet())
            {
                writer.write(rec.getKey(), 0, rec.getKey().length());  // stupid syntax for write rec
                // 0 is where to start (1st char) the write
                // rec. length() is how many chars to write (all)
                writer.write(" : ");
                writer.write(rec.getValue().toString(), 0, rec.getValue().toString().length());
                writer.newLine();  // adds the new line
            }
            writer.close(); // must close the file to seal it and flush buffer
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
