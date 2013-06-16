package jp.chichiritsuka.spring.multipletest.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.apache.commons.codec.binary.Base64;

/**
 * 
 * @author donguri0126
 */
@Controller
public class HomeController {

	@RequestMapping(value="/")
	public ModelAndView test(HttpServletResponse response) throws IOException{
		return new ModelAndView("home");
	}

	@RequestMapping(value="/filepost", method=RequestMethod.POST)
	@ResponseBody
	public String posttest(HttpServletRequest req, HttpServletResponse res) throws IllegalStateException, IOException, ServletException{
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		//factory.setSizeThreshold(1024);
		//upload.setSizeMax(-1);
		upload.setHeaderEncoding("UTF-8");

		List<String> datauris = new ArrayList<String>();

		try {
			List<FileItem> list = upload.parseRequest(req);
			upload.getItemIterator(req);
			for(FileItem fItem: list) {
				if(!(fItem.isFormField())){
					//datauriとして表示可
					datauris.add(new String(Base64.encodeBase64(getBytes(fItem.getInputStream())), "UTF8"));
					
					//file出力
					String fileName = fItem.getName();
					if((fileName != null) && (!fileName.equals(""))){
						fileName = (new File(fileName)).getName();
						fItem.write(new File("/Users/donguri/Desktop/img/" + fileName));
					}
				}
			}
		}catch (FileUploadException e) {
			e.printStackTrace();
			return "failed";
		}catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}

		return datauris.get(0);
	}


	//こっちだと3.8KBまでしか読み込めない。理由は不明。
	@RequestMapping(value="/filepost2", method=RequestMethod.POST)
	@ResponseBody
	public String posttest2(HttpServletRequest req, HttpServletResponse res) throws IllegalStateException, IOException, ServletException{
		List<String> fulls = new ArrayList<String>();
		List<String> imgDatas = new ArrayList<String>();

		try {
			ServletFileUpload upload = new ServletFileUpload();

			upload.setHeaderEncoding("UTF-8");
			FileItemIterator iterator = upload.getItemIterator(req);
			while(iterator.hasNext()){

				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();
				if(item.isFormField()){
					System.out.println(item.getName());
					if(item.getFieldName().equals("vFormName")){
						byte[] str = new byte[stream.available()];
						stream.read(str);
						fulls.add(new String(str,"UTF8"));
					}
				}else{
					System.out.println(item.getName());
					byte[] data = new byte[stream.available()];
					stream.read(data);
					imgDatas.add(new String(Base64.encodeBase64(data), "UTF8"));
				}
			}

		} catch (FileUploadException e) {
			e.printStackTrace();
			return "error";
		}

		//複数ファイル読み込めるけど実装面倒なので1ファイルしか返さないs
		return imgDatas.get(0);
	}

	private static byte[] getBytes(InputStream is) {
		// byte型の配列を出力先とするクラス。
		// 通常、バイト出力ストリームはファイルやソケットを出力先とするが、
		// ByteArrayOutputStreamクラスはbyte[]変数、つまりメモリを出力先とする。 
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		OutputStream os = new BufferedOutputStream(b);
		int c;
		try {
			while ((c = is.read()) != -1) {
				os.write(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.flush();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// 書き込み先はByteArrayOutputStreamクラス内部となる。
		// この書き込まれたバイトデータをbyte型配列として取り出す場合には、
		// toByteArray()メソッドを呼び出す。 
		return b.toByteArray();
	}
}
