package ����;

import java.awt.Color;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class OrderLog extends BasePage{
	int page=0;
	DefaultTableModel model=new DefaultTableModel(null, "�ֹ�����,�ð�,����,����,����".split(","));
	JTable table=new JTable(model);
	
	public OrderLog() {
		super("�ֹ�����", 700, 300);
		var scr=new JScrollPane(table);
		var s=new JPanel();
		add(s, "South");
		add(scr);
		add(font(new JLabel(ID+"�� ���ų���", 0), 20, Color.black), "North");
		
		s.add(btn("��������", e->move(new Main())));
		next();
		
		var var=scr.getVerticalScrollBar();
		var.addAdjustmentListener(it -> {
			if(it.getValue()!=0 && it.getValue()+var.getHeight()==var.getMaximum()) next();
		});
		s.setOpaque(false);
	}
	
	void next(){
		try {
			var rs=stmt.executeQuery("select date(order_time), date_format(order_time, '%H:%i:%s'), name, author, quantity from order_log o inner join book b on b.id=o.book_id where member_id="+no+" order by order_time desc limit "+page+", 15");
			int cnt=0;
			Object row[]=new Object[model.getColumnCount()];
			
			while(rs.next()) {
				for(int i=0; i<row.length; i++) row[i]=rs.getString(i+1);
				model.addRow(row);
				cnt++;
			}
			if(cnt==0) msg("�� �̻��� ���ų����� �����ϴ�.", "�ȳ�");
			else page+=15;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
