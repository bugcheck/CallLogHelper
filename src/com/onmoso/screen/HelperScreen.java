package com.onmoso.screen;

import java.util.Date;

import net.rim.blackberry.api.phone.phonelogs.CallLog;
import net.rim.blackberry.api.phone.phonelogs.PhoneCallLog;
import net.rim.blackberry.api.phone.phonelogs.PhoneCallLogID;
import net.rim.blackberry.api.phone.phonelogs.PhoneLogs;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.text.TextFilter;
/**
 * 通话记录生成器
 * @author xiangguang
 * @version 1.0
 * http://www.onmoso.com
 */
public class HelperScreen extends FullScreen{
	
	//标题
	private final String APP_NAME = "Call Log Helper";//通话记录生成器
	private final Font F_20 = Font.getDefault().derive(Font.PLAIN, 20);
	private final Bitmap BANNER = Bitmap.getBitmapResource("banner.png");
	
	private VerticalFieldManager _mainManager = new VerticalFieldManager(VERTICAL_SCROLL|VERTICAL_SCROLLBAR);
	private VerticalFieldManager _verManager = new VerticalFieldManager();
	private MyManager _contentManager = new MyManager();
	private MyManager _aboutManager = new MyManager();
	
	private final int[] _arrayIndex = {PhoneCallLog.TYPE_RECEIVED_CALL,PhoneCallLog.TYPE_PLACED_CALL,PhoneCallLog.TYPE_MISSED_CALL_UNOPENED};
	private final String[] _array = {"Incoming Call","Outgoing Call","Missed Call"};//来电去电未接
	
	private ObjectChoiceField _typeField = new ObjectChoiceField("Call Type:", _array);//类型
	private DateField _dateField = new DateField("Call Date:", System.currentTimeMillis(), DateField.DATE_TIME);//日期
	private EditField _durtion = new EditField("Call Duration:", "");//持续时间(秒)
	private EditField _number = new EditField("Phone Number:", "");//电话
	private EditField _notes = new EditField("Notes:", "");//备注
	private HorizontalFieldManager _buttonManager = new HorizontalFieldManager(FIELD_HCENTER);
	private ButtonField _save = new ButtonField("Generate");//生成
	
	/**
	 * 构造方法
	 */
	public HelperScreen() {
		super(DEFAULT_CLOSE|DEFAULT_MENU);
		this.init();
	}

	/**
	 * 初始化界面信息
	 */
	private void init(){
		_durtion.setFilter(TextFilter.get(TextFilter.NUMERIC));
		_number.setFilter(TextFilter.get(TextFilter.PHONE));
		
		_verManager.add(new TitleLabelField("Options"));//设置
		_verManager.add(new ColorSeparatorField());
		_verManager.add(_typeField);
		_verManager.add(_dateField);
		_verManager.add(_number);
		_verManager.add(_durtion);
		_verManager.add(_notes);
		_buttonManager.add(_save);
		_verManager.add(_buttonManager);
		_verManager.setMargin(5,5,5,5);
		_contentManager.add(_verManager);
		_contentManager.setMargin(5,5,5,5);
		
		VerticalFieldManager ver2 = new VerticalFieldManager();
		ver2.setMargin(5,5,5,5);
		ver2.add(new TitleLabelField("About"));//关于
		ver2.add(new ColorSeparatorField());
		ver2.add(new NullField(Field.FOCUSABLE));
		ver2.add(new LabelField(APP_NAME + " "+getVersion(),FIELD_HCENTER));
		ver2.add(new LabelField("zhaoxiangguang@gmail.com",FIELD_HCENTER));
		ver2.add(new LabelField("http://www.onmoso.com",FIELD_HCENTER));
		
		_aboutManager.add(ver2);
		_aboutManager.setMargin(5,5,5,5);
		
		_mainManager.add(_contentManager);
		_mainManager.add(_aboutManager);
		
		_mainManager.setMargin(BANNER.getHeight(), 0, 0, 0);
		this.add(_mainManager);
	}
	
	/**
	 * (non-Javadoc)
	 * @see net.rim.device.api.ui.Screen#paint(net.rim.device.api.ui.Graphics)
	 */
	protected void paint(Graphics g) {
		g.setFont(F_20);
        //画主界面顶部图片
		int bannerWidth = BANNER.getWidth();
		int bannerHeight = BANNER.getHeight();
		g.setColor(0xededed);
		int disWidth = Display.getWidth();
		g.fillRect(0, 0, disWidth, this.getHeight());
		int t = disWidth/bannerWidth + 1 ;
		for(int i=0;i<t;i++){
			g.drawBitmap(bannerWidth * i, 0, bannerWidth, bannerHeight,BANNER, 0, 0);
		}
	    g.setColor(0xc6c6c6);
		int y = (bannerHeight - F_20.getHeight()) >> 1;
		g.drawText(APP_NAME,1, y);
		
		g.setColor(0x545454);
		super.paint(g);
	}
	
	/**
	 * (non-Javadoc)
	 * @see net.rim.device.api.ui.Screen#trackwheelClick(int, int)
	 */
	protected boolean trackwheelClick(int status, int time) {
		// TODO Auto-generated method stub
		if(_save.isFocus()){
			onClick();
			return true;
		}
		return super.trackwheelClick(status, time);
	}
	
	private void onClick(){
		String number = _number.getText();
		String dur = _durtion.getText();
		if(checkNull(number)){
			Dialog.alert("Phone number must be entered");//电话不能为空
			_number.setFocus();
		}else if(checkNull(dur)){
			Dialog.alert("Call duration must be entered");//持续时间不能为空
			_durtion.setFocus();
		}else{
			int answer = Dialog.ask(Dialog.D_OK_CANCEL, "Generate Call Log ?");//确定生成一条新的通话记录？
			if(answer == Dialog.D_OK){
				generateLog(number,dur);
			}
		}
	}
	
	protected boolean keyChar(char c, int status, int time) {
		// TODO Auto-generated method stub
		switch(c){
		case Characters.ENTER:
			onClick();
			return true;
		}
		return super.keyChar(c, status, time);
	}
	
	public boolean checkNull(String str) {
		boolean result = true;
		try {
			if (str != null && !str.equals("") && !str.equals(" "))
				result = false;
		} catch (Exception e) {
		}
		return result;
	}
	
	/**
	 * 获得当前软件版本号
	 * @return 版本号
	 */
	public String getVersion() {
		ApplicationDescriptor ap = null;
		ap = ApplicationDescriptor.currentApplicationDescriptor();
		String curVersion = null;
		if (ap != null)
			curVersion = ap.getVersion();

		if (curVersion == null || curVersion.equals(""))
			curVersion = "1.0.0";

		return curVersion;
	} 
	
	/**
	 * 生成通话记录
	 */
	private void generateLog(String number,String dur){
		PhoneLogs pl = PhoneLogs.getInstance();
		PhoneCallLogID callLogId = new PhoneCallLogID(number);
		Date date = new Date(_dateField.getDate());
		int index = _typeField.getSelectedIndex();
		int inn = _arrayIndex[index];
		int inn2 = Integer.parseInt(dur);
		PhoneCallLog callLog = new PhoneCallLog(date, inn, inn2,CallLog.STATUS_NORMAL , callLogId, _notes.getText());
		pl.addCall(callLog);
		Dialog.alert("Success");//新增成功
		callLogId = null;
		callLog = null;
		pl = null;
	}
	
	class TitleLabelField extends LabelField{
		public TitleLabelField(String text){
			super(text);
		}
		
		protected void paint(Graphics graphics) {
			// TODO Auto-generated method stub
			graphics.setColor(0xa5a2a5);
			super.paint(graphics);
		}
	}
	
	class ColorSeparatorField extends SeparatorField{
		
		public ColorSeparatorField() {
			// TODO Auto-generated constructor stub
			super();
		}
		protected void paint(Graphics graphics) {
			// TODO Auto-generated method stub
			graphics.setColor(0xd6d3d6);
			super.paint(graphics);
		}
	}
	
	class MyManager extends VerticalFieldManager{
		public MyManager() {
			// TODO Auto-generated constructor stub
			super();
		}
		protected void paint(Graphics g) {
			g.setColor(Color.WHITE);
			g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 8, 8);
			g.setColor(0xBBBBBB);
			g.drawRoundRect(0, 0, this.getWidth(), this.getHeight(),8,8);
			g.setColor(Color.BLACK);
			super.paint(g);
		}
	}
}

