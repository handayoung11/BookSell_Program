package 강원;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Report extends BasePage {

	public Report() {
		super("설문결과", 1200, 500);
		JPanel c=new JPanel(new GridLayout(0, 1, 0, 10)), c_s=new JPanel(new GridLayout(1, 0, 10, 0)), s=new JPanel();
		add(s, "South");
		add(c);
		add(font(new JLabel("고객 서비스 설문결과", 0), 20, Color.BLACK), "North");
		s.add(btn("메인으로", e->move(new Main())));
		setBorder(new EmptyBorder(10, 5, 10, 5));
		int total[]=new int[5];
		try {
			var rs=stmt.executeQuery("select sc.group, sum(if(rating=1,1,0)), sum(if(rating=2,1,0)), sum(if(rating=3,1,0)), sum(if(rating=4,1,0)), sum(if(rating=5,1,0)) from survey_category sc inner join survey_results sr on sr.survey_category_id=sc.id group by sc.group order by sc.group asc");
			while(rs.next()) {
				c_s.add(new Chart(rs.getString(1), new int[] {rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6)}));
				for(int i=0; i<5; i++) total[i]+=rs.getInt(i+2);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		c.add(new Chart("전체 결과 항목", total));
		c.add(c_s);
		c.setOpaque(false);
		c_s.setOpaque(false);
		s.setOpaque(false);
	}
	Color color[]= {new Color(255, 129, 129),new Color(255, 195, 129),new Color(201, 201, 201),new Color(143, 255, 194),new Color(0, 176, 80)};
	class Chart extends JPanel{
		int data[];
		
		Chart(String cap, int data[]){
			this.data=data;
			setLayout(new BorderLayout());
			add(font(new JLabel(cap, 0), 20, Color.black), "North");
			add(new Data());
			setBorder(black);
		}
		
		class Data extends JPanel{
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				int hgap=getWidth()/5, height=getHeight()-40, total=Arrays.stream(data).sum();
				Graphics2D g2=(Graphics2D)g;

				for(int i=0; i<5; i++) {
					double p=(double)data[i]/total;
					int st=(int)(height*(1-p)), bar=(int)(height*p);
					String lbl=String.format("%.2f%% (%d)건", p*100, data[i]);
					g2.setColor(Color.BLACK);
					var b1=g2.getFontMetrics().getStringBounds(lbl, g);
					var b2=g2.getFontMetrics().getStringBounds(scol[i], g);
					if(total>0) {
						g2.drawString(lbl, (int)(hgap*i+(hgap-b1.getWidth())/2), st-5);
						g2.drawString(scol[i], (int)(hgap*i+(hgap-b2.getWidth())/2), height+25);
						g2.setColor(color[i]);
						g2.fillRect(hgap*i+(hgap-30)/2, st, 30, bar);
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		move(new Report());
		main.setVisible(true);
	}
}
