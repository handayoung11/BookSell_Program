package ����;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class DBSetting extends JFrame{
	JTextPane log=new JTextPane();
	SimpleAttributeSet red=new SimpleAttributeSet(log.getInputAttributes());
	SimpleAttributeSet blue=new SimpleAttributeSet(log.getInputAttributes());
	SimpleAttributeSet black=new SimpleAttributeSet(log.getInputAttributes()), blueBold, redBold;
	int sid=0;
	static Statement stmt;
	static Connection con;
	static final String path="./�����ڷ�/";
	
	public DBSetting() {
		setSize(300, 300);
		setTitle("bookdb �ʱ�ȭ");
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		add(new JScrollPane(log));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				new Thread(()->init()).start();
			}
		});
		StyleConstants.setForeground(red, Color.red);
		StyleConstants.setForeground(blue, Color.blue);
		redBold=new SimpleAttributeSet(red.copyAttributes());
		StyleConstants.setBold(redBold, true);
		blueBold=new SimpleAttributeSet(blue.copyAttributes());
		StyleConstants.setBold(blueBold, true);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new DBSetting();
	}
	
	static {
		try {
			con=DriverManager.getConnection("jdbc:mysql://localhost?serverTimezone=UTC&allowLoadLocalInfile=true", "root", "1234");
//			con=DriverManager.getConnection("jdbc:mysql://localhost?serverTimezone=UTC&allowLoadLocalInfile=true", "root", "1234");
			stmt=con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void insertLog(String cap, SimpleAttributeSet a) {
		try {
			SwingUtilities.invokeAndWait(()->{
				try {
					log.getDocument().insertString(log.getDocument().getLength(), cap+"\n", a);
					log.setSelectionStart(log.getDocument().getLength());
					log.setSelectionEnd(log.getDocument().getLength());
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void execute(String cap, String sql) {
		try {
			stmt.execute(sql);
			insertLog(cap+" ����", blue);
		} catch (SQLException e) {
			insertLog(cap+" ����", red);
			e.printStackTrace();
		}
	}
	
	void init() {
		try {
			execute("DB ����", "drop database if exists bookdb");
			execute("DB ����", "create database bookdb default character set utf8");
			stmt.execute("use bookdb");
			stmt.execute("set global local_infile=1");
			execute("main_category ���̺� ����", "create table main_category(id int primary key auto_increment not null, name varchar(20) not null)");
			execute("sub_category ���̺� ����", "create table sub_category(id int primary key auto_increment not null, main_category_id int not null,  name varchar(20) not null, foreign key(main_category_id) references main_category(id) on delete restrict on update restrict)");
			execute("book ���̺� ����", "create table book(id int primary key auto_increment not null, sub_category_id int not null, name varchar(45) not null, image mediumblob not null, stock int not null, price int not null, author varchar(45) not null, intro text not null, numpages int not null, isbn varchar(25) not null, created_by date not null, hashtag text, foreign key(sub_category_id) references sub_category(id) on delete restrict on update restrict)");
			execute("member ���̺� ����", "create table member(id int primary key auto_increment not null, login_id varchar(20) not null, login_pwd varchar(32) not null, birthdate date not null, authority varchar(5) not null, phone varchar(15) not null, email varchar(50) not null, unique index idx2(login_id), unique index idx3(phone), unique index idx4(email))");
			execute("order_log ���̺� ����", "create table order_log(id int primary key auto_increment not null, book_id int not null, member_id int not null, quantity int not null, order_time datetime not null, foreign key(book_id) references book(id) on delete restrict on update restrict, foreign key(member_id) references member(id) on delete restrict on update restrict)");
			execute("survey_category ���̺� ����", "create table survey_category(id int primary key auto_increment not null, `group` varchar(5) not null, description varchar(80) not null, index idx1 (`group`))");
			execute("survey_results ���̺� ����", "create table survey_results(id int primary key auto_increment not null, member_id int not null, survey_category_id int not null, rating int not null, foreign key(member_id) references member(id) on delete restrict on update restrict, foreign key(survey_category_id) references survey_category(id) on delete restrict on update restrict)");

			member();
			category();
			load("���� �׸� ������ ����", "survey_category");
			load("�ֹ� ���� ������ ����", "order_log");
			load("���� ��� ������ ����", "survey_results");
			execute("DB user ����	", "drop user if exists user@localhost");
			execute("DB user ����", "create user user@localhost identified by '1234'");
			execute("DB user ���� �ο�", "grant select, update, delete, insert on bookdb.* to user@localhost");
			
			
			insertLog("DB �ʱ�ȭ �Ϸ�", blueBold);
		} catch (Exception e) {
			try {
				stmt.execute("drop database if exists bookdb");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			insertLog("DB �ʱ�ȭ ����", redBold);
		} finally {
			for(int i=5; i>0; i--) {
				insertLog(i+"���� ���α׷��� ����˴ϴ�.", black);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			dispose();
		}
	}

	private void category() throws Exception{
		String mname="", sname="", bname="", c=path+"categories/";
		int mid=0;
		try {
			var pst=con.prepareStatement("update book set image=? where name=?");
			for(var mfile:Files.list(Paths.get(c)).toArray(Path[]::new)) {
				mname=mfile.getName(mfile.getNameCount()-1).toString();
				mid++;
				insertLog("main_category "+mname+" �߰� ����", blue);
				stmt.execute("insert into main_category values(0, '"+mname+"')");
				for(var sfile:Files.list(Paths.get(c+mname)).toArray(Path[]::new)) {
					sname=sfile.getName(sfile.getNameCount()-1).toString();
					sid++;
					insertLog("�� sub_category "+sname+" �߰� ����", blue);
					stmt.execute("insert into sub_category values(0, "+mid+", '"+sname+"')");
					read("id,sub_category_id,name,image,stock,price,author,intro,numpages,isbn,created_by,hashtag".split(","), "book", c+mname+"/"+sname);
					for(var bfile:Files.list(Paths.get(c+mname+"/"+sname)).toArray(Path[]::new)) {
						bname=bfile.getName(bfile.getNameCount()-1).toString().replace("txt", "");
						if(!bname.endsWith("jpg")){
							pst.setBinaryStream(1, new FileInputStream(bfile.toString().replace("txt", "jpg")));
							pst.setString(2, bname);
							pst.execute();
							insertLog(" �� "+bname+" �߰� �Ϸ�", blue);
						}
					}
					insertLog("�� sub_category "+sname+" �߰� �Ϸ�", blue);
				}
				insertLog("main_category "+mname+" �߰� �Ϸ�", blue);
			}
		} catch (Exception e) {
			insertLog(" �� "+bname+" �߰� ����", red);
			insertLog(e.getMessage(), red);
			insertLog("�� sub_category "+sname+" �߰� ����", red);
			insertLog("main_category "+mname+" �߰� ����", red);
			throw e;
		}
	}

	private void member() {
		read("id,login_id,login_pwd,birthdate,authority,phone,email".split(","), "member", path+"member_list");
		execute("member ���̺� ����", "update member set login_pwd=md5(login_pwd)");
	}

	private void read(String[] col, String table, String path) {
		try {
			HashMap<String, String> map=new HashMap<>();
			for(var file:Files.list(Paths.get(path)).toArray(Path[]::new)) {
				if(file.getFileName().toString().endsWith("jpg")) continue;
				for(var line:Files.readAllLines(file)) {
					var spl=line.split("\t");
					map.put(spl[0], spl[1]);
					if(table.equals("book")) {
						map.put("image", "1");
						map.put("sub_category_id", sid+"");
				}
				}
				var val="";
				for(int i=0; i<col.length; i++) val+=(val==""?"":",")+"'"+map.get(col[i])+"'";
				stmt.execute("insert into "+table+" values("+val+")");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void load(String cap, String table) throws Exception{
		try {
			stmt.execute("load data local infile '"+path+table+".csv' into table "+table+" fields terminated by ',' ignore 1 lines");
			insertLog(cap+" ����", blue);
		} catch (SQLException e) {
			insertLog(cap+" ����", red);
			insertLog((path+table+".csv").replace("/", "\\")+" (������ ������ ã�� �� �����ϴ�)", red);
			throw e;
		}
	}
}
