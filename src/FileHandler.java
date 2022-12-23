import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHandler implements Writable, Serializable {

    private String filename;

    public FileHandler(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public void save(Serializable serializable) {

        FamilyTree saveTree = (FamilyTree)serializable;
        String content = toText(saveTree.toString());

        try (FileWriter fw = new FileWriter(filename, false)) {
            fw.write(content);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public FamilyTree  load() {
        String result = "";

        File file = new File(filename);
        try (Scanner sc = new Scanner(file, StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                result += line + "\n";
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return fromText(result);
    }

    private String findMatch(String extractStr, String field) {

        Pattern pattern = Pattern.compile(field + ": (.*?);");
        Matcher matcher = pattern.matcher(extractStr);

        String findStr = "";
        if (matcher.find() && matcher.group(1) != null)
            findStr = matcher.group(1);
        return findStr;
    }

    public String toText(String str) {
        int itemIndex = 0;
        String txt = "";

        while (str.indexOf("\n", itemIndex) > 0) {
            String line = str.substring(itemIndex, str.indexOf("\n", itemIndex)) + ';';

            txt = txt + "name: " + findMatch(line, "name") + ";";
            txt = txt + " gender: " + findMatch(line, "gender") + ";";
            if (line.contains("father"))
                txt = txt + " father: " + findMatch(line, "father") + ";";
            if (line.contains("mother"))
                txt = txt + " mother: " + findMatch(line, "mother") + ";";
            txt = txt + "\n";

            itemIndex = str.indexOf("\n", itemIndex) + 1;
        }
        return txt;
    }


    public FamilyTree fromText(String str) {
        int itemIndex = 0;
        String name = "", gender = "", fathername = "", mothername = "";
        FamilyTree loadTree = new FamilyTree();

        while (str.indexOf("\n", itemIndex) > 0) {
            String line = str.substring(itemIndex, str.indexOf("\n", itemIndex)) + ';';

            name = findMatch(line, "name");
            gender = findMatch(line, "gender");
            fathername = findMatch(line, "father");
            mothername = findMatch(line, "mother");
            Human human = new Human(name, gender, loadTree.getHumanByName(fathername), loadTree.getHumanByName(mothername));
            loadTree.addHuman(human);

            itemIndex = str.indexOf("\n", itemIndex) + 1;
        }
        return loadTree;
    }
}