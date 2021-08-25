package duke;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import task.*;
import ui.*;

/**
 * Storage for todo list information
 *
 * @author: Wei Yangken
 */
public class Storage {

    private String filepath;
    private Tasklist tasklist;

    /**
     * Constructs a storage that stores information at filepath
     * @param filepath Location of stored information
     */
    public Storage(String filepath) {
        this.filepath = filepath;
    }

    /**
     * Loads data from filepath
     * @return a tasklist from specified filepath
     */
    public Tasklist load() {
        System.out.println(Ui.breakline);
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            File file = new File(filepath);
            Scanner scanner = new Scanner(file);
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] fields = data.split(",");
                String type = fields[1];
                String isDone = fields[2];
                String taskName = fields[3];
                String remarks;
                try {
                    remarks = fields[4];
                } catch (IndexOutOfBoundsException e) {
                    remarks = "";
                }

                String input = taskName + " " + remarks;
                Task task = Parser.parseStringIntoTask(input, type, Boolean.parseBoolean(isDone));
                tasks.add(task);

            }
            System.out.printf("Loaded old tasklist...\n");
            System.out.println(Ui.breakline);

        } catch (FileNotFoundException e) {
            System.out.printf("Initializing new tasklist...\n");
            System.out.println(Ui.breakline);
        }
        tasklist = new Tasklist(tasks);
        return tasklist;
    }

    /**
     * Saves the tasklist to the specified filepath
     */
    public void save() {
        File file = new File(filepath);

       // Creates all the file directories
        String[] fileDirectories = filepath.split("/");
        int len = fileDirectories.length;
        String currDir = "";

        for (int i = 0; i < len - 1; i++) {
            currDir += fileDirectories[i];
            File currFile = new File(currDir);
            if(currFile.mkdir()) {
                System.out.printf("Added directory %s\n", currDir);
            }
            currDir += "/";
        }

        //Creates file
        try {
            if (file.createNewFile()) {
                System.out.printf("Saved file at %s\n", filepath);
            } else {
                System.out.printf("File already exists at %s\n", filepath);
            }

        } catch (IOException e) {
            System.out.println("An error occurred during creation of file.");
        }

        try {
            FileWriter fileWriter = new FileWriter(filepath);
            String headers = "S/n,Type,Status,Name,Remarks\n";
            fileWriter.write(headers);
            for (int i = 0; i < tasklist.size(); i++) {
                Task task = tasklist.get(i);
                String listEntry = String.format("%d,%s,%b,%s,%s\n",
                        i+1, task.getTaskCat(), task.isDone(), task.getName(), task.getDetail());
                fileWriter.append(listEntry);
            }
            fileWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to file.");
        }
    }

}
