package 강원;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class Survey extends BasePage {
	JButton submit;
	JPanel survey=new JPanel(new FlowLayout(0));
	
	public Survey() {
		super("설문조사", 750, 700);
		add(font(new JLabel("고객 만족도 조사", 0), 20, Color.BLACK), "North");
		var s=new JPanel();
		add(s, "South");
		s.add(submit=btn("제출", e->submit()));
		s.add(btn("메인으로", e->move(new Main())));
		s.setOpaque(false);
		init();
	}
	
	void init() {
		add(survey);
		try {
			var rs=stmt.executeQuery("select *, ifnull((select rating from survey_results sr where sr.survey_category_id=sc.id and sr.member_id="+no+"), 0) from survey_category sc");
			String tmp="";
			boolean flag=false;
			while(rs.next()) {
				if(!tmp.equals(rs.getString(2))) {
					tmp=rs.getString(2);
					survey.add(font(new JLabel(tmp), 20, Color.BLACK));
				}
				survey.add(new SurveyItem(rs.getInt(1), rs.getString(3), rs.getInt(4)));
				if(rs.getInt(4)!=0) flag=true;
			}
			if(flag) {
				msg("이미 설문 조사를 응했습니다.", "확인");
				submit.setEnabled(false);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		setSession(6);
		move(new Survey());
		main.setVisible(true);
	}
	
	class SurveyItem extends JPanel{
		int id;
		JRadioButton radio[]=new JRadioButton[5];
		ButtonGroup gr=new ButtonGroup();
		
		public SurveyItem(int id, String q, int rating) {
			this.id=id;
			setLayout(new FlowLayout(0));
			setPreferredSize(new Dimension(720, 40));
			add(BasePage.size(font(new JLabel(id+". "+q), 11, Color.BLACK), 300, 30));
			for(int i=0; i<5; i++) {
				add(radio[i]=new JRadioButton(scol[i]));
				gr.add(radio[i]);
				if(rating!=0) radio[i].setEnabled(false);
				if(rating-1==i) radio[i].setSelected(true);
			}
		}
		
		int getRate() {
			for(int i=0; i<5; i++) if(radio[i].isSelected()) return i+1;
			return 0;
		}
	}
	
	void submit() {
		ArrayList<SurveyItem> list=new ArrayList<>();
		for(var com:survey.getComponents()) {
			if(com instanceof SurveyItem) {
				var item=(SurveyItem)com;
				if(item.getRate()==0) {
					err_msg("체크하지 않은 항목이 있습니다!", "확인");
					return;
				}
				list.add(item);
			}
		}
		
		for(var item:list) {
			try {
				stmt.execute("insert into survey_results values(0, "+no+", "+item.id+", "+item.getRate()+")");
				for(int i=0; i<5; i++) item.radio[i].setEnabled(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		msg("설문에 응해주셔서 감사합니다.", "안내");
		submit.setEnabled(false);
		move(new Main());
	}
}
