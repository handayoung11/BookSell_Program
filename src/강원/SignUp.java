package 강원;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class SignUp extends BasePage {
	JLabel val[]=new JLabel[6];
	JTextField txt[]={
		new JTextField(),
		new JPasswordField(),
		new JPasswordField(),
		new JTextField(),
		new JTextField(),
		new JTextField()
	};
	String cap[]="아이디,비밀번호,비밀번호 확인,생년월일,휴대폰,이메일".split(",");
	ArrayList<List<Validation>> valid=new ArrayList<>();
	
	class Validation{
		Predicate<String> pre;
		String msg;
		boolean err;
		
		Validation(Predicate<String>p, String m){
			pre=p;
			msg=m;
		}
	}
	
	public SignUp() {
		super("회원가입", 330, 420);
		JPanel c=new JPanel(new FlowLayout(0)), sp=new JPanel();
		
		for(int i=0; i<cap.length; i++) {
			c.add(size(font(new JLabel(cap[i]+" :"), 11, Color.BLACK), 100, 25));
			c.add(size(txt[i], 170, 25));
			c.add(size(val[i]=font(new JLabel(), 11, Color.red), 310, 20));
			val[i].setBorder(new EmptyBorder(0, 110, 0, 0));
			val[i].setVerticalAlignment(JLabel.TOP);
			txt[i].addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					chk();
				}
			});
		}
		size(val[5], 310, 40);
		
		valid.add(Arrays.asList(
				new Validation(s->!s.matches(".*\\W.*"), "영문과 숫자만 입력"),
				new Validation(s->dup(s, "login_id"), "이미 존재하는 아이디")
				));
		valid.add(Arrays.asList(
				new Validation(s->contain(s), "연속되는 3자리 없어야함"),
				new Validation(s->s.matches(".*[a-zA-Z].*"), "영문이 포함되어야 함"),
				new Validation(s->s.matches(".*\\d.*"), "숫자가 포함되어야 함")
				));
		valid.add(Arrays.asList(
				new Validation(s->s.equals(txt[1].getText()), "비밀번호와 일치해야 함")
				));
		valid.add(Arrays.asList(
				new Validation(s->s.matches("\\d{4}-\\d{1,2}-\\d{1,2}"), "yyyy-MM-dd 형식으로 입력"),
				new Validation(s->chkDate(s), "년월일 정확히 입력")
				));
		valid.add(Arrays.asList(
				new Validation(s->s.matches("\\d{3}-\\d{4}-\\d{4}"), "000-0000-0000 형식으로 입력"),
				new Validation(s->dup(s, "phone"), "이미 존재하는 휴대폰")
				));
		valid.add(Arrays.asList(
				new Validation(s->s.matches("[\\w_]+@[\\w_]+\\.[\\w_]+"), "xxx@xxx.xxx 형식으로 입력"),
				new Validation(s->dup(s, "email"), "이미 존재하는 이메일")
				));
		
		sp.add(btn("회원가입", e->sign()));
		sp.add(btn("취소", e->move(new Login())));
		
		add(sp, "South");
		add(c);
		
		setMax(txt[0], 20);
		setMax(txt[3], 10);
		setMax(txt[4], 13);
		setMax(txt[5], 50);
		sp.setOpaque(false);
		c.setOpaque(false);
	}

	boolean chkDate(String s) {
		SimpleDateFormat smf=new SimpleDateFormat("yyyy-MM-dd");
		smf.setLenient(false);
		try {
			smf.parse(s);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
	
	boolean dup(String s, String field) {
		try {
			var rs=stmt.executeQuery("select * from member where "+field+"='"+s+"'");
			if(rs.next()) return false;
		} catch (SQLException e) {
		}
		
		return true;
	}
	
	boolean contain(String s) {
		String keys[]= {"`1234567890-=", "~!@#$%^&*()_+", "qwertyuiop[]\\", "QWERTYUIOP{}|", "asdfghjkl;'", "ASDFGHJKL:\"", "zxcvbnm,./", "ZXCVBNM<>?"};
		
		for(var key:keys){
			for(int i=0; i<key.length()-2; i++)
				if(s.contains(key.substring(i, i+3))) return false;
		}
		return true;
	}
	
	void chk() {
		for(int i=0; i<cap.length; i++) {
			if(txt[i].getText().equals("")) {
				val[i].setText("");
				continue;
			}
			
			for(var item:valid.get(i)) {
				item.err=false;
				if(!item.pre.test(txt[i].getText())) {
					val[i].setText(item.msg);
					item.err=true;
					break;
				}
				if(item.err==false) val[i].setText("");
			}
		}
	}
	
	private void setMax(JTextField txt, int len) {
		txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(txt.getText().length()==len) e.consume();
			}
		});
	}

	void sign() {
		ArrayList<String> err=new ArrayList<>();
		for(int i=0; i<cap.length; i++) {
			if(txt[i].getText().equals("")) {
				err.add(cap[i]);
				continue;
			}
			for(var tem:valid.get(i)) {
				if(tem.err) {
					err.add(cap[i]);
					break;
				}
			}
		}
		
		if(err.size()>0) {
			err_msg("입력되지 않았거나, 잘못 입력된 필드가 있습니다.\n해당 필드 : "+String.join(",", err), "입력 오류");
			return;
		}
		
		try {
			stmt.execute("insert into member values(0, '"+txt[0].getText()+"', md5('"+txt[1].getText()+"'), '"+txt[3].getText()+"', 'USER', '"+txt[4].getText()+"', '"+txt[5].getText()+"')");
			msg("회원가입을 축하합니다.", "회원가입");
			move(new Login());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
