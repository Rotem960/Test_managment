package TestManagment;

import java.sql.Connection;

public interface Examable {
	
	void createExam(DataBaseHelper dbHelper, Connection conn, Stock repository);
	
}
