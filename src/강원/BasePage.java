package 강원;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class BasePage extends JPanel{
	static Statement stmt;
	static Connection con;
	static final String path="./지급자료/icon_list/", scol[]= {"매우 불만족", "불만족", "보통", "만족", "매우 만족"};
	static String ID, authority;
	static final Border black=new LineBorder(Color.BLACK);
	static final MainFrame main=new MainFrame(); 
	static int no;
	String name;
	
	static void move(BasePage page) {
		main.addPage(page);
	}
	
	public BasePage(String name, int w, int h) {
		this.name=name;
		setPreferredSize(new Dimension(w, h));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setLayout(new BorderLayout());
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		RoundRectangle2D rec=new RoundRectangle2D.Float(1.5f, 1.5f, getWidth()-3, getHeight()-3, 15, 15);
		Graphics2D g2=(Graphics2D)g;
		g2.setStroke(new BasicStroke(3));
		g2.setColor(Color.white);
		g2.fill(rec);
		g2.setColor(Color.LIGHT_GRAY);
		g2.draw(rec);
	}
	
	static <T extends JComponent> T size(T comp, int w, int h) {
		comp.setPreferredSize(new Dimension(w, h));
		return comp;
	}
	
	static JButton btn(String txt, ActionListener a) {
		JButton jB=new JButton(txt);
		jB.addActionListener(a);
		return jB;
	}
	
	static JButton btn(String txt, ImageIcon icon, ActionListener a) {
		JButton jB=new JButton(txt, icon);
		jB.setVerticalTextPosition(JLabel.BOTTOM);
		jB.setHorizontalTextPosition(JLabel.CENTER);
		jB.addActionListener(a);
		return jB;
	}
	
	void clearHistory() {
		main.pages.clear();
		main.idx=-1;
		main.update();
	}
	
	static void setSession(int id) {
		try {
			var rs=stmt.executeQuery("select * from member where id="+id);
			rs.next();
			ID=rs.getString(2);
			no=rs.getInt(1);
			authority=rs.getString("authority");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static JLabel font(JLabel jL, int size, Color color) {
		jL.setFont(new Font("맑은 고딕", 1, size));
		jL.setForeground(color);
		return jL;
	}
	
	void err_msg(String msg, String tit) {
		JOptionPane.showMessageDialog(null, msg, tit, JOptionPane.ERROR_MESSAGE);
	}
	
	void msg(String msg, String tit) {
		JOptionPane.showMessageDialog(null, msg, tit, JOptionPane.INFORMATION_MESSAGE);
	}
	
	static ImageIcon getIcon(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}
	
	static {
		try {
			con=DriverManager.getConnection("jdbc:mysql://localhost/bookdb?serverTimezone=UTC&allowLoadLocalInfile=true", "user", "1234");
			stmt=con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
