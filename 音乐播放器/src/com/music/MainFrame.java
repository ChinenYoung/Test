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
	//����ʽ�˵�
	private JPopupMenu jpm;
	private JMenuItem zanting;
	private JMenuItem del;
	private JMenuItem delall;
	private JMenuItem addload;
	private JMenuItem cleartable;
	
	//����
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
	private int row;//tableѡ����
	private int popRow;//����Ҽ��ͷ�ʱ������ڵ���
	private int whenrow;//��ǰ�����������ֵ�λ��
	
	private boolean hasStop=true;
	private boolean isStop=true;//��������Ƿ����ڲ���
	private boolean btnstate=true;//��ǲ���/��ͣ��ť��״̬
	private boolean musicplay=true;//��������Ƿ�Ϊ�Զ��������
	private boolean change=true;//�����л���ǰ�����б�ͱ����б�
	private boolean lrcstate=true;//���ڱ�Ǹ�ʰ�ť�Ƿ���
	
	private AudioInputStream audioInputStream;// �ļ���
	private AudioFormat audioFormat;// �ļ���ʽ
	private SourceDataLine sourceDataLine;// ����豸
	
	//�����Ϣ
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
		columnNameV.add("������Ϣ");
		columnNameV.add("ʱ��");
		
		model=new DefaultTableModel(tableValueV,columnNameV);
		table=new JTable(model)
		{
			//����ָ����Ԫ��ɱ༭
			public boolean isCellEditable(int row,int column)
			{
				return false;
			}
			
		};//����������
		JTableHeader tableHeader=table.getTableHeader();//��ȡ��ͷ
		//����������Ϊ͸��
		JScrollPane scroll=new JScrollPane();
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setOpaque(false);
        scroll.setViewportView(table);//װ�ر��
        scroll.getViewport().setOpaque(false);
        scroll.setColumnHeaderView(tableHeader);//װ�ر�ͷ
		scroll.getColumnHeader().setOpaque(false);
		scroll.getVerticalScrollBar().setOpaque(false);
	
		centerPanel.add(scroll,"Center");
		
		table.setAutoResizeMode(0);
		table.setSelectionMode(0);
		//����ĳһ�еĿ��
		TableColumn col=table.getColumnModel().getColumn(0);
		col.setPreferredWidth(215);
		
		//���������Ϊ͸��
		table.setOpaque(false);
		DefaultTableCellRenderer render = new DefaultTableCellRenderer();   
        render.setOpaque(false); //����Ⱦ������Ϊ͸��  
        table.setDefaultRenderer(Object.class,render);//�������Ⱦ�����õ�table�   
     
		//����ͷ����Ϊ͸��
        tableHeader.setOpaque(false);//����ͷ��Ϊ͸��  
        tableHeader.getTable().setOpaque(false);//����ͷ������ı��͸��  
        DefaultTableCellRenderer renderheader = new DefaultTableCellRenderer();   
        renderheader.setOpaque(false); //����Ⱦ������Ϊ͸��   
        tableHeader.setDefaultRenderer(renderheader);  
        TableCellRenderer headerRenderer =renderheader; 
        ((JLabel) headerRenderer).setHorizontalAlignment(JLabel.CENTER);
      
	}
	//ͨ��ѡ��ť��ȡ�ļ���Ϣ

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
	//��ȡ��Ƶ�ļ�ʱ��
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
			MSGBOX.MessageBox("��ȡ��Ƶ�ļ�ʱ��ʧ��");
		}
		return dt;
	}
	//����ϵͳ����
	private void createTray()
	{
		if(SystemTray.isSupported())
		{
			PopupMenu jpMenu=new PopupMenu();//��������ʽ�˵�
			MenuItem tuichu=new MenuItem("�˳�");
			MenuItem shang=new MenuItem("��һ��    <<");
			MenuItem xia=new MenuItem("��һ��    >>");
			MenuItem bofang=new MenuItem("����/��ͣ");
			MenuItem fangshi=new MenuItem("����˳��");
			MenuItem geci=new MenuItem("��ʾ���");
			jpMenu.add(fangshi);
			jpMenu.add(geci);
			jpMenu.add(shang);
			jpMenu.add(bofang);
			jpMenu.add(xia);
			jpMenu.add(tuichu);
			ImageIcon img = new ImageIcon(".\\res\\Icon.jpg");//����ͼ��ͼ��
			final TrayIcon tray=new TrayIcon(img.getImage(),"Chinen Music",jpMenu);
			try {
				SystemTray.getSystemTray().add(tray);
			} catch (AWTException ex) {
				MSGBOX.MessageBox("����ϵͳ����ʧ��");
			}
			//�����˵��Ĳ˵������Ϣ��Ӧ
			//�˳�
			tuichu.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					SystemTray.getSystemTray().remove(tray);
					System.exit(0);
				}
			});
			ButtonLinkItem(last,shang);//��һ��
			ButtonLinkItem(stop,bofang);//���š���ͣ
			ButtonLinkItem(next,xia);//��һ��
			ButtonLinkItem(lrc,geci);//���
			ButtonLinkItem(style,fangshi);//����˳��
			//˫������
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
	//���������˵����밴ť����ϵ
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
	//���������ĵ���ʽ�˵�
	private void createPopMenu()
	{
		//����һ������ʽ�˵����в���
		jpm=new JPopupMenu();
		zanting=new JMenuItem("����/��ͣ");
		del=new JMenuItem("ɾ��");
		delall=new JMenuItem("ɾ��(�����ļ�)");
		addload=new JMenuItem("���������");
		cleartable=new JMenuItem("����б�");
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
		
		//����ʽ�˵�����Ϣ��Ӧ����
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
				//ɾ������ѡ����
				model.removeRow(popRow);
				//ɾ�������е��ļ�
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
					//����������Ŀ
					next.doClick();
				}
			}
		});
		delall.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//ɾ���ļ�
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
		//����б�
		cleartable.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//��ձ�������
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
		//���������
		addload.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				selectFile=list.get(popRow);
				//�ж��ļ��Ƿ�����ڱ���
				if(selectFile.getParent().equals("F:\\ChinenMusic"))
				{
					MSGBOX.MessageBox("�ļ��Ѵ����ڱ���");
					return;
				}
				//�����ļ��������б�
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
					MSGBOX.MessageBox("����ļ��������б�ʧ��");
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
		
		jf=new JFrame("���ֲ�����");
		
		state=new JLabel("��ǰ�޲�����Ŀ");
		state.setPreferredSize(new Dimension(80,25));
		state.setHorizontalAlignment(SwingConstants.CENTER);
		tl=new JLabel("00:00");
		tl.setHorizontalAlignment(SwingConstants.CENTER);
		time=new JLabel("00:00");
		time.setHorizontalAlignment(SwingConstants.CENTER);
		showLrc=new JLabel("");
		showLrc.setHorizontalAlignment(SwingConstants.CENTER);
		showLrc.setFont(new Font("����",Font.BOLD,16));
		showLrc.setForeground(Color.GREEN);
		jpb=new JProgressBar();
		jpb.setIndeterminate(false);
		jpb.setPreferredSize(new Dimension(80,3));
		js=new JSlider(JSlider.VERTICAL,0,100,30);
	
		
		
		
		
		next=new JButton("����");
		last=new JButton("����");
		stop=new JButton("��");
		lrc=new JButton("���");
		style=new JButton("˳��");
		choose=new JButton(new ImageIcon(".\\res\\Choose.jpg"));
		when=new JButton(new ImageIcon(".\\res\\When.jpg"));
		load=new JButton(new ImageIcon(".\\res\\Load.jpg"));
	
		list=new ArrayList<File>();
		
		js.setOpaque(false);
		//���ð�ť͸��
		lrc.setContentAreaFilled(false);
		choose.setContentAreaFilled(false);
		style.setContentAreaFilled(false);
		next.setContentAreaFilled(false);
		stop.setContentAreaFilled(false);
		last.setContentAreaFilled(false);
		when.setContentAreaFilled(false);
		load.setContentAreaFilled(false);
		//���ð�ť��С
		style.setPreferredSize(new Dimension(60,28));
		lrc.setPreferredSize(new Dimension(60,28));
		next.setPreferredSize(new Dimension(60,28));
		stop.setPreferredSize(new Dimension(60,28));
		last.setPreferredSize(new Dimension(60,28));
		when.setPreferredSize(new Dimension(38,80));
		load.setPreferredSize(new Dimension(38,80));
		choose.setPreferredSize(new Dimension(63,30));
		
		//���ð�ť�߿򲻿ɼ�
		style.setBorderPainted(false);
		last.setBorderPainted(false);
		stop.setBorderPainted(false);
		next.setBorderPainted(false);
		lrc.setBorderPainted(false);
		choose.setBorderPainted(false);
		when.setBorderPainted(false);
		load.setBorderPainted(false);
		//ȡ����ťѡ�е��Ч��
		style.setUI(new BasicButtonUI());
		last.setUI(new BasicButtonUI());
		stop.setUI(new BasicButtonUI());
		next.setUI(new BasicButtonUI());
		lrc.setUI(new BasicButtonUI());
		choose.setUI(new BasicButtonUI());
		when.setUI(new BasicButtonUI());
		load.setUI(new BasicButtonUI());
		
		//��ʼ�������ر���
	    currentTime = 0;//�����ʱʱ��  
	    currentContent = null;//�����ʱ���  
	    maps = new HashMap<Long, String>();//�û��������еĸ�ʺ�ʱ�����Ϣ���ӳ���ϵ��Map  
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
		
		//�ؼ�1
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx=0;c1.gridy=0;c1.weightx=2;
		c1.fill=GridBagConstraints.HORIZONTAL;
		//�ؼ�4
		GridBagConstraints c4 = new GridBagConstraints();
		c4.insets=new Insets(0,8,0,0);
		c4.gridx=1;c4.gridy=0;c4.weightx=80;
		c4.fill=GridBagConstraints.HORIZONTAL;
		//�ؼ�3
		GridBagConstraints c3 = new GridBagConstraints();
		c3.insets=new Insets(0,8,0,0);
		c3.gridx=2;c3.gridy=0;c3.weightx=2;
		c3.fill=GridBagConstraints.HORIZONTAL;
		
		//�ؼ�5
		GridBagConstraints c5 = new GridBagConstraints();
		c5.gridx=0;c5.gridy=1;c5.weightx=2;
		c5.fill=GridBagConstraints.HORIZONTAL;
		//�ؼ�2
		GridBagConstraints c2 = new GridBagConstraints();
		c2.insets=new Insets(8,0,0,0);
		c2.gridx=1;c2.gridy=1;c2.weightx=100;
		c2.fill=GridBagConstraints.HORIZONTAL;
		//�ؼ�6
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
		//����ϵͳ����
		createTray();
		//��������ʽ�˵�
		createPopMenu();
		//����ϵͳͼ��
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
				if(change)//����ڵ�ǰ���Ž��棬�����ִ��ѡ�����
				{
					JFileChooser filechoose=new JFileChooser();
					filechoose.setFileFilter(new FileNameExtensionFilter(
							"֧����Ƶ�ļ�(*.mp3��*.wav)","mp3","wav"));
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
							MSGBOX.MessageBox("��Ч����Ƶ�ļ�");
						}
					}
				}
			}
		});
		table.addMouseListener(new MouseAdapter()
		{
            public void mouseClicked(MouseEvent e)
            {
            	if(e.getButton()==MouseEvent.BUTTON1)//��Ӧ�������������
            	{
            		row=table.getSelectedRow();
            		// ˫��ʱ����
                    if (e.getClickCount() == 2)
                    {
                    	//˫��ʱ�������ָı䲥�Ű�ť���� ѡ���ļ� ��ʼ����
                    	doplay();
                    }
            	}
            }
            public void mouseReleased(MouseEvent e)
            {
            	//��Ӧ������Ҽ��ͷŲ���
            	popRow=table.rowAtPoint(e.getPoint());//�õ�������ڵ��� 
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
					style.setText("���");
					i++;
				}
				else if(i%3==1)
				{
					style.setText("����");
					i++;
				}
				else
				{
					style.setText("˳��");
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
					if(s.equals("˳��"))
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
					else if(s.equals("���"))
					{
						//���
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
				//���Ĳ��Ű�ť��״̬
				if(!hasStop&&whenrow==row)
				{
					if(btnstate)
					{
						stop.setText("| |");
						btnstate=false;
					}
					else
					{
						stop.setText("��");
						btnstate=true;
					}
				}
				//ѡ�и������ִ�в��Ų���
				if(isStop && row!=-1)
				{
					doplay();
				}
				if(whenrow!=row)
				{
					//�л���Ŀ
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
					showLrc.setText("���������...");
					lrcstate=false;
					if(it==null)
					{
						showLrc.setText("���޸��");
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
				if(!change)//������ǰ����ҳ��
				{
					row=-1;//��ѡ�е����б��ѡ������Ϊ-1
					whenrow=row;
					//��ձ�������
					model.getDataVector().clear();
					model.fireTableDataChanged();
					table.updateUI();
					//��list�����е��ļ���Ϣд�����
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
				
				if(change)//���������б�
				{
					row=-1;//��ѡ�е����б��ѡ������Ϊ-1
					whenrow=row;
					//��ձ�������
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
		//����ѡ����Ŀ���и�ֵ����ǰ������Ŀ������
		whenrow=row;
    	play();
		
	}
	//�������ֲ���
	private void play()
	{
		//�����ǰ���ڲ�����������   �ȴ�֮ǰ�����ֹر�     ��Ҫ�ȴ��������̵߳Ĺر�
		if(!hasStop)
		{
			isStop=true;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex){
				MSGBOX.MessageBox("�ȴ������̹߳رմ���");
			}
		}
		String s=selectFile.getName();
		state.setText(s.substring(0,s.length()-4));
		time.setText(getMusicTime(selectFile));
		musicplay=true;
		stop.setText("| |");
		btnstate=false;
		//���в������ֲ���
		try
		{
            // ȡ���ļ�������
            audioInputStream = AudioSystem.getAudioInputStream(selectFile);
            audioFormat = audioInputStream.getFormat();
            // ת��mp3�ļ�����
            if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) 
            {
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        audioFormat.getSampleRate(), 16, audioFormat
                                .getChannels(), audioFormat.getChannels() * 2,
                        audioFormat.getSampleRate(), false);
                audioInputStream = AudioSystem.getAudioInputStream(audioFormat,
                        audioInputStream);
            }
 
            // ������豸
            DataLine.Info dataLineInfo = new DataLine.Info(
                    SourceDataLine.class, audioFormat,
                    AudioSystem.NOT_SPECIFIED);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
            
            //�õ���ʵ�maps��
            set=null;it=null;
            maps.clear();//���maps�����ӳ��
            treemap.clear();
    		if(parser("F:\\ChinenMusic\\"+state.getText()+".lrc"))
    		{
    			treemap.putAll(maps);
        		set=treemap.keySet();//����Map����������key����ļ���
        		it=set.iterator();//�������ϵ�����
    		}
            // ���������߳̽��в���
            isStop = false;//��ʾ��ǰ������������
            Thread playThread = new Thread(new PlayThread());
            playThread.start();
              
        } 
		catch (Exception ex)
		{
			MSGBOX.MessageBox("���ֲ���׼�����ִ���");
        }
	}
	//�Զ���������
	private void PlayAll(String s)
	{
		if(row>-1)
		{
			if(s.equals("˳��"))
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
			else if(s.equals("���"))
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
	            hasStop=false;//��������Ƿ����
	            //�����̻߳�ȡ���ֲ��ŵĽ���
	            Thread playprocess=new Thread(new PlayProcess());
	            playprocess.start();
	            // ��ȡ���ݵ���������
	            while ((cnt = audioInputStream.read(tempBuffer, 0,
	                    tempBuffer.length)) != -1)
	            {
	            	if (isStop)
	                {
	            		musicplay=false;
	                	break;//���isStopΪ���������ǰ���ŵ�����
	                }
	            	if (cnt > 0)
		            {
	                    // д�뻺������
	                    sourceDataLine.write(tempBuffer, 0, cnt);
	                }
	                //��ͣ�벥��
                	while(btnstate)
                	{
                		if (isStop)
    	                {
                			btnstate=false;
    	                }  
                	}
	            } 
	            //Block�ȴ���ʱ���ݱ����Ϊ��
	            sourceDataLine.drain();
	            sourceDataLine.close();
	            hasStop=true;//�����ѽ���
	            //�ı����ֳ�ʼ״̬
	            state.setText("��ǰ�޲�����Ŀ");
	            stop.setText("��");
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
	           MSGBOX.MessageBox("�������̲߳������ֳ���");
	        }
	    }
	}
	//�������߳�
	class PlayProcess extends Thread
	{	
		//��ȡ��ǰ��������ʱ��
        String dt=time.getText();//��ȡ��ǰ��Ƶ��ʱ��
        String m=dt.substring(0,2);
        String s=dt.substring(3,5);
        int t=Integer.parseInt(m)*60+Integer.parseInt(s);
        
		public void run()
		{
			int i=0;//����߳���ѭ�����еĴ���
			boolean isShow=true;//���ĳ�и���Ƿ���ʾ
			Long tt = null;//��¼TreeMap�е�ʱ��
			//�ж��Ƿ��и��
			if(!lrcstate&&it==null)
			{
				showLrc.setText("���޸��");
			}
			//ѭ����ʼ
			while(!hasStop)//�����ڲ�����  
			{
				if(!btnstate)//�жϸ����Ƿ���ͣ
				{
					//��̬��ʾ���
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
					
					//��̬��ʾ�������Ž���
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
	//������
	public static void main(String [] args)
	{
		new MainFrame();
	}
	
	//�õ������Ϣ
	private boolean parser(String path) throws Exception
	{
		File f=new File(path);
		if(!f.exists())
		{
			return false;
		}
		//���ض��ı��뷽ʽ��ȡLrc�ļ�
		InputStream ins=new FileInputStream(f);//������
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