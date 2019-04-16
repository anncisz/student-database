import java.util.ArrayList;
import java.util.List;

public class Student {
    public static final String NAME_COLUMN = "name";
    public static final String SURNAME_COLUMN = "surname";
    public static final String INDEX_COLUMN = "indexNumber";

    private String name;
    private String surname;
    private String indexNumber;

    public String toString(List<String> columnsToDisplay) {
        List<String> values = new ArrayList<>();
        for (String column : columnsToDisplay) {
            if (column.equalsIgnoreCase(NAME_COLUMN)) {
                values.add(name);
            } else if (column.equalsIgnoreCase(SURNAME_COLUMN)) {
                values.add(surname);
            } else if (column.equalsIgnoreCase(INDEX_COLUMN)) {
                values.add(indexNumber);
            } else {
                throw new StudentException("Unknown column name: " + column);
            }
        }
        return String.join(StudentDatabase.DISPLAY_SEPARATOR, values);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(String indexNumber) {
        this.indexNumber = indexNumber;
    }
}
