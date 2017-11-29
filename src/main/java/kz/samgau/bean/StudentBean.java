package kz.samgau.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

@ManagedBean(name = "studentBean")
@SessionScoped
public class StudentBean implements Serializable {
    long id;
    String fullName;
    String faculty;
    ArrayList studentList;
    private Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    Connection connection;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/students", "root", "root");
        } catch (Exception e) {
            System.out.println(e);
        }
        return connection;
    }

    public ArrayList<StudentBean> getStudentList() {
        try {
            studentList = new ArrayList();
            connection = getConnection();
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");
            while (rs.next()) {
                StudentBean student = new StudentBean();
                student.setId(rs.getInt("id"));
                student.setFullName(rs.getString("full_name"));
                student.setFaculty(rs.getString("faculty"));
                studentList.add(student);
            }
            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return studentList;
    }

    public String save() {
        int result = 0;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO students(full_name, faculty) VALUES (?,?)");
            stmt.setString(1, fullName);
            stmt.setString(2, faculty);
            result = stmt.executeUpdate();
            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        if (result != 0)
            return "success.xhtml?faces-redirect=true";
            else return "create.xhtml?faces-redirect=true";
    }

    public String edit(long id){
        StudentBean student = null;
        System.out.println(id);
        try{
            connection = getConnection();
            Statement stmt=getConnection().createStatement();
            ResultSet rs=stmt.executeQuery("select * from students where id = "+(id));
            rs.next();
            student = new StudentBean();
            student.setId(rs.getInt("id"));
            student.setFullName(rs.getString("full_name"));
            student.setFaculty(rs.getString("faculty"));
            sessionMap.put("editStudent", student);
            connection.close();
        }catch(Exception e){
            System.out.println(e);
        }
        return "/edit.xhtml?faces-redirect=true";
    }

    public String update(StudentBean s){
        try{
            connection = getConnection();
            PreparedStatement stmt=connection.prepareStatement(
                    "update students set full_name=?,faculty=? where id=?");
            stmt.setString(1,s.getFullName());
            stmt.setString(2,s.getFaculty());
            stmt.setLong(3,s.getId());
            stmt.executeUpdate();
            connection.close();
        }catch(Exception e){
            System.out.println();
        }
        return "/success.xhtml?faces-redirect=true";
    }

    public void delete(long id){
        try{
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement("delete from students where id = "+id);
            stmt.executeUpdate();
            connection.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }
}