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
		jlog.setTitle("提示");
		jlog.setModal(true);
		jlog.setLayout(null);
		jlog.setResizable(false);
		jlog.setBounds(500, 180, 300, 200);
		
		jl=new JLabel();
		jl.setFont(new Font("Dialog",3,14));
		jl.setBounds(30, 50, 240, 30);
		sure=new JButton("确定");
		sure.setBounds(50, 120, 85, 30);
		exit=new JButton("退出");
		exit.setBounds(50, 120, 85, 30);
		cancel=new JButton();
		cancel.setBounds(155, 120, 85, 30);
		
		
		jlog.add(jl);
		jlog.add(cancel);
	
		MyEvent();
	}
	//普通的消息通知
	public static void MessageBox(String str)
	{
		jl.setText(str);
		cancel.setText("确定");
		jlog.setVisible(true);
	}
	//退出系统提示
	public static void MessageBox(String str,boolean t)
	{
		jl.setText(str);
		jlog.add(exit);
		cancel.setText("取消");
		jlog.setVisible(true);
	}
	//选择提示
	public static void MessageBox(String str,JButton btn)
	{
		jl.setText(str);
		jlog.add(sure);
		cancel.setText("取消");
		choose=btn;
		jlog.setVisible(true);
		
	}
	//选择按钮执行

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


