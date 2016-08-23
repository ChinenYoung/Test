package com.music;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
public class MSGBOX
{
	private static JButton sure;
	private static JButton cancel;
	private static JButton exit;
	private static JButton choose;
	private static JLabel jl;
	private static JDialog jlog;
	
	
	private MSGBOX()
	{}
	static
	{
		jlog=new JDialog();
		jlog.setTitle("��ʾ");
		jlog.setModal(true);
		jlog.setLayout(null);
		jlog.setResizable(false);
		jlog.setBounds(500, 180, 300, 200);
		
		jl=new JLabel();
		jl.setFont(new Font("Dialog",3,14));
		jl.setBounds(30, 50, 240, 30);
		sure=new JButton("ȷ��");
		sure.setBounds(50, 120, 85, 30);
		exit=new JButton("�˳�");
		exit.setBounds(50, 120, 85, 30);
		cancel=new JButton();
		cancel.setBounds(155, 120, 85, 30);
		
		
		jlog.add(jl);
		jlog.add(cancel);
	
		MyEvent();
	}
	//��ͨ����Ϣ֪ͨ
	public static void MessageBox(String str)
	{
		jl.setText(str);
		cancel.setText("ȷ��");
		jlog.setVisible(true);
	}
	//�˳�ϵͳ��ʾ
	public static void MessageBox(String str,boolean t)
	{
		jl.setText(str);
		jlog.add(exit);
		cancel.setText("ȡ��");
		jlog.setVisible(true);
	}
	//ѡ����ʾ
	public static void MessageBox(String str,JButton btn)
	{
		jl.setText(str);
		jlog.add(sure);
		cancel.setText("ȡ��");
		choose=btn;
		jlog.setVisible(true);
		
	}
	//ѡ��ťִ��

	private static void MyEvent()
	{
		jlog.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				jlog.dispose();
				jlog.remove(exit);
				jlog.remove(sure);
			}
		});
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				jlog.dispose();
				jlog.remove(exit);
				jlog.remove(sure);
				
			}
			
		});
		exit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
				
		});
		sure.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				choose.doClick();
				jlog.dispose();
				jlog.remove(sure);
			}	
		});
	}
}


