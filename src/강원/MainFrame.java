package 강원;

import static 강원.BasePage.font;
import static 강원.BasePage.getIcon;
import static 강원.BasePage.main;
import static 강원.BasePage.path;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainFrame extends JFrame{
	JPanel np=new JPanel(new FlowLayout(0)), c=new JPanel(new GridBagLayout());
	JButton prev=new JButton("뒤로", getIcon(path+"previous_icon.png", 20, 20)), next=new JButton("앞으로", getIcon(path+"next_icon.png", 20, 20));
	ArrayList<BasePage> pages=new ArrayList<>();
	int idx=-1;
			
	void update() {
		np.removeAll();
		c.removeAll();
		
		np.add(prev);
		np.add(next);
		
		for(int i=0; i<pages.size(); i++) {
			np.add(font(new JLabel(pages.get(i).name), 15, i==idx?Color.blue:Color.black));
			if(i!=pages.size()-1) np.add(new JLabel(">"));
		}
		
		prev.setEnabled(idx>0);
		next.setEnabled(pages.size()-1>idx);
		
		if(idx!=-1) c.add(pages.get(idx));
		np.setBackground(Color.white);
		repaint();
		revalidate();
	}
	
	void addPage(BasePage page) {
		if(pages.size()==5) pages.remove(0);
		pages.add(page);
		idx=pages.size()-1;
		update();
	}
	
	public static void main(String[] args) {
		main.addPage(new Login());
		main.setVisible(true);
	}
	
	void changePage(int idx) {
		this.idx=idx;
		update();
	}
	
	public MainFrame() {
		setSize(1500, 850);
		setIconImage(Toolkit.getDefaultToolkit().getImage(path+"main_icon.png"));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(2);
		add(np, "North");
		add(c);
		prev.addActionListener(e->changePage(idx-1));
		next.addActionListener(e->changePage(idx+1));
	}
}
