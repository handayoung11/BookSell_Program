package 강원;

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
	JCheckBox save=new JCheckBox("ID 저장");
	Preferences pre=Preferences.userNodeForPackage(BasePage.class);
	
	public Login() {
		super("로그인", 280, 210);
		JPanel cp=new JPanel(new GridLayout(0, 1, 5, 0)), south=new JPanel(new GridLayout(1, 0, 5, 0));
		add(cp);
		add(south, "South");
		String cap[]="아이디 또는 이메일, 휴대폰 번호\t비밀번호".split("\t");
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
		south.add(btn("로그인", e->login()));
		south.add(btn("회원가입", e->move(new SignUp())));
	}
	
	void login() {
		LocalDateTime now=LocalDateTime.now(), se=LocalDateTime.parse(pre.get("login", LocalDateTime.now().toString()));
		try {
			long dif=se.until(now, ChronoUnit.SECONDS);
			if(dif<15) {
				err_msg(15-dif+"초후 시도해주세요.", "로그인 BLOCK");
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
			msg("환영합니다!", "Welcome");
			if(save.isSelected()) pre.put("id", txt[0].getText());
			clearHistory();
			move(new Main());
		} catch (SQLException e) {
			e.printStackTrace();
			cnt++;
			err_msg("일치하는 정보가 없습니다.\n"+cnt+"회 틀렸습니다.\n연속 3회 틀릴 시, 15초간 로그인 기능이 중단됩니다.", "정보 확인");
			if(cnt==3) {
				cnt=0;
				pre.put("login", LocalDateTime.now().toString());
				err_msg("연속 3회 실패로 15초간 로그인이 불가능합니다.", "Login Block");
			}
		}
	}
}
