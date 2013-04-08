import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
public class Logger
{
	private File fpath;
	public Logger(String path)
	{
		fpath = new File(path);
	}
	public void log(String str) throws Exception
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(fpath,true));
		bw.write(currDate() + " " + str + "\n");
		bw.close();
	}
	private String currDate()
	{
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("[dd/MM/yy kk:mm:ss]");
	        return (sdf.format(d));
	}
	public void clear() throws Exception
	{
		if(fpath.exists())
		{
			fpath.delete();
		}
		fpath.createNewFile();
	}
	public void start(String str) throws Exception
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(fpath,true));
		bw.write(str + "\n\n");
		bw.close();
	}
	public void end(String str) throws Exception
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(fpath,true));
		bw.write("\n" + str);
		bw.close();
	}
}