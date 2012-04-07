import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
class PicExtractor
{
	static Logger l = new Logger("log.txt");
	static int tot=0;
	static int mval = 20;
	static BufferedWriter bw;
	static String imgUrl[] = new String[200000];
	static int ti=-1;
	static String totryDown[];
	static int uad=0;
	static int ttd = 0;
	public static void main(String abc[]) throws Exception
	{
		totryDown = new String[6];	totryDown[0] = "1366x768"; totryDown[1] = "1920x1200";	totryDown[2] = "1680x1050"; totryDown[3] = "-1600"; totryDown[5] = "-1280"; totryDown[4] = "-1280";
		l.start("Log started");
		analyze();
		System.out.println("Program started");
		Scanner s = new Scanner(System.in);
		Scanner srf = new Scanner(new File("page"));
		int i = Integer.parseInt(srf.nextLine());
		srf.close();
		BufferedWriter bwf;
		for(;i<=22000;i+=18)
		{
			bwf = new BufferedWriter(new FileWriter(new File("page"),false));
			bwf.write(Integer.toString(i));
			bwf.close();
			savePg("http://www.carwalls.com/?p="+i);
			if(mval==8)
				break;
			if(uad>=40)
			{
				BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("page"),false));
				bwr.write("0");
				bwr.close();
				break;
			}
		}
		l.end("Total images downloaded: "+tot);
		System.out.println("Program completed. Total Images Downloaded: "+tot);
		l.end("END OF LOG");
	}
	public static boolean isUrlPresent(String str) throws Exception
	{
		for(int i=0;i<=ti;i++)
			if(str.equals(imgUrl[i]))
				return true;
		return false;
	}
	public static void analyze() throws Exception
	{
		File f = new File("downloads.imp");
		if(f.exists())
		{
			Scanner sr = new Scanner(f);
			for(;sr.hasNextLine();)
				imgUrl[++ti] = sr.nextLine();
			sr.close();
			l.log("File loaded to memory");
		}
		else
		{
			l.log("File not present, creating downloads.imp");
			f.createNewFile();
		}
	}
	public static boolean readIm(String str) throws Exception
	{
		l.log("Downloading Image with url: "+str);
		String name = str.substring(str.lastIndexOf("/",str.length())+1,str.length());
		String company;
		if(name.indexOf("_")!=-1)
			company = name.substring(0,name.indexOf("_"));
		else if(name.indexOf("-")!=-1)
			company = name.substring(0,name.indexOf("-"));
		else
			company = name.substring(0,5);
		Date dt = new Date();
		System.out.println("Downloading Image: "+name);
		File f = new File("Images\\"+company);
		if(!f.exists())
			f.mkdir();
		BufferedImage image = null;
        
		URL url = new URL(str);
		int count = 0;
		while(count<5)
		{
			try
			{
				image = ImageIO.read(url);
				ImageIO.write(image, "jpg",new File("Images\\"+company+"\\"+name));
				System.out.println("Image Downloaded");
				l.log("Image Downloaded: "+name);
				tot++;
				imgUrl[++ti] = str;
				bw = new BufferedWriter(new FileWriter(new File("downloads.imp"), true));
				bw.write(str + "\n");
				bw.close();
				break;
			}
			catch(ConnectException e)
			{
				count++;
				l.log("Connection Failed, Trying Again");
			}
			catch(UnknownHostException ue)
			{
				count=0;
				System.out.println("The system is not connected to the internet. Please check your connections");
				l.log("The system is not connected to the internet. Please check your connections");
				Thread.sleep(1000);
			}
			catch(javax.imageio.IIOException ie)
			{
				System.out.println("URL does not exist...");
				l.log("URL does not exist: "+str);
				return false;
			}
			catch(IllegalArgumentException iae)
			{
				System.out.println("Image not found on server");
				l.log("Image not found on server");
				return false;
			}
		}
		if(count==5)
			return false;
		return true;
	}
	public static int checkPg() throws Exception
	{
		Scanner sr = new Scanner(new File("temp"));
		int c=1;
		String rd;
		while(c<=18)
		{
			rd=sr.nextLine();

			if( rd.indexOf("100x75.jpg")!=-1 )	
				c++;
			if( !sr.hasNextLine() )
				break;
		}
		sr.close();
		return c;
	}
	public static void readPg() throws Exception
	{
		l.log("Analyzing Downloaded Page");
		int val = checkPg();
		if(val >=18)
		{
			l.log("val = "+val);
			Scanner sr = new Scanner(new File("temp"));
			int c=1;
			String rd;
			while(c<=18)
			{
				rd=sr.nextLine();
				int startind = rd.indexOf("http://www.desktopmachine.com/pics/");
				int endind = rd.indexOf("100x75.jpg");
				
				if( startind!=-1 && endind!=-1 && startind<endind )
				{
					String an = rd.substring(startind,endind+10);
					c++;
					boolean setter = true;
					ttd = 0;
					String an2;
					do{
					an2 = an.replaceAll("100x75",totryDown[ttd]);
					if(!isUrlPresent(an2))
					{
						setter = readIm(an2);
						if(!setter)
						{
							if(ttd<5)
							{
								ttd++;
								l.log("Using different dimensions: "+totryDown[ttd]);
								System.out.println("Using different dimensions: "+totryDown[ttd]);
							}
							else
							{
								l.log("Out of dimensions, skipping file");
								System.out.println("Out of dimensions, skipping file");
								setter = true;
							}
						}
					}
					else
					{
						l.log("Url already downloaded: "+an2);
						uad++;
						setter = true;
					}
					}while(setter==false);
				}
				if( !sr.hasNextLine() )
					break;
			}
			sr.close();
		}
		else
		{
			mval = val;
			l.log("Value is less. val = "+val);
		}
	}
	public static void savePg(String str) throws Exception
	{
		l.log("Downloading page to system: "+str);
		System.out.println("Downloading pages..." + str);
		URL url = new URL(str);
		int count = 0;
		while(count<5)
		{
			try
			{
				URLConnection conn = url.openConnection();
				conn.connect();
				InputStreamReader content = new InputStreamReader(conn.getInputStream());
				BufferedReader br = new BufferedReader(content);
				FileOutputStream fos = new FileOutputStream("temp");
				for (int i=0,c=0; i != -1 && c<95000 ; c++)
					fos.write((char)br.read());
				fos.close();
				l.log("Downloaded page");
				readPg();
				break;
			}
			catch(ConnectException e)
			{
				count++;
				l.log("Connection Failed, Trying Again");
			}
			catch(UnknownHostException ue)
			{
				count=0;
				System.out.println("The system is not connected to the internet. Please check your connections");
				l.log("The system is not connected to the internet. Please check your connections");
				Thread.sleep(1000);
			}
		}
	}
}