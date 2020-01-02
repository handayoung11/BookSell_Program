package ����;

import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.prefs.Preferences;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login extends BasePage{
	JTextField txt[]= {
			new JTextField(),
			new JPasswordField()
	};
	int cnt=0;
	JCheckBox save=new JCheckBox("ID ����");
	Preferences pre=Preferences.userNodeForPackage(BasePage.class);
	
	public Login() {
		super("�α���", 280, 210);
		JPanel cp=new JPanel(new GridLayout(0, 1, 5, 0)), south=new JPanel(new GridLayout(1, 0, 5, 0));
		add(cp);
		add(south, "South");
		String cap[]="���̵� �Ǵ� �̸���, �޴��� ��ȣ\t��й�ȣ".split("\t");
		for(int i=0; i<2; i++) {
			cp.add(new JLabel(cap[i]));
			cp.add(txt[i]);
		}
		cp.add(save);
		cp.setOpaque(false);
		south.setOpaque(false);
		save.setOpaque(false);
		
		if(!pre.get("id", "").equals("")) {
			save.setSelected(true);
			txt[0].setText(pre.get("id", ""));
		}
		save.addActionListener(it -> {
			if(!save.isSelected()) pre.remove("id");
		});
		south.add(btn("�α���", e->login()));
		south.add(btn("ȸ������", e->move(new SignUp())));
	}
	
	void login() {
		LocalDateTime now=LocalDateTime.now(), se=LocalDateTime.parse(pre.get("login", LocalDateTime.now().toString()));
		try {
			long dif=se.until(now, ChronoUnit.SECONDS);
			if(dif<15) {
				err_msg(15-dif+"���� �õ����ּ���.", "�α��� BLOCK");
				return;
			}
		} catch (Exception e1) {
		}
		try {
			var rs=stmt.executeQuery("select * from member where '"+txt[0].getText()+"' in (login_id, phone, email) and login_pwd=md5('"+txt[1].getText()+"')");
			rs.next();
			ID=rs.getString(2);
			no=rs.getInt(1);
			authority=rs.getString("authority");
			msg("ȯ���մϴ�!", "Welcome");
			if(save.isSelected()) pre.put("id", txt[0].getText());
			clearHistory();
			move(new Main());
		} catch (SQLException e) {
			e.printStackTrace();
			cnt++;
			err_msg("��ġ�ϴ� ������ �����ϴ�.\n"+cnt+"ȸ Ʋ�Ƚ��ϴ�.\n���� 3ȸ Ʋ�� ��, 15�ʰ� �α��� ����� �ߴܵ˴ϴ�.", "���� Ȯ��");
			if(cnt==3) {
				cnt=0;
				pre.put("login", LocalDateTime.now().toString());
				err_msg("���� 3ȸ ���з� 15�ʰ� �α����� �Ұ����մϴ�.", "Login Block");
			}
		}
	}
}
