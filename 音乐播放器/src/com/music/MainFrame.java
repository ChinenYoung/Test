package com.music;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class MainFrame
{
	//弹出式菜单
	private JPopupMenu jpm;
	private JMenuItem zanting;
	private JMenuItem del;
	private JMenuItem delall;
	private JMenuItem addload;
	private JMenuItem cleartable;
	
	//窗体
	private JFrame jf;
	private JLabel state;
	private JLabel time;
	private JLabel tl;
	private JLabel showLrc;
	private JProgressBar jpb;
	private JTable table;
	private JSlider js;
	
	private DefaultTableModel model;
	private JPanel centerPanel;
	private JButton next;
	private JButton stop;
	private JButton last;
	private JButton choose;
	private JButton style;
	private JButton when;
	private JButton load;
	private JButton lrc;
	private ArrayList<File> list;
	private ArrayList<File> loadlist;
	private File selectFile;
	private int row;//table选中行
	private int popRow;//鼠标右键释放时鼠标所在的行
	private int whenrow;//当前播放音乐音乐的位置
	
	private boolean hasStop=true;
	private boolean isStop=true;//标记音乐是否正在播放
	private boolean btnstate=true;//标记播放/暂停按钮的状态
	private boolean musicplay=true;//标记音乐是否为自动播放完毕
	private boolean change=true;//用于切换当前播放列表和本地列表
	private boolean lrcstate=true;//用于标记歌词按钮是否开启
	
	private AudioInputStream audioInputStream;// 文件流
	private AudioFormat audioFormat;// 文件格式
	private SourceDataLine sourceDataLine;// 输出设备
	
	//歌词信息
    private long currentTime;
    private String currentContent;
    private Map<Long, String> maps;
    private TreeMap<Long,String> treemap;
    private Set<Long> set;
    private Iterator<Long> it;
    

    
	public MainFrame()
	{
		init();
	}
	
	@SuppressWarnings("serial")
	private void createTable()
	{
		Vector<String> columnNameV=new Vector<String>();
		Vector<Vector<String>> tableValueV=new Vector<Vector<String>>();
		columnNameV.add("歌曲信息");
		columnNameV.add("时长");
		
		model=new DefaultTableModel(tableValueV,columnNameV);
		table=new JTable(model)
		{
			//设置指定单元格可编辑
			public boolean isCellEditable(int row,int column)
			{
				return false;
			}
			
		};//创建表格对象
		JTableHeader tableHeader=table.getTableHeader();//获取表头
		//将滑动条置为透明
		JScrollPane scroll=new JScrollPane();
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setOpaque(false);
        scroll.setViewportView(table);//装载表格
        scroll.getViewport().setOpaque(false);
        scroll.setColumnHeaderView(tableHeader);//装载表头
		scroll.getColumnHeader().setOpaque(false);
		scroll.getVerticalScrollBar().setOpaque(false);
	
		centerPanel.add(scroll,"Center");
		
		table.setAutoResizeMode(0);
		table.setSelectionMode(0);
		//设置某一列的宽度
		TableColumn col=table.getColumnModel().getColumn(0);
		col.setPreferredWidth(215);
		
		//将表格设置为透明
		table.setOpaque(false);
		DefaultTableCellRenderer render = new DefaultTableCellRenderer();   
        render.setOpaque(false); //将渲染器设置为透明  
        table.setDefaultRenderer(Object.class,render);//将这个渲染器设置到table里。   
     
		//将表头设置为透明
        tableHeader.setOpaque(false);//设置头部为透明  
        tableHeader.getTable().setOpaque(false);//设置头部里面的表格透明  
        DefaultTableCellRenderer renderheader = new DefaultTableCellRenderer();   
        renderheader.setOpaque(false); //将渲染器设置为透明   
        tableHeader.setDefaultRenderer(renderheader);  
        TableCellRenderer headerRenderer =renderheader; 
        ((JLabel) headerRenderer).setHorizontalAlignment(JLabel.CENTER);
      
	}
	//通过选择按钮获取文件信息

	private void getFileMSG()
	{
		Vector<String> rowV=new Vector<String>();
		String s=selectFile.getName();
		rowV.add(s.substring(0,s.length()-4));
		rowV.add(getMusicTime(selectFile));
		model.addRow(rowV);
		list.add(selectFile);
		selectFile=null;
	}
	//获取音频文件时长
	private String getMusicTime(File f)
	{
		String dt=null,S=null,M=null;
		try
		{
			AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(f);  
			Map<?, ?> properties = fileFormat.properties();  
			Long ms = (Long)properties.get("duration"); 
			int t=(int)(ms/1000000);
			int m=t/60;int s=t%60;
			if(m<10){M="0"+Integer.toString(m);}else{M=Integer.toString(m);}
			if(s<10){S="0"+Integer.toString(s);}else{S=Integer.toString(s);}
			dt=M+":"+S;
		}
		catch(Exception ex)
		{
			MSGBOX.MessageBox("获取音频文件时长失败");
		}
		return dt;
	}
	//创建系统托盘
	private void createTray()
	{
		if(SystemTray.isSupported())
		{
			PopupMenu jpMenu=new PopupMenu();//创建弹出式菜单
			MenuItem tuichu=new MenuItem("退出");
			MenuItem shang=new MenuItem("上一曲    <<");
			MenuItem xia=new MenuItem("下一曲    >>");
			MenuItem bofang=new MenuItem("播放/暂停");
			MenuItem fangshi=new MenuItem("播放顺序");
			MenuItem geci=new MenuItem("显示歌词");
			jpMenu.add(fangshi);
			jpMenu.add(geci);
			jpMenu.add(shang);
			jpMenu.add(bofang);
			jpMenu.add(xia);
			jpMenu.add(tuichu);
			ImageIcon img = new ImageIcon(".\\res\\Icon.jpg");//创建图盘图标
			final TrayIcon tray=new TrayIcon(img.getImage(),"Chinen Music",jpMenu);
			try {
				SystemTray.getSystemTray().add(tray);
			} catch (AWTException ex) {
				MSGBOX.MessageBox("创建系统托盘失败");
			}
			//弹出菜单的菜单项的消息相应
			//退出
			tuichu.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					SystemTray.getSystemTray().remove(tray);
					System.exit(0);
				}
			});
			ButtonLinkItem(last,shang);//上一曲
			ButtonLinkItem(stop,bofang);//播放、暂停
			ButtonLinkItem(next,xia);//下一曲
			ButtonLinkItem(lrc,geci);//歌词
			ButtonLinkItem(style,fangshi);//播放顺序
			//双击托盘
			tray.addMouseListener(new MouseAdapter()
			{
	            public void mouseClicked(MouseEvent e)
	            {
	                if (e.getClickCount() == 2)
	                {
	                	jf.setVisible(true);
	                }
	            }
	        }); 
		}	
	}
	//关联弹出菜单项与按钮的联系
	private void ButtonLinkItem(final JButton btn,MenuItem mi)
	{
		mi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				btn.doClick();
			}
		});
	}
	//创建歌曲的弹出式菜单
	private void createPopMenu()
	{
		//创建一个弹出式菜单进行操作
		jpm=new JPopupMenu();
		zanting=new JMenuItem("播放/暂停");
		del=new JMenuItem("删除");
		delall=new JMenuItem("删除(包括文件)");
		addload=new JMenuItem("添加至本地");
		cleartable=new JMenuItem("清空列表");
		jpm.add(zanting);
		jpm.add(cleartable);
		jpm.add(addload);
		jpm.add(del);
		
		jpm.setBackground(Color.GREEN);
		zanting.setOpaque(false);
		del.setOpaque(false);
		delall.setOpaque(false);
		addload.setOpaque(false);
		cleartable.setOpaque(false);
		
		//弹出式菜单的消息响应函数
		zanting.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				row=popRow;
				stop.doClick();
			}
		});
		del.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//删除表中选中行
				model.removeRow(popRow);
				//删除集合中的文件
				if(change)
				{
					list.remove(popRow);
				}
				else
				{
					loadlist.remove(popRow);
				}
				if(popRow==whenrow)
				{
					//更换播放曲目
					next.doClick();
				}
			}
		});
		delall.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//删除文件
				if(change)
				{
					selectFile=list.get(popRow);
				}
				else
				{
					selectFile=loadlist.get(popRow);
				}
				selectFile.delete();
				selectFile=null;
				del.doClick();
			}
		});
		//清空列表
		cleartable.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//清空表中数据
				model.getDataVector().clear();
				model.fireTableDataChanged();
				table.updateUI();
				if(change)
				{
					list=new ArrayList<File>();
				}
				else
				{
					loadlist=null;
				}
				row=-1;
				whenrow=row;
			}
		});
		//添加至本地
		addload.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				selectFile=list.get(popRow);
				//判断文件是否存在于本地
				if(selectFile.getParent().equals("F:\\ChinenMusic"))
				{
					MSGBOX.MessageBox("文件已存在于本地");
					return;
				}
				//复制文件到本地列表
				try
				{
					FileInputStream fis=new FileInputStream(selectFile);
					FileOutputStream fos=new FileOutputStream("F:\\ChinenMusic\\"+selectFile.getName());
					BufferedOutputStream bos=new BufferedOutputStream(fos);
					BufferedInputStream bis=new BufferedInputStream(fis);
					int len=0;
					while((len=bis.read())!=-1)
					{
						bos.write(len);
					}
					bis.close();
					bos.close();
					loadlist=null;
				}
				catch(Exception ex)
				{
					MSGBOX.MessageBox("添加文件至本地列表失败");
				}
			}
		});
	}
	
	
	@SuppressWarnings("serial")
	private void init()
	{
		selectFile=null;
		row=-1;
		popRow=-1;
		whenrow=-1;
		
		jf=new JFrame("音乐播放器");
		
		state=new JLabel("当前无播放曲目");
		state.setPreferredSize(new Dimension(80,25));
		state.setHorizontalAlignment(SwingConstants.CENTER);
		tl=new JLabel("00:00");
		tl.setHorizontalAlignment(SwingConstants.CENTER);
		time=new JLabel("00:00");
		time.setHorizontalAlignment(SwingConstants.CENTER);
		showLrc=new JLabel("");
		showLrc.setHorizontalAlignment(SwingConstants.CENTER);
		showLrc.setFont(new Font("宋体",Font.BOLD,16));
		showLrc.setForeground(Color.GREEN);
		jpb=new JProgressBar();
		jpb.setIndeterminate(false);
		jpb.setPreferredSize(new Dimension(80,3));
		js=new JSlider(JSlider.VERTICAL,0,100,30);
	
		
		
		
		
		next=new JButton("＞＞");
		last=new JButton("＜＜");
		stop=new JButton("◆");
		lrc=new JButton("歌词");
		style=new JButton("顺序");
		choose=new JButton(new ImageIcon(".\\res\\Choose.jpg"));
		when=new JButton(new ImageIcon(".\\res\\When.jpg"));
		load=new JButton(new ImageIcon(".\\res\\Load.jpg"));
	
		list=new ArrayList<File>();
		
		js.setOpaque(false);
		//设置按钮透明
		lrc.setContentAreaFilled(false);
		choose.setContentAreaFilled(false);
		style.setContentAreaFilled(false);
		next.setContentAreaFilled(false);
		stop.setContentAreaFilled(false);
		last.setContentAreaFilled(false);
		when.setContentAreaFilled(false);
		load.setContentAreaFilled(false);
		//设置按钮大小
		style.setPreferredSize(new Dimension(60,28));
		lrc.setPreferredSize(new Dimension(60,28));
		next.setPreferredSize(new Dimension(60,28));
		stop.setPreferredSize(new Dimension(60,28));
		last.setPreferredSize(new Dimension(60,28));
		when.setPreferredSize(new Dimension(38,80));
		load.setPreferredSize(new Dimension(38,80));
		choose.setPreferredSize(new Dimension(63,30));
		
		//设置按钮边框不可见
		style.setBorderPainted(false);
		last.setBorderPainted(false);
		stop.setBorderPainted(false);
		next.setBorderPainted(false);
		lrc.setBorderPainted(false);
		choose.setBorderPainted(false);
		when.setBorderPainted(false);
		load.setBorderPainted(false);
		//取消按钮选中点击效果
		style.setUI(new BasicButtonUI());
		last.setUI(new BasicButtonUI());
		stop.setUI(new BasicButtonUI());
		next.setUI(new BasicButtonUI());
		lrc.setUI(new BasicButtonUI());
		choose.setUI(new BasicButtonUI());
		when.setUI(new BasicButtonUI());
		load.setUI(new BasicButtonUI());
		
		//初始化歌词相关变量
	    currentTime = 0;//存放临时时间  
	    currentContent = null;//存放临时歌词  
	    maps = new HashMap<Long, String>();//用户保存所有的歌词和时间点信息间的映射关系的Map  
	    treemap=new TreeMap<Long,String>();
	    
		JPanel topPanel=new JPanel(new GridBagLayout())
		{
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				ImageIcon img = new ImageIcon(".\\res\\North.jpg");
				img.paintIcon(this, g,0,0);
			}
		};
		JPanel bottomPanel=new JPanel()
		{
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				ImageIcon img = new ImageIcon(".\\res\\South.jpg");
				img.paintIcon(this, g,0,0);
			}
		};
		centerPanel=new JPanel(new BorderLayout())
		{
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				ImageIcon img = new ImageIcon(".\\res\\Center.jpg");
				img.paintIcon(this, g,0,0);
			}
		};
		JPanel leftPanel=new JPanel(new GridLayout(0,1))
		{
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				ImageIcon img = new ImageIcon(".\\res\\Left.jpg");
				img.paintIcon(this, g,0,0);
			}
		};
		JPanel tLeft=new JPanel(new GridLayout(0,1));
		JPanel cLeft=new JPanel(new GridLayout(0,1));
		JPanel bLeft=new JPanel(new GridLayout(0,1));
		tLeft.setOpaque(false);
		cLeft.setOpaque(false);
		bLeft.setOpaque(false);
		createTable();
		
		//控件1
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx=0;c1.gridy=0;c1.weightx=2;
		c1.fill=GridBagConstraints.HORIZONTAL;
		//控件4
		GridBagConstraints c4 = new GridBagConstraints();
		c4.insets=new Insets(0,8,0,0);
		c4.gridx=1;c4.gridy=0;c4.weightx=80;
		c4.fill=GridBagConstraints.HORIZONTAL;
		//控件3
		GridBagConstraints c3 = new GridBagConstraints();
		c3.insets=new Insets(0,8,0,0);
		c3.gridx=2;c3.gridy=0;c3.weightx=2;
		c3.fill=GridBagConstraints.HORIZONTAL;
		
		//控件5
		GridBagConstraints c5 = new GridBagConstraints();
		c5.gridx=0;c5.gridy=1;c5.weightx=2;
		c5.fill=GridBagConstraints.HORIZONTAL;
		//控件2
		GridBagConstraints c2 = new GridBagConstraints();
		c2.insets=new Insets(8,0,0,0);
		c2.gridx=1;c2.gridy=1;c2.weightx=100;
		c2.fill=GridBagConstraints.HORIZONTAL;
		//控件6
		GridBagConstraints c6 = new GridBagConstraints();
		c6.gridx=2;c6.gridy=1;c6.weightx=2;
		c6.fill=GridBagConstraints.HORIZONTAL;
		
		
		topPanel.add(new JLabel("Chinen Music"),c1);
		topPanel.add(choose,c3);
		topPanel.add(state,c4);
		topPanel.add(tl,c5);
		topPanel.add(jpb,c2);
		topPanel.add(time,c6);
		
		bottomPanel.add(style);
		bottomPanel.add(last);
		bottomPanel.add(stop);
		bottomPanel.add(next);
		bottomPanel.add(lrc);
		
		leftPanel.add(tLeft);
		leftPanel.add(cLeft);
		leftPanel.add(bLeft);

		tLeft.add(when);
		tLeft.add(load);
		bLeft.add(js);
		
		centerPanel.add(showLrc,"South");
		
		jf.add(topPanel,"North");
		jf.add(centerPanel,"Center");
		jf.add(bottomPanel,"South");
		jf.add(leftPanel,"West");
		
		jf.setBounds(200, 40, 350, 660);
		jf.setResizable(false);
		MyEvent();
		//创建系统托盘
		createTray();
		//创建弹出式菜单
		createPopMenu();
		//更改系统图标
		Toolkit tk=Toolkit.getDefaultToolkit();
		Image img=tk.createImage(".\\res\\Icon.jpg");
		jf.setIconImage(img);
		
		//load.doClick();
		jf.setVisible(true);
		
	}
	private void MyEvent()
	{
		choose.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(change)//如果在当前播放界面，则可以执行选择操作
				{
					JFileChooser filechoose=new JFileChooser();
					filechoose.setFileFilter(new FileNameExtensionFilter(
							"支持音频文件(*.mp3、*.wav)","mp3","wav"));
					filechoose.showOpenDialog(jf);
					selectFile=filechoose.getSelectedFile();
					if(selectFile!=null)
					{
						String filename=selectFile.getName();
						if(filename.endsWith(".mp3")||filename.endsWith(".wav"))
						{
							getFileMSG();
						}
						else
						{
							selectFile=null;
							MSGBOX.MessageBox("无效的音频文件");
						}
					}
				}
			}
		});
		table.addMouseListener(new MouseAdapter()
		{
            public void mouseClicked(MouseEvent e)
            {
            	if(e.getButton()==MouseEvent.BUTTON1)//响应鼠标左键点击操作
            	{
            		row=table.getSelectedRow();
            		// 双击时处理
                    if (e.getClickCount() == 2)
                    {
                    	//双击时播放音乐改变播放按钮属性 选中文件 开始播放
                    	doplay();
                    }
            	}
            }
            public void mouseReleased(MouseEvent e)
            {
            	//响应鼠标破右键释放操作
            	popRow=table.rowAtPoint(e.getPoint());//得到鼠标所在的行 
            	if(e.isPopupTrigger())
            	{
            		jpm.show(table,e.getX(),e.getY());
            	}
            }
           
        });
		style.addActionListener(new ActionListener()
		{
			int i=0;
			public void actionPerformed(ActionEvent e)
			{
				if(i%3==0)
				{
					style.setText("随机");
					i++;
				}
				else if(i%3==1)
				{
					style.setText("单曲");
					i++;
				}
				else
				{
					style.setText("顺序");
					i=0;
				}
			}
		});
		last.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!hasStop&&row!=-1)
				{
					String s=style.getText();
					if(s.equals("顺序"))
					{
						if(row>0)
						{
							row--;
						}
						else
						{
							row=table.getRowCount()-1;
						}
					}
					else if(s.equals("随机"))
					{
						//随机
						int i=(int)(Math.random()*(table.getRowCount()));
						row=i;
					}
					doplay();
				}	
			}
		});
		next.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!hasStop&&row!=-1)
				{
					PlayAll(style.getText());
				}
			}
		});
		stop.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//更改播放按钮的状态
				if(!hasStop&&whenrow==row)
				{
					if(btnstate)
					{
						stop.setText("| |");
						btnstate=false;
					}
					else
					{
						stop.setText("◆");
						btnstate=true;
					}
				}
				//选中歌曲后就执行播放操作
				if(isStop && row!=-1)
				{
					doplay();
				}
				if(whenrow!=row)
				{
					//切换曲目
					doplay();
				}
			}
		});
		lrc.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(lrcstate)
				{
					showLrc.setText("歌词搜索中...");
					lrcstate=false;
					if(it==null)
					{
						showLrc.setText("暂无歌词");
					}
				}
				else
				{
					showLrc.setText("");
					lrcstate=true;
				}
			}
			
		});
		when.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!change)//跳到当前播放页面
				{
					row=-1;//将选中的新列表的选中行置为-1
					whenrow=row;
					//清空表中数据
					model.getDataVector().clear();
					model.fireTableDataChanged();
					table.updateUI();
					//将list集合中的文件信息写入表中
					for(int r=0;r<list.size();r++)
					{
						Vector<String> rowV=new Vector<String>();
						String s=list.get(r).getName();
						rowV.add(s.substring(0,s.length()-4));
						rowV.add(getMusicTime(list.get(r)));
						model.addRow(rowV);
					}
					jpm.remove(delall);
					jpm.add(addload);
					change=true;
				}
			}
		});
		load.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				
				if(change)//跳到本地列表
				{
					row=-1;//将选中的新列表的选中行置为-1
					whenrow=row;
					//清空表中数据
					model.getDataVector().clear();
					model.fireTableDataChanged();
					table.updateUI();
					if(loadlist==null)
					{
						loadlist=new ArrayList<File>();
						File dir=new File("F:\\ChinenMusic");
						if(dir.exists())
						{
							File [] files=dir.listFiles();
							for(File f : files)
							{	
								if(f.getName().endsWith(".mp3")||f.getName().endsWith(".wav"))
								{
									loadlist.add(f);
								}
							}
						}
						else
						{
							dir.mkdir();
						}
					}
					for(int r=0;r<loadlist.size();r++)
					{
						Vector<String> rowV=new Vector<String>();
						String s=loadlist.get(r).getName();
						rowV.add(s.substring(0,s.length()-4));
						rowV.add(getMusicTime(loadlist.get(r)));
						model.addRow(rowV);
					}
					jpm.add(delall);
					jpm.remove(addload);
					change=false;	
				}
			}
		});
		js.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				
			}
		});
		
	}
	private void doplay()
	{
		if(change)
		{
			selectFile=list.get(row);
		}
		else
		{
			selectFile=loadlist.get(row);
		}
		table.setRowSelectionInterval(row, row);
		table.setSelectionForeground(Color.MAGENTA);
		Rectangle rect=table.getCellRect(row, 0, true);
		table.updateUI();
		table.scrollRectToVisible(rect);
		//将所选中曲目的行赋值给当前播放曲目的行数
		whenrow=row;
    	play();
		
	}
	//播放音乐操作
	private void play()
	{
		//如果当前正在播放其他音乐   等待之前的音乐关闭     还要等待进度条线程的关闭
		if(!hasStop)
		{
			isStop=true;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex){
				MSGBOX.MessageBox("等待音乐线程关闭错误");
			}
		}
		String s=selectFile.getName();
		state.setText(s.substring(0,s.length()-4));
		time.setText(getMusicTime(selectFile));
		musicplay=true;
		stop.setText("| |");
		btnstate=false;
		//进行播放音乐操作
		try
		{
            // 取得文件输入流
            audioInputStream = AudioSystem.getAudioInputStream(selectFile);
            audioFormat = audioInputStream.getFormat();
            // 转换mp3文件编码
            if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) 
            {
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        audioFormat.getSampleRate(), 16, audioFormat
                                .getChannels(), audioFormat.getChannels() * 2,
                        audioFormat.getSampleRate(), false);
                audioInputStream = AudioSystem.getAudioInputStream(audioFormat,
                        audioInputStream);
            }
 
            // 打开输出设备
            DataLine.Info dataLineInfo = new DataLine.Info(
                    SourceDataLine.class, audioFormat,
                    AudioSystem.NOT_SPECIFIED);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
            
            //得到歌词到maps里
            set=null;it=null;
            maps.clear();//清空maps里面的映射
            treemap.clear();
    		if(parser("F:\\ChinenMusic\\"+state.getText()+".lrc"))
    		{
    			treemap.putAll(maps);
        		set=treemap.keySet();//构建Map集合中所有key对象的集合
        		it=set.iterator();//创建集合迭代器
    		}
            // 创建独立线程进行播放
            isStop = false;//表示当前音乐正在运行
            Thread playThread = new Thread(new PlayThread());
            playThread.start();
              
        } 
		catch (Exception ex)
		{
			MSGBOX.MessageBox("音乐播放准备出现错误");
        }
	}
	//自动播放音乐
	private void PlayAll(String s)
	{
		if(row>-1)
		{
			if(s.equals("顺序"))
			{
				if(row<(table.getRowCount()-1))
				{
					row++;
				}
				else
				{
					row=0;
				}	
			}
			else if(s.equals("随机"))
			{
				int i=(int)(Math.random()*(table.getRowCount()));
				row=i;
			}
	    	doplay();
		}
		
	}
	
	class PlayThread extends Thread
	{
	    byte tempBuffer[] = new byte[320];
	    @SuppressWarnings("deprecation")
		public void run()
	    {
	        try 
	        {
	            int cnt;
	            hasStop=false;//标记音乐是否进行
	            //开启线程获取音乐播放的进度
	            Thread playprocess=new Thread(new PlayProcess());
	            playprocess.start();
	            // 读取数据到缓存数据
	            while ((cnt = audioInputStream.read(tempBuffer, 0,
	                    tempBuffer.length)) != -1)
	            {
	            	if (isStop)
	                {
	            		musicplay=false;
	                	break;//如果isStop为真则结束当前播放的音乐
	                }
	            	if (cnt > 0)
		            {
	                    // 写入缓存数据
	                    sourceDataLine.write(tempBuffer, 0, cnt);
	                }
	                //暂停与播放
                	while(btnstate)
                	{
                		if (isStop)
    	                {
                			btnstate=false;
    	                }  
                	}
	            } 
	            //Block等待临时数据被输出为空
	            sourceDataLine.drain();
	            sourceDataLine.close();
	            hasStop=true;//音乐已结束
	            //改变音乐初始状态
	            state.setText("当前无播放曲目");
	            stop.setText("◆");
	            if(!lrcstate)
	            {
	            	showLrc.setText(" ");
	            }
	            btnstate=true;
	            isStop=true;
	            playprocess.stop();
	            if(musicplay)
	            {
	            	PlayAll(style.getText());	            	
	            }
	        } 
	        catch (Exception ex)
	        {
	           MSGBOX.MessageBox("开启新线程播放音乐出错");
	        }
	    }
	}
	//进度条线程
	class PlayProcess extends Thread
	{	
		//获取当前歌曲的总时长
        String dt=time.getText();//获取当前音频的时长
        String m=dt.substring(0,2);
        String s=dt.substring(3,5);
        int t=Integer.parseInt(m)*60+Integer.parseInt(s);
        
		public void run()
		{
			int i=0;//标记线程中循环运行的次数
			boolean isShow=true;//标记某行歌词是否显示
			Long tt = null;//记录TreeMap中的时间
			//判断是否有歌词
			if(!lrcstate&&it==null)
			{
				showLrc.setText("暂无歌词");
			}
			//循环开始
			while(!hasStop)//歌曲在播放中  
			{
				if(!btnstate)//判断歌曲是否暂停
				{
					//动态显示歌词
					if(!lrcstate&&it!=null)
					{
						if(isShow&&it.hasNext())
						{
							tt=it.next();
						}
						if(i*1000>=tt)
						{
							String lrc=(String)treemap.get(tt);	
							showLrc.setText(lrc);
							isShow=true;
						}
						else
						{
							isShow=false;
						}			
					}
					
					//动态显示歌曲播放进度
					try
					{
						jpb.setValue((i*100)/t);
						if(i<10)
						{
							tl.setText("00:0"+Integer.toString(i));
						}
						else if(i<60)
						{
							tl.setText("00:"+Integer.toString(i));
						}
						else if(i<600&&i%60<10)
						{
							tl.setText("0"+Integer.toString(i/60)+":0"+Integer.toString(i%60));
						}
						else if(i<600)
						{
							tl.setText("0"+Integer.toString(i/60)+":"+Integer.toString(i%60));
						}
						else
						{
							tl.setText(Integer.toString(i/60)+":"+Integer.toString(i%60));
						}
						Thread.sleep(995);
						i++;
					}
					catch (InterruptedException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
	}
	//主函数
	public static void main(String [] args)
	{
		new MainFrame();
	}
	
	//得到歌词信息
	private boolean parser(String path) throws Exception
	{
		File f=new File(path);
		if(!f.exists())
		{
			return false;
		}
		//以特定的编码方式读取Lrc文件
		InputStream ins=new FileInputStream(f);//输入流
		InputStreamReader inr=new InputStreamReader(ins,"gbk");
		BufferedReader reader=new BufferedReader(inr);
		String line = null;  
        while ((line = reader.readLine()) != null)
        {
            parserLine(line);  
        }
        ins.close();
        inr.close();
        reader.close();
        return true;
	}
	private void parserLine(String str) 
	{  
        if (!(str.startsWith("[ti:")||str.startsWith("[ar:")||str.startsWith("[al:")||str.startsWith("[by:"))) 
        {  
        	 String reg = "\\[(\\d{2}:\\d{2}\\.\\d{2})\\]";  
             Pattern pattern = Pattern.compile(reg);  
             Matcher matcher = pattern.matcher(str);  
             while (matcher.find()) 
             {    
                 int groupCount = matcher.groupCount();  
                 for (int i = 0; i <= groupCount; i++) 
                 {  
                     String timeStr = matcher.group(i);  
                     if (i == 1) 
                     {  
                         currentTime = strToLong(timeStr);  
                     }  
                 }  
                 String[] content = pattern.split(str);  
                 for (int i = 0; i < content.length; i++)
                 {  
                     if (i == content.length - 1)
                     {  
                         currentContent = content[i];  
                     }  
                 }  
                 maps.put(currentTime, currentContent);
             }
        }  
      
    }  
	private long strToLong(String timeStr) 
	{  
        String[] s = timeStr.split(":");  
        int min = Integer.parseInt(s[0]);  
        String[] ss = s[1].split("\\.");  
        int sec = Integer.parseInt(ss[0]);  
        int mill = Integer.parseInt(ss[1]);  
        return min * 60 * 1000 + sec * 1000 + mill * 10;  
    }  
	
}