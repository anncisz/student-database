import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static StudentDatabase database = new StudentDatabase();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        println("Hello!");
        while (true) {
            try {
                processCommand();
            } catch (StudentException e) {
                println(e.getMessage());
            }
        }
    }

    private static void processCommand() {
        println("Please enter command");
        print("> ");
        String cmd = getUserInput();
        String firstWord = getFirstWord(cmd);
        if (firstWord.equals("exit")) {
            scanner.close();
            println("Bye!");
            System.exit(0);
        } else if (firstWord.equals("select")) {
            processSelect(cmd);
        } else if (firstWord.equals("update")) {
            processUpdate(cmd);
        } else if (firstWord.equals("insert")) {
            processInsert(cmd);
        } else if (firstWord.equals("delete")) {
            processDelete(cmd);
        } else {
            throw new StudentException(
                    "Unrecognised command. Should be one of: select, update, insert, delete or exit");
        }
    }

    private static void processDelete(String cmd) {
        // DELETE FROM students WHERE indexNumber='987'
        List<String> tokens = Arrays.asList(cmd.split("[\\s,]+"));
        if (tokens.size() < 3 || !tokens.get(1).equals("from") || !tokens.get(2).equals("students")) {
            throw new StudentException("Delete should start with 'delete from students'");
        }
        int whereLocation = tokens.indexOf("where");
        if (whereLocation == -1) {
            if (tokens.size() != 3) {
                throw new StudentException("Unrecognised delete clause body");
            }
            database.delete(null);
        } else {
            if (whereLocation != 3) {
                throw new StudentException("'Where' on wrong position");
            }
            if (tokens.size() != 5) {
                throw new StudentException("Unrecognised where clause body");
            }
            database.delete(tokens.get(4));
        }
    }

    private static void processInsert(String cmd) {
        // INSERT INTO students (name,surname,indexNumber) VALUES ('Jan','Kowalski','5500');
        String insertPrefix = "insert into students (name,surname,indexnumber) values ";
        if (!cmd.startsWith(insertPrefix)) {
            throw new StudentException(String.format("Insert should start with '%s'", insertPrefix));
        }
        String values = cmd.substring(insertPrefix.length());
        List<String> tokens = new ArrayList<>(Arrays.asList(values.split("[',()]+", 0)));
        tokens.removeIf(String::isEmpty);
        if (tokens.size() != 3) {
            throw new StudentException(String.format("There should be 3 values in values section: '%s'", values));
        }
        database.insert(tokens.get(0), tokens.get(1), tokens.get(2));
    }

    private static void processUpdate(String cmd) {
        // UPDATE students SET name='Jan' WHERE indexNumber='987'
        List<String> tokens = Arrays.asList(cmd.split("[\\s,]+"));
        if (!tokens.contains("set")) {
            throw new StudentException("Update should contain 'set' clause");
        }
        if (tokens.size() < 3 || !tokens.get(1).equals("students") || !tokens.get(2).equals("set")) {
            throw new StudentException("Update should start with 'update students set'");
        }
        int whereLocation = tokens.indexOf("where");
        if (whereLocation == -1) {
            if (tokens.size() != 4) {
                throw new StudentException("Unrecognised set clause body");
            }
            database.update(tokens.get(3), null);
        } else {
            if (whereLocation != 4) {
                throw new StudentException("'Where' on wrong position");
            }
            if (tokens.size() != 6) {
                throw new StudentException("Unrecognised where clause body");
            }
            database.update(tokens.get(3), tokens.get(5));
        }
    }

    private static String getFirstWord(String cmd) {
        return cmd.split(" ")[0];
    }

    private static void processSelect(String cmd) {
        // SELECT * FROM student
        // SELECT name,indexNumber FROM students
        List<String> tokens = Arrays.asList(cmd.split("[\\s,]+"));
        if (!tokens.contains("from")) {
            throw new StudentException("Select should contain 'from' clause");
        }
        if (!tokens.get(tokens.size() - 2).equals("from") || !tokens.get(tokens.size() - 1).equals("students")) {
            throw new StudentException("Select should end with 'from students'");
        }
        List<String> columns = tokens.subList(1, tokens.indexOf("from"));
        database.select(columns);
    }

    private static void println(String s) {
        System.out.println(s);
    }

    private static void print(String s) {
        System.out.print(s);
    }

    private static String getUserInput() {
        return scanner.nextLine().trim().toLowerCase();
    }
}
