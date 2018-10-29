import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.minxing.client.app.AppAccount;
import com.minxing.client.model.MxException;

public class TestDownloadFile {

	public static void main(String[] args) {
		AppAccount account = AppAccount.loginByAccessToken(
				"http://localhost:3000",
				"iPefUDrrardwZMWQXaZnBDBCLyY3iksJTmYtP2rcrJ0EYCJA");

		try {
			
			account.setFromUserLoginName("exampleAccount");

			InputStream finput = account.downloadFile(37L,"3f9eda65386e5ec3f8f9dbcf95adc726");
			File f = new File("/tmp/download_file");
			FileOutputStream out = new FileOutputStream(f);
			byte[] buf = new byte[1024*128];
			int fread = 0;
			int total_size = 0;
			while((fread = finput.read(buf)) != -1) {
				out.write(buf, 0, fread);
				total_size += fread;
				
			}
			
			out.flush();
			out.close();
	
			finput.close();
			
			System.out.println("download file with size:" + total_size);

		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(MxException e) {
			// Log the error, 文件不存在或已被删除
			System.out.println("error found: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
