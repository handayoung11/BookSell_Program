package ����;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class Main extends BasePage {

	public Main() {
		super("����", 500, 240);
		setLayout(new BorderLayout(0, 10));
		if(authority.equals("USER")) {
			JPanel c=new JPanel(new GridLayout(1, 0, 10, 0)), s=new JPanel(new GridLayout(1, 0, 10, 0));
			add(c);
			add(s, "South");
			c.add(btn("���� �˻�", getIcon(path+"searching_book.png", 150, 150), e->move(new Search())));
			c.add(btn("���� ����", getIcon(path+"survey_icon.png", 150, 150), e->move(new Survey())));
			s.add(btn("���� ���ų���", e->move(new OrderLog())));
			s.add(btn("�α׾ƿ�", e->{
				clearHistory();
				move(new Login());
			}));
		} else {
			add(btn("���� ���", getIcon(path+"reporting_icon.png", 150, 150), e->move(new Report())));
			add(btn("�α׾ƿ�", e->{
				clearHistory();
				move(new Login());
			}), "South");
		}
	}
	
	public static void main(String[] args) {
		setSession(6);
		move(new Main());
		main.setVisible(true);
	}
}
