import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class StudentDatabase {
    private final static String SEPARATOR = "|";
    final static String DISPLAY_SEPARATOR = " | ";
    private List<Student> students = new ArrayList<>();
    private String fileName = "student.db";

    public StudentDatabase() {
        load();
    }

    public List<Student> getStudents() {
        return students;
    }

    public void insert(String name, String surname, String indexNumber) {
        Student student = new Student();
        student.setName(name);
        student.setSurname(surname);
        student.setIndexNumber(indexNumber);
        students.add(student);
        save();
    }

    public void delete(String whereBody) {
        List<Student> filteredStudents = getStudents(whereBody);
        students.removeAll(filteredStudents);
        save();
    }

    public void select(List<String> columns) {
        if (columns.isEmpty()) {
            throw new StudentException("Columns cannot be empty");
        }
        List<String> columnsToDisplay = new ArrayList<>();
        if (columns.size() == 1 && columns.get(0).equals("*")) {
            columnsToDisplay.add(Student.NAME_COLUMN);
            columnsToDisplay.add(Student.SURNAME_COLUMN);
            columnsToDisplay.add(Student.INDEX_COLUMN);
        } else {
            columnsToDisplay.addAll(columns);
        }
        println(String.join(StudentDatabase.DISPLAY_SEPARATOR, columnsToDisplay));
        for (Student student : students) {
            println(student.toString(columnsToDisplay));
        }
    }

    public void update(String setBody, String whereBody) {
        List<Student> filteredStudents = getStudents(whereBody);
        List<String> tokens = Arrays.asList(setBody.split("['=]+"));
        if (tokens.size() != 2) {
            throw new StudentException(String.format("Unrecognised set clause body '%s'", setBody));
        }
        String column = tokens.get(0);
        String value = tokens.get(1);
        if (column.equalsIgnoreCase(Student.INDEX_COLUMN)) {
            for (Student student : filteredStudents) {
                student.setIndexNumber(value);
            }
        } else if (column.equalsIgnoreCase(Student.SURNAME_COLUMN)) {
            for (Student student : filteredStudents) {
                student.setSurname(value);
            }
        } else if (column.equalsIgnoreCase(Student.NAME_COLUMN)) {
            for (Student student : filteredStudents) {
                student.setName(value);
            }
        } else {
            throw new StudentException(String.format("Unrecognised column name '%s' in where clause", column));
        }
        save();
    }

    private List<Student> getStudents(String whereBody) {
        if (whereBody == null) {
            return students;
        }
        List<String> tokens = Arrays.asList(whereBody.split("['=]+"));
        if (tokens.size() != 2) {
            throw new StudentException(String.format("Unrecognised where clause body '%s'", whereBody));
        }
        String column = tokens.get(0);
        String value = tokens.get(1);
        List<Student> filteredStudents = new ArrayList<>();
        if (column.equalsIgnoreCase(Student.INDEX_COLUMN)) {
            for (Student student : students) {
                if (student.getIndexNumber().equals(value)) {
                    filteredStudents.add(student);
                }
            }
        } else if (column.equalsIgnoreCase(Student.SURNAME_COLUMN)) {
            for (Student student : students) {
                if (student.getSurname().equals(value)) {
                    filteredStudents.add(student);
                }
            }
        } else if (column.equalsIgnoreCase(Student.NAME_COLUMN)) {
            for (Student student : students) {
                if (student.getName().equals(value)) {
                    filteredStudents.add(student);
                }
            }
        } else {
            throw new StudentException(String.format("Unrecognised column name '%s' in where clause", column));
        }
        return filteredStudents;
    }

    private void save() {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        try (PrintWriter pw = new PrintWriter(fileName)) {
            for (Student student : students) {
                pw.print(student.getName());
                pw.print(SEPARATOR);
                pw.print(student.getSurname());
                pw.print(SEPARATOR);
                pw.print(student.getIndexNumber());
                pw.println();
            }
        } catch (FileNotFoundException e) {
            println("Could not save data in file " + fileName + "because file does not exist");
        }
    }

    private void load() {
        students.clear();
        try (FileReader fr = new FileReader(fileName); BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(Pattern.quote(SEPARATOR), -1);
                Student student = new Student();
                student.setName(fields[0]);
                student.setSurname(fields[1]);
                student.setIndexNumber(fields[2]);
                students.add(student);
            }
        } catch (FileNotFoundException e) {
            println("File " + fileName + " does not exist. It will be created after adding first student");
        } catch (IOException e) {
            println("Could not load data from file " + fileName);
        }
    }

    private static void println(String s) {
        System.out.println(s);
    }
}
